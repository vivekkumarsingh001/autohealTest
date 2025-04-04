package stepdefinitions;
    
import io.cucumber.java.en.*;
import workflows.SeleniumWorkFlow;
import common.*;

  @SuppressWarnings("all")
  public class DemoWebShopStepDefinition
	 {
      SeleniumWorkFlow workFlow = new SeleniumWorkFlow();
      
            @When("^I entered Email in demo web shop as '(.*)'$")			
            public void whenIEnteredEmailInDemoWebShopAsemail(String  varemail)
            {
                workFlow.enterText(varemail,0,"Demo Web Shop","Demo Web Shop.EmailTextBoxXPATH","XPATH");
                
            }
            @When("^I entered Password in demo web shop as '(.*)'$")			
            public void whenIEnteredPasswordInDemoWebShopAspassword(String  varpassword)
            {
                workFlow.enterText(varpassword,0,"Demo Web Shop","Demo Web Shop.PasswordTextBoxXPATH","XPATH");
                
            }
            @When("^I selected Log in in demo web shop$")			
            public void whenISelectedLogInInDemoWebShop()
            {
                workFlow.clickedElement(0,"Demo Web Shop","Demo Web Shop.LoginButtonXPATH","XPATH");
                
            }
             @Then("^'(.*)' is displayed with '(.*)'$")			
            public void thenpageIsDisplayedWithcontent(String varpage, String varcontent)
            {
                Assertion.isTrue(workFlow.verifyDefaultpageIsdisplayed(varpage), "Then '<page>' is displayed with '<content>'");
                Assertion.isTrue(workFlow.verifymessageIsDisplayed(varcontent), "");
                
            }
             @Then("^verify text Books in demo web shop$")			
            public void thenVerifyTextBooksInDemoWebShop()
            {
                Assertion.isTrue(workFlow.verifyTextInLink(0,"Demo Web Shop","Demo Web Shop.BooksLinkXPATH","XPATH"), "Then verify text Books in demo web shop");
                WebBrowserUtil.captureScreenshot();
                
            }
            @When("^I clicked Books in demo web shop$")			
            public void whenIClickedBooksInDemoWebShop()
            {
                workFlow.clickedElement(0,"Demo Web Shop","Demo Web Shop.BooksLinkXPATH","XPATH");
                
            }
             @Then("^verify text Computers in demo web shop$")			
            public void thenVerifyTextComputersInDemoWebShop()
            {
                Assertion.isTrue(workFlow.verifyTextInLink(0,"Demo Web Shop","Demo Web Shop.ComputersLinkXPATH","XPATH"), "Then verify text Computers in demo web shop");
                WebBrowserUtil.captureScreenshot();
                
            }
            @When("^I clicked Computers in demo web shop$")			
            public void whenIClickedComputersInDemoWebShop()
            {
                workFlow.clickedElement(0,"Demo Web Shop","Demo Web Shop.ComputersLinkXPATH","XPATH");
                
            }
             @Then("^verify text Electronics in demo web shop$")			
            public void thenVerifyTextElectronicsInDemoWebShop()
            {
                Assertion.isTrue(workFlow.verifyTextInLink(0,"Demo Web Shop","Demo Web Shop.ElectronicsLinkXPATH","XPATH"), "Then verify text Electronics in demo web shop");
                WebBrowserUtil.captureScreenshot();
                
            }
            @When("^I clicked Electronics in demo web shop$")			
            public void whenIClickedElectronicsInDemoWebShop()
            {
                workFlow.clickedElement(0,"Demo Web Shop","Demo Web Shop.ElectronicsLinkXPATH","XPATH");
                
            }
             @Then("^verify text Apparel Shoes in demo web shop$")			
            public void thenVerifyTextApparelShoesInDemoWebShop()
            {
                Assertion.isTrue(workFlow.verifyTextInLink(0,"Demo Web Shop","Demo Web Shop.ApparelShoesLinkXPATH","XPATH"), "Then verify text Apparel Shoes in demo web shop");
                WebBrowserUtil.captureScreenshot();
                
            }
            @When("^I clicked Apparel Shoes in demo web shop$")			
            public void whenIClickedApparelShoesInDemoWebShop()
            {
                workFlow.clickedElement(0,"Demo Web Shop","Demo Web Shop.ApparelShoesLinkXPATH","XPATH");
                
            }
             @Then("^verify text Digital downloads in demo web shop$")			
            public void thenVerifyTextDigitalDownloadsInDemoWebShop()
            {
                Assertion.isTrue(workFlow.verifyTextInLink(0,"Demo Web Shop","Demo Web Shop.DigitaldownloadsLinkXPATH","XPATH"), "Then verify text Digital downloads in demo web shop");
                WebBrowserUtil.captureScreenshot();
                
            }
            @When("^I clicked Digital downloads in demo web shop$")			
            public void whenIClickedDigitalDownloadsInDemoWebShop()
            {
                workFlow.clickedElement(0,"Demo Web Shop","Demo Web Shop.DigitaldownloadsLinkXPATH","XPATH");
                
            }
            @When("^I clicked Computing and Internet in demo web shop$")			
            public void whenIClickedComputingInternetInDemoWebShop()
            {
                workFlow.clickedElement(0,"Demo Web Shop","Demo Web Shop.ComputingandInternetLinkXPATH","XPATH");
                
            }
            @When("^I selected Add to cart in demo web shop$")			
            public void whenISelectedAddToCartInDemoWebShop()
            {
                workFlow.clickedElement(0,"Demo Web Shop","Demo Web Shop.AddtocartButtonXPATH","XPATH");
                
            }
            @When("^I clicked Shopping cart in demo web shop$")			
            public void whenIClickedShoppingCartInDemoWebShop()
            {
                workFlow.clickedElement(0,"Demo Web Shop","Demo Web Shop.ShoppingcartLinkXPATH","XPATH");
                
            }
             @Then("^verify displayed Shopping cart in demo web shop$")			
            public void thenVerifyDisplayedShoppingCartInDemoWebShop()
            {
                Assertion.isTrue(workFlow.verifyTextInLink(0,"Demo Web Shop","Demo Web Shop.ShoppingcartLinkXPATH","XPATH"), "Then verify displayed Shopping cart in demo web shop");
                WebBrowserUtil.captureScreenshot();
                
            }
            @When("^I checked removefromcart in demo web shop$")			
            public void whenICheckedRemovefromcartInDemoWebShop()
            {
                workFlow.checkCheckbox(0,"Demo Web Shop","Demo Web Shop.removefromcartCheckBoxXPATH","XPATH");
                
            }
            @When("^I selected updatecart in demo web shop$")			
            public void whenISelectedUpdatecartInDemoWebShop()
            {
                workFlow.clickedElement(0,"Demo Web Shop","Demo Web Shop.updatecartButtonXPATH","XPATH");
                
            }
            @When("^I selected continueshopping in demo web shop$")			
            public void whenISelectedContinueshoppingInDemoWebShop()
            {
                workFlow.clickedElement(0,"Demo Web Shop","Demo Web Shop.continueshoppingButtonXPATH","XPATH");
                
            }
    }
