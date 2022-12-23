package generic;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.time.Duration;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseTest {
	public static ExtentReports extent;
	
	public WebDriver driver;
	public WebDriverWait wait;
	public ExtentTest test;
	
	public final String PPT_PATH="base.properties";
	public String REPORT_PATH="./target/Spark.html";
	
	static
	{
		WebDriverManager.chromedriver().setup();
		WebDriverManager.firefoxdriver().setup();
	}
	
	@BeforeSuite
	public void createReport() {
		extent = new ExtentReports(REPORT_PATH);
	}
	
	@AfterSuite
	public void publishReport() {
		extent.flush();
	}
	
	@Parameters({"config"})
	@BeforeMethod
	public void openApp(@Optional(PPT_PATH) String path,Method testMethod) throws Exception
	{
		path="./properties/"+path;
		test = extent.startTest(testMethod.getName());
		test.log(LogStatus.INFO, "property file:"+path);
		
		String useGrid =  Util.getProperty(path,"USEGRID");
		String remote =   Util.getProperty(path,"REMOTE");
		String browser = Util.getProperty(path,"BROWSER");
		String appUrl = Util.getProperty(path,"APPURL");
		String sITO = Util.getProperty(path,"ITO");
		String sETO =  Util.getProperty(path,"ETO");
		
		if(useGrid.equalsIgnoreCase("yes"))
		{
			test.log(LogStatus.INFO, "Open the "+browser+" Browser in Remote System");
			DesiredCapabilities capability = new DesiredCapabilities();
			capability.setBrowserName(browser);
			driver =new RemoteWebDriver(new URL(remote),capability );
		}
		
		else
		{
			test.log(LogStatus.INFO, "Open the "+browser+" Browser in Local System");
			
				if(browser.equalsIgnoreCase("chrome"))
				{
					driver=new ChromeDriver();
				}
				else
				{
					driver=new FirefoxDriver();
				}
		}
		
		test.log(LogStatus.INFO, "Maximize the Browser");
		driver.manage().window().maximize();
		
		test.log(LogStatus.INFO, "Enter the url:"+appUrl);
		driver.get(appUrl);
		
		test.log(LogStatus.INFO, "Set implicitlyWait:"+sITO);
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(Long.valueOf(sITO)));
		
		test.log(LogStatus.INFO, "Set ExplicitWait"+sETO);
		wait=new WebDriverWait(driver,Duration.ofSeconds(Long.valueOf(sETO)));
	}
	
	
	@AfterMethod
	public void closeApp(ITestResult result) throws IOException
	{
		
		int status = result.getStatus();//1-PASS 2-FAILED
		if(status==2)
		{
			String errDetails = result.getThrowable().getMessage();
			String testName=result.getName();
			test.log(LogStatus.FAIL, errDetails);
		
			TakesScreenshot t=(TakesScreenshot)driver;
			File srcIMG = t.getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(srcIMG, new File("./target/"+testName+".png"));
			String htmlcode = test.addScreenCapture(testName+".png");
			test.log(LogStatus.FAIL, htmlcode);
		}
		
		test.log(LogStatus.INFO, "close the Browser");
		driver.quit();
	}
}
