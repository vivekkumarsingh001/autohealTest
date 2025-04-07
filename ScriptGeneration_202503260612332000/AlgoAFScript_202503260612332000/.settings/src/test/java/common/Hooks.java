package common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.*;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.hamcrest.CoreMatchers;
import org.junit.ClassRule;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.asserts.SoftAssert;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter;
//import com.cucumber.listener.Reporter;
import com.google.common.io.Files;

import JiraUtility.JiraServiceProvider;
//import org.testng.asserts.SoftAssert;
//vish commented following 3 lines
//import cucumber.api.Scenario;
//import cucumber.api.java.After;
//import cucumber.api.java.Before;
import common.CommonUtil;
import common.TFSUtil;
import common.WebBrowser;
import io.cucumber.core.backend.TestCaseState;
import io.cucumber.java.*;
import ScreenRecorder.ScreenRecorderUtil;
import org.monte.screenrecorder.ScreenRecorder;

import java.lang.reflect.Field;
import java.util.stream.Collectors;
import io.cucumber.plugin.event.TestCase;
import io.cucumber.plugin.event.PickleStepTestStep;

public class Hooks {
	public WebDriver driver;
	public static String userName;
	public static String password;
	private static List<Map<String, Object>> tstSteps = new ArrayList<>();
	final static Logger log = Logger.getLogger(Hooks.class);
	public static boolean closeBrowser = true;
	public static boolean flag = true;
	public static SoftAssert softAssertions = new SoftAssert();
	public static List<String> tagsExecuted = new ArrayList<String>();
	public static int passCount = 0;
	public static int failCount = 0;
	public static int skipCount = 0;
	public static String JiraIntegration;
	public static String AzureTFSIntegration;
	public static String currentStep;
	private int currentStepDefIndex = 0;
	public static boolean apiScenario=false;
	public static List<PickleStepTestStep> stepDefs;
	private static boolean scenarioName = true;

	private static String path = System.getProperty("user.dir");
	public static WebDriver browser;
	public static Set<Cookie> cookies = null;
	public static boolean CookiesAdded;
	public static String Cookies = "";
	String EnableVideoCaptureForSuccess = CommonUtil.GetXMLData(
			Paths.get(path.toString(), "src", "test", "java", "ApplicationSettings.xml").toString(),
			"EnableVideoCaptureForSuccess");
	String EnableVideoCaptureForFailure = CommonUtil.GetXMLData(
			Paths.get(path.toString(), "src", "test", "java", "ApplicationSettings.xml").toString(),
			"EnableVideoCaptureForFailure");
	String console = CommonUtil
			.GetXMLData(Paths.get(path.toString(), "src", "test", "java", "ApplicationSettings.xml").toString(),
					"PrintTextInConsole")
			.toLowerCase();
	String storeScreenshot = CommonUtil
			.GetXMLData(Paths.get(path.toString(), "src", "test", "java", "ApplicationSettings.xml").toString(),
					"StoreScreenshot")
			.toLowerCase();

	@BeforeAll
	private static void deleteDirectoryContents() {
	    // Define the screenshots folder path
	    String screenshotsFolderPath = Paths.get(System.getProperty("user.dir"), "screenshots").toString();
	    File screenshotsFolder = new File(screenshotsFolderPath);
	    // Check if the folder exists
	    if (screenshotsFolder.exists()) {
	        clearDirectoryContents(screenshotsFolder); // Clear all contents of the directory
	        System.out.println("Existing screenshots have been cleared.");
	    } else {
	        System.out.println("No existing screenshots folder found to clear.");
	    }
	}

	/**
	 * Clears all contents of the specified directory (files and subdirectories).
	 *
	 * @param directory Directory whose contents need to be cleared
	 */
	private static void clearDirectoryContents(File directory) {
	    File[] files = directory.listFiles();

	    if (files != null) { // Ensure the directory is not empty
	        for (File file : files) {
	            if (file.isDirectory()) {
	                clearDirectoryContents(file); // Recursively clear subdirectories
	            }
	            file.delete(); // Delete individual files and now-empty directories
	        }
	    }
	}


	@Before

	public void init(Scenario scenario) {
		log.info("*********************************"+scenario.getSourceTagNames()+" "+ scenario.getName()+" Execution started *******************************");
		System.out.println("*********************************"+scenario.getSourceTagNames()+" "+ scenario.getName()+" Execution started*******************************");
		String node = System.getProperty("Node");
		if (node == null || node.isEmpty()) {
			node = "Node1";
		}
		String url = System.getProperty("Url");
		// System.out.println("Url-----------"+url);
		if (url != null && !url.isEmpty()) {
			CommonUtil.appUrl = url;
		}
		String apiurl = System.getProperty("apiUrl");
		// System.out.println("apiurl-----------"+apiurl);
		if (apiurl != null && !apiurl.isEmpty()) {
			RestAssuredUtil.apiCmdUrl = apiurl;
		}
		String browserName = System.getProperty("browserName");
		// System.out.println("browserName-----------"+browserName);
		if (browserName != null && !browserName.isEmpty()) {
			CommonUtil.browserName = browserName;
		}
		YMLUtil.loadYML("src/test/java/", node);
		YMLUtil.loadObjectRepoYML("src/test/java/ObjectRepository.yml");
		// YMLUtil.readObjectRepoYML("src/test/java/ObjectRepository.yml");
		String testDataFile = Paths.get(path.toString(), "src", "test", "java", "TestData.yml").toString();
		String objectRepoFile = Paths.get(path.toString(), "src", "test", "java", "ObjectRepository.yml").toString();

		File testData = new File(testDataFile);
		if (testData.exists()) {
			YMLUtil.readObjectRepoYML(objectRepoFile, testDataFile);
		} else {
			YMLUtil.readObjectRepoYML(objectRepoFile);
		}
		YMLUtil.PayloadYML("src/test/java/Payload.yml", node);
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		// Reporter.addScenarioLog("Start Time : "+timestamp);
		ExtentCucumberAdapter.addTestStepLog("" + scenario.getSourceTagNames());
		ExtentCucumberAdapter.addTestStepLog("Start Time : " + timestamp);
		if (EnableVideoCaptureForSuccess.toUpperCase().equals("TRUE")
				|| EnableVideoCaptureForFailure.toUpperCase().equals("TRUE")) {
			try {
				ScreenRecorderUtil.startRecord("TestCase");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		JiraIntegration = CommonUtil.GetXMLData(
				Paths.get(path.toString(), "src", "test", "java", "ApplicationSettings.xml").toString(),
				"JiraIntegration");
		AzureTFSIntegration = CommonUtil.GetXMLData(
				Paths.get(path.toString(), "src", "test", "java", "ApplicationSettings.xml").toString(),
				"AzureTFSIntegration");

		// extent = new ExtentReports(System.getProperty("user.dir") +
		// "/test-output/ExtentScreenshot.html", true);
	}

	// Method to wait for 2 minutes before executing the test case
	// This method is used to handle LRS execution so that execution will happen
	// without locking the user to Login
	@Before("@waitinminutes")
	public void beforeScenario() throws Throwable {
		log.info("*********************************Scenario started*******************************");

		Thread.sleep(120000);
		CommonUtil.setCopiedCount(0);
	}
	@Before(order=1)
	public void beforeScenarioTags(Scenario scenario) throws Throwable {
		for (String tag : scenario.getSourceTagNames()) {
			if (tag.toLowerCase().contains("api")) {
				apiScenario = true;
				flag=false;
				break;
			}
		}
	}

	@BeforeStep
	public void storeScenario(Scenario scenario) {
		if (console.equals("true")) {
			if (scenarioName) {
				System.out.println("\nThe Scenario Name : " + scenario.getName() + "\n");
				scenarioName = false;
			}
		}
		WebBrowserUtil.sce = scenario;
		// Following lines are to get the failed step details for Jira integration
		Field f = null;
		try {
			f = scenario.getClass().getDeclaredField("delegate");
		} catch (NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		f.setAccessible(true);
		TestCaseState tcs = null;
		try {
			tcs = (TestCaseState) f.get(scenario);
		} catch (IllegalArgumentException | IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Field f2 = null;
		try {
			f2 = tcs.getClass().getDeclaredField("testCase");
		} catch (NoSuchFieldException | SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		f2.setAccessible(true);
		TestCase r = null;
		try {
			r = (TestCase) f2.get(tcs);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// You need to filter out before/after hooks
		stepDefs = r.getTestSteps().stream().filter(x -> x instanceof PickleStepTestStep)
				.map(x -> (PickleStepTestStep) x).collect(Collectors.toList());

		// This object now holds the information about the current step definition
		PickleStepTestStep currentStepDef = stepDefs.get(currentStepDefIndex);
		String currentStep = currentStepDef.getStepText();

	}

	@AfterStep
	public void addScreenshot(Scenario scenario) {
		if (console.equals("true")) {
			System.out.println("the step name: " + StepListener.stepName);
		}
		WebBrowserUtil.takeEachStepScrenshot(scenario);
		currentStepDefIndex += 1;
		if (flag) {
			driver = WebBrowser.getBrowser();
		}
		Collection<String> alltags = scenario.getSourceTagNames();
		if (storeScreenshot.equalsIgnoreCase("true") && StepListener.stepName.toLowerCase().contains("verify")) {
		    String testCaseName = alltags.stream().filter(tag -> tag.startsWith("@test")).findFirst()
		            .map(tag -> tag.substring(1)).orElse("UnknownTest");
		    String screenshotsFolderPath = Paths.get(System.getProperty("user.dir"), "screenshots").toString();
		    File screenshotsFolder = new File(screenshotsFolderPath);
		    if (!screenshotsFolder.exists()) {
		        screenshotsFolder.mkdir();
		    }
		    String testCaseFolderPath = Paths.get(screenshotsFolderPath, testCaseName).toString();
		    File testCaseFolder = new File(testCaseFolderPath);
		    if (!testCaseFolder.exists()) {
		        testCaseFolder.mkdir();
		    }
		    File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		    String sanitizedStepName = StepListener.stepName.replaceAll("[^a-zA-Z0-9_-]", "_");
		    String screenshotName = "step_" + sanitizedStepName + "_" + System.currentTimeMillis() + ".png";
		    File destination = new File(Paths.get(testCaseFolderPath, screenshotName).toString());
		    try {
		        FileUtils.copyFile(screenshot, destination);
		        System.out.println("Screenshot saved at: " + destination.getAbsolutePath());
		    } catch (IOException e) {
		        System.err.println("Failed to save screenshot: " + e.getMessage());
		    }
		}
	}

	

	@After(order = 1)
	public void afterScenario(Scenario scenario) {
		Assertion.assertAll();
		Cookies = CommonUtil.GetXMLData(
				Paths.get(path.toString(), "src", "test", "java", "ApplicationSettings.xml").toString(), "Cookies");
		if (Cookies.toUpperCase().equals("TRUE")) {
			if (!CookiesAdded) {
				browser = WebBrowser.getBrowser();
				try {
					cookies = browser.manage().getCookies();
					CookiesAdded = true;
				} catch (Throwable e) {
					System.err.println("Error While getting Cookies: " + e.getMessage());
				}
			}
		}
		if (EnableVideoCaptureForSuccess.toUpperCase().equals("TRUE")
				|| EnableVideoCaptureForFailure.toUpperCase().equals("TRUE")) {
			try {
				ScreenRecorderUtil.stopRecord();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (EnableVideoCaptureForSuccess.toUpperCase().equals("TRUE")
					&& EnableVideoCaptureForFailure.toUpperCase().equals("FALSE")) {
				if (scenario.isFailed())
					ScreenRecorderUtil.deleteRecordedFile();
			} else if (EnableVideoCaptureForSuccess.toUpperCase().equals("FALSE")
					&& EnableVideoCaptureForFailure.toUpperCase().equals("TRUE")) {
				if (!scenario.isFailed())
					ScreenRecorderUtil.deleteRecordedFile();
			}

		}

		if (JiraIntegration.toUpperCase().equals("TRUE"))
			if (scenario.isFailed()) {
				// scenario.getName()=>name of the failing scenario; scenario.getLine=>the
				// line(s) in the feature file of the Scenario; scenario.getStatus()=>scenario
				// Status

				String JiraIntegrationParameters = CommonUtil.GetXMLData(
						Paths.get(path.toString(), "src", "test", "java", "ApplicationSettings.xml").toString(),
						"JiraIntegrationParameters");
				String[] JiraPara = JiraIntegrationParameters.split("[,]", 0);
				// To get failed step
				PickleStepTestStep currentStepDef = stepDefs.get(currentStepDefIndex - 1);
				String currentStep = currentStepDef.getStepText();
				// String description = scenario.getSourceTagNames()+"\n"+" Test is failed at
				// the step : "+ currentStep + "\n"+ CommonUtil.error;
				String description = scenario.getSourceTagNames() + "\n" + " Scenario is failed at line "
						+ scenario.getLine() + " in the feature file " + "\n Failed step : " + currentStep + "\n"
						+ CommonUtil.error;
				String Summary = "Scenario " + scenario.getName() + " is failed";
				// To limit the size of the summary to 255 characters
				Summary = Summary.substring(0, Math.min(Summary.length(), 255));
				// To remove special characters
				Summary = Summary.replaceAll("[^a-zA-Z0-9 ]", "");
				JiraServiceProvider JiraServiceProvider = new JiraServiceProvider(JiraPara[0], JiraPara[1], JiraPara[2],
						JiraPara[3]);
				JiraServiceProvider.createJiraIssue(JiraPara[4], Summary, description, JiraPara[5]);

			}
		if (AzureTFSIntegration.equalsIgnoreCase("true"))
			if (scenario.isFailed()) {
				String title = "Scenario " + scenario.getName() + " is failed";
				PickleStepTestStep currentStepDef = stepDefs.get(currentStepDefIndex - 1);
				String currentStep = currentStepDef.getStepText();
				String description = scenario.getSourceTagNames() + "\n" + " Scenario is failed at line "
						+ scenario.getLine() + " in the feature file " + "\n Failed step : " + currentStep + "\n"
						+ CommonUtil.error;
				AzureTFSUtil.createAzureTFSIssue(title,description);
			
			}

		log.info("*********************************Scenario ended*******************************");
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		ExtentCucumberAdapter.addTestStepLog("End Time: " + timestamp);
		CommonUtil.setCopiedCount(0);
		CommonUtil.setCopiedCountTextNull();
		softAssertions = new SoftAssert();
		boolean semiAuto = false;
		String scenarioStatusLowercase = new String();
		scenarioStatusLowercase = scenario.getStatus().toString().toLowerCase();

		if (scenarioStatusLowercase.equals("skipped")) {
			skipCount++;
		}

		if (scenario.isFailed() || (scenarioStatusLowercase.equals("passed"))) {

			if (scenarioStatusLowercase.equals("passed")) {
				passCount++;
			} else if (scenarioStatusLowercase.equals("failed")) {
				failCount++;
				Collection<String> tagsString = scenario.getSourceTagNames();
				List<String> tagsList = new ArrayList<>(tagsString);
				if (!tagsList.isEmpty()) {
//				    System.out.println("first  tagsString: " + tagsList.get(0));
					AutoHealUtil.saveConfigDeatils(tagsList.get(0));
				}
//                 System.out.println("tagsString:" + tagsString.get(0));
//				String uidTag = tagsString.stream().filter(tag -> tag.contains("@uid"))
//						.collect(Collectors.joining(", ")); // Join with a comma and space
			}

			String screenshotName = "Image_" + new Date().getTime();
			boolean flag = true;
			for (String tag : scenario.getSourceTagNames()) {
				tagsExecuted.add(tag);
				if (tag.toLowerCase().contains("api")) {
					flag = false;
					break;
				}
			}
			if (flag) {
				driver = WebBrowser.getBrowser();
				WebBrowserUtil.takeScrenshot(scenario);

			}
		}
		for (String tag : scenario.getSourceTagNames()) {
			closeBrowser = true;
			if (tag.contains("usesamesession")) {
				closeBrowser = false;
				break;
			}
		}
		String testcaseId = "";
		for (String tag : scenario.getSourceTagNames()) {
			if (tag.contains("set2") || tag.contains("semiauto") || tag.contains("set3") || tag.contains("set21")
					|| tag.contains("set22") || tag.contains("set23")) {
				if (closeBrowser) {
					WebBrowser.closeBrowserInstance();
				}
			}
			if (tag.contains("semiauto")) {
				semiAuto = true;
			}
			if (scenario.isFailed() && semiAuto) {
				semiAuto = false;
				throw new CustomException("Semi Auto test cases may fail due to OTP / Captcha.");
			}
			if (tag.contains("test")) {
				testcaseId = tag;
			}

			String SubmitTfsResult = CommonUtil.GetXMLData(Paths
					.get(System.getProperty("user.dir").toString(), "src", "test", "java", "ApplicationSettings.xml")
					.toString(), "SubmitResultToTFS");
			boolean tfsResult = Boolean.parseBoolean(SubmitTfsResult);
			if (tfsResult) {
				int testPointId = 0, testPlanId = 0, testRunId = 0, testCaseId = 0;
				TFSUtil tfsUtil = new TFSUtil();
				testPointId = tfsUtil.getTestPointId(testCaseId)[0];
				testPlanId = tfsUtil.getTestPointId(testCaseId)[1];
				testRunId = tfsUtil.getTestRunId(testCaseId, testPointId, testPlanId);
				System.out.println(tstSteps);
				tfsUtil.updateResultsToTFS(testCaseId, testPointId, testRunId, tstSteps, userName, password);
				tstSteps.clear();

			}

		}
		scenarioName = true;
		softAssertions = new SoftAssert();
	}

	@After(order = 0)
	public void closeBrowser() {
		log.info("*********************************Execution ended*******************************");
		System.out.println("------------------------------");
		System.out.println(" Status - ");
		System.out.println("------------------------------");
		if (WebBrowser.isBrowserOpened() && closeBrowser) {
			WebBrowser.closeBrowserInstance();
		}
		String abc = System.getProperty("user.dir");
		File dir = new File(abc + "//output//");
		File[] files = dir.listFiles();
		File lastModified = Arrays.stream(files).filter(File::isDirectory).max(Comparator.comparing(File::lastModified))
				.orElse(null);
		System.out.println(lastModified);

		try {

			int totalCount = passCount + failCount + skipCount;
			String json = "{\"TotalTest\":" + String.valueOf(totalCount) + "," + "\"passed\":" + passCount + ","
					+ "\"failed\":" + failCount + "," + "\"skipped\":" + skipCount + "}";
			String path = lastModified + "//Execution_status.json";
			System.out.println("PATH :" + path);
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path)));
			writer.write(json);
			writer.close();
			FileWriter myWriter = new FileWriter(lastModified + "//ExexutedTagDetails.txt");
			Set<String> uniqueTag = new HashSet<String>(tagsExecuted);
			String test = "";
			String set = "";
			String otherTags = "";
			for (String tag : uniqueTag) {
				if (tag.contains("@test")) {
					test += test == "" ? tag : "," + tag;

				} else if (tag.contains("@set")) {
					set += set == "" ? tag : "," + tag;

				} else {
					otherTags += otherTags == "" ? tag : "," + tag;

				}
			}

			myWriter.write("Test Tags:\n");
			myWriter.write(test + "\n\n");
			myWriter.write("Set Tags:\n");
			myWriter.write(set + "\n\n");
			myWriter.write("Other Tags:\n");
			myWriter.write(otherTags + "\n");
			myWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
