package common;

import java.nio.file.Paths;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import io.percy.selenium.Percy;

public class Constants {
	public static WebDriver browser;
	public static int Number_of_Iteration = 5;
	public static final String PROJECT_PATH = System.getProperty("user.dir");
	public static final String APPLICATION_SETTINGS = "ApplicationSettings.xml";
	public static final String APPLICATION_SETTING_PATH = Paths
			.get(PROJECT_PATH, "src", "test", "java", APPLICATION_SETTINGS).toString();
	public static String Path = System.getProperty("user.dir");
	public static String Application_Settings = "ApplicationSettings.xml";
	public static String App_Settings_Path = Paths.get(Path, "src", "test", "java", Application_Settings).toString();
}
