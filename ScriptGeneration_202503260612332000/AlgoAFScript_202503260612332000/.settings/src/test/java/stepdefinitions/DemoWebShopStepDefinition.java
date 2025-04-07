package stepdefinitions;
    import io.cucumber.java.en.*;
    import workflows.SeleniumWorkFlow;
    import org.junit.Assert;
    import common.Assertion;
    import common.WebBrowserUtil;
    public class DemoWebShopStepDefinition
	{
        SeleniumWorkFlow workFlow = new SeleniumWorkFlow();
        

            @When("^I clicked Log in in demo web shop$")			
            public void WhenIClickedLogInInDemoWebShop()
            {
                workFlow.clickedElement(0,"Demo Web Shop","Demo Web Shop.LoginLinkXPATH","XPATH");
                
            }

            @When("^I clicked Books2 in demo web shop$")			
            public void WhenIClickedBooks2InDemoWebShop()
            {
                workFlow.clickedElement(0,"Demo Web Shop","Demo Web Shop.Books2LinkXPATH","XPATH");
                
            }
    }