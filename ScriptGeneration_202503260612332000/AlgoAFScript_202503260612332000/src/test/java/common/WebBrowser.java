package common;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.v123.network.Network;
import java.net.MalformedURLException;
import java.net.URL;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.devtools.v123.network.model.RequestId;
import java.util.Optional;
import org.openqa.selenium.devtools.DevTools;
import com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter;
import java.time.Duration;
import org.apache.log4j.Logger;
import org.openqa.selenium.Cookie;
import java.nio.file.Paths;

public class WebBrowser {
	private static WebDriver driver;
	private static String path = System.getProperty("user.dir");
	static String parentWindowHandle;
	private static boolean isBrowserOpen = false;
	private static boolean launchNewBrowser = false;
	public static boolean boolHighLightElement = false;
	public static boolean boolEachstepScreenshot = false;
	public static boolean boolEachSoftAssersion = false;
	static List<WebDriver> webdriverList = new ArrayList<WebDriver>();
	public static String browserType = "";
	public static String PageLoadTimeout = "";
	public static String DirectoryPAth = "";
	public static String HighLightElement = "";
	public static String EachstepScreenshot = "";
	public static String softassertion = "";
	public static String profilePath = "";
	public static String lambdaTestBuild = "";
	public static String lambdaTestURL = "";
	public static String NetworkLog = "";
	public static String type = "";
	public static String hubURL = "";
	public static DevTools devTools;
	static final Logger log = Logger.getLogger(WebBrowser.class);

	public static WebDriver getBrowser(boolean launchBrowser) {
		if ((driver == null || launchBrowser) && !isBrowserOpen) {

			if (CommonUtil.browserName != null) {
				browserType = CommonUtil.browserName;
				log.info("browserName-----------------" + browserType);
			} else {
				browserType = CommonUtil.getXMLData(
						Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(), "BrowserType");
			}
			PageLoadTimeout = CommonUtil.getXMLData(
					Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(),
					"MaximumTimeInSecondsToWaitForControl");
			DirectoryPAth = CommonUtil.getXMLData(
					Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(),
					"DownloadInCurrentDirectory");
			HighLightElement = CommonUtil.getXMLData(
					Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(),
					"HighLightWebElement");
			EachstepScreenshot = CommonUtil.getXMLData(
					Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(),
					"EnableEachStepScreenshot");
			softassertion = CommonUtil.getXMLData(
					Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(),
					"EnableSoftassertion");
			profilePath = CommonUtil.getXMLData(
					Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(), "ProfilePath");
			String pathOfBrowser = CommonUtil.getXMLData(
					Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(), "WebdriverPath");

			if (HighLightElement.toUpperCase().equals("TRUE")) {
				boolHighLightElement = true;
			}
			if (EachstepScreenshot.toUpperCase().equals("TRUE")) {
				boolEachstepScreenshot = true;
			}
			if (softassertion.toUpperCase().equals("TRUE")) {
				boolEachSoftAssersion = true;
			}
			if (browserType.equals("Firefox")) {
				System.out.print("Launching Firefox");
				if (pathOfBrowser.equals("Na")) {
					WebDriverManager.firefoxdriver().clearDriverCache().setup();
				} else {
					System.setProperty("webdriver.gecko.driver", pathOfBrowser + "/geckodriver.exe");
				}
				driver = new FirefoxDriver();
				driver.manage().window().maximize();
			} else if (browserType.equals("Edge")) {
				System.out.print("Launching Edge");
				if (pathOfBrowser.equals("Na")) {
					WebDriverManager.edgedriver().clearDriverCache().setup();
				} else {
					System.setProperty("webdriver.edge.driver", pathOfBrowser + "/msedgedriver.exe");
				}
				driver = new EdgeDriver();
				driver.manage().window().maximize();
			} else if (browserType.toUpperCase().equals("Lambda".toUpperCase())) {
				System.out.print("Attempting connection to LambdaTest");
				lambdaTestBuild = CommonUtil.getXMLData(
						Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(),
						"LambdaTestBuild");
				lambdaTestURL = CommonUtil.getXMLData(
						Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(), "LambdaTestURL");
				NetworkLog = CommonUtil.getXMLData(
						Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(), "NetworkLog");

				RemoteWebDriver remotedriver = null;
				DesiredCapabilities caps = new DesiredCapabilities();
				caps.setCapability("build", lambdaTestBuild);
				caps.setCapability("name", "");
				caps.setCapability("platform", "Windows 10");
				caps.setCapability("browserName", "Chrome");
				caps.setCapability("version", "97.0");
				caps.setCapability("network", NetworkLog);
				System.out.println("Desired caps made successfully");

				try {

					remotedriver = new RemoteWebDriver(new URL(lambdaTestURL), caps);
					SessionId sessionid = remotedriver.getSessionId();
					log.info("Driver session id is :" + sessionid.toString());
				} catch (Exception e) {
					log.error(e.getMessage());
				}

				driver = remotedriver;
			} else {
				System.out.print("Launching Chrome");
				if (pathOfBrowser.equals("Na")) {
					WebDriverManager.chromedriver().clearDriverCache().setup();
				} else {
					System.setProperty("webdriver.chrome.driver", pathOfBrowser + "/chromedriver.exe");
				}
				// Create a map to store preferences
				Map<String, Object> prefs = new HashMap<String, Object>();
				// add key and value to map as follow to switch off browser notification
				// Pass the argument 1 to allow and 2 to block
				prefs.put("profile.default_content_setting_values.notifications", 2);
				DesiredCapabilities caps = new DesiredCapabilities();
				LoggingPreferences logPrefs = new LoggingPreferences();
				logPrefs.enable(LogType.BROWSER, Level.ALL);
				logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
				caps.setCapability("goog:loggingPrefs", logPrefs);
				if (DirectoryPAth.toUpperCase().equals("TRUE")) {
					String downloadFilepath = System.getProperty("user.dir");
					prefs.put("download.default_directory", downloadFilepath);
				}
				ChromeOptions options = new ChromeOptions();
				if (browserType.toUpperCase().equals("HEADLESS CHROME")) {
					options.addArguments("--no-sandbox"); // Bypass OS security model
					options.addArguments("--headless"); // headless -> no browser window. needed for jenkins
					options.addArguments("disable-infobars"); // disabling infobars
					options.addArguments("--disable-extensions"); // disabling extensions
					options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
					options.addArguments("window-size=1920,1080");
					options.addArguments("--disable-gpu");
					// Set a custom user-agent string; sometimes necessary for avoiding bot
					// detection or simulating specific browsers
					options.addArguments(
							"user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
				}

				if (profilePath != null && !profilePath.isEmpty()) {
					// Here you set the path of the profile ending with User Data not the profile
					// folder
					options.addArguments("user-data-dir=" + profilePath);
				}
				options.addArguments("--ignore-ssl-errors=yes");
				options.addArguments("--ignore-certificate-errors");
				// set ExperimentalOption - prefs
				options.setExperimentalOption("prefs", prefs);
				options.setCapability("goog:loggingPrefs", java.util.Map.of(LogType.PERFORMANCE, Level.ALL));
				options.addArguments("use-fake-ui-for-media-stream");
				options.addArguments("use-fake-device-for-media-stream");
				options.addArguments("--remote-allow-origins=*");
				options.addArguments("--disable-blink-features=AutomationControlled");
				if (browserType.equals("Kiosk Chrome")) {
					options.addArguments("--kiosk");
				}

				type = CommonUtil.getXMLData(
						Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(), "Type");

				if (type.toUpperCase().contains("GRID")) {
					hubURL = CommonUtil.getXMLData(
							Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(), "HubURL");
					try {
						driver = new RemoteWebDriver(new URL(hubURL), options);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				} else {
					options.merge(caps);
					driver = new ChromeDriver(options);
					driver.manage().window().maximize();
				}
			}
			webdriverList.add(driver);
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(Integer.parseInt(PageLoadTimeout)));
			parentWindowHandle = driver.getWindowHandle();
			isBrowserOpen = true;
		} else if (launchBrowser) {
			WebDriver driver2 = multipleBrowserInstance();
			webdriverList.add(driver2);
			driver2.manage().window().maximize();
		}
		return driver;

	}

	@SuppressWarnings("deprecation")
	public static WebDriver multipleBrowserInstance() {

		if (CommonUtil.browserName != null) {
			browserType = CommonUtil.browserName;
			System.out.println("browserName-----------------" + browserType);
		} else {
			browserType = CommonUtil.getXMLData(
					Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(), "BrowserType");
		}
		PageLoadTimeout = CommonUtil.getXMLData(
				Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(),
				"MaximumTimeInSecondsToWaitForControl");
		DirectoryPAth = CommonUtil.getXMLData(
				Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(),
				"DownloadInCurrentDirectory");
		HighLightElement = CommonUtil.getXMLData(
				Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(), "HighLightWebElement");
		EachstepScreenshot = CommonUtil.getXMLData(
				Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(),
				"EnableEachStepScreenshot");
		softassertion = CommonUtil.getXMLData(
				Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(), "EnableSoftassertion");

		profilePath = CommonUtil.getXMLData(
				Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(), "ProfilePath");

		if (HighLightElement.toUpperCase().equals("TRUE")) {
			boolHighLightElement = true;
		}
		if (EachstepScreenshot.toUpperCase().equals("TRUE")) {
			boolEachstepScreenshot = true;
		}
		if (softassertion.toUpperCase().equals("TRUE")) {
			boolEachSoftAssersion = true;
		}
		if (browserType.equals("Firefox")) {
			System.out.print("Launching Firefox");
			WebDriverManager.firefoxdriver().setup();
			driver = new FirefoxDriver();
			driver.manage().window().maximize();
		} else if (browserType.equals("Edge")) {
			System.out.print("Launching Edge");
			WebDriverManager.edgedriver().setup();
			driver = new EdgeDriver();
			driver.manage().window().maximize();
		} else if (browserType.toUpperCase().equals("Lambda".toUpperCase())) {
			System.out.print("Attempting connection to LambdaTest");
			lambdaTestBuild = CommonUtil.getXMLData(
					Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(), "LambdaTestBuild");
			lambdaTestURL = CommonUtil.getXMLData(
					Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(), "LambdaTestURL");
			NetworkLog = CommonUtil.getXMLData(
					Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(), "NetworkLog");

			RemoteWebDriver remotedriver = null;
			DesiredCapabilities caps = new DesiredCapabilities();
			caps.setCapability("build", lambdaTestBuild);
			caps.setCapability("name", "");
			caps.setCapability("platform", "Windows 10");
			caps.setCapability("browserName", "Chrome");
			caps.setCapability("version", "97.0");
			caps.setCapability("network", NetworkLog);
			System.out.println("Desired caps made successfully");

			try {
				remotedriver = new RemoteWebDriver(new URL(lambdaTestURL), caps);
				SessionId sessionid = remotedriver.getSessionId();
				log.info("Driver session id is :" + sessionid.toString());
			} catch (Exception e) {
				log.error(e.getMessage());
			}

			driver = remotedriver;
		} else {
			log.info("Launching Chrome");
			WebDriverManager.chromedriver().setup();
			// Create a map to store preferences
			Map<String, Object> prefs = new HashMap<String, Object>();
			// add key and value to map as follow to switch off browser notification
			// Pass the argument 1 to allow and 2 to block
			prefs.put("profile.default_content_setting_values.notifications", 2);
			DesiredCapabilities caps = new DesiredCapabilities();
			LoggingPreferences logPrefs = new LoggingPreferences();
			logPrefs.enable(LogType.BROWSER, Level.ALL);
			logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
			caps.setCapability("goog:loggingPrefs", logPrefs);
			// Create an instance of ChromeOptions
			if (DirectoryPAth.toUpperCase().equals("TRUE")) {
				String downloadFilepath = System.getProperty("user.dir");
				prefs.put("download.default_directory", downloadFilepath);
			}
			ChromeOptions options = new ChromeOptions();
			if (browserType.toUpperCase().equals("HEADLESS CHROME")) {
				options.addArguments("--no-sandbox"); // Bypass OS security model
				options.addArguments("--headless"); // headless -> no browser window. needed for jenkins
				options.addArguments("disable-infobars"); // disabling infobars
				options.addArguments("--disable-extensions"); // disabling extensions
				options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
				options.addArguments("window-size=1920,1080");
			}

			if (profilePath != null && !profilePath.isEmpty()) {
				options.addArguments("user-data-dir=" + profilePath);
			}

			options.addArguments("--ignore-ssl-errors=yes");
			options.addArguments("--ignore-certificate-errors");
			// set ExperimentalOption - prefs
			options.setExperimentalOption("prefs", prefs);
			options.addArguments("use-fake-ui-for-media-stream");
			options.addArguments("use-fake-device-for-media-stream");
			options.addArguments("--remote-allow-origins=*");
			if (browserType.equals("Kiosk Chrome")) {
				options.addArguments("--kiosk");
			}

			type = CommonUtil.getXMLData(Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(),
					"Type");

			if (type.toUpperCase().contains("GRID")) {
				hubURL = CommonUtil.getXMLData(
						Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(), "HubURL");
				try {
					driver = new RemoteWebDriver(new URL(hubURL), options);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			} else {
				options.merge(caps);
				driver = new ChromeDriver(options);
				driver.manage().window().maximize();
			}
		}
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(Integer.parseInt(PageLoadTimeout)));
		parentWindowHandle = driver.getWindowHandle();
		isBrowserOpen = true;
		return driver;
	}

	public static WebDriver getBrowser() {
		return getBrowser(launchNewBrowser);
	}

	public void setBrowser(WebDriver webDriver) {
		this.driver = webDriver;
	}

	public static void closetab(int tab) {
		try {
			ArrayList<String> tabs2 = new ArrayList<String>(driver.getWindowHandles());
			driver.switchTo().window(tabs2.get(tab));
			driver.close();
		} catch (Exception e) {
			log.error(e.getMessage());
		}

	}

	public static void LaunchApplication(boolean openBrowser) {
		String autUrl = "";
		if (CommonUtil.getAppUrl() != null) {
			autUrl = CommonUtil.getAppUrl();
		} else {
			autUrl = CommonUtil.getXMLData(Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(),
					"URL");
		}
		if (driver == null) {
			getBrowser(openBrowser);
		}
		driver.get(autUrl);

		if (Hooks.cookiesAdded) {

			for (Cookie cookie : Hooks.cookies) {
				driver.manage().addCookie(cookie);
			}
			driver.navigate().refresh();
		}

	}

	public static void LaunchApplication(boolean openBrowser, String autUrl) {

		getBrowser(openBrowser);
		((JavascriptExecutor) driver).executeScript("window.open()");
		ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
		for (int i = 0; i < tabs.size(); i++) {
			driver.switchTo().window(tabs.get(i));
		}
		WebDriver newDriver = webdriverList.get(webdriverList.size() - 1);
		newDriver.get(autUrl);
	}

	public static void LaunchNewInstance(boolean openBrowser, String autUrl) {
		getBrowser(openBrowser);
		WebDriver newDriver = webdriverList.get(webdriverList.size() - 1);
		newDriver.get(autUrl);
	}

	public static void LaunchApplication1(String autUrl) {

		driver.navigate().to(autUrl);
	}

	public static void openNewTab(boolean openBrowser, String autUrl) {
		((JavascriptExecutor) driver).executeScript("window.open()");
		ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
		for (int i = 0; i < tabs.size(); i++) {
			driver.switchTo().window(tabs.get(i));
		}
		WebDriver newDriver = webdriverList.get(webdriverList.size() - 1);
		newDriver.get(autUrl);
	}

	public static void LaunchAPIApplication() {
		String autUrl = "";
		if (RestAssuredUtil.apiCmdUrl != null) {
			autUrl = RestAssuredUtil.apiCmdUrl;
		} else {
			autUrl = CommonUtil.getXMLData(Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(),
					"APIURL");
		}
		RestAssuredUtil.setAPIURL(autUrl);
	}

	public static String getParentWindowHandle() {
		return parentWindowHandle;
	}

	public static void setCurrentBrowser(int index) {
		if (webdriverList.size() > index) {
			driver = webdriverList.get(index);
			isBrowserOpen = true;
		}
	}

	public static void closeBrowserInstance() {
		for (int counter = 0; counter < webdriverList.size(); counter++) {
			if (webdriverList.get(counter) != null) {
				webdriverList.get(counter).quit();
			}
		}

		driver = null;
		webdriverList = new ArrayList<WebDriver>();
		isBrowserOpen = false;
	}

	public static boolean isBrowserOpened() {
		return isBrowserOpen;
	}

	public static boolean DevTool(List<String> list, String url) {
		devTools = ((ChromeDriver) driver).getDevTools();
		devTools.createSession();
		devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
		Boolean flag = true;
		AtomicInteger count = new AtomicInteger(0);
		final RequestId[] id = new RequestId[3];
		List<String> responseBodies = new ArrayList<>();
		for (int retry = 0; retry < 5; retry++) {
			driver.navigate().refresh();
			WebBrowserUtil.ScrollDown(String.valueOf(10));
			devTools.addListener(Network.responseReceived(), responseReceived -> {

				String responseUrl = responseReceived.getResponse().getUrl();
				log.info("Received response URL: " + responseUrl);

				if (responseUrl.equals(url)) {
					count.getAndIncrement();
					if (count.get() <= 3) {
						id[count.get() - 1] = responseReceived.getRequestId();
						log.info("ID" + count.get() + ": " + id[count.get() - 1]);
						ExtentCucumberAdapter.addTestStepLog("ID" + count.get() + ": " + id[count.get() - 1]);

					}
				}
			});

			try {
				Thread.sleep(14000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				log.error("Thread sleep interrupted: " + e.getMessage());
			}

			for (int i = 0; i < id.length; i++) {
				if (id[i] != null) {
					try {
						String responseBody = devTools.send(Network.getResponseBody(id[i])).getBody();
						responseBodies.add(responseBody);
					} catch (Exception e) {
						log.error("Not able to capture response body in id[" + i + "]: " + e.getMessage());
					}
				}
			}
			if (!responseBodies.isEmpty()) {
				break;
			} else {
				log.info("Retrying to fetch response bodies...");
			}
		}
		if (!responseBodies.isEmpty()) {
			for (String responseBody : responseBodies) {
				if (responseBody.contains(list.get(list.size() - 1))) {
					for (String item : list) {
						if (responseBody.contains(item)) {
							ExtentCucumberAdapter.addTestStepLog("Yes! " + item + " it is present in the algonomy url");
						} else {
							ExtentCucumberAdapter.addTestStepLog(item + " No! it is not present in alognomy url");
							flag = false;
						}
						String[] reqStrings = responseBody.split("\\?");
						String reqString = "";
						for (String key : reqStrings) {
							if (key.contains(item)) {
								reqString = key;
								break;
							}
						}
						String[] reqStrings1 = reqString.split(",");
						String[] reqString2 = reqStrings1[0].split("&");
						for (String key : reqString2) {
							if (key.contains(item)) {
								log.info("The value is ### " + key);
								ExtentCucumberAdapter.addTestStepLog("The value is ### " + key);
								break;
							}
						}
					}
				} else {
					for (String item : list) {
						if (responseBody.contains(item)) {
							ExtentCucumberAdapter.addTestStepLog("Yes! " + item + " it is present in the algonomy url");
						} else {
							ExtentCucumberAdapter.addTestStepLog(item + " No! it is not present in alognomy url");
							flag = false;
						}
						String[] reqStrings = responseBody.split("\\?");
						String reqString = "";
						for (String key : reqStrings) {
							if (key.contains(item)) {
								reqString = key;
								break;
							}
						}
						String[] reqStrings1 = reqString.split(",");
						String[] reqString2 = reqStrings1[0].split("&");
						for (String key : reqString2) {
							if (key.contains(item)) {
								ExtentCucumberAdapter.addTestStepLog("The value is ### " + key);
								break;
							}
						}
					}
				}
			}
		} else {
			throw new CustomException("Response body is empty");
		}
		return flag;
	}

	public static boolean isWindows() {
		String os = System.getProperty("os.name").toLowerCase();
		return os.contains("win");
	}

}
