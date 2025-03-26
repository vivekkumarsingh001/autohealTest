package common;

import java.io.*;
import java.util.*;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class YMLUtil {

	static ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
	static Map<String, Object> ymlData;
	static String YMLNode = "";
	static List<Object> ymlObject = new ArrayList<>();
	static Object ymlObjectRepo;
	static Object ymlpayloadObject;
	static final Logger log = Logger.getLogger(YMLUtil.class);

	public static String getYMLData(String key) {
		String output = "";

		try {
			Map map = (Map) ymlObject.get(0);
			try {
				output = ((Map) map).get(key).toString();
			} catch (Exception e) {
				output = ((Map) map.get(YMLNode)).get(key).toString();
			}
			if (output == null || output.isEmpty()) {
				output = ((Map) map.get(YMLNode)).get(key).toString();
			}
			if (output == null || output.isEmpty()) {
				output = key;
			}
		} catch (Exception ex) {
			output = key;
		}
		if (output.toLowerCase().contains("multiplefilesearch.")) {
			String[] splitKey = output.split("filesearch.");
			String mainKey = splitKey[1];
			for (int i = 1; i < ymlObject.size(); i++) {
				try {
					Map map = (Map) ymlObject.get(i);
					try {
						output = ((Map) map).get(mainKey).toString();
					} catch (Exception e) {
						output = ((Map) map.get(YMLNode)).get(mainKey).toString();
					}
					if (output == null || output.isEmpty()) {
						output = ((Map) map.get(YMLNode)).get(mainKey).toString();
					}
					if (output == null || output.isEmpty()) {
						output = key;
					} else {
						break;
					}
				} catch (Exception ex) {
					output = key;
				}
			}
		}
		return output;

	}

	public static String getYMLObjectRepositoryData(String key) {
		String output = "";
		try {
			String[] keys = key.split("[.]", 0);
			if (keys.length > 1) {
				Map map = (Map) ymlObjectRepo;
				output = ((Map) map.get(keys[0])).get(keys[1]).toString();
			} else {
				Map map = (Map) ymlObjectRepo;
				output = ((Map) map).get(key).toString();
			}
			if (output == null || output.isEmpty()) {
				output = key;
			}
		} catch (Exception ex) {
			output = key;
		}
		AutoHealUtil.setXpath(output);
		AutoHealUtil.setXpathKey(key);
		try {
			String data = HtmlElementReader.getTargetByLocator(output).replace("\'", "\"");
			AutoHealUtil.setTarget(data);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		return output;
	}

	public static void updateYML(String key, String value) {
		try {
			if (ymlData.containsKey(key)) {
				ymlData.replace(key, value);
			} else {
				ymlData.put(key, value);
			}
			objectMapper.writeValue(new File("src/test/java/TestData.yml"), ymlData);
		} catch (Exception ex) {
			log.error(ex.getMessage());

		}
	}

	public static void loadYML(String path, String node) {

		File dir = new File(path);
		FileFilter fileFilter = new WildcardFileFilter("*.yml*");
		File[] files = dir.listFiles(fileFilter);
		for (int i = 0; i < files.length; i++) {
			try {
				if (files[i].getName().contains("TestData")) {
					YamlReader reader = new YamlReader(new FileReader(files[i]));
					ymlObject.add(reader.read());
					YMLNode = node;
				}
			} catch (Exception ex) {
				log.error(ex.getMessage());
			}
		}
	}

	public static void loadObjectRepoYML(String file) {
		try {
			YamlReader reader = new YamlReader(new FileReader(file));
			ymlObjectRepo = reader.read();
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
	}

	public static void readObjectRepoYML(String... files) {
		for (String file : files) {
			String fileName = file.substring(file.lastIndexOf("/") + 1); // Extract the filename
			try {
				Yaml yaml = new Yaml();
				FileInputStream inputStream = new FileInputStream(file);
				Object data = yaml.load(inputStream);
				log.info(fileName + " file is valid");
			} catch (Exception e) {
				log.error(fileName + " file is invalid: " + e.getMessage());
			}
		}
	}

	public static void PayloadYML(String file, String node) {

		try {
			YamlReader reader = new YamlReader(new FileReader(file));
			ymlpayloadObject = reader.read();
			Map map = (Map) ymlpayloadObject;
			int abc = ((Map) map).keySet().size();
			for (Object rule : ((Map) map).keySet()) {
				RestAssuredUtil.apiPayloadDictionary.put(rule.toString(), ((Map) map).get(rule).toString());
			}
			YMLNode = node;
		} catch (Exception ex) {
			log.error(ex.getMessage());

		}
	}

}
