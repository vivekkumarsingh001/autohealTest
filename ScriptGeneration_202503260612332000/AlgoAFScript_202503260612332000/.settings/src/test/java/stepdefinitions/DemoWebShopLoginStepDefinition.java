package stepdefinitions;
    import io.cucumber.java.en.*;
    import workflows.SeleniumWorkFlow;
    import org.junit.Assert;
    import common.Assertion;
    import common.WebBrowserUtil;
    public class DemoWebShopLoginStepDefinition
	{
        SeleniumWorkFlow workFlow = new SeleniumWorkFlow();
        

            @When("^I entered Email in demo web shop login as '(.*)'$")			
            public void WhenIEnteredEmailInDemoWebShopLoginAsemail(String  _email)
            {
                workFlow.enterText(_email,0,"Demo Web Shop Login","Demo Web Shop Login.EmailTextBoxXPATH","XPATH");
                
            }

            @When("^I entered Password in demo web shop login as '(.*)'$")			
            public void WhenIEnteredPasswordInDemoWebShopLoginAspassword(String  _password)
            {
                workFlow.enterText(_password,0,"Demo Web Shop Login","Demo Web Shop Login.PasswordTextBoxXPATH","XPATH");
                
            }

            @When("^I selected Log in in demo web shop login$")			
            public void WhenISelectedLogInInDemoWebShopLogin()
            {
                workFlow.clickedElement(0,"Demo Web Shop Login","Demo Web Shop Login.LoginButtonXPATH","XPATH");
                
            }
    }