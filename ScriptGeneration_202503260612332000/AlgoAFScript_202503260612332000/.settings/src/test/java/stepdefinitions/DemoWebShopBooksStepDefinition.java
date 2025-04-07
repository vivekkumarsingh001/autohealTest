package stepdefinitions;
    import io.cucumber.java.en.*;
    import workflows.SeleniumWorkFlow;
    import org.junit.Assert;
    import common.Assertion;
    import common.WebBrowserUtil;
    public class DemoWebShopBooksStepDefinition
	{
        SeleniumWorkFlow workFlow = new SeleniumWorkFlow();
        

            @When("^I clicked Computing and Internet in demo web shop books$")			
            public void WhenIClickedComputingInternetInDemoWebShopBooks()
            {
                workFlow.clickedElement(0,"Demo Web Shop Books","Demo Web Shop Books.ComputingandInternetLinkXPATH","XPATH");
                
            }
    }