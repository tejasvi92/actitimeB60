package scripts;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.LogStatus;

import generic.BaseTest;
import generic.Util;

public class Script1 extends BaseTest
{
	@Test(priority = 1)
	public void createCustomer()
	{
		String data = Util.getXlData("./data/input.xlsx","script1",1, 0);
		test.log(LogStatus.PASS,data);
		
		test.log(LogStatus.PASS,driver.getTitle());
//		Assert.fail("Home page is not displayed");
	}
}
