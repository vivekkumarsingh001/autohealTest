package stepdefinitions;
    import io.cucumber.java.en.*;
    import workflows.SeleniumWorkFlow;
    import org.junit.Assert;
    import common.Assertion;
    import common.WebBrowserUtil;
    public class DemoWebShopComputingandInternetStepDefinition
	{
        SeleniumWorkFlow workFlow = new SeleniumWorkFlow();
        

            @When("^I selected Add to cart in demo web shop computing and internet$")			
            public void WhenISelectedAddToCartInDemoWebShopComputingInternet()
            {
                workFlow.clickedElement(0,"Demo Web Shop Computing and Internet","Demo Web Shop Computing and Internet.AddtocartButtonXPATH","XPATH");
                
            }

             @Then("^verify text The product has been added to your in demo web shop computing and internet$")			
            public void ThenVerifyTextTheProductHasBeenAddedToYourInDemoWebShopComputingInternet()
            {
                Assertion.IsTrue(workFlow.verifyTextInLink(0,"Demo Web Shop Computing and Internet","Demo Web Shop Computing and Internet.TheproducthasbeenaddedtoyourLabelXPATH","XPATH"), "Then verify text The product has been added to your in demo web shop computing and internet");
                WebBrowserUtil.captureScreenshot();
                
            }

            @When("^I clicked Shopping cart in demo web shop computing and internet$")			
            public void WhenIClickedShoppingCartInDemoWebShopComputingInternet()
            {
                workFlow.clickedElement(0,"Demo Web Shop Computing and Internet","Demo Web Shop Computing and Internet.ShoppingcartLinkXPATH","XPATH");
                
            }
    }