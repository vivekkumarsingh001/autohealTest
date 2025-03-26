package common;

import java.nio.file.Paths;

public class Constants {
    public static final int NUMBER_OF_ITERATION = 5;
    public static final String PROJECT_PATH = System.getProperty("user.dir");
    public static final String APPLICATION_SETTINGS = "ApplicationSettings.xml";
    public static final String APPLICATION_SETTING_PATH = Paths.get(PROJECT_PATH, "src", "test", "java", APPLICATION_SETTINGS).toString();
    public static final String OBJECT_PATH = Paths.get(PROJECT_PATH, "src", "test", "java", "ObjectRepository.yml").toString();
    public static final String TESTDATA_PATH = Paths.get(PROJECT_PATH, "src", "test", "java", "TestData.yml").toString();
    private Constants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }
}
