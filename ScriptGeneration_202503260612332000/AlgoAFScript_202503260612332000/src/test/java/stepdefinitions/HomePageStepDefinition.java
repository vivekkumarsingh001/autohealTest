package stepdefinitions;
    
import io.cucumber.java.en.*;
import workflows.SeleniumWorkFlow;
import common.*;

  @SuppressWarnings("all")
  public class HomePageStepDefinition
	 {
      SeleniumWorkFlow workFlow = new SeleniumWorkFlow();
      
             @Given("^I have access to application$")			
            public void givenIHaveAccessToApplication()
            {
                workFlow.accessToPage();
                
            }
    }