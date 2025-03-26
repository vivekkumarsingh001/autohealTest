package common;

import java.util.*;
import java.io.*;
import java.awt.Rectangle;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Collator;
import java.text.RuleBasedCollator;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.AWTException;
import java.awt.Graphics2D;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.util.stream.Collectors;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.OutputType;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.text.PDFTextStripper;
import java.net.URL;
import java.util.stream.IntStream;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.openqa.selenium.NoSuchWindowException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.UnhandledAlertException;
import com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ComparisonChain;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.log4j.Logger;
import io.cucumber.java.Scenario;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class WebBrowserUtil {
	static WebDriver driver;
	static String path = System.getProperty("user.dir");
	static int timeInterval = Integer.parseInt(
			CommonUtil.getXMLData(Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(),
					"TimeIntervalInMilliSecondsToWaitForPage"));
	static int maxDelay = Integer.parseInt(
			CommonUtil.getXMLData(Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(),
					"MaximumTimeInMilliSecondsToWaitForPage"));
	static int controlLoadTimeout = Integer.parseInt(
			CommonUtil.getXMLData(Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(),
					"MaximumTimeInSecondsToWaitForControl"));
	static Scenario sce;
	public static String ScrenshotForSucess = "FALSE";
	static final Logger log = Logger.getLogger(WebBrowserUtil.class);

	public static void turnOffImplicitWaits() {
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
	}

	public static void turnOnImplicitWaits() {
		String pageLoadTimeout = CommonUtil.getXMLData(
				Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(),
				"MaximumTimeInSecondsToWaitForControl");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(Integer.parseInt(pageLoadTimeout)));
	}

	public static boolean IsElementPresent(String message) {
		driver = WebBrowser.getBrowser();
		List<WebElement> webElements = driver.findElements(By.xpath("//*[contains(text(), '" + message + "')]"));
		for (int counter = 0; counter < webElements.size(); counter++) {
			if (webElements.get(counter) != null && webElements.get(counter).isDisplayed()) {
				return true;
			}
		}
		return false;
	}

	public static boolean IsElementPresent1(String message) {
		driver = WebBrowser.getBrowser();
		List<WebElement> webElements = driver.findElements(By.xpath("//*[contains(text(), \"" + message + "\")]"));
		for (int counter = 0; counter < webElements.size(); counter++) {
			if (webElements.get(counter) != null && webElements.get(counter).isDisplayed()) {
				return true;
			}
		}
		return false;
	}

	public static String GetContent(String message) {
		String value = "";
		driver = WebBrowser.getBrowser();
		List<WebElement> webElements = driver.findElements(By.xpath("//*[contains(text(), '" + message + "')]"));
		for (int counter = 0; counter < webElements.size(); counter++) {
			if (webElements.get(counter) != null && webElements.get(counter).isDisplayed()) {
				return GetText(webElements.get(counter));
			} else {
				value = GetText(webElements.get(counter));
			}
		}
		return value;
	}

	public static boolean VerifyURL(String url) {
		boolean isVerified = false;
		try {
			driver = WebBrowser.getBrowser();
			isVerified = driver.getCurrentUrl().toUpperCase().contains(url.toUpperCase());
			if (isVerified) {
				return isVerified;
			}
			Thread.sleep(500);
			Set<String> windowHandles = driver.getWindowHandles();
			for (int i = 0; i < windowHandles.size(); i++) {
				driver.switchTo().window(windowHandles.toArray()[i].toString());
				isVerified = driver.getCurrentUrl().toUpperCase().contains(url.toUpperCase());

				if (isVerified) {
					break;
				}
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		return isVerified;
	}

	public static void ClickIfVisible(WebElement element) {
		int i = 0;
		while (i < 2) {
			try {
				i++;
				Click(element);
				break;
			} catch (Exception ex) {
				log.error(ex.getMessage());
			}
		}
	}

	public static boolean verifyLabelDisplayed(WebElement element) {
		boolean isVerified = false;
		try {
			// List<WebElement> links = getElementByXpath(value);
			if (element != null) {
				isVerified = true;
			}
		} catch (Exception ex) {
			isVerified = false;
		}
		return isVerified;
	}

	public static void ScrollAndEnterText(WebElement element, String text) {
		Scroll(element);
		try {
			Thread.sleep(1000);
			element.clear();
			element.sendKeys(text);
			for (int i = 0; i < 1; i++) {
				if (element.getAttribute("value").equals(text)) {
					break;
				} else {
					element.sendKeys(Keys.CONTROL + "a");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						log.error(e.getMessage());
					}
					element.sendKeys(text);
				}
			}
		} catch (Exception ex) {
			throw new CustomException("Scroll And EnterText unsuccessful" + ex.getMessage());
		}
	}

	public static WebElement findElement(String xpath, String identificationType) {
		driver = WebBrowser.getBrowser();
		if (xpath.contains("||")) {
			String[] xpathSplit = xpath.split("\\|\\|");
			xpath = xpathSplit[0];
			driver = GetFrame(xpathSplit[1]);
		}
		WebElement element = null;
		if (WebBrowser.boolHighLightElement) {

			int i = 0;
			while (i < 5)
				try {

					if (identificationType.toUpperCase().equals("xpath".toUpperCase())) {
						element = driver.findElement(By.xpath(xpath));
					} else if (identificationType.toUpperCase().equals("id".toUpperCase())) {
						element = driver.findElement(By.id(xpath));
					}
					JavascriptExecutor jse = (JavascriptExecutor) driver;
					jse.executeScript("arguments[0].style.border='2px solid red'", element);
					break;
				} catch (Exception e) {
					try {
						Thread.sleep(2000);
					} catch (Exception e1) {
						log.error(e1.getMessage());
					}
					i++;
					if (i == 4) {
						throw new CustomException(e.getMessage(), e);
					}

				}
		} else {

			if (xpath.contains("shadowRoot")) {
				xpath = "return " + xpath;
				element = (WebElement) ((JavascriptExecutor) driver).executeScript(xpath);
			} else if (identificationType.toUpperCase().equals("xpath".toUpperCase())) {
				element = driver.findElement(By.xpath(xpath));
			} else if (identificationType.toUpperCase().equals("id".toUpperCase())) {
				element = driver.findElement(By.id(xpath));
			}
		}
		return element;
	}

	public static WebElement findElement(String xpath, String identificationType, WebDriver frame) {
		WebElement element = null;
		if (identificationType.toUpperCase().equals("xpath".toUpperCase())) {
			element = frame.findElement(By.xpath(xpath));
		} else if (identificationType.toUpperCase().equals("id".toUpperCase())) {
			element = frame.findElement(By.id(xpath));
		}
		return element;
	}

	public static List<WebElement> findElements(String xpath, String identificationType) {
		driver = WebBrowser.getBrowser();
		if (xpath.contains("||")) {
			String[] xpathSplit = xpath.split("\\|\\|");
			xpath = xpathSplit[0];
			driver = GetFrame(xpathSplit[1]);
		}
		List<WebElement> element = null;
		if (identificationType.toUpperCase().equals("xpath".toUpperCase())) {
			element = driver.findElements(By.xpath(xpath));
		} else if (identificationType.toUpperCase().equals("id".toUpperCase())) {
			element = driver.findElements(By.id(xpath));
		}
		return element;
	}

	public static List<WebElement> findElements(String xpath, String identificationType, WebDriver frame) {
		List<WebElement> element = null;
		if (identificationType.toUpperCase().equals("xpath".toUpperCase())) {
			element = frame.findElements(By.xpath(xpath));
		} else if (identificationType.toUpperCase().equals("id".toUpperCase())) {
			element = frame.findElements(By.id(xpath));
		}
		return element;
	}

	public static void EnterText(WebElement element, String text) {
		try {
			element.click();
			element.sendKeys(text);
			for (int i = 0; i <= 1; i++) {
				try {
					if (element.getAttribute("value").equals(text)) {
						break;
					} else {
						element.sendKeys(Keys.CONTROL + "a");
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							log.error(e.getMessage());

						}
						element.sendKeys(text);
					}
				} catch (Exception ex) {
					log.error(ex.getMessage());
				}
			}
		} catch (Exception ex) {
			throw new CustomException(ex.getMessage(), ex);
		}
	}

	public static boolean verifyEnabledDisabledOptions(String verificationType, List<WebElement> eleList, String text) {
		boolean isVerified = false;
		boolean disabled = false;
		boolean verficationStarted = false;
		if (verificationType.toUpperCase().equals("Disabled".toUpperCase())) {
			disabled = true;
		}
		String[] textSplit = text.split(",");
		boolean breakLoop = false;
		int textSplitCount = 0;

		for (int k = 0; k < eleList.size(); k++) {
			String eleText = eleList.get(k).getText();
			for (int j = textSplitCount; j < textSplit.length; j++) {
				if (textSplitCount == textSplit.length - 1) {
					breakLoop = true;
				}
				if (eleText.equals(textSplit[j])) {
					verficationStarted = true;
					textSplitCount++;
					String classname = eleList.get(k).getAttribute("class");
					ExtentCucumberAdapter
							.addTestStepLog("Element text : " + eleText + " , Element Class : " + classname);
					if (classname.contains("disabled")) {
						if (disabled) {
							isVerified = true;
							break;
						} else {
							isVerified = false;
							break;
						}

					} else {
						if (disabled) {
							isVerified = false;
							break;
						} else {
							isVerified = true;
							break;
						}
					}
				}

			}
			if (breakLoop) {
				break;
			}
			if (verficationStarted && !isVerified) {
				if (disabled) {
					ExtentCucumberAdapter.addTestStepLog(eleText + " is enabled, Expected to be disabled");
					break;
				} else {
					ExtentCucumberAdapter.addTestStepLog(eleText + " is disabled, Expected to be enabled");
					break;
				}
			}

		}
		return isVerified;

	}

	public static void takeScrenshot(Scenario scenario) {
		String fullScreenshot = CommonUtil.getXMLData(
				Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(), "EnableFullScreenshot");

		String desktopFullScreenshot = CommonUtil.getXMLData(
				Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(),
				"EnableDesktopFullScreenshot");

		driver = WebBrowser.getBrowser();
		if (desktopFullScreenshot.equalsIgnoreCase("true")) {
			try {
				// Capture full desktop screenshot using Robot class
				Robot robot = new Robot();
				Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
				BufferedImage screenFullImage = robot.createScreenCapture(screenRect);

				// Save the full screenshot locally
				String fullScreenshotPath = Paths.get(path, "src", "test", "java", "desktop_full_screenshot.png")
						.toString();
				File fullScreenshotFile = new File(fullScreenshotPath);
				ImageIO.write(screenFullImage, "png", fullScreenshotFile);

				// Attach the full screenshot to the scenario
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ImageIO.write(screenFullImage, "png", bos);
				byte[] data = bos.toByteArray();
				scenario.attach(data, "image/png", "Full Desktop Screenshot");
			} catch (AWTException | IOException e) {
				e.printStackTrace();
				scenario.log("Failed to capture full desktop screenshot: " + e.getMessage());
			}
		}
		if (fullScreenshot.toLowerCase().equals("true")) {
			try {
				JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
				Long height = (Long) jsExecutor.executeScript(
						"return Math.max(document.body.scrollHeight, document.body.offsetHeight, document.documentElement.clientHeight, document.documentElement.scrollHeight, document.documentElement.offsetHeight);");

				if (height < 1000) {
					height = Long.valueOf(1000);
				}

				Actions actions = new Actions(driver);
				int xCoordinate = 100;
				int yCoordinate = 350;
				actions.moveByOffset(xCoordinate, yCoordinate).click().perform();

				for (int i = 0; i <= height; i += driver.manage().window().getSize().height) {
					jsExecutor.executeScript("window.scrollTo(0," + i + ");");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					File screenshotBase64 = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
					String pathh = Paths.get(path, "src", "test", "java", "image_" + i + ".png").toString();
					File destFile = new File(pathh);
					FileUtils.copyFile(screenshotBase64, destFile);
					actions.sendKeys(Keys.PAGE_DOWN).perform();
				}

				String pathhh = Paths.get(path, "src", "test", "java").toString();
				File dir = new File(pathhh);
				FileFilter fileFilter = new WildcardFileFilter("image_*.png");
				File[] files = dir.listFiles(fileFilter);

				if (files.length > 0) {
					Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);
					int width = 0;
					int heightt = 0;

					for (File file : files) {
						BufferedImage image1 = ImageIO.read(file);
						if (width < image1.getWidth()) {
							width = image1.getWidth();
						}
						heightt += image1.getHeight();
					}

					BufferedImage result = new BufferedImage(width, heightt, BufferedImage.TYPE_INT_ARGB);
					Graphics2D g = result.createGraphics();
					int currentHeight = 0;

					for (File file : files) {
						BufferedImage image1 = ImageIO.read(file);
						g.drawImage(image1, 0, currentHeight, null);
						currentHeight += image1.getHeight();
					}
					g.dispose();

					// Compress the merged screenshot
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					float compressionQuality = 0.6f;
					javax.imageio.ImageWriter jpegWriter = ImageIO.getImageWritersByFormatName("jpg").next();
					javax.imageio.ImageWriteParam jpegParams = jpegWriter.getDefaultWriteParam();
					jpegParams.setCompressionMode(javax.imageio.ImageWriteParam.MODE_EXPLICIT);

					boolean withinRange = false;
					while (!withinRange) {
						outputStream.reset();
						jpegParams.setCompressionQuality(compressionQuality);

						try (ImageOutputStream ios = ImageIO.createImageOutputStream(outputStream)) {
							jpegWriter.setOutput(ios);
							jpegWriter.write(null, new javax.imageio.IIOImage(result, null, null), jpegParams);
						}

						long compressedSize = outputStream.size();
						if (compressedSize <= 70 * 1024) { // Target size: 70 KB
							withinRange = true;
						} else if (compressionQuality > 0.05f) {
							compressionQuality -= 0.05f; // Incrementally reduce quality
						} else {
							System.out.println("Compression could not meet size constraints.");
							break;
						}
					}
					jpegWriter.dispose();
					if (withinRange) {
						File compressedFile = new File(
								Paths.get(path, "src", "test", "java", "compressed_image.jpg").toString());
						try (FileOutputStream fos = new FileOutputStream(compressedFile)) {
							fos.write(outputStream.toByteArray());
						}
						System.out.println("Compressed screenshot saved as: " + compressedFile.getPath());
						scenario.attach(outputStream.toByteArray(), "image/jpeg", "compressed_image");
					}
				}
			} catch (IOException e) {
				captureAndCompressScreenshot();
			}
		} else {
			captureAndCompressScreenshot();
		}
	}

	public static void captureScreenshot() {
		ScrenshotForSucess = CommonUtil.getXMLData(
				Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(),
				"EnableScrenshotForSucess");
		if ("true".equalsIgnoreCase(ScrenshotForSucess) && !Hooks.apiScenario) {
			captureAndCompressScreenshot();
		}
	}

	public static void takeEachStepScrenshot(Scenario scenario) {
		if (WebBrowser.boolEachstepScreenshot) {
			driver = WebBrowser.getBrowser();
			captureAndCompressScreenshot();
			log.info(scenario);
		}
	}

	public static WebDriver SwitchToBrowserWindow(String windowHandle) {
		driver = WebBrowser.getBrowser();
		return driver.switchTo().window(windowHandle);

	}

	public static void Scroll(WebElement element) {
		try {
			// Thread.sleep(2 * timeInterval);
			driver = WebBrowser.getBrowser();
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
			Thread.sleep(800);
			Actions actions = new Actions(driver);
			actions.moveToElement(element).build().perform();
			// actions.MoveToElement(element).Click().Build().Perform();
			// Thread.sleep(timeInterval);
			// Log.Info("Scroll is Successfull");
		} catch (Exception ex) {
			/*
			 * try { Thread.sleep(2 * timeInterval); } catch (InterruptedException e) {
			 * 
			 * }
			 */
			// Log.Info("Error in Scroll" + ex);
			throw new CustomException("Scroll and click unsuccessful" + ex.getMessage());
		}
	}

	public static void RefreshPage() {

		driver.navigate().refresh();
	}

	public static List<WebElement> getElementByXpath(String xpath) {
		if (xpath.contains("||")) {
			String[] xpathSplit = xpath.split("\\|\\|");
			xpath = xpathSplit[0];
			driver = GetFrame(xpathSplit[1]);
		}
		return driver.findElements(By.xpath(xpath));

	}

	public static WebElement getSingleElementByXpath(String xpath) {

		if (xpath.contains("||")) {
			String[] xpathSplit = xpath.split("\\|\\|");
			xpath = xpathSplit[0];
			driver = GetFrame(xpathSplit[1]);
		}
		return driver.findElement(By.xpath(xpath));

	}

	public static void SelectByIndex(WebElement dropDownList, int index) {
		Select drpList = new Select(dropDownList);
		if (dropDownList.isDisplayed()) {
			drpList.selectByIndex(index);
		} else {

			throw new CustomException("Select List not displayed");
		}
	}

	public static void SelectByRandomIndex(WebElement dropDownList) {
		Select drpList = new Select(dropDownList);
		if (dropDownList.isDisplayed()) {
			int totalCount = drpList.getOptions().size();
			Random random = new Random();
			int randomNumber = random.nextInt(totalCount - 1);
			drpList.selectByIndex(randomNumber);
		} else {

			throw new CustomException("Select List not displayed");
		}
	}

	public static void SelectByLastIndex(WebElement dropDownList) {
		Select drpList = new Select(dropDownList);
		if (dropDownList.isDisplayed()) {
			drpList.selectByIndex(drpList.getOptions().size() - 1);
		} else {

			throw new CustomException("Select List not displayed");
		}
	}

	public static int GetListCount(WebElement dropDownList) {
		Select drpList = new Select(dropDownList);
		if (dropDownList.isDisplayed()) {
			return drpList.getOptions().size();
		} else {

			throw new CustomException("Select List not displayed");
		}
	}

	public static void SelectByFirstIndex(WebElement dropDownList) {
		Select drpList = new Select(dropDownList);
		if (dropDownList.isDisplayed()) {
			drpList.selectByIndex(1);
		} else {

			throw new CustomException("Select List not displayed");
		}
	}

	public static void CloseBrowserWindow() {
		driver = WebBrowser.getBrowser();
		driver.close();
	}

	public static void SelectByValue(WebElement dropDownList, String text) {
		Select drpList = new Select(dropDownList);
		if (dropDownList.isDisplayed()) {
			drpList.selectByValue(text);
		} else {

			throw new CustomException("Select List not displayed");
		}
	}

	public static String GetSelectedValue(WebElement dropDownList) {
		Select drpList = new Select(dropDownList);
		if (dropDownList.isDisplayed()) {
			return drpList.getFirstSelectedOption().getText();
		} else {

			throw new CustomException("Select List not displayed");
		}
	}

	public static void ScrollAndSelectByText(WebElement dropDownList, String text) {
		Scroll(dropDownList);
		Select drpList = new Select(dropDownList);

		if (dropDownList.isDisplayed()) {
			drpList.selectByVisibleText(text);
		} else {

			throw new CustomException("Select List not displayed");
		}

	}

	public static WebDriver AttachPage(String pageTitle) {
		driver = WebBrowser.getBrowser();
		try {
			String title = driver.getTitle();

		} catch (UnhandledAlertException e) {
			log.error(e.getMessage());
		} catch (NoSuchWindowException ex) {
			driver.switchTo().window((driver.getWindowHandles().toArray()[0]).toString());

		}
		try {

			if (!driver.getTitle().toUpperCase().contains(pageTitle.toUpperCase())) {
				driver.switchTo().defaultContent();
				try {
					WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2*timeInterval/1000));
					wait.until(ExpectedConditions.numberOfWindowsToBe(2));
				} catch (Exception ex) {
					if (driver.getWindowHandles().size() <= 2) {
						return driver;
					}
				}
				int i = 0;
				int index = maxDelay / timeInterval;
				while (i < index) {
					for (String winHandle : driver.getWindowHandles()) {
						try {
							String title = pageTitle.replace("Page", "");
							boolean isNumeric = isStringInt(title);
							if (isNumeric) {
								int windowIndex = Integer.parseInt(title);
								return driver.switchTo()
										.window((driver.getWindowHandles().toArray()[windowIndex - 1]).toString());
							}
						} catch (Exception ex) {
							log.error(ex.getMessage());
						}

						if (driver.switchTo().window(winHandle).getTitle().toUpperCase()
								.contains(pageTitle.toUpperCase())) {
							return driver.switchTo().window(winHandle);
						}

					}
					i++;
					Thread.sleep(timeInterval);
				}
			} else {
				Thread.sleep(timeInterval);
			}
			return driver;
		} catch (UnhandledAlertException e) {
			return driver;
		} catch (Exception ex) {
			return driver.switchTo().window((driver.getWindowHandles().toArray()[0]).toString());
		}
	}

	public static void ScrollAndClick(WebElement element) throws InterruptedException {
		try {
			driver = WebBrowser.getBrowser();
			Thread.sleep(2 * timeInterval);
			Thread.sleep(timeInterval);
			try {
				element.click();
			} catch (Exception ex) {
				((JavascriptExecutor) driver).executeScript("arguments[0].click()", element);
			}
		} catch (Exception ex) {
			Thread.sleep(2 * timeInterval);
			throw new CustomException("Scroll and click unsuccessful" + ex.getMessage());
		}
	}

	public static WebDriver GetFrame(String propertyValue) {
		try {
			driver = WebBrowser.getBrowser();
			driver.switchTo().defaultContent();
			int i = 0;
			while (i < maxDelay / timeInterval) {
				try {
					String[] frameIndex = propertyValue.split("_");

					boolean isNumeric = isStringInt(frameIndex[0]);
					// bool isNumeric = int.TryParse(title, out n);
					if (isNumeric) {
						int index = Integer.parseInt(frameIndex[0]);
						driver.switchTo().frame(index);
						if (frameIndex.length > 1) {
							isNumeric = isStringInt(frameIndex[1]);
							Thread.sleep(2 * timeInterval);
							// bool isNumeric = int.TryParse(title, out n);
							if (isNumeric) {
								index = Integer.parseInt(frameIndex[1]);
								return driver.switchTo().frame(index);

							} else {
								return driver.switchTo().frame(frameIndex[1]);
							}
						}
						return driver;
					} else {
						for (int j = 0; j < frameIndex.length; j++) {
							driver.switchTo().frame(frameIndex[j]);
							Thread.sleep(2 * timeInterval);
						}
						return driver;
					}
				} catch (Exception e) {
					Thread.sleep(timeInterval);
				}
				i++;
			}
			return driver;
		} catch (Exception ex) {
			throw new CustomException("Web Frame with :" + propertyValue + " not found");
		}

	}

	public static boolean isStringInt(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	public static boolean IsSorted(List<Integer> sortedList) {
		return IntStream.range(1, sortedList.size())
				.map(index -> sortedList.get(index - 1).compareTo(sortedList.get(index))).allMatch(order -> order >= 0);
	}

	public static boolean IsReverseSorted(List<Integer> sortedList) {
		return IntStream.range(1, sortedList.size())
				.map(index -> sortedList.get(index - 1).compareTo(sortedList.get(index))).allMatch(order -> order >= 0);
	}

	public static String Text(WebElement element) {

		return element.getAttribute("value");

	}

	public static boolean Checked(WebElement checkBox) {
		return checkBox.isSelected();
	}

	public static void SelectByText(WebElement element, String value) {

		Select drpDownLst = new Select(element);
		drpDownLst.selectByVisibleText(value);

	}

	public static WebElement GetLinkByPartialLinkText(String text) {
		return driver.findElement(By.partialLinkText(text));
	}

	public static boolean validationOfSortedDropdownAscending(WebElement element) {

		boolean orderStatus = false;
		Select dropdown = new Select(element);

		List<WebElement> allOptionsElement = dropdown.getOptions();
		List<String> options = new ArrayList<String>();

		for (WebElement optionElement : allOptionsElement) {
			options.add(optionElement.getText().trim());
		}

		System.out.println("Options in dropdown with Default order :" + options);

		List<String> tempList = options.stream().sorted().collect(Collectors.toList());

		String englishRules1 = ("< '\u0021' < '\u0040' < '\u0023' < '\u0024' < '\u0025' < '\u005E' "
				+ "< '\u0026' < '\u002A' < '\u0028' < '\u0029'< '\u002D' < '\u002B' < '\u0020'"
				+ " < 0 < 1 < 2 < 3 < 4 < 5 < 6 < 7 < 8 < 9 < a < b < c < d < e < f < g < h < i < j < k < l "
				+ " < m < n < o < p < q < r < s < t < u < v < w < x < y < z < A < B < C < D < E < F "
				+ " < G < H < I < J < K < L < M < N < O < P < Q < R < S < T < U < V < W < X < Y < Z");

		try {

			RuleBasedCollator rbc = new RuleBasedCollator(englishRules1);
			rbc.setStrength(Collator.PRIMARY);

			Collections.sort(tempList, rbc);
			System.out.println("After sorting tempList :" + tempList);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.fillInStackTrace();
		}

		orderStatus = options.equals(tempList);

		System.out.println(" orderStatus : " + orderStatus);

		return orderStatus;

	}

	public static Comparator<String> stringAlphabeticalComparator = new Comparator<String>() {
		public int compare(String str1, String str2) {
			return ComparisonChain.start().compare(str1, str2, String.CASE_INSENSITIVE_ORDER).compare(str1, str2)
					.result();
		}
	};
	public static Comparator<String> stringReverseAlphabeticalComparator = new Comparator<String>() {
		public int compare(String str1, String str2) {
			return -1 * ComparisonChain.start().compare(str1, str2, String.CASE_INSENSITIVE_ORDER).compare(str1, str2)
					.result();
		}
	};

	public static void AcceptAlert() {
		int i = 0;
		driver = WebBrowser.getBrowser();
		while (i < maxDelay / timeInterval) {
			try {
				Thread.sleep(timeInterval);
				Alert alert = driver.switchTo().alert();
				alert.accept();
				i++;
				break;
			} catch (Exception e) {
				i++;
				try {
					Thread.sleep(timeInterval);
				} catch (InterruptedException e1) {
					log.error(e1.getMessage());
				}
			}
		}
	}

	public static void DismissAlert() {
		int i = 0;
		driver = WebBrowser.getBrowser();
		while (i < maxDelay / timeInterval) {
			try {
				Thread.sleep(timeInterval);
				Alert alert = driver.switchTo().alert();
				alert.dismiss();
				i++;
				break;
			} catch (Exception e) {
				i++;
				try {
					Thread.sleep(timeInterval);
				} catch (InterruptedException e1) {
					log.error(e1.getMessage());
				}
			}
		}
	}

	public static void EnterAlertText(String text) {
		int i = 0;
		driver = WebBrowser.getBrowser();
		while (i < maxDelay / timeInterval) {
			try {
				Thread.sleep(timeInterval);
				Alert alert = driver.switchTo().alert();
				alert.sendKeys(text);
				alert.accept();
				i++;
				break;
			} catch (Exception e) {
				i++;
				try {
					Thread.sleep(timeInterval);
				} catch (InterruptedException e1) {
					log.error(e1.getMessage());
				}
			}
		}
	}

	public static boolean verifyAlertText(String text) {
		int i = 0;
		boolean isVerified = false;
		driver = WebBrowser.getBrowser();
		while (i < maxDelay / timeInterval) {
			try {
				Thread.sleep(timeInterval);
				Alert alert = driver.switchTo().alert();
				isVerified = alert.getText().contains(text);
				// alert.accept();
				driver.switchTo().defaultContent();
				i++;
				break;
			} catch (Exception e) {
				i++;
				driver.switchTo().defaultContent();
				try {
					Thread.sleep(timeInterval);
				} catch (InterruptedException e1) {
					log.error(e1.getMessage());
				}
			}
		}
		return isVerified;

	}

	public static void Check(WebElement element) {
		if (!Checked(element)) {
			Click(element);
		}
	}

	public static void UnCheck(WebElement element) {
		if (Checked(element)) {
			Click(element);
		}
	}

	public static boolean IsEnabled(WebElement element) {

		return element.isEnabled();

	}

	public static String GetText(WebElement element) {
		String text = "";
		text = element.getText();
		if (text == null || text.trim().isEmpty()) {
			text = element.getAttribute("value");
		}
		if (text == null || text.trim().isEmpty()) {
			text = element.getAttribute("innerHTML");
		}

		return text;
	}

	public static boolean IsDisplayed(WebElement element) {

		return element.isDisplayed();

	}

	public static boolean ReadOnly(WebElement element) {

		return element.isSelected();

	}

	public static void ClearText(WebElement element) {
		element.clear();
	}

	public static void SelectEnterKey(WebElement element) {
		try {
			element.click();
			element.sendKeys(Keys.ENTER);
		} catch (Exception e) {
			throw new CustomException(e.getMessage(), e);
		}
	}

	/*
	 * public static String GetToolTip(WebElement element) { return
	 * element.getAttribute("alt"); }
	 */

	public static String GetToolTip(WebElement element) {
		String toolTipText = element.getAttribute("alt");
		if (toolTipText == null || toolTipText.isEmpty()) {
			toolTipText = element.getAttribute("title");
		}
		if (toolTipText == null || toolTipText.isEmpty()) {
			toolTipText = element.getAttribute("data-original-title");
		}
		if (toolTipText == null || toolTipText.isEmpty()) {
			toolTipText = element.getAttribute("id");
		}
		if (toolTipText == null || toolTipText.isEmpty()) {
			toolTipText = element.getAttribute("uib-tooltip");
		}
		if (toolTipText == null || toolTipText.isEmpty()) {
			toolTipText = element.getAttribute("mattooltip");
		}
		if (toolTipText == null || toolTipText.isEmpty()) {
			toolTipText = GetText(element);
		}
		return toolTipText;
	}

	public static List<WebElement> GetOptions(WebElement element) {
		Select drpDwnLst = new Select(element);
		return drpDwnLst.getOptions();

	}

	public static boolean IsMultiple(WebElement element) {
		Select drpDwnLst = new Select(element);
		return drpDwnLst.isMultiple();

	}

	public static List<WebElement> GetAllSelectedOptions(WebElement element) {
		Select drpDwnLst = new Select(element);
		return drpDwnLst.getAllSelectedOptions();

	}

	public static boolean HasSelectedItems(WebElement element) {
		Select drpDwnLst = new Select(element);
		List<WebElement> elementsList = drpDwnLst.getAllSelectedOptions();
		// if (elementsList.size() > 1) {
		if (!elementsList.get(0).getText().isEmpty()) {
			return true;
		} else {
			return false;
		}

	}

	public static String GetFirstOption(WebElement element) {
		Select drpDwnLst = new Select(element);
		WebElement option = drpDwnLst.getFirstSelectedOption();
		return option.getText();
	}

	public static String SelectedItem(WebElement element) {
		Select drpDwnLst = new Select(element);
		if (element.isDisplayed()) {
			return drpDwnLst.getFirstSelectedOption().getText();
		} else {
			throw new CustomException("Select List not displayed");
		}

	}

	public static void Selected(WebElement element) {
		try {
			boolean staleElement = true;
			int i = 0;
			while (staleElement && i < 5) {
				try {
					Click(element);
					staleElement = false;
				} catch (Exception ex) {
					i++;
					staleElement = true;
					try {
						ScrollAndClickUsingJS(element);
						staleElement = false;
					} catch (Exception exc) {
						log.error(exc.getMessage());
					}
					if (i == 4) {
						throw new CustomException(ex.getMessage(), ex);
					}
				}
			}

		} catch (Exception e) {
			throw new CustomException(e.getMessage(), e);
		}
	}

	public static void PressCTRLAndClick(WebElement element) {
		try {
			// Thread.sleep(2 * timeInterval);
			driver = WebBrowser.getBrowser();
			Actions actions = new Actions(driver);
			actions.moveToElement(element).keyDown(Keys.CONTROL).click().keyUp(Keys.CONTROL).build().perform();
			Thread.sleep(timeInterval);

		} catch (Exception ex) {
			throw new CustomException("Scroll and click unsuccessful" + ex.getMessage());
		}
	}

	public static int GetColumnIndex(WebElement table, String colName) {
		Collection<WebElement> rows = null;
		Collection<WebElement> cells = null;
		try {

			if (table.isDisplayed()) {
				rows = table.findElements(By.tagName("tr"));
				for (WebElement row : rows) {
					if (!row.getText().isEmpty()) {
						cells = row.findElements(By.xpath("td"));
						if (cells.size() == 0) {
							cells = row.findElements(By.xpath("th"));
						}
						int columnNumber = 0;
						for (WebElement cell : cells) {
							if (cell.getText() != null && !cell.getText().isEmpty()
									&& cell.getText().toUpperCase().contains(colName.toUpperCase().trim())) {
								cells = null;
								return columnNumber;
							}
							columnNumber++;
						}
					}
				}

				throw new CustomException("Table column with text '" + colName + "' does not exists");
			} else {
				throw new CustomException("Table does not exists");
			}
		} catch (Exception ex) {

			try {
				throw ex;
			} catch (Exception e) {
				log.error(e.getMessage());
			}
			return 0;
		}
	}

	public static String IsReadOnly(WebElement element) {

		return element.getAttribute("readonly");

	}

	public static void ScrollAndCheck(WebElement element) throws InterruptedException, CustomException {
		// Log.Info("Scroll And Check is Successful");
		ScrollAndClickUsingJS(element);
		Thread.sleep(1000);
		if (!Checked(element)) {
			// Log.Info("Scroll And Check is Successful");
			element.click();
		}
	}

	public static void RightClick(WebElement element) {
		driver = WebBrowser.getBrowser();
		Actions actions = new Actions(driver);
		actions.moveToElement(element);
		actions.contextClick(element).build().perform();

	}

	public static void DoubleClick(WebElement element) {
		driver = WebBrowser.getBrowser();
		Actions actions = new Actions(driver);
		actions.moveToElement(element);
		actions.doubleClick(element).build().perform();

	}

	public static void ScrollAndClearEnterText(WebElement element, String text) throws InterruptedException {
		Scroll(element);
		try {
			// element.click();
			element.clear();
			Thread.sleep(1000);
			element.sendKeys(text);
			for (int i = 0; i < 1; i++) {
				if (element.getAttribute("value").equals(text)) {
					break;
				} else {
					element.sendKeys(Keys.CONTROL + "a");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						log.error(e.getMessage());
					}
					element.sendKeys(text);
				}
			}
		} catch (Exception ex) {
			throw new CustomException("Scroll And Clear EnterText unsuccessful" + ex.getMessage());
		}
	}

	public static void ScrollAndUncheck(WebElement element) throws InterruptedException {

		ScrollAndClickUsingJS(element);
		Thread.sleep(1000);
		if (Checked(element)) {
			WebBrowserUtil.Click(element);
			// element.click();
		}
	}

	public static void ScrollAndWait() {
		((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 800)");
		try {
			Thread.sleep(timeInterval);
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		}
		((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 1500)");
		try {
			Thread.sleep(3 * timeInterval);
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		}
	}

	public static void DragAndDropHorizontally(WebElement sourceElement, int distance) {
		try {
			driver = WebBrowser.getBrowser();
			// create object 'action' of Actions class
			Actions action = new Actions(driver);
			action.dragAndDropBy(sourceElement, distance, 0);
			action.build().perform();
			Thread.sleep(2 * timeInterval);

		} catch (Exception ex) {
			throw new CustomException("Drag And Drop Horizontally unsuccessful" + ex.getMessage());
		}
	}

	public static void DragAndDropHorizontally(WebElement sourceElement, WebElement targetElement) {
		try {
			driver = WebBrowser.getBrowser();
			// create object 'action' of Actions class
			Actions action = new Actions(driver);
			action.dragAndDrop(sourceElement, targetElement);
			action.build().perform();
			Thread.sleep(2 * timeInterval);

		} catch (Exception ex) {
			throw new CustomException("Drag And Drop Horizontally unsuccessful" + ex.getMessage());
		}
	}

	public static void ClickAndHoldAndRelease(WebElement sourceElement, WebElement targetElement) {
		try {
			driver = WebBrowser.getBrowser();
			// create object 'action' of Actions class
			Actions action = new Actions(driver);
			action.clickAndHold(sourceElement).moveToElement(targetElement).release(targetElement).build().perform();
			Thread.sleep(2 * timeInterval);

		} catch (Exception ex) {
			throw new CustomException("Drag And Drop Horizontally unsuccessful" + ex.getMessage());
		}
	}

	public static void ScrollDown(String param) {
		Actions actions = new Actions(driver);
		param = param.toLowerCase().replace(" ", "");
		if (param.contains("pagedown")) {
			Pattern pattern = Pattern.compile("\\d+");
			Matcher matcher = pattern.matcher(param);
			if (matcher.find()) {
				String numberString = matcher.group();
				int number = Integer.parseInt(numberString);
				for (int i = 0; i < number; i++) {
					try {
						actions.moveToElement(driver.findElement(By.tagName("body"))).sendKeys(Keys.PAGE_DOWN)
								.perform();
						Thread.sleep(3 * timeInterval);
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				}
			}
		} else if (param.contains("pageup")) {
			Pattern pattern = Pattern.compile("\\d+");
			Matcher matcher = pattern.matcher(param);
			if (matcher.find()) {
				String numberString = matcher.group();
				int number = Integer.parseInt(numberString);
				for (int i = 0; i < number; i++) {
					try {
						actions.moveToElement(driver.findElement(By.tagName("body"))).sendKeys(Keys.PAGE_UP).perform();
						Thread.sleep(3 * timeInterval);
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				}
			}
		} else if (param.contains("downarrow")) {
			Pattern pattern = Pattern.compile("\\d+");
			Matcher matcher = pattern.matcher(param);
			if (matcher.find()) {
				String numberString = matcher.group();
				int number = Integer.parseInt(numberString);
				for (int i = 0; i < number; i++) {
					try {
						actions.moveToElement(driver.findElement(By.tagName("body"))).sendKeys(Keys.ARROW_DOWN)
								.perform();
						Thread.sleep(3 * timeInterval);
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				}
			}
		} else if (param.contains("uparrow")) {
			Pattern pattern = Pattern.compile("\\d+");
			Matcher matcher = pattern.matcher(param);
			if (matcher.find()) {
				String numberString = matcher.group();
				int number = Integer.parseInt(numberString);
				for (int i = 0; i < number; i++) {
					try {
						actions.moveToElement(driver.findElement(By.tagName("body"))).sendKeys(Keys.ARROW_UP).perform();
						Thread.sleep(3 * timeInterval);
					} catch (Exception e) {
						// Handle exception or add logging here
					}
				}
			}
		} else {
			int scrollValue = Integer.parseInt(param);
			for (int i = 0; i < Math.abs(scrollValue); i++) {
				try {
					if (scrollValue > 0) {
						((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 2000)"); // Scroll down
					} else {
						((JavascriptExecutor) driver).executeScript("window.scrollBy(0, -2000)"); // Scroll up
					}
					Thread.sleep(3 * timeInterval);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
		}
	}

	public static void ScrollVerifyTooltip(String info, String mousehover) {
		((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0)");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// e1.printStackTrace();
		}
		for (int i = 0; i < 10; i++) {
			if (i == 0) {
				((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 100)");
			} else if (i == 1) {
				((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 200)");
			} else if (i == 2) {
				((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 300)");
			} else if (i == 3) {
				((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 400)");
			} else if (i == 4) {
				((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 500)");
			} else if (i == 5) {
				((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 600)");
			} else if (i == 6) {
				((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 700)");
			} else if (i == 7) {
				((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 800)");
			} else if (i == 8) {
				((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 900)");
			} else if (i == 9) {
				((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 1000)");
			}
			try {
				Thread.sleep(timeInterval);
				WebElement hoverEle = WebBrowserUtil.getSingleElementByXpath(info);
				if (hoverEle.isDisplayed()) {
					WebBrowserUtil.MouseHover(hoverEle);
					Thread.sleep(2000);
					WebElement eleTooltip = WebBrowserUtil.getSingleElementByXpath(mousehover);
					if (eleTooltip.isDisplayed()) {
						break;
					}
				}
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
	}

	public static void ScrollToTheElement(int numberOfTimes, WebElement element) {
		for (int i = 0; i < numberOfTimes; i++) {
			try {
				Thread.sleep(2 * timeInterval);
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", element);
				Thread.sleep(2 * timeInterval);
				System.out.println(" Scrolling " + i + " time(s)");
			} catch (InterruptedException e) {
				throw new CustomException(e.getMessage());
			}

		}
	}

	public static void ScrollVertically(int numberOfTimes) {

		try {

			long endHeight = 400;
			long startHeight = 0;
			String scrollCommand = "window.scrollTo(startHeight, endHeight)";
			long lastHeight = 0;
			long newHeight = 0;

			while (true) {

				lastHeight = (long) ((JavascriptExecutor) driver).executeScript("return document.body.scrollHeight");

				System.out.println(" Last hight " + lastHeight);
				if (endHeight <= lastHeight) {
					System.out.println("Scroll start value " + startHeight + " and end value " + endHeight);
					String cmd = scrollCommand.replace("startHeight", String.valueOf(startHeight)).replace("endHeight",
							String.valueOf(endHeight));
					((JavascriptExecutor) driver).executeScript(cmd);
				}

				else {
					String cmd = scrollCommand.replace("startHeight", String.valueOf(startHeight)).replace("endHeight",
							String.valueOf(lastHeight));
					((JavascriptExecutor) driver).executeScript(cmd);
					Thread.sleep(3000);
					break;
				}
				Thread.sleep(3000);

				newHeight = (long) ((JavascriptExecutor) driver).executeScript("return document.body.scrollHeight");
				System.out.println(" new height " + newHeight);
				startHeight = endHeight;
				endHeight = startHeight + endHeight;

				Thread.sleep(3000);
				lastHeight = newHeight;
			}
		} catch (Exception e) {
			log.error(e.getMessage() + numberOfTimes);
		}

	}

	public static void MouseHover(WebElement element) {
		driver = WebBrowser.getBrowser();
		new Actions(driver).moveToElement(element).perform();
	}

	public static void ClickBrowserBackButton() {
		driver = WebBrowser.getBrowser();
		driver.navigate().back();

	}

	public static void Click(WebElement element) {
		try {
			driver = WebBrowser.getBrowser();

			try {
				element.click();
			} catch (Exception ex) {
				((JavascriptExecutor) driver).executeScript("arguments[0].click()", element);
			}
		} catch (Exception ex) {
			throw new CustomException("Click unsuccessful" + ex.getMessage());
		}
	}

	public static void ScrollAndClickUsingJS(WebElement element) {
		driver = WebBrowser.getBrowser();
		Actions actions = new Actions(driver);
		try {
			if (element != null) {

				if (element.isDisplayed()) {
					WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
					wait.until(ExpectedConditions.elementToBeClickable(element));
					((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
				} else {
					System.out.println("In first else");
					actions.moveToElement(element).perform();
					actions.moveToElement(element).click().build().perform();
				}
			} else {
				System.out.println("Element is not present!!!");
				throw new CustomException("Element is not present!!!");
			}
		} catch (Exception ex) {
			throw new CustomException("Scroll and click unsuccessful" + ex.getMessage());
		}
	}

	public static void ScrollAndClickUsingJS(String xpath, String identifier) {
		int i = 0;
		boolean staleElement = true;
		try {

			while (i < 4 && staleElement) {
				WebElement element = WebBrowserUtil.findElement(xpath, identifier);
				try {
					Actions actions = new Actions(driver);
					actions.moveToElement(element).perform();
					((JavascriptExecutor) driver).executeScript("arguments[0].click()", element);
					staleElement = false;
				} catch (Exception e) {
					i++;
					if (i == 3) {
						throw new CustomException("Scroll and click unsuccessful" + e.getMessage());
					}
				}
			}
		} catch (Exception ex) {
			throw new CustomException("Scroll and click unsuccessful" + ex.getMessage());
		}
	}

	public static void ClearAndEnterText(WebElement element, String text) {
		try {
			element.click();
			element.clear();
			element.sendKeys(text);
			for (int i = 0; i < 1; i++) {
				if (element.getAttribute("value").equals(text)) {
					break;
				} else {
					element.sendKeys(Keys.CONTROL + "a");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						log.error(e.getMessage());
					}
					element.sendKeys(text);
				}
			}
		} catch (Exception ex) {
			throw new CustomException(" Clear And EnterText unsuccessful" + ex.getMessage());
		}
	}

	public static void OpenNewTabAndNavigate(String url) {
		try {
			driver = WebBrowser.getBrowser();
			// To open a new tab
			Robot r = new Robot();
			r.keyPress(KeyEvent.VK_CONTROL);
			r.keyPress(KeyEvent.VK_T);
			r.keyRelease(KeyEvent.VK_CONTROL);
			r.keyRelease(KeyEvent.VK_T);
			Thread.sleep(4 * timeInterval);
			driver.switchTo().window(
					driver.getWindowHandles().toArray()[driver.getWindowHandles().toArray().length - 1].toString());
			driver.navigate().to(url);
			Thread.sleep(2 * timeInterval);
		} catch (Exception ex) {
			throw new CustomException(ex.getMessage());
		}
	}

	public static void waitForElementToBeHidden(String xPath) {
		try {
			driver = WebBrowser.getBrowser();
			int i = 0;
			while (i < maxDelay / timeInterval) {
				try {
					driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
					Thread.sleep(timeInterval);
					WebElement element = driver.findElement(By.xpath(xPath));
					if (element == null) {
						driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(controlLoadTimeout));
						break;
					}
					i++;
				} catch (Exception e) {
					i++;
					Thread.sleep(timeInterval);
					driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(controlLoadTimeout));
					break;
				}
			}
		} catch (Exception ex) {
			throw new CustomException(ex.getMessage());
		}
	}

	public static void waitForElementToBeVisible(String xPath) {
		try {
			driver = WebBrowser.getBrowser();
			int i = 0;
			while (i < maxDelay / timeInterval) {
				try {
					driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
					Thread.sleep(timeInterval);
					WebElement element = driver.findElement(By.xpath(xPath));
					if (element != null) {
						driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(controlLoadTimeout));
						break;
					}
					i++;
				} catch (Exception e) {
					i++;
					Thread.sleep(timeInterval);
					driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(controlLoadTimeout));
					break;
				}
			}
		} catch (Exception ex) {
			throw new CustomException(ex.getMessage());
		}
	}

	public static void uploadFile(String fileName) {
		try {
			// put path to your image in a clipboard
			StringSelection ss = new StringSelection(fileName);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);

			// imitate mouse events like ENTER, CTRL+C, CTRL+V
			Robot robot = new Robot();
			robot.delay(4000);
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.delay(50);
			robot.keyRelease(KeyEvent.VK_ENTER);
		} catch (Exception ex) {
			throw new CustomException(ex.getMessage());
		}
	}

	public static boolean validateDropdownDataSize(WebElement element) {

		Select dropdown = new Select(element);

		List<WebElement> allOptionsElement = dropdown.getOptions();
		int k = allOptionsElement.size();
		if (k == 1)

		{
			return true;
		} else {
			return false;
		}

	}

	public static List<WebElement> getElementByXpathFromIframe(String xpath) {

		List<WebElement> list = null;
		int size = driver.findElements(By.tagName("iframe")).size();

		if (size > 0) {
			for (int i = 0; i <= size; i++) {
				driver.switchTo().frame(i);
				list = driver.findElements(By.xpath(xpath));
				if (list.size() > 0) {
					driver.switchTo().defaultContent();
					return list;
				}

				driver.switchTo().defaultContent();
			}
		} else {
			return getElementByXpath(xpath);
		}

		return null;

	}

	public static void getElementByXpathFromIframeAndClick(String xpath) {

		List<WebElement> list = null;
		int size = driver.findElements(By.tagName("iframe")).size();

		if (size > 0) {
			for (int i = 0; i <= size; i++) {
				driver.switchTo().frame(i);
				list = driver.findElements(By.xpath(xpath));
				if (list.size() > 0) {
					try {
						Click(list.get(0));
						driver.switchTo().defaultContent();
						return;
					} catch (Exception e) {
						driver.switchTo().defaultContent();
					}
				}
				driver.switchTo().defaultContent();
			}

		} else {
			try {
				Click(getElementByXpath(xpath).get(0));
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
	}

	public static void openCmd(String cmd) {
		try {
			Runtime.getRuntime().exec(new String[] { "cmd.exe", "/K", "Start", cmd });
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static boolean validateDateFormat(String date, String format) {

		List<String> formatStrings = Arrays.asList(format);
		for (String formatString : formatStrings) {
			try {
				new SimpleDateFormat(formatString).parse(date);
				return true;
			} catch (Exception e) {
				return false;
			}
		}

		return false;
	}

	public static boolean verifyDateFormat(String xpath, String text) {
		boolean result = false;
		String[] splitText = text.split("--");
		if (text.contains("verify_list")) {
			List<WebElement> links = getElementByXpath(xpath);
			String printData = "";
			for (int i = 0; i < links.size(); i++) {
				String eleText = WebBrowserUtil.GetText(links.get(i));
				if (eleText != null && !eleText.isEmpty()) {
					if (!WebBrowserUtil.validateDateFormat(eleText, splitText[1])) {
						ExtentCucumberAdapter.addTestStepLog(eleText + " is not correct date format");
						return false;
					}
				} else {
					printData += eleText + ",";
					result = true;
				}

			}
			ExtentCucumberAdapter.addTestStepLog(printData + " are in correct date format");
			return result;
		} else {
			WebElement links = getSingleElementByXpath(xpath);
			String eleText = WebBrowserUtil.GetText(links);
			if (eleText == "" || eleText == null || eleText.isEmpty()) {
				ExtentCucumberAdapter.addTestStepLog("Element is null");
				result = true;
			} else if (WebBrowserUtil.validateDateFormat(eleText, splitText[1]) == false) {
				ExtentCucumberAdapter.addTestStepLog(eleText + " is not correct date format");
				return false;
			} else {
				ExtentCucumberAdapter.addTestStepLog(eleText + " is correct date format");
				result = true;
			}
			return result;
		}
	}

	public static void enterDataInExcel(String path) {
		// path="path_enter-row"
		// path="path_update-cell_idToFindRowNumber_columnNumber_valueToBeUpdated"
		String[] splitText = path.split("--");
		String excelFilePath = splitText[0];
		try {

			File file = new File(excelFilePath);
			FileInputStream inputdocument = new FileInputStream(file);
			// convert it into a POI object
			XSSFWorkbook myxlsxworkbook = new XSSFWorkbook(inputdocument);
			// Read excel sheet that needs to be updated
			XSSFSheet myworksheet = myxlsxworkbook.getSheetAt(0);
			if (path.contains("enter-row")) {
				int lastRowIndex = 0;
				int lastCellIndex = 0;
				if (myworksheet.getPhysicalNumberOfRows() > 0) {

					lastRowIndex = myworksheet.getLastRowNum();
					// Object [] objArr = new Object[] {};
					List<String> lastRowData = new ArrayList<>();
					Row row = myworksheet.getRow(lastRowIndex);
					int colNum = row.getLastCellNum();
					System.out.println("total cell : " + colNum);
					for (int j = 0; j < colNum; j++) {
						final Cell cell = row.getCell(j);
						System.out.println("j : " + j + " value : " + cell);
						if (cell != null) {
							String regex = "[0-9]+";
							Pattern p = Pattern.compile(regex);
							Matcher m = p.matcher(cell.toString());
							if (m.matches()) {
								lastRowData.add(String.valueOf(Integer.parseInt(cell.toString()) + 1));
							} else {
								lastRowData.add(cell.toString());
							}

						} else {
							lastRowData.add("");
						}

					}
					inputdocument.close();

					Row rowNew = myworksheet.createRow(lastRowIndex++);
					int cellnum = 0;
					for (String c : lastRowData) {
						Cell cell = rowNew.createCell(cellnum++);
						cell.setCellValue(c);
					}

				}
			} else if (path.contains("update-cell")) {
				String[][] excelData = null;

				int rows = myworksheet.getPhysicalNumberOfRows();

				// get number of cell from row
				int cells = myworksheet.getRow(0).getPhysicalNumberOfCells();

				excelData = new String[rows][cells];
				for (int p = 0; p < rows; p++) {
					Row row2 = myworksheet.getRow(p);
					for (int n = 0; n < cells; n++) {

						Cell cell = row2.getCell(n);
						if (cell != null) {
							excelData[p][n] = cell.toString();
						} else {
							excelData[p][n] = "";
						}
					}
				}
				int rowCount = 0;
				for (int l = 0; l < excelData.length; l++) {
					if (excelData[l][0] != null && excelData[l][0].contains(splitText[2])) {
						rowCount = l;
						break;
					}
				}
				inputdocument.close();
				Cell cellEdit = myworksheet.getRow(rowCount).getCell(Integer.parseInt(splitText[3]));
				if (cellEdit == null) {
					Row rowNew = myworksheet.getRow(rowCount);
					cellEdit = rowNew.createCell(Integer.parseInt(splitText[3]));
				}
				cellEdit.setCellValue(splitText[4]);
			}
			if (file != null) {
				FileOutputStream fileOut = new FileOutputStream(file);

				// write this workbook to an Outputstream.
				if (myxlsxworkbook != null)
					myxlsxworkbook.write(fileOut);
				fileOut.flush();
				fileOut.close();
			}
			if (myxlsxworkbook != null)
				myxlsxworkbook.close();

		} catch (Exception e) {
			throw new CustomException(e.getMessage(), e);
		}
	}

	public static void enterDataInRequiredCell(String filePath) {
		// path : path of the
		// file,sheetname,celldata-rownumber-colnumber,celldata2-rownumber2-colnumber2
		String[] splitPath = filePath.split(",");
		String path = splitPath[0];
		String sheetName = splitPath[1];

		if (splitPath.length >= 3) {
			try {
				FileInputStream fis = new FileInputStream(new File(path));
				XSSFWorkbook workbook = new XSSFWorkbook(fis);

				if (workbook.getSheetIndex(sheetName) != -1) {
					XSSFSheet sheet = workbook.getSheet(sheetName);
					for (String data : splitPath) {
						if (data.contains("-")) {
							String[] entries = data.split("-");
							String cellValue = entries[0];
							int row = Integer.parseInt(entries[1]);
							int col = Integer.parseInt(entries[2]);
							XSSFRow sheetRow = sheet.getRow(row);
							if (sheetRow == null) {
								sheetRow = sheet.createRow(row);
							}
							XSSFCell cell = sheetRow.getCell(col);
							if (cell == null) {
								cell = sheetRow.createCell(col);
							}
							cell.setCellValue(cellValue);
						}
					}
					fis.close();
					FileOutputStream fos = new FileOutputStream(new File(path));
					workbook.write(fos);
					fos.close();
					System.out.println("Data is entered into excel sheet");
				}
			} catch (IOException e) {
				System.out.println("Unable to enter and save to " + path);
				e.printStackTrace();
			}
		} else {
			try {
				FileInputStream fis = new FileInputStream(new File(filePath));
				XSSFWorkbook workbook = new XSSFWorkbook(fis);
				XSSFSheet sheet = workbook.getSheetAt(0);

				for (String data : splitPath) {
					if (data.contains("-")) {
						String[] entries = data.split("-");
						String cellValue = entries[0];
						int row = Integer.parseInt(entries[1]);
						int col = Integer.parseInt(entries[2]);
						XSSFRow sheetRow = sheet.getRow(row);
						if (sheetRow == null) {
							sheetRow = sheet.createRow(row);
						}
						XSSFCell cell = sheetRow.getCell(col);
						if (cell == null) {
							cell = sheetRow.createCell(col);
						}
						cell.setCellValue(cellValue);
						sheet.getRow(row).getCell(col).setCellValue(cellValue);
					}
				}

				fis.close();
				FileOutputStream fos = new FileOutputStream(new File(filePath));
				workbook.write(fos);
				fos.close();
				System.out.println("Data is entered into excel sheet");
			} catch (IOException e) {
				System.out.println("Unable to enter and save to " + filePath);
				e.printStackTrace();
			}
		}
	}

	public static boolean verifyValueinJson(String path) {
		try {

			String[] splitedText = path.split("--");
			String[] splitKey = splitedText[1].split("\\.");
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(splitedText[0]));
			JSONArray jsonArray = (JSONArray) jsonObject.get(splitKey[0]);
			String value = jsonArray.get(0).toString();
			HashMap<String, Object> userResponseMap = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			userResponseMap = mapper.readValue(value, new TypeReference<HashMap<String, Object>>() {
			});
			String finalString = userResponseMap.get(splitKey[1]).toString();
			ExtentCucumberAdapter.addTestStepLog(finalString + " length is : " + finalString.length());
			if (finalString.length() == Integer.parseInt(splitedText[2])) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			ExtentCucumberAdapter.addTestStepLog("Error : " + e);
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	public static boolean ReadPDF(String url, String pdf) {
		try {
			URL testURL = new URL(url);

			RandomAccessBufferedFileInputStream testFile = new RandomAccessBufferedFileInputStream(
					testURL.openStream());
			PDFParser testPDF = new PDFParser(testFile);
			testPDF.parse();
			String testText = new PDFTextStripper().getText(testPDF.getPDDocument());
			if (testText.contains(pdf)) {
				System.out.println("TestText is present");
				ExtentCucumberAdapter.addTestStepLog("PDF content : " + testText + " , Expected content : " + pdf);
				testFile.close();
				return true;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			ExtentCucumberAdapter.addTestStepLog("Error : " + e);
		}
		return false;
	}

	public static void takescrenshot() {
		if (WebBrowser.boolEachstepScreenshot) {
			driver = WebBrowser.getBrowser();
			String dest = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
			try {
				ExtentCucumberAdapter.addTestStepScreenCaptureFromPath("data:image/jpg;base64, " + dest);
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
	}

	public static boolean AsecndingNumber(List<String> options) {
		boolean orderStatus = false;
		List<String> tempList = (List<String>) options.stream().sorted().collect(Collectors.toList());
		String englishRules1 = (" < 0 < 1 < 2 < 3 < 4 < 5 < 6 < 7 < 8 < 9");
		RuleBasedCollator rbc;
		try {
			rbc = new RuleBasedCollator(englishRules1);
			rbc.setStrength(Collator.PRIMARY);
			Collections.sort(tempList, rbc);
			System.out.println("After sorting tempList :" + tempList);
			orderStatus = options.equals(tempList);
			System.out.println(" orderStatus : " + orderStatus);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return orderStatus;
	}

	public static boolean GetdatafromDBandverifyosofdevice(String query, String graphValue) {
		boolean isverified = false;
		try {
			RedShiftUtil redShiftUtil = new RedShiftUtil();
			List resultList = redShiftUtil.getExcelData(query);
			for (int i = 0; i < resultList.size(); i++) {
				HashMap row = (HashMap) resultList.get(i);
				for (Object mapVal : row.values()) {
					System.out.println(mapVal.toString());
					if ((graphValue).contains(",")) {
						graphValue = ((String) graphValue).replaceAll("[^a-zA-Z0-9]", "");
					} else if ((graphValue).contains("%")) {
						graphValue = graphValue.replace("%", "");
					}
					ExtentCucumberAdapter
							.addTestStepLog("UI Value: " + graphValue + ", DB Value: " + mapVal.toString());

					if (graphValue.equals(mapVal.toString())) {
						isverified = true;
					} else {
						isverified = false;
					}
				}
			}
		} catch (Exception ex) {
			System.out.println("No data found");
			isverified = false;
		}
		if (!isverified) {
			ExtentCucumberAdapter.addTestStepLog("Assertion Failed");
		}
		return isverified;
	}

	public static boolean verifyexceldatafromdb(String sheetName) {
		try {
			sheetName = CommonUtil.getData(sheetName);// 0049 List of all possible combinations - Lists method
														// Complete.xlsx--1M1S
			String[] splitSheet = sheetName.split("--");
			String filepath = System.getProperty("user.dir") + "\\src\\test\\java\\attachments\\" + splitSheet[0];// filename

			FileInputStream input = new FileInputStream(filepath);
			XSSFWorkbook workbook = new XSSFWorkbook(filepath);
			XSSFSheet sheet = workbook.getSheet(splitSheet[1]);// name of excel sheet

			WebElement elem = WebBrowserUtil.getSingleElementByXpath("//span[text()='Reset']");
			elem.click();

			int rows = sheet.getLastRowNum();
			String month = "";
			String src = "";
			String dst = "";
			String toverify = "";

			List<Boolean> myList = new ArrayList<>();
			for (int r = 1; r <= rows; r++) {
				XSSFRow row = sheet.getRow(r);
				try {
					month = row.getCell(0).getStringCellValue();
				} catch (Exception e1) {
					month = null;
				}
				try {
					src = row.getCell(1).getStringCellValue();
				} catch (Exception e2) {
					src = null;
				}
				try {
					dst = row.getCell(2).getStringCellValue();
				} catch (Exception e3) {
					dst = null;
				}

				try {
					toverify = row.getCell(3).getStringCellValue();

				} catch (Exception e4) {
					toverify = null;
				}

				String queryExtension = "";

				if ((month != null)) {
					queryExtension = queryExtension + "and (";
					String[] multmnth = month.split(",");
					for (int i = 0; i < multmnth.length; i++) {
						WebElement nametxt = driver
								.findElement(By.xpath("(//div[@class='ant-select-selector']//input)[1]"));
						nametxt.sendKeys(multmnth[i]);
						nametxt.sendKeys(Keys.ENTER);
						queryExtension = queryExtension + "Month= '" + multmnth[i] + "'";
						if (multmnth.length > 1 && i < (multmnth.length - 1)) {
							queryExtension = queryExtension + " OR ";
						}
					}
					ExtentCucumberAdapter.addTestStepLog("Month: " + month);
					queryExtension = queryExtension + ")";
				}

				if ((src != null)) {
					queryExtension = queryExtension + "and (";
					String[] multsrc = src.split(",");
					for (int i = 0; i < multsrc.length; i++) {
						WebElement srctxt = driver
								.findElement(By.xpath("(//div[@class='ant-select-selector']//input)[2]"));
						srctxt.sendKeys(multsrc[i]);
						srctxt.sendKeys(Keys.ENTER);
						queryExtension = queryExtension + "Source= '" + multsrc[i] + "'";
						if (multsrc.length > 1 && i < (multsrc.length - 1)) {
							queryExtension = queryExtension + " OR ";
						}
					}
					ExtentCucumberAdapter.addTestStepLog("Source: " + src);
					queryExtension = queryExtension + ")";
				}
				if ((dst != null)) {
					queryExtension = queryExtension + "and (";
					String[] multdst = dst.split(",");
					for (int i = 0; i < multdst.length; i++) {
						WebElement dsttxt = driver
								.findElement(By.xpath("(//div[@class='ant-select-selector']//input)[3]"));
						dsttxt.sendKeys(multdst[i]);
						dsttxt.sendKeys(Keys.ENTER);
						queryExtension = queryExtension + "Destination= '" + multdst[i] + "'";
						if (multdst.length > 1 && i < (multdst.length - 1)) {
							queryExtension = queryExtension + " OR ";
						}
					}
					ExtentCucumberAdapter.addTestStepLog("Destination: " + dst);
					queryExtension = queryExtension + ")";
				}

				driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
				WebElement elems = WebBrowserUtil.getSingleElementByXpath("//span[text()='Submit']");
				elems.click();
				driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
				if (toverify.contains("totalTrips")) {
					String totaltripsdetails = WebBrowserUtil
							.getSingleElementByXpath(YMLUtil.getYMLObjectRepositoryData("MILE.TotalTripslLabelXpath"))
							.getText();
					String query = CommonUtil.getData("TotalTripsQuery");
					query = CommonUtil.getXMLData(Paths.get(path, "src", "test", "java", "DBSettings.xml").toString(),
							query);
					String dbquery = query.replace("@Dynamic_data", queryExtension);
					myList.add(WebBrowserUtil.GetdatafromDBandverifyosofdevice(dbquery, totaltripsdetails));
				}

				if (toverify.contains("validTrips")) {
					String validtripsdetails = WebBrowserUtil
							.getSingleElementByXpath(YMLUtil.getYMLObjectRepositoryData("MILE.ValidTripsLabelXpath"))
							.getText();
					String query = CommonUtil.getData("ValidTripsQuery");
					query = CommonUtil.getXMLData(Paths.get(path, "src", "test", "java", "DBSettings.xml").toString(),
							query);
					String dbquery = query.replace("@Dynamic_data", queryExtension);
					myList.add(WebBrowserUtil.GetdatafromDBandverifyosofdevice(dbquery, validtripsdetails));
				}
				if (toverify.contains("openTrips")) {
					String opentripsdetails = WebBrowserUtil
							.getSingleElementByXpath(YMLUtil.getYMLObjectRepositoryData("MILE.OpenTripsLabelXpath"))
							.getText();
					String query = CommonUtil.getData("OpenTripsQuery");
					query = CommonUtil.getXMLData(Paths.get(path, "src", "test", "java", "DBSettings.xml").toString(),
							query);
					String dbquery = query.replace("@Dynamic_data", queryExtension);
					myList.add(WebBrowserUtil.GetdatafromDBandverifyosofdevice(dbquery, opentripsdetails));

				}
				if (toverify.contains("closedTrips")) {
					String closedtripsdetails = WebBrowserUtil
							.getSingleElementByXpath(YMLUtil.getYMLObjectRepositoryData("MILE.ClosedTripsLabelXpath"))
							.getText();
					String query = CommonUtil.getData("ClosedTripsQuery");
					query = CommonUtil.getXMLData(Paths.get(path, "src", "test", "java", "DBSettings.xml").toString(),
							query);
					String dbquery = query.replace("@Dynamic_data", queryExtension);
					myList.add(WebBrowserUtil.GetdatafromDBandverifyosofdevice(dbquery, closedtripsdetails));
				}
				if (toverify.contains("geofenceClosures")) {
					String geofenceclosuresdetails = WebBrowserUtil.getSingleElementByXpath(
							YMLUtil.getYMLObjectRepositoryData("MILE.GeofenceClosuresLabelXpath")).getText();
					String query = CommonUtil.getData("GeofenceClosuresQuery");
					query = CommonUtil.getXMLData(Paths.get(path, "src", "test", "java", "DBSettings.xml").toString(),
							query);
					String dbquery = query.replace("@Dynamic_data", queryExtension);
					myList.add(WebBrowserUtil.GetdatafromDBandverifyosofdevice(dbquery, geofenceclosuresdetails));
				}
				if (toverify.contains("forcefulClosures")) {
					String forcefulclosuresdetails = WebBrowserUtil.getSingleElementByXpath(
							YMLUtil.getYMLObjectRepositoryData("MILE.ForcefulClosuresLabelXpath")).getText();

					String query = CommonUtil.getData("ForcefulClosuresQuery");
					query = CommonUtil.getXMLData(Paths.get(path, "src", "test", "java", "DBSettings.xml").toString(),
							query);
					String dbquery = query.replace("@Dynamic_data", queryExtension);
					myList.add(WebBrowserUtil.GetdatafromDBandverifyosofdevice(dbquery, forcefulclosuresdetails));
				}
				if (toverify.contains("delayTrips")) {
					String delaytripsdetails = WebBrowserUtil
							.getSingleElementByXpath(YMLUtil.getYMLObjectRepositoryData("MILE.DelayTripsLabelXpath"))
							.getText();

					String query = CommonUtil.getData("DelayTripsQuery");
					query = CommonUtil.getXMLData(Paths.get(path, "src", "test", "java", "DBSettings.xml").toString(),
							query);
					String dbquery = query.replace("@Dynamic_data", queryExtension);
					myList.add(WebBrowserUtil.GetdatafromDBandverifyosofdevice(dbquery, delaytripsdetails));
				}
				if (toverify.contains("averagedailydistance")) {
					String averagedailydistancedetails = WebBrowserUtil
							.getSingleElementByXpath(
									YMLUtil.getYMLObjectRepositoryData("MILE.AverageDailyDistanceLabelXpath"))
							.getText();
					String query = CommonUtil.getData("AverageDailyDistanceQuery");
					query = CommonUtil.getXMLData(Paths.get(path, "src", "test", "java", "DBSettings.xml").toString(),
							query);
					String dbquery = query.replace("@Dynamic_data", queryExtension);
					myList.add(WebBrowserUtil.GetdatafromDBandverifyosofdevice(dbquery, averagedailydistancedetails));
				}
				if (toverify.contains("averagedaystodeliver")) {
					String averagedaystodeliverdetails = WebBrowserUtil
							.getSingleElementByXpath(
									YMLUtil.getYMLObjectRepositoryData("MILE.AverageDaysToDeliverLabelXpath"))
							.getText();
					String query = CommonUtil.getData("AverageDaystoDeliverQuery");
					query = CommonUtil.getXMLData(Paths.get(path, "src", "test", "java", "DBSettings.xml").toString(),
							query);
					String dbquery = query.replace("@Dynamic_data", queryExtension);
					myList.add(WebBrowserUtil.GetdatafromDBandverifyosofdevice(dbquery, averagedaystodeliverdetails));
				}
				if (toverify.contains("otif")) {
					String otifdetails = WebBrowserUtil
							.getSingleElementByXpath(YMLUtil.getYMLObjectRepositoryData("MILE.OtifLabelXpath"))
							.getText();
					String query = CommonUtil.getData("OtifQuery");
					query = CommonUtil.getXMLData(Paths.get(path, "src", "test", "java", "DBSettings.xml").toString(),
							query);
					String dbquery = query.replace("@Dynamic_data", queryExtension);
					myList.add(WebBrowserUtil.GetdatafromDBandverifyosofdevice(dbquery, otifdetails));
				}
				if (toverify.contains("carbonemission")) {
					String carbonemissiondetails = WebBrowserUtil.getSingleElementByXpath(
							YMLUtil.getYMLObjectRepositoryData("MILE.CarbonEmissionLabelXpath")).getText();
					String query = CommonUtil.getData("CarbonEmissionQuery");
					query = CommonUtil.getXMLData(Paths.get(path, "src", "test", "java", "DBSettings.xml").toString(),
							query);
					String dbquery = query.replace("@Dynamic_data", queryExtension);
					myList.add(WebBrowserUtil.GetdatafromDBandverifyosofdevice(dbquery, carbonemissiondetails));
				}
				WebElement elex = WebBrowserUtil.getSingleElementByXpath("//span[text()='Reset']");
				elex.click();
				driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
			}

			for (int i = 0; i < myList.size(); i++) {
				if (!myList.get(i)) {
					return false;
				}
			}

			workbook.close();
			input.close();
		} catch (Exception e) {
			ExtentCucumberAdapter.addTestStepLog("Error: " + e);
			return false;
		}
		return true;
	}

	public static boolean CompareAndVerifyImage(String imageName) {
		boolean verified = false;
		driver = WebBrowser.getBrowser();
		File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			boolean enableCompareImage = Boolean.parseBoolean(
					CommonUtil.getXMLData(Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(),
							"EnableCompareImage"));

			String compareImages = "src/test/java/Images/Compare/";
			File file = new File(compareImages);
			file.mkdirs();

			String baseImages = "src/test/java/Images/Baseline/";
			File file1 = new File(baseImages);
			file1.mkdirs();

			if (enableCompareImage == false) {
				File dest = new File(baseImages + imageName + ".jpeg");
				FileUtils.copyFile(source, dest);
				return verified;
			} else {
				File dest = new File(compareImages + imageName + ".jpeg");
				FileUtils.copyFile(source, dest);
				BufferedImage imgA = ImageIO.read(new File(compareImages + imageName + ".jpeg"));

				DataBuffer dbA = imgA.getData().getDataBuffer();
				int sizeA = dbA.getSize();

				BufferedImage imgB = ImageIO.read(new File(baseImages + imageName + ".jpeg"));
				DataBuffer dbB = imgB.getData().getDataBuffer();
				int sizeB = dbB.getSize();
				if (sizeA == sizeB) {
					for (int i = 0; i < sizeA; i++) {
						if (dbA.getElem(i) != dbB.getElem(i)) {
							ExtentCucumberAdapter.addTestStepLog("Images are not same");
							return verified;
						}
					}
					ExtentCucumberAdapter.addTestStepLog("Images are same");
					verified = true;
				} else {
					ExtentCucumberAdapter.addTestStepLog("Images are not same");
					verified = false;
				}
			}
		} catch (Exception ex) {
			verified = false;
			System.err.println("Error: " + ex.getMessage());
		}
		return verified;

	}

	public static String getlatestfile(String ext) {
		File theNewestFile = null;
		try {
			String path = System.getProperty("user.dir");

			File dir = new File(path);
			FileFilter fileFilter = new WildcardFileFilter("*." + ext);
			File[] files = dir.listFiles(fileFilter);
			if (files.length > 0) {
				/** The newest file comes first **/
				Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
				theNewestFile = files[0];
				System.out.println("theNewestFile is :------" + theNewestFile);
			}

		} catch (Exception e) {
			ExtentCucumberAdapter.addTestStepLog("Error: " + e);
			throw new CustomException(e.getMessage(), e);
		}
		return theNewestFile.toString();
	}

	public static String FormatDate(String data) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		LocalDate currentDate = LocalDate.now();

		if ("current date".equalsIgnoreCase(data)) {
			return currentDate.format(formatter);
		}

		boolean isAddition = data.contains("+");
		boolean isSubtraction = data.contains("-");

		if (isAddition || isSubtraction) {
			return processDateAdjustment(data, currentDate, isAddition);
		}
		return "";
	}

	private static String processDateAdjustment(String data, LocalDate currentDate, boolean isAddition) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		String[] dateParts = data.split("\\+|-", 2);
		if (dateParts.length < 2) {
			return "";
		}

		String[] timeUnits = dateParts[1].trim().split(" ", 2);
		if (timeUnits.length < 2) {
			return "";
		}

		int amount;
		try {
			amount = Integer.parseInt(timeUnits[0]);
		} catch (NumberFormatException e) {
			return "";
		}

		String unit = timeUnits[1].toLowerCase();
		LocalDate adjustedDate = currentDate; // Default value

		switch (unit) {
		case "days":
			adjustedDate = isAddition ? currentDate.plusDays(amount) : currentDate.minusDays(amount);
			break;
		case "weeks":
			adjustedDate = isAddition ? currentDate.plusWeeks(amount) : currentDate.minusWeeks(amount);
			break;
		case "months":
			adjustedDate = isAddition ? currentDate.plusMonths(amount) : currentDate.minusMonths(amount);
			break;
		case "years":
			adjustedDate = isAddition ? currentDate.plusYears(amount) : currentDate.minusYears(amount);
			break;
		default:
			throw new IllegalArgumentException("Invalid time unit: " + unit);
		}

		return adjustedDate.format(formatter);
	}

// To edit CSV file with specified value for the specified header
// The values in testdata file should be passed as "sample-csv.csv,##1,Exam_Code:randomnumber_10,ExamCode_withModifier:@randomtext,##2,modality:JK,bodyRegion:Head,exam_code_short_name:Short Name,##3,exam_code_med_name:Text,dateStart:current date,dateEnd:current date+5 Years"
	@SuppressWarnings("deprecation")
	public static void EditCSVfile(String values) {
		String[] splitValues1 = values.split("##");

		String filePath = Paths.get(System.getProperty("user.dir"), splitValues1[0].split(",")[0]).toString();

		try {
			// Read existing CSV data
			FileReader reader = new FileReader(filePath);
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
			List<CSVRecord> records = csvParser.getRecords();
			List<String> headers = new ArrayList<>(csvParser.getHeaderMap().keySet());

			// Create a temporary list to hold the modified records
			List<List<String>> modifiedRecords = new ArrayList<>();
			String value = "";
			int i = 0;
			// Iterate over the records and append text to the specified column
			for (CSVRecord record : records) {
				i++;
				Map<String, String> modifications = new HashMap<>();
				for (String item : splitValues1) {
					String[] items = item.split(",");
					if (items[0].equals(String.valueOf(i))) { // if the specified row(items[0]) is same as the current
																// row (i)?
						// Create new string "items" by removing the first element
						String[] dataList = new String[items.length - 1];
						System.arraycopy(items, 1, dataList, 0, dataList.length);
						for (String Data : dataList) {
							String[] splitData = Data.split(":");
							// Modify the value as needed
							String modifiedValue = GetRequiredValue(splitData[1]);
							if (splitData[0].equals("Exam_Code"))
								CommonUtil.setCopiedText(modifiedValue);
							modifications.put(splitData[0], modifiedValue);
						}
					}
				}
				List<String> newRecord = new ArrayList<>();
				for (String header : headers) {
					if (modifications.containsKey(header))
						value = modifications.get(header);
					else
						value = record.get(header);
					newRecord.add(value);
				}
				modifiedRecords.add(newRecord); // modifiedRecords=> To get modifiedRecord
			}

			csvParser.close();
			reader.close();

			// Write the modified data back to the original file
			FileWriter writer = new FileWriter(filePath);
			CSVPrinter csvPrinter = new CSVPrinter(writer,
					CSVFormat.DEFAULT.withHeader(headers.toArray(new String[0])));

			for (List<String> modifiedRecord : modifiedRecords) {
				csvPrinter.printRecord(modifiedRecord);
			}

			csvPrinter.close();
			writer.close();

		} catch (IOException e) {
			System.out.println("Error in editing CSV file" + e);
			throw new CustomException(e.getMessage(), e);

		}
	}

// If data contains "date" format date as required else get random number or random text or retain the data
	public static String GetRequiredValue(String data) {
		if (data.contains("date")) {
			data = WebBrowserUtil.FormatDate(data);
			return data;
		} else
			return CommonUtil.getData(data);
	}

// read specified value of data in CSV file
// The value is specified in testdata file as "sample-csv.csv,##2,exam_code_short_name"	 ##2=> 2nd row excluding header row	
	@SuppressWarnings("deprecation")
	public static boolean ReadCSVfile(String values) {
		values = CommonUtil.getData(values);
		boolean isVerified = false;
		String[] splitValues = values.split(",");
		String expectedValue = splitValues[2];

		String[] splitValues1 = values.split("##");

		String filePath = Paths.get(System.getProperty("user.dir"), splitValues1[0].split(",")[0]).toString();
		String value = "";
		try {
			// Read existing CSV data
			FileReader reader = new FileReader(filePath);
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
			List<CSVRecord> records = csvParser.getRecords();
			List<String> headers = new ArrayList<>(csvParser.getHeaderMap().keySet());

			// Create a temporary list to hold the modified records
			int i = 0;
			// Iterate over the records and append text to the specified column
			for (CSVRecord record : records) {
				i++;
				for (String item : splitValues1) {
					String[] items = item.split(",");
					if (items[0].equals(String.valueOf(i))) { // if the specified row(items[0]) is same as the current
																// row (i)?
						for (String header : headers) {
							if (header.equals(items[1])) {
								value = record.get(header);
								break;
							}
						}
					}
				}
			}
			csvParser.close();
			reader.close();
		} catch (IOException e) {
			System.out.println("Error in editing CSV file" + e);
			throw new CustomException(e.getMessage(), e);
		}

		isVerified = value.equals(expectedValue);
		ExtentCucumberAdapter
				.addTestStepLog("Value read from CSV file : " + value + " ;  Expected value : " + expectedValue);
		return isVerified;
	}

	public static void ScrollRandomlyByNumber(String xpath, String identifier, String text) {
		int i = 0;
		boolean staleElement = true;
		driver = WebBrowser.getBrowser();
		int num = Integer.parseInt(text);
		try {
			while (i < 4 && staleElement) {
				WebElement element = WebBrowserUtil.findElement(xpath, identifier);
				try {

					JavascriptExecutor js = (JavascriptExecutor) driver;
					for (int j = 0; j < num; j++) {
						js.executeScript("arguments[0].scrollTop += 350;", element);
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					staleElement = false;
				} catch (Exception e) {
					i++;
					if (i == 3) {
						throw new CustomException("Scroll unsuccessful" + e.getMessage());
					}
				}
			}
		} catch (Exception ex) {
			throw new CustomException("Scroll  unsuccessful" + ex.getMessage());
		}
	}

	public static void deleteFile(String filePath) {
		try {
			filePath = CommonUtil.getData(filePath);
			File fileToBeDeleted = new File(filePath);
			if (fileToBeDeleted.exists()) {
				if (fileToBeDeleted.delete()) {
					ExtentCucumberAdapter.addTestStepLog("Deleted the file: " + fileToBeDeleted.getName());
				} else {
					ExtentCucumberAdapter.addTestStepLog("Failed to delete the file.");
				}
			} else {
				ExtentCucumberAdapter.addTestStepLog("File does not exists.");
			}
		} catch (Exception e) {
			throw new CustomException("Failed to delete the file." + e.getMessage());
		}
	}

	public static boolean verifyPDFData(String filePath) {
		boolean isVerified = false;
		String actualText = "";
		try {
			filePath = CommonUtil.getData(filePath);
			String[] data = filePath.split(",");
			filePath = data[0];
			if (!(data[0].contains(":"))) {
				filePath = WebBrowserUtil.getlatestfile("pdf");
			}
			actualText = common.PDFUtil.getText(filePath);
			System.out.println("PDF data: " + actualText);
			ExtentCucumberAdapter.addTestStepLog("PDF data: " + actualText);
			String[] expectedText = data[1].split("--");
			String verifyText = "";
			if (actualText != null && !actualText.trim().isEmpty()) {
				for (int i = 0; i < expectedText.length; i++) {
					if (actualText.contains(expectedText[i])) {
						verifyText = verifyText + "true,";
						System.out.println(expectedText[i] + " is present in pdf data");
						ExtentCucumberAdapter.addTestStepLog(expectedText[i] + " is present in pdf data");
					} else {
						ExtentCucumberAdapter.addTestStepLog(expectedText[i] + " is NOT present in pdf data");
						verifyText = verifyText + "false,";
					}
				}
			}
			if (verifyText.contains("false")) {
				isVerified = false;
			} else {
				isVerified = true;
			}
			return isVerified;
		} catch (Exception ex) {
			return isVerified;
		}
	}

	public static void selectByCoordinates(String text) {

		driver = WebBrowser.getBrowser();
		try {
			int yaxis = 0;
			int xaxis = 0;
			String desiredPath = System.getProperty("user.dir");
			File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			// Define the destination file
			File destinationFile = new File("screenshot.png");

			try {
				// Copy the screenshot to the destination file
				Files.copy(screenshot.toPath(), destinationFile.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
			String screenshotPath = destinationFile.getAbsolutePath();
			String pythonPath = desiredPath + "/detect.py";
			String[] textSplit = text.split(",");
			String imagePath = desiredPath + "\\src\\test\\java\\attachments\\" + textSplit[0];
			if (!textSplit[1].isEmpty()) {
				yaxis = Integer.parseInt(textSplit[1]);
			}
			if (!textSplit[2].isEmpty()) {
				xaxis = Integer.parseInt(textSplit[2]);
			}
			// Load the screenshot
			File file = new File(screenshotPath);
			BufferedImage screenshot1 = ImageIO.read(file);

			List<List<Integer>> resultList = GetCoordinatesByImage(pythonPath, screenshotPath, imagePath);

			List<Integer> coordinates = resultList.get(0);
			int x = coordinates.get(0) + xaxis;
			int y = coordinates.get(1) + yaxis;

			Robot robot = new Robot();
			robot.setAutoDelay(1000);

			// Move the mouse to the coordinates and click
			robot.mouseMove(x, y);

			robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
			try {
				Thread.sleep(100); // 100 milliseconds delay
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
			robot.delay(2000);

			destinationFile.delete();

		} catch (Exception ex) {
			log.error(ex.getMessage());
		}

	}

	public static boolean verifyByImage(String text) {

		driver = WebBrowser.getBrowser();
		boolean isVerifed = false;
		try {
			String desiredPath = System.getProperty("user.dir");
			File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			// Define the destination file
			File destinationFile = new File("screenshot.png");
			try {
				// Copy the screenshot to the destination file
				Files.copy(screenshot.toPath(), destinationFile.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
			String screenshotPath = destinationFile.getAbsolutePath();
			String pythonPath = desiredPath + "\\detect.py";
			String imagePath = desiredPath + "\\src\\test\\java\\attachments\\" + text;

			// Load the screenshot
			File file = new File(screenshotPath);
			BufferedImage screenshot1 = ImageIO.read(file);

			List<List<Integer>> resultList = GetCoordinatesByImage(pythonPath, screenshotPath, imagePath);
			destinationFile.delete();
			List<Integer> coordinates = resultList.get(0);
			int x = coordinates.get(0);
			int y = coordinates.get(1);
			if (x != 0 && y != 0) {
				isVerifed = true;
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		return isVerifed;

	}

	public static List<List<Integer>> GetCoordinatesByImage(String pythonScriptPath, String sccreenshot,
			String target) {
		// Path to the Python script
		// Construct the command
		String[] command = { "python", pythonScriptPath, sccreenshot, target };
		List<List<Integer>> resultList = new ArrayList<>();
		try {
			// Start the process
			ProcessBuilder processBuilder = new ProcessBuilder(command);
			Process process = processBuilder.start();

			// Wait for the process to complete
			int exitCode = process.waitFor();

			if (exitCode != 0) {
				BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				StringBuilder errorOutput = new StringBuilder();
				String line;
				while ((line = errorReader.readLine()) != null) {
					errorOutput.append(line).append("\n");
				}
				System.err.println("Error output: " + errorOutput.toString());
			} else {
				String desiredPath = System.getProperty("user.dir");
				// Read the JSON file with detections
				String jsonFilePath = desiredPath + "\\detect.json";
				resultList = readDetectionsFromJson(jsonFilePath);
			}

		} catch (IOException | InterruptedException | JSONException e) {
			e.printStackTrace();
		}

		return resultList;
	}

	private static List<List<Integer>> readDetectionsFromJson(String filePath) throws IOException, JSONException {
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		StringBuilder jsonContent = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			jsonContent.append(line);
		}
		reader.close();

		// Parse the JSON content
		JSONArray jsonArray = new JSONArray(jsonContent.toString());
		List<List<Integer>> detections = new ArrayList<>();
		System.out.println(jsonArray.length());
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONArray innerArray = jsonArray.getJSONArray(i);
			List<Integer> detection = new ArrayList<>();
			for (int j = 0; j < innerArray.length(); j++) {
				detection.add((int) innerArray.getDouble(j));
			}
			detections.add(detection);
		}
		return detections;
	}

	public static void captureAndCompressScreenshot() {
		driver = WebBrowser.getBrowser();
		try {
			// Take the screenshot and read it as a BufferedImage
			File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			BufferedImage image = ImageIO.read(screenshotFile);

			// Get the original image size
			long originalFileSize = screenshotFile.length(); // in bytes
			// If the image size is already below the threshold, skip compression
			if (originalFileSize <= 70 * 1024) { // If <= 70 KB
				sce.attach(java.nio.file.Files.readAllBytes(screenshotFile.toPath()), "image/jpeg", "image");
				return;
			}

			// Initialize compression settings
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			float compressionQuality = 0.6f; // Starting compression quality
			javax.imageio.ImageWriter jpegWriter = ImageIO.getImageWritersByFormatName("jpg").next();
			javax.imageio.ImageWriteParam jpegParams = jpegWriter.getDefaultWriteParam();
			jpegParams.setCompressionMode(javax.imageio.ImageWriteParam.MODE_EXPLICIT);

			byte[] bestCompressedScreenshot = null;
			long bestFileSize = originalFileSize;
			boolean withinRange = false;

			// Compression loop
			while (!withinRange) {
				outputStream.reset();
				jpegParams.setCompressionQuality(compressionQuality);

				try (ImageOutputStream ios = ImageIO.createImageOutputStream(outputStream)) {
					jpegWriter.setOutput(ios);
					jpegWriter.write(null, new javax.imageio.IIOImage(image, null, null), jpegParams);
				}

				long compressedFileSize = outputStream.size();
				// Store the best compressed version
				if (compressedFileSize < bestFileSize) {
					bestCompressedScreenshot = outputStream.toByteArray();
					bestFileSize = compressedFileSize;
				}

				// Stop if within target size
				if (compressedFileSize <= 70 * 1024) {
					withinRange = true;
				} else if (compressionQuality > 0.05f) {
					compressionQuality -= 0.03f; // Reduce quality gradually
				} else {
					break;
				}
			}
			jpegWriter.dispose();

			// Attach the best available image
			if (bestCompressedScreenshot != null && bestFileSize <= originalFileSize) {
				sce.attach(bestCompressedScreenshot, "image/jpeg", "image");
			} else {
				sce.attach(java.nio.file.Files.readAllBytes(screenshotFile.toPath()), "image/jpeg", "image");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}