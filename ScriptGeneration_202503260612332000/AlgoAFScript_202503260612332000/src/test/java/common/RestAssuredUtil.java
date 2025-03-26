package common;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.Headers;
import io.restassured.http.Method;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class RestAssuredUtil {
	private static String APIURL;
	private static String MethodType;
	private static String RequestParameters;
	private static String APIHeaders;
	private static String APIendpoint;
	private static String BasicAuth;
	private static String APIparameter;
	private static String responseHeader;
	public static String apiCmdUrl;
	private static Response apiResponse;
	public static String appUrl;
	private static String OAuth2;
	private static String path = System.getProperty("user.dir");
	public static Map<String, String> apiPayloadDictionary = new HashMap<String, String>();
	private static Map<String, String> apiResponseDictionary = new HashMap<String, String>();
	static final Logger log = Logger.getLogger(RestAssuredUtil.class);

	public static void LaunchAPIApplication() {
		String autUrl = "";
		if (RestAssuredUtil.apiCmdUrl != null) {
			autUrl = RestAssuredUtil.apiCmdUrl;
		} else {
			autUrl = CommonUtil.getXMLData(Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(),
					"APIURL");
		}
		if (autUrl == null || autUrl.isEmpty()) {
			autUrl = CommonUtil.getXMLData(Paths.get(path, "src", "test", "java", "ApplicationSettings.xml").toString(),
					"URL");
		}
		RestAssuredUtil.setAPIURL(autUrl);
	}

	// To set value of apiResponse variable
	public static void setAPIResponse(Response text) {
		apiResponse = text;
	}

	// To get value of apiResponse
	public static Response getAPIResponse() {
		return apiResponse;
	}

	// To set value of apiResponse variable
	public static void setAPIOAuth2(String text) {
		OAuth2 = text;
	}

	// To get value of apiOAuth2
	public static String getAPIOAuth2() {
		return OAuth2;
	}

	// To set value of ApiResponseDict variable ,store the value in dictionary
	public static void setApiResponseDict(String key, String text) {
		if ("NA".equalsIgnoreCase(text)) {
			return;
		}
		if (text.contains("--")) {

			String[] verifystr = text.split("and");
			if (verifystr[0].contains("check --") && verifystr[0].contains("[]")) {

				String[] str = verifystr[0].replaceAll("\\{", "").replaceAll("check --", "").trim().split("\\.");
				int size = getAPIResponse().jsonPath().getList(str[0].replaceAll("\\[\\]", "")).size();

				String[] sanitizeStr = verifystr[0].replaceAll("\\{", "").replaceAll("check --", "").trim().split("=");
				String checkStrbool;
				String[] checkStrkey = new String[2];

				checkStrbool = sanitizeStr[1];
				if (sanitizeStr[0].contains("[]")) {
					checkStrkey = sanitizeStr[0].split("\\[\\]");
				}

				String sanitizegetStr = verifystr[1].replaceAll("get --", "").replaceAll("}", "").trim();
				String[] getstrkey = sanitizegetStr.split("\\[\\]");

				for (int i = 0; i < size - 1; i++) {
					if (getAPIResponse().jsonPath().getString(checkStrkey[0] + "[" + i + "]" + checkStrkey[1])
							.equalsIgnoreCase(checkStrbool)) {
						apiResponseDictionary.put("sku" + i,
								getAPIResponse().jsonPath().getString(getstrkey[0] + "[" + i + "]" + getstrkey[1]));
					}
				}
			}
		} else {
			if (text.contains("::")) {

				String[] actualKey = text.split("::");
				String[] splitText = actualKey[1].split(",");
				int count = 0;
				ExtentCucumberAdapter.addTestStepLog("Storing Values : ");
				for (int i = 0; i < splitText.length; i++) {
					String value = "";
					try {
						XmlPath xmlPath = new XmlPath(getAPIResponse().asString());
						value = xmlPath.getString(splitText[i]);
					} catch (Exception e) {
						value = getAPIResponse().jsonPath().getString(splitText[i]);
					}
					if (value.contains(",")) {
						value = value.replace(",", "#");
					}
					String keyy = actualKey[0] + "::" + splitText[i];
					ExtentCucumberAdapter.addTestStepLog(keyy + " : " + value);
					apiResponseDictionary.put(keyy, value);
				}

			} else {
				String[] splitText = text.split(",");
				int count = 0;
				ExtentCucumberAdapter.addTestStepLog("Storing Values : ");
				for (int i = 0; i < splitText.length; i++) {
					String value = "";
					try {
						XmlPath xmlPath = new XmlPath(getAPIResponse().asString());
						value = xmlPath.getString(splitText[i]);
					} catch (Exception e) {
						value = getAPIResponse().jsonPath().getString(splitText[i]);
					}
					if (value.contains(",")) {
						value = value.replace(",", "#");
					}
					ExtentCucumberAdapter.addTestStepLog(key + "." + splitText[i] + " : " + value);
					apiResponseDictionary.put(key + "." + splitText[i], value);
				}
			}
		}
	}

	// To get value of apiResponseDictionary, fetch the value in dictionary
	public static String getValueFromAPiResponse(String text) {

		try {
			for (String key : apiResponseDictionary.keySet()) {
				if (text.contains("@" + key)) {
					try {
						Integer.parseInt(apiResponseDictionary.get(key));
						if (text.contains("\"@" + key)) {
							text = text.replace("\"@" + key + "\"", apiResponseDictionary.get(key));
						} else {
							text = text.replace("@" + key + "", apiResponseDictionary.get(key));
						}
					} catch (NumberFormatException e) {
						text = text.replace("@" + key, apiResponseDictionary.get(key));
					}
				}
			}
			return text;
		} catch (Exception e) {
			return text;
		}
	}

	// To get value of ApiResponseDict variable ,fetch the value in dictionary
	public static String getApiResponseDict(String text) {

		return apiResponseDictionary.get(text);
	}

	// To set vale of APIURL for API execution
	public static void setAPIURL(String text) {
		APIURL = text.replaceAll("'", "");
	}

	// To get value of APIURL for API execution
	public static String getAPIURL() {
		return APIURL;
	}

	// To set value of MethodType for API execution
	public static void setMethodType(String text) {
		MethodType = text;
	}

	// To get value of MethodType for API execution
	public static String getMethodType() {
		return MethodType;
	}

	// To set value of RequestParameters for API execution
	public static void setRequestParameters(String text) {
		RequestParameters = text;
	}

	// To get value of RequestParameters for API execution
	public static String getRequestParameters() {
		return RequestParameters;
	}

	// To set value of APIHeaders for API execution
	public static void setAPIHeaders(String text) {
		APIHeaders = text;
	}

	// To get value of APIHeaders for API execution
	public static String getAPIResponseHeaders() {
		return responseHeader;
	}

	// To get value of APIHeaders for API execution
	public static void setAPIResponseHeaders(String text) {
		responseHeader = text;
	}

	// To get value of APIHeaders for API execution
	public static String getAPIHeaders() {
		return APIHeaders;
	}

	// To set value of APIendpoint for API execution
	public static void setAPIEndpoint(String text) {
		APIendpoint = text;
	}

	// To get value of APIendpoint for API execution
	public static String getAPIEndpoint() {
		return APIendpoint;
	}

	// To set value of APIparameter for API execution
	public static void setAPIParameter(String text) {
		APIparameter = text;
	}

	// To set value of Basic auth for API execution
	public static String getBasicAuth() {
		return BasicAuth;
	}

	// To set value of Basic auth for API execution
	public static void setBasicAuth(String text) {
		BasicAuth = text;
	}

	// To get value of APIparameter for API execution
	public static String getAPIParameter() {
		return APIparameter;
	}

	public static String getApipayloadDict(String text) {

		for (String key : apiPayloadDictionary.keySet()) {
			if (text.contains("$" + key)) {
				text = text.replace("$" + key, apiPayloadDictionary.get(key));
			}
		}
		ExtentCucumberAdapter.addTestStepLog("Actual value :" + text);
		return text;
	}

	private static boolean processing(Object mapper, Object usermapper/* HashMap<String, Object> userResponseMap */) {
		HashMap<String, Object> map = new LinkedHashMap<String, Object>();
		map = (HashMap<String, Object>) mapper;
		Set<String> check = new HashSet<String>();
		HashMap<String, Object> userResponseMap = new LinkedHashMap<String, Object>();
		userResponseMap = (HashMap<String, Object>) usermapper;
		boolean flag = false;
		for (String k : userResponseMap.keySet()) {
			if (!(userResponseMap.get(k) != null
					&& userResponseMap.get(k).getClass().equals(java.util.LinkedHashMap.class))) {
				for (String k1 : map.keySet()) {
					if (k/* map */.contains(k1)) {
						if (userResponseMap.get(k).equals(map.get(k1))) {
							flag = true;
							if (check.add(userResponseMap.get(k1).toString())) {
								System.out.println(k1 + "                                 checked                ");
								break;
							}

						} else
							flag = false;
					}
				}
			} else {
				flag = processing(map.get(k), userResponseMap);
			}
		}
		check.clear();
		return flag;
	}

	// verify Json With ApiResponse
	public static boolean verifyJsonWithApiResponse(String userInput) {
		Response res = getAPIResponse();
		HashMap<String, Object> responseMap = new HashMap<String, Object>();
		HashMap<String, Object> userResponseMap = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		boolean flag = false;
		if (!CommonUtil.isValidJson(userInput)) {
			ExtentCucumberAdapter.addTestStepLog("Input json is invalid");
			return flag;
		}

		try {
			responseMap = mapper.readValue(res.asString(), new TypeReference<HashMap<String, Object>>() {
			});
			userResponseMap = mapper.readValue(userInput, new TypeReference<HashMap<String, Object>>() {
			});
			responseMap.remove("_id");
			userResponseMap.remove("_id");

			if (!responseMap.keySet().equals(userResponseMap.keySet())) {
				log.info("Warning: Keys set are not equal");
			}

			if (responseMap.equals(userResponseMap))
				return true;
			for (String k : userResponseMap.keySet()) {
				if (responseMap.get(k) == null && userResponseMap.get(k) == null) {
					continue;
				} else if (!(responseMap.containsValue(userResponseMap.get(k)))) {
					for (String d : responseMap.keySet()) {
						if (responseMap.get(d) != null
								&& responseMap.get(d).getClass().equals(java.util.LinkedHashMap.class)) {
							log.info("object" + responseMap.get(d));
							if (d.contains(k))
								flag = processing(responseMap.get(d), userResponseMap.get(k));
						}
					}
					if (!flag) {
						ExtentCucumberAdapter.addTestStepLog(
								"Unable to verify: " + "\"" + k + "\" with value:" + userResponseMap.get(k));
						flag = false;
					}
				} else if (responseMap.get(k) == null || userResponseMap.get(k) == null) {
					ExtentCucumberAdapter.addTestStepLog("Unable to verify: " + "\"" + k + "\"");
					flag = false;
				} else {
					ExtentCucumberAdapter.addTestStepLog(
							"JSON key: " + "\"" + k + "\" with value: " + userResponseMap.get(k) + " is verified");
					System.out.println(k + "                                 checked                ");
					flag = true;
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			ExtentCucumberAdapter.addTestStepLog("Input json is invalid");
		}
		return flag;

	}

	public static boolean executeApiAndVerifyResponse(String executeApiAndVerifyResponse) {
		boolean verified = false;
		int numericStatusCode;
		String content = "";
		executeApiAndVerifyResponse = CommonUtil.getData(executeApiAndVerifyResponse);
		executeApiAndVerifyResponse = RestAssuredUtil.getValueFromAPiResponse(executeApiAndVerifyResponse);
		Dictionary<String, String> parameters = new Hashtable();
		String apiURL = null, methodType = null, requestParameters = null, apiHeaders = null, apiEndPoint = null,
				apiParameter = null, basicAuth = null, oAuth = null, oAuthtoken = null;
		if (RestAssuredUtil.getAPIURL() != "") {
			apiURL = RestAssuredUtil.getAPIURL();
		}
		if (RestAssuredUtil.getMethodType() != "") {
			methodType = RestAssuredUtil.getMethodType();
		}
		if (RestAssuredUtil.getRequestParameters() != "") {
			requestParameters = RestAssuredUtil.getRequestParameters();
		}
		if (RestAssuredUtil.getAPIHeaders() != "") {
			apiHeaders = RestAssuredUtil.getAPIHeaders();
		}
		if (RestAssuredUtil.getAPIEndpoint() != "") {
			apiEndPoint = RestAssuredUtil.getAPIEndpoint();
		}
		if (RestAssuredUtil.getAPIParameter() != "") {
			apiParameter = RestAssuredUtil.getAPIParameter();
		}
		if (RestAssuredUtil.getBasicAuth() != "") {
			basicAuth = RestAssuredUtil.getBasicAuth();
		}
		if (RestAssuredUtil.getAPIOAuth2() != "") {
			oAuth = RestAssuredUtil.getAPIOAuth2();
		}
		if (apiHeaders != null && apiHeaders.toUpperCase().contains("IMAGE/PNG")
				&& methodType.toUpperCase().equals("GET")) {

			try {
				Response response = RestAssured.given().when().get(apiEndPoint).andReturn();
				int resCode = response.statusCode();
				ExtentCucumberAdapter.addTestStepLog("Status Code : " + resCode);
				if (resCode == 200 || resCode == 201 || resCode == 202 || resCode == 503) {
					verified = true;
					ExtentCucumberAdapter.addTestStepLog("Refer to the attached image to check output.");
				}
				byte[] image = response.getBody().asByteArray();
				String base64String = Base64.getEncoder().encodeToString(image);
				ExtentCucumberAdapter.addTestStepScreenCaptureFromPath("data:image/jpg;base64, " + base64String);

			} catch (Exception e) {
				ExtentCucumberAdapter.addTestStepLog("Error------------" + e);
				verified = false;
			}
			return verified;
		} else {
			RestAssured.useRelaxedHTTPSValidation();
			RestAssured.baseURI = apiURL;
			RequestSpecification httpRequest = RestAssured.given();
			EncoderConfig encoderconfig = new EncoderConfig();
			httpRequest.config(RestAssured.config()
					.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
			String status = "Failed";
			if (basicAuth != null && !basicAuth.isEmpty()) {
				String[] basicAuthSplit = basicAuth.split(",");
				httpRequest.relaxedHTTPSValidation().auth().preemptive().basic(basicAuthSplit[0], basicAuthSplit[1]);
			}

			Response response = null;
			try {
				if (methodType.toUpperCase().equals("Post".toUpperCase())
						|| methodType.toUpperCase().equals("Put".toUpperCase())
						|| methodType.toUpperCase().equals("Get".toUpperCase())
						|| methodType.toUpperCase().equals("Delete".toUpperCase())
						|| methodType.toUpperCase().equals("Patch".toUpperCase())
						|| methodType.toUpperCase().equals("Options".toUpperCase())) {
					try {
						if (OAuth2 != null) {
							OAuth2Util oa = new OAuth2Util();
							oAuthtoken = oa.getAccessTokenfromOAuthAPI(OAuth2);
							httpRequest.header("Authorization", "Bearer " + oAuthtoken);
						}
						if (!apiHeaders.trim().isEmpty()) {
							String[] inputParameters = apiHeaders.split(",");
							for (String parameter : inputParameters) {
								String[] headerText = parameter.split(":");
								if (headerText.length > 1) {
									String headerValue = headerText[1];
									if (headerValue.contains("@colon")) {
										headerValue = headerValue.replace("@colon", ":");
									}
									httpRequest.header(headerText[0], headerValue);
								}
							}
						} else {
							if (!apiHeaders.contains("$") && !apiHeaders.isEmpty()) {
								httpRequest.header("content-type", apiHeaders);
							} else {
								httpRequest.header("content-type", "application/x-www-form-urlencoded");
							}
						}
					} catch (Exception e) {
						httpRequest.header("content-type", "application/x-www-form-urlencoded");
					}
					if (apiParameter != null && !"NA".equalsIgnoreCase(apiParameter)) {
						String[] paramList = apiParameter.split(",");
						for (int l = 0; l < paramList.length; l++) {
							String[] queryParamValues = paramList[l].split(":");
							httpRequest.queryParam(queryParamValues[0], queryParamValues[1]);
						}
					}
					if (requestParameters != null && !requestParameters.equalsIgnoreCase("NA")) {
						if (CommonUtil.isValidJson(requestParameters) || requestParameters.contains("[")) {
							if (apiHeaders.contains("x-www-form-urlencoded")) {
								JsonArray ob = new Gson().fromJson(requestParameters, JsonArray.class);
								ob.forEach(action -> {
									JsonObject obb = action.getAsJsonObject();
									String key = obb.get("key").getAsString();
									String value = obb.get("value").getAsString();
									httpRequest.formParam(key, value);
								});
							}
							if (apiHeaders.contains("multipart/form-data")) {
								JsonArray reqBody = new JsonParser().parse(requestParameters).getAsJsonArray();
								reqBody.forEach(action -> {
									JsonObject obj = action.getAsJsonObject();
									if ("file".equals(
											obj.has("type") ? obj.getAsJsonObject().get("type").getAsString() : "")) {
										String filepath = obj.get("src").getAsString();
										String key = obj.get("key").getAsString();
										httpRequest.multiPart(key, new File(filepath), "multipart/form-data");
									} else
										httpRequest.body(reqBody.getAsString());
								});
							} else
								httpRequest.body(requestParameters);
						} else if (apiHeaders.toLowerCase().contains("xml") || apiHeaders.toLowerCase().contains("html")
								|| apiHeaders.toLowerCase().contains("text")) {
							httpRequest.body(requestParameters);
						} else {
							try {
								JSONParser parser = new JSONParser();
								JSONObject json = (JSONObject) parser.parse(requestParameters);
								httpRequest.body(json.toJSONString());
							} catch (Exception e) {
								log.error(e.getMessage());
							}
						}
					} else {
						httpRequest.body("");
					}
					if (apiURL.contains(apiEndPoint)) {
						if (methodType.toUpperCase().equals("Get".toUpperCase())) {
							response = httpRequest.request(Method.GET);
						} else if (methodType.toUpperCase().equals("Put".toUpperCase())) {
							response = httpRequest.request(Method.PUT);
						} else if (methodType.toUpperCase().equals("Delete".toUpperCase())) {
							response = httpRequest.request(Method.DELETE);
						} else if (methodType.toUpperCase().equals("Patch".toUpperCase())) {
							response = httpRequest.request(Method.PATCH);
						} else if (methodType.toUpperCase().equals("Options".toUpperCase())) {
							response = httpRequest.request(Method.OPTIONS);
						} else {
							response = httpRequest.request(Method.POST);
						}
					} else {
						if (methodType.toUpperCase().equals("Get".toUpperCase())) {
							response = httpRequest.request(Method.GET, apiEndPoint);
						} else if (methodType.toUpperCase().equals("Put".toUpperCase())) {
							response = httpRequest.request(Method.PUT, apiEndPoint);
						} else if (methodType.toUpperCase().equals("Delete".toUpperCase())) {
							response = httpRequest.request(Method.DELETE, apiEndPoint);
						} else if (methodType.toUpperCase().equals("Patch".toUpperCase())) {
							response = httpRequest.request(Method.PATCH, apiEndPoint);
						} else if (methodType.toUpperCase().equals("Options".toUpperCase())) {
							response = httpRequest.request(Method.OPTIONS);
						} else {
							response = httpRequest.request(Method.POST, apiEndPoint);
						}
					}
				}
				numericStatusCode = response.getStatusCode();
				content = "Status code: " + numericStatusCode + "  Response: " + response.getBody().asString();
				System.out.println(" < b style = 'color:#038CFB;' > " + content + " </ b > ");
				Headers allHeaders = response.headers();
				RestAssuredUtil.setAPIResponseHeaders(allHeaders.toString());
				RestAssuredUtil.setAPIResponse(response);
			} catch (Exception e) {
				e.printStackTrace();
				numericStatusCode = 000;
				content = "Response: " + e.getMessage();
			}
			if (executeApiAndVerifyResponse.toUpperCase().contains("verify_negative".toUpperCase())) {
				if (numericStatusCode == 400 || numericStatusCode == 500 || numericStatusCode == 401
						|| numericStatusCode == 415 || numericStatusCode == 000 || numericStatusCode == 422
						|| numericStatusCode == 404 || numericStatusCode == 403 || numericStatusCode == 503) {
					verified = true;
					status = "Passed";
					if (executeApiAndVerifyResponse.toUpperCase().contains("--")) {
						String[] splitNeagtiveValue = executeApiAndVerifyResponse.split("--");
						for (int i = 1; i < splitNeagtiveValue.length; i++) {
							if (content.contains(splitNeagtiveValue[i])) {
								verified = true;
								status = "Passed";
								ExtentCucumberAdapter
										.addTestStepLog("Verified values in API response: " + splitNeagtiveValue[i]);
							} else {
								verified = false;
								status = "Failed";
								ExtentCucumberAdapter
										.addTestStepLog(splitNeagtiveValue[i] + " is not present in API response.");
							}
						}
					}
				} else {
					verified = false;
					status = "Failed";
				}
			} else {
				if (response.getStatusLine().toUpperCase() == "OK" || numericStatusCode == 201
						|| numericStatusCode == 200 || numericStatusCode == 202 || numericStatusCode == 204
						|| numericStatusCode == 503) {
					verified = true;
					status = "Passed";
				} else {
					verified = false;
					status = "Failed";
				}
			}
			if (verified && !executeApiAndVerifyResponse.toUpperCase().equals("NA".toUpperCase())
					&& !executeApiAndVerifyResponse.toUpperCase().contains("verify_negative".toUpperCase())) {
				if (executeApiAndVerifyResponse.toUpperCase().contains("verifyCopiedList".toUpperCase())) {
					String passedString = "";
					List<String> copiedList = CommonUtil.getCopiedList();
					for (int i = 0; i < copiedList.size(); i++) {
						String verifyText = "";
						if (!executeApiAndVerifyResponse.toUpperCase()
								.contains("verifyCopiedList_nospace".toUpperCase())) {
							verifyText = (copiedList.get(i).split(" "))[0];
						} else {
							verifyText = copiedList.get(i);
						}
						if (!content.contains(verifyText)) {
							verified = false;
							ExtentCucumberAdapter
									.addTestStepLog(verifyText + " is not presnt in API response: " + content);
							break;
						}
						passedString = passedString + verifyText + ",";
						verified = true;
					}
					ExtentCucumberAdapter.addTestStepLog("Verified values in API response: " + passedString);
				} else {
					String[] splitArray = executeApiAndVerifyResponse.split(",");
					for (int i = 0; i < splitArray.length; i++) {
						if (content.contains(splitArray[i]) == true) {
							verified = true;
							ExtentCucumberAdapter.addTestStepLog(splitArray[i] + " is verified in API output.");
						} else {
							verified = false;
							ExtentCucumberAdapter.addTestStepLog(splitArray[i] + " is not verified in API output.");
							break;
						}

					}
				}
			}
			if (apiHeaders != null && apiHeaders.contains("$")) {
				try {
					JSONParser parser = new JSONParser();
					JSONObject userObj = (JSONObject) parser.parse(content);
					String key = apiHeaders.replace("$", "");
					parameters.put(userObj.get(key).toString(), apiHeaders);
				} catch (Exception ex) {
					log.error(ex.getMessage());
				}
			}
			String printParam = content.replaceAll("<", "&lt;");
			printParam = printParam.replaceAll(">", "&gt;");
			ExtentCucumberAdapter.addTestStepLog(
					"Actual content: " + printParam + ", Expected content: " + executeApiAndVerifyResponse);
			RestAssuredUtil.setMethodType(null);
			RestAssuredUtil.setRequestParameters(null);
			RestAssuredUtil.setAPIHeaders(null);
			RestAssuredUtil.setAPIEndpoint(null);
			RestAssuredUtil.setAPIParameter(null);
			RestAssuredUtil.setBasicAuth(null);
			RestAssuredUtil.setAPIOAuth2(null);
			return verified;
		}
	}

}