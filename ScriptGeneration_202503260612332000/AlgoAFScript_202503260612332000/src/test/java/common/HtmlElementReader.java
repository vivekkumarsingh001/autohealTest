package common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.nio.file.Paths;
import java.util.*;

import org.apache.log4j.Logger;

public class HtmlElementReader {

	static final Logger log = Logger.getLogger(HtmlElementReader.class);

	public static String getTargetByLocator(String locator) {
		ObjectMapper objectMapper = new ObjectMapper();
		String targetValue = "";

		try {
			// Get the current working directory
			String currentDir = System.getProperty("user.dir");
			// Construct the path to the HtmlElement.json file
			File jsonFile = new File(
					Paths.get(currentDir, "src", "test", "java", "common", "HtmlElement.json").toString());

			// Check if the file exists, if not create an empty file
			if (!jsonFile.exists()) {
				log.info("HtmlElement.json not found. Creating an empty file.");

				// Ensure the parent directory exists
				jsonFile.getParentFile().mkdirs();

				// Create the empty file
				if (jsonFile.createNewFile()) {
					// Write an empty list to the newly created file
					objectMapper.writeValue(jsonFile, new ArrayList<HtmlElement>());
				} else {
					log.info("Failed to create HtmlElement.json file.");
				}
			}

			// Read the JSON file
			List<HtmlElement> elements = objectMapper.readValue(jsonFile, new TypeReference<List<HtmlElement>>() {
			});

			// Iterate through the list to find the matching locator
			for (HtmlElement element : elements) {
				if (element.getLocator().equals(locator)) {
					targetValue = element.getTarget();
					break;
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}

		// Escape double quotes in the targetValue
		if (targetValue != null) {
			targetValue = targetValue.replace("\"", "\\\"");
			return targetValue;
		} else {
			return "";
		}

	}

}