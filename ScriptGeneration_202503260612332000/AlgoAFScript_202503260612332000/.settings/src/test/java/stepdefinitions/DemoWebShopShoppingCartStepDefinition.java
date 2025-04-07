package stepdefinitions;
    import io.cucumber.java.en.*;
    import workflows.SeleniumWorkFlow;
    import org.junit.Assert;
    import common.Assertion;
    import common.WebBrowserUtil;
    public class DemoWebShopShoppingCartStepDefinition
	{
        SeleniumWorkFlow workFlow = new SeleniumWorkFlow();
        

            @When("^I checked removefromcart in demo web shop shopping cart$")			
            public void WhenICheckedRemovefromcartInDemoWebShopShoppingCart()
            {
                workFlow.checkCheckbox(0,"Demo Web Shop Shopping Cart","Demo Web Shop Shopping Cart.removefromcartCheckBoxXPATH","XPATH");
                
            }

            @When("^I selected updatecart in demo web shop shopping cart$")			
            public void WhenISelectedUpdatecartInDemoWebShopShoppingCart()
            {
                workFlow.clickedElement(0,"Demo Web Shop Shopping Cart","Demo Web Shop Shopping Cart.updatecartButtonXPATH","XPATH");
                
            }

             @Then("^verify text Your Shopping Cart is empty in demo web shop shopping cart$")			
            public void ThenVerifyTextYourShoppingCartIsEmptyInDemoWebShopShoppingCart()
            {
                Assertion.IsTrue(workFlow.verifyTextInLink(0,"Demo Web Shop Shopping Cart","Demo Web Shop Shopping Cart.YourShoppingCartisemptyLabelXPATH","XPATH"), "Then verify text Your Shopping Cart is empty in demo web shop shopping cart");
                WebBrowserUtil.captureScreenshot();
                
            }

             @Then("^'(.*)' is displayed with '(.*)'$")			
            public void ThenpageIsDisplayedWithcontent(String  _page, String _content)
            {
                Assertion.IsTrue(workFlow.VerifyDefaultpageIsdisplayed(_page), "Then '<page>' is displayed with '<content>'");
                Assertion.IsTrue(workFlow.VerifymessageIsDisplayed(_content), "");;
                //Assertion.assertAll();
            }
    }