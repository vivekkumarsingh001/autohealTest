package workflows;
import common.*;
import java.util.stream.Collectors;
import java.util.*;
import com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter;
import java.text.SimpleDateFormat;
import java.io.BufferedWriter;
import java.text.DateFormat;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.http.Headers;
import io.restassured.specification.RequestSpecification;
import java.io.File;
import org.openqa.selenium.interactions.Actions;
import java.nio.file.Path;
import org.openqa.selenium.Keys;
import java.nio.file.Paths;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Assert;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogEntries;
import java.io.IOException;
import java.awt.Color;
import io.percy.selenium.Percy;
import java.io.ByteArrayInputStream;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.OutputType;
    
@SuppressWarnings("all")
public class SeleniumWorkFlow
{
  public static WebDriver browser;
	private static Percy percy;
	private static String percyFlag = CommonUtil.getXMLData(Constants.APPLICATION_SETTING_PATH, "Percy");
	private static String language = CommonUtil.getXMLData(Constants.APPLICATION_SETTING_PATH, "Language");
	private static String yes = "Yes";
	static final Logger log = Logger.getLogger(SeleniumWorkFlow.class);

 		 
  public void accessToPage()
  {
    WebBrowser.LaunchApplication(true);
    log.info("Method accessToPage completed.");
  }public void enterText(String textToBeEntered,int index, String page,String xpathKey,String identifier)
    {
        WebBrowser.setCurrentBrowser(index);
        browser=WebBrowser.getBrowser();
        WebBrowserUtil.AttachPage(page);
        textToBeEntered= CommonUtil.getData(textToBeEntered);
      try
          {
            String xpath=YMLUtil.getYMLObjectRepositoryData(xpathKey);
            WebElement element = WebBrowserUtil.findElement(xpath,identifier);
            if (element != null) {
                String elementHtml = element.getAttribute("outerHTML");
                CommonUtil.storeElementInJson(xpath, elementHtml);
            } else {
                throw new CustomException("Element not found for XPath: " + xpath);
            }
            WebBrowserUtil.EnterText(element, textToBeEntered);
            log.info("Method enterText completed.");
          }
      catch(Exception e)
          {
            log.error("Method enterText not completed."+e);
            throw new CustomException(e.getMessage(), e);
      }
  }
    public void clickedElement(int index,String page,String xpathKey,String identifier)
    {
      WebBrowser.setCurrentBrowser(index);
      browser=WebBrowser.getBrowser();
      WebBrowserUtil.AttachPage(page);
      WebElement elementToBeClicked = null;
      try
      {
        String xpath=YMLUtil.getYMLObjectRepositoryData(xpathKey);        
    		boolean staleElement = true;
    		int i = 0;
        elementToBeClicked = WebBrowserUtil.findElement(xpath,identifier);
        if (elementToBeClicked != null) {
            String elementHtml = elementToBeClicked.getAttribute("outerHTML");
            CommonUtil.storeElementInJson(xpath, elementHtml);
        } else {
            throw new CustomException("Element not found for XPath: " + xpath);
        }
    		while (staleElement && i < 5) {
    			try {
    				WebBrowserUtil.Click(elementToBeClicked);
    				staleElement = false;
    			} catch (Exception ex) {
    				i++;
    				staleElement = true;
    				try {
    					WebBrowserUtil.ScrollAndClickUsingJS(elementToBeClicked);
    					staleElement = false;
    				} catch (Exception exc) {
              log.error(exc.getMessage());
    				}
    				if (i == 4) {
    					throw new CustomException(ex.getMessage(), ex);
    				}
    			}
    		}        
            log.info("Method clickedElement completed.");
      }
      catch(Exception e)
      {
        log.error("Method clickedElement not completed."+e);
        throw new CustomException(e.getMessage(), e);
      }
  }
  public boolean verifyDefaultpageIsdisplayed(String defaultpage)
  {
    boolean isVerified = false;
    for (int i = 0; i <= 2; i++)
    {
      WebBrowser.setCurrentBrowser(0);
      browser=WebBrowser.getBrowser();
      try{
        browser = WebBrowserUtil.AttachPage(defaultpage);
      }
      catch(Exception ex){
      log.error(ex.getMessage());
      }
      if (defaultpage != null && !defaultpage.isEmpty()  && !defaultpage.toUpperCase().equals("NA".toUpperCase()) && !defaultpage.toUpperCase().equals("Page1".toUpperCase()) && !defaultpage.toUpperCase().equals("Page2".toUpperCase()) && !defaultpage.toUpperCase().equals("Page3".toUpperCase())&& !defaultpage.toUpperCase().equals("Page4".toUpperCase()))
      {
        isVerified = browser.getTitle().toUpperCase().contains(defaultpage.toUpperCase());
      }
      else
      {
        isVerified = true;
      }
      if (isVerified)
      {
        break;
      }
    }
      try
      {
        ExtentCucumberAdapter.addTestStepLog("Actual Page Name: "+browser.getTitle()+", Expected Page Name: "+defaultpage);
        log.info("Method VerifyDefaultpageIsdisplayed completed.");
      }
      catch(Exception e)
      {
        log.error("Method VerifyDefaultpageIsdisplayed not completed."+e);
      }
    return isVerified;
  }
  public boolean verifymessageIsDisplayed(String message)
  {
   boolean isVerified = false;
		String[] messages;
		String[] messages1;
		List<String> boolvalues = new ArrayList<>();

		if (message.contains("verifyalert_")) {
			String[] msgs = message.split("_");
			return WebBrowserUtil.verifyAlertText(msgs[1]);
		}

		if ((message.contains("frameid_")) && (message.contains("&or&"))) {
			String[] msgs = message.split("_");
			StringBuilder frameID = new StringBuilder();

			for (int i = 0; i < msgs.length - 1; i++) {
				if (i != 0) {
					frameID.append(msgs[i]).append("_");
				}
			}
			String frameIDStr = frameID.toString().replaceAll("_$", "");
			WebBrowserUtil.GetFrame(frameIDStr);

			String[] multiplemessages = msgs[msgs.length - 1].split("&or&");
			for (String multiplemessage : multiplemessages) {
				if (WebBrowserUtil.IsElementPresent(multiplemessage)) {
					isVerified = true;
				}
			}
			return isVerified;
		}

		if (message.contains("frameid_")) {
			String[] msgs = message.split("_");
			StringBuilder frameID = new StringBuilder();

			for (int i = 0; i < msgs.length - 1; i++) {
				if (i != 0) {
					frameID.append(msgs[i]).append("_");
				}
			}
			String frameIDStr = frameID.toString().replaceAll("_$", "");
			WebBrowserUtil.GetFrame(frameIDStr);

			isVerified = WebBrowserUtil.IsElementPresent(msgs[msgs.length - 1]);
			return isVerified;
		}

		if (message.contains(")_or_(")) {
			messages = message.split(")_or_(");
			for (String msg : messages) {
				if (msg.equalsIgnoreCase("NA")) {
					return true;
				}
				messages1 = msg.split("_and_");
				boolvalues.clear();

				for (String msg1 : messages1) {
					String messagetocheck = msg1.replace("(", "").replace(")", "");
					isVerified = WebBrowserUtil.IsElementPresent(messagetocheck);
					if (isVerified) {
						boolvalues.add(Boolean.toString(isVerified));
					}
				}

				if (messages1.length == boolvalues.size()) {
					Assert.assertTrue(isVerified);
					return isVerified;
				}
			}
			return isVerified;
		}

		if (message.contains("_and_")) {
			messages = message.split("_and_");
			for (String msg : messages) {
				isVerified = WebBrowserUtil.IsElementPresent(msg);
				if (!isVerified) {
					return false;
				}
			}
			return true;
		}

		if (message.contains("_or_")) {
			messages = message.split("_or_");
			for (String msg : messages) {
				if (msg.equals("NA")) {
					Assert.assertTrue(isVerified);
					return true;
				}
				isVerified = WebBrowserUtil.IsElementPresent(msg);
				if (isVerified) {
					Assert.assertTrue(isVerified);
					return true;
				}
			}
			return false;
		}

		if (!message.equals("NA")) {
			for (int i = 0; i <= 2; i++) {
				isVerified = WebBrowserUtil.IsElementPresent(message);
				String content = WebBrowserUtil.GetContent(message);
				if (isVerified) {
					Assert.assertTrue(isVerified);
					if (content != null && !content.isEmpty()) {
						ExtentCucumberAdapter
								.addTestStepLog("Actual content: " + content + ", Expected content: " + message);
						log.info("Method verifymessageIsDisplayed completed.");
					}
					return true;
				}
			}
			return false;
		}
		return true;
  }
  public boolean verifyTextInLink(int index,String page,String xpathKey, String identifier)
  {
    WebBrowser.setCurrentBrowser(index);
    browser=WebBrowser.getBrowser();
    WebBrowserUtil.AttachPage(page);
    boolean staleElement = false;
    try {
      String xpath=YMLUtil.getYMLObjectRepositoryData(xpathKey);
      WebElement element = WebBrowserUtil.findElement(xpath,identifier);
      staleElement = WebBrowserUtil.verifyLabelDisplayed(element);
      ExtentCucumberAdapter.addTestStepLog(element.getText() + " is Dispalyed");
    }  catch(Exception ex) {
      staleElement = false;
    }
    return staleElement;
  }
  public void checkCheckbox(int index,String page,String xpathKey, String identifier)
  {
      WebBrowser.setCurrentBrowser(index);
      browser=WebBrowser.getBrowser();
      WebBrowserUtil.AttachPage(page);
    try
        {
      boolean staleElement = true;
      int i = 0;
      while (staleElement && i < Constants.NUMBER_OF_ITERATION)
      {
      String xpath=YMLUtil.getYMLObjectRepositoryData(xpathKey);
      WebElement element=WebBrowserUtil.findElement(xpath, identifier);
      if (element != null) {
                String elementHtml = element.getAttribute("outerHTML");
                CommonUtil.storeElementInJson(xpath, elementHtml);
            } else {
                throw new CustomException("Element not found for XPath: " + xpath);
            }
          try
          {
              WebBrowserUtil.Check(element);
              log.info("Method checkCheckbox completed.");
              staleElement = false;
          }
          catch (Exception ex)
          {
            i++;
            staleElement = true;
            try
            {
              WebBrowserUtil.ScrollAndCheck(element);
              log.info("Method checkCheckbox completed.");
              staleElement = false;
            }
            catch(Exception exc)
            {}
            if (i == 4)
            {
                  log.error("Method checkCheckbox not completed."+ex);
                  throw new CustomException(ex.getMessage(),ex);
              }
          }
      }

        }
    catch(Exception e)
        {
        throw new CustomException(e.getMessage(), e);
    }
  }
		 
    }