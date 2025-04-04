package stepdefinitions;
    import io.cucumber.java.en.*;
    import workflows.SeleniumWorkFlow;
    import org.junit.Assert;
    import common.Assertion;
    import common.WebBrowserUtil;
    public class SwagLabsStepDefinition
	{
        SeleniumWorkFlow workFlow = new SeleniumWorkFlow();
        

            @When("^I clicked Username in swag labs as '(.*)'$")			
            public void WhenIClickedUsernameInSwagLabsAsusername1(String _username1)
            {
                workFlow.enterText(_username1,0,"Swag Labs","Swag Labs.UsernameTextBoxXPATH","XPATH");
                
            }

            @When("^I clicked Password in swag labs as '(.*)'$")			
            public void WhenIClickedPasswordInSwagLabsAspassword2(String _password2)
            {
                workFlow.enterText(_password2,0,"Swag Labs","Swag Labs.PasswordTextBoxXPATH","XPATH");
                
            }

            @When("^I selected loginbutton in swag labs$")			
            public void WhenISelectedLoginbuttonInSwagLabs()
            {
                workFlow.clickedElement(0,"Swag Labs","Swag Labs.loginbuttonButtonXPATH","XPATH");
                
            }

             @Then("^'(.*)' is displayed with '(.*)'$")			
            public void ThenpageIsDisplayedWithcontent(String _page, String _content)
            {
                Assertion.IsTrue(workFlow.VerifyDefaultpageIsdisplayed(_page), "Then '<page>' is displayed with '<content>'");
                Assertion.IsTrue(workFlow.VerifymessageIsDisplayed(_content), "");;
                //Assertion.assertAll();
            }
    }
