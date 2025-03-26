package common;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import org.apache.log4j.Logger;
import com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter;

public class MySqlServerUtil {

	private Connection conn = null;
	private ResultSet results;
	static String dbsqlURLdb = "***jdbc cluster connection string ****";
	static String dbsqlMasterUsername = "***master user name***";
	static String dbsqlMasterUserPassword = "***master user password***";
	private static String path = System.getProperty("user.dir");
	static String query1 = null;
	static final Logger log = Logger.getLogger(MySqlServerUtil.class);

	public MySqlServerUtil() {
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public void createConnection() {
		try {
			System.out.println("Entering getdata method ");
			dbsqlURLdb = CommonUtil.getXMLData(Paths.get(path, "src", "test", "java", "DBSettings.xml").toString(),
					"MySql_DbURL");
			dbsqlMasterUsername = CommonUtil.getXMLData(
					Paths.get(path, "src", "test", "java", "DBSettings.xml").toString(), "MySql_DbUsername");
			dbsqlMasterUserPassword = CommonUtil.getXMLData(
					Paths.get(path, "src", "test", "java", "DBSettings.xml").toString(), "MySql_DbPassword");
			log.info("DB details : " + dbsqlURLdb + " username " + dbsqlMasterUsername + " password "
					+ dbsqlMasterUserPassword);
		} catch (Exception ex) {
			throw new CustomException("DBSettings.xml file does not exist");
		}
		try {
			if (this.conn == null) {
				setConnection();
			} else {
				log.info("Already connected to DB");
			}
		} catch (Exception e1) {
			log.error(" ERROR MESSAGE " + e1.getMessage());
		}

	}

	public void setConnection() {
		String url = CommonUtil.getXMLData(Paths.get(path, "src", "test", "java", "DBSettings.xml").toString(),
				"MySql_DbURL");
		String user = CommonUtil.getXMLData(Paths.get(path, "src", "test", "java", "DBSettings.xml").toString(),
				"MySql_DbUsername");
		String password = CommonUtil.getXMLData(Paths.get(path, "src", "test", "java", "DBSettings.xml").toString(),
				"MySql_DbPassword");
		log.info("DB details : " + url + " username " + user + " password " + password);

		Properties props = new Properties();
		props.setProperty("user", user.trim());
		props.setProperty("password", password.trim());
		props.setProperty("ssl", "true");
		try {
			log.info("Before MySqlDB is not connected");
			setConn(DriverManager.getConnection(url.trim(), props));
			log.info("After MySqlDB is connected");
			this.conn.setAutoCommit(true);
			if (conn != null) {
				log.info("Connected to the MySql database!");
			} else {
				log.error("Failed to make connection!");
			}
		} catch (Exception exc2) {
			log.error(exc2.getClass().getName() + ": " + exc2.getMessage());
		}
	}

	public Connection getConnection() {
		return this.conn;
	}

	public void closeConnection() {
		try {
			this.conn.close();
		} catch (Exception e2) {
			log.error(e2.getClass().getName() + ": " + e2.getMessage());
		}
	}

	public MySqlServerUtil(Connection conn) {
		this.conn = conn;
	}

	public void update(String query1) {
		int executeUpdteStatus = 0;
		boolean connStatus = true;
		try {
			System.out.println("Query value " + query1);
			createConnection();
			try {
				connStatus = this.conn.isClosed();
			} catch (SQLException e) {
				log.error(e.getMessage());
			}
			if (!connStatus) {
				Statement statement = this.conn.createStatement();
				executeUpdteStatus = statement.executeUpdate(query1.trim());
				log.info("Query is Updated in DB ");
			} else {
				query1 = CommonUtil.getXMLData(Paths.get(path, "src", "test", "java", "DBSettings.xml").toString(),
						query1);
				log.info("Query value " + query1);
				setConnection();
				PreparedStatement prepareStatement = this.conn.prepareStatement(query1.trim());
				executeUpdteStatus = prepareStatement.executeUpdate();
				log.info(" DB update row count :" + executeUpdteStatus);
			}
		} catch (Exception e) {
			throw new CustomException(e.getMessage(), e);
		} finally {
			if (this.conn != null) {
				closeConnection();
			}
		}
	}

	public void select(String query) {
		try {
			String sqlQuery = CommonUtil.getXMLData(Paths.get(path, "src", "test", "java", "DBSettings.xml").toString(),
					query);
			getConnection();
			this.results = this.conn.prepareStatement(sqlQuery).executeQuery();
			List abd = CommonUtil.resultSetToArrayList(results);
			log.info(abd.size());
			for (int i = 0; i < abd.size(); i++) {
				HashMap row = (HashMap) abd.get(i);
				for (Object mapVal : row.values())
					log.info(mapVal.toString());
			}
		} catch (SQLException e1) {
			log.error(e1.getMessage());
		} finally {
			if (this.conn != null) {
				closeConnection();
			}
		}
	}

	public List getdetails(String query) {
		List resultList = null;
		try {
			log.info("Before  setConnection ");
			setConnection();
			log.info("After  setConnection ");
			this.results = this.conn.createStatement().executeQuery(query);
			resultList = CommonUtil.resultSetToArrayList(results);
			log.info("DB query result size : " + resultList.size());
		} catch (Exception exc) {
			log.error(" ERROR MESSAGE " + exc.getMessage());
			log.error(exc.getClass().getName() + ": " + exc.getMessage());
		} finally {
			if (this.conn != null) {
				closeConnection();
			}
		}
		return resultList;
	}

	public List getData(String query2) {
		List resultList = null;
		try {
			log.info("Entering getdata method.. ");
			log.info("Query value is " + query2);
		} catch (Exception exc) {
			throw new CustomException("DBSettings.xml file does not exist");
		}
		try {
			log.info("Before  setConnection... ");
			setConnection();
			log.info("After  setConnection... ");
			this.results = this.conn.createStatement().executeQuery(query2);
			resultList = CommonUtil.resultSetToArrayList(results);
			log.info("DB query result size : " + resultList.size());
		} catch (Exception e) {
			log.error(" ERROR MESSAGE.. " + e.getMessage());
			log.error(e.getClass().getName() + ": " + e.getMessage());
		} finally {
			if (this.conn != null) {
				closeConnection();
			}
		}
		return resultList;
	}

	public String getSingleData(String query2) {
		List resultList = null;
		try {
			log.info("Entering getdata method ");
			log.info("Query value " + query2);
		} catch (Exception ex) {
			throw new CustomException("DBSettings.xml file does not exist");
		}

		try {
			log.info("Before  setConnection ");
			setConnection();
			log.info("After  setConnection ");
			resultList = getData(query2);
			log.info("getSingleData output -------------------------" + resultList);
			log.info("DB query result size : " + resultList);
			for (int i = 0; i < resultList.size(); i++) {
				HashMap rows = (HashMap) resultList.get(i);
				log.info("row value is :" + rows);
				for (Object mapVal : rows.values()) {
					ExtentCucumberAdapter.addTestStepLog("DB Value copied : " + mapVal);
					return mapVal.toString();
				}
			}
		} catch (Exception ex) {
			log.error(" ERROR MESSAGE -  " + ex.getMessage());
			log.error(ex.getClass().getName() + ": " + ex.getMessage());
		} finally {
			if (this.conn != null) {
				closeConnection();
			}
		}
		return "";
	}

	public boolean verifyDbData(String paramquery) throws Exception {
		MySqlServerUtil sql = new MySqlServerUtil();
		String query1 = null;
		String compareValues = null;
		String[] queryDetails = paramquery.split("--");
		boolean status = false;
		if (queryDetails.length == 2) {
			query1 = queryDetails[0];
			compareValues = queryDetails[1];
			compareValues = compareValues.replaceAll("\\[", "").replaceAll("\\]", "");
		} else {
			ExtentCucumberAdapter.addTestStepLog("Invalid parameters");
			throw new Exception("Invalid parameters");
		}

		try {

			log.info("compareValue Should be : " + compareValues);
			List dataList = getData(query1);
			System.out.println("DB output : " + dataList);
			for (int i = 0; i < dataList.size(); i++) {
				HashMap row = (HashMap) dataList.get(i);
				System.out.println("row value is :" + row);
				for (Object mapVal : row.values()) {
					if (mapVal == null) {
						ExtentCucumberAdapter.addTestStepLog("DB Output. : Null");
					} else if (mapVal.toString().equals("null")) {
						ExtentCucumberAdapter.addTestStepLog("DB Output. : Null");
					} else if (mapVal.toString().contains(compareValues)) {
						ExtentCucumberAdapter
								.addTestStepLog("DB Output. : " + dataList + " , Compare Value : " + compareValues);
						status = true;
						return true;
					}
				}
			}
			return status;
		}

		catch (Exception exc1) {
			ExtentCucumberAdapter.addTestStepLog("Error : " + exc1);
			return false;
		} finally {
			try {
				getConn().close();
			} catch (SQLException ex2) {
				log.error(ex2.getMessage());
			}
		}
	}

	public static boolean validateResultSetRecords(ResultSet rs, String compareValue, int columnIndex)
			throws SQLException {
		Object columnObject = rs.getObject(columnIndex);
		String columnValue = null;
		if (columnObject instanceof Integer) {
			columnValue = String.valueOf((int) columnObject);

		} else if (columnObject instanceof Boolean) {
			columnValue = String.valueOf((boolean) columnObject);

		} else if (columnObject instanceof Float) {
			columnValue = String.valueOf((Float) columnObject);

		} else if (columnObject instanceof Double) {
			columnValue = String.valueOf((Double) columnObject);

		} else if (columnObject instanceof Character) {
			columnValue = String.valueOf((Character) columnObject);

		} else if (columnObject instanceof String) {
			columnValue = (String) columnObject;

		}
		log.info("result value " + columnValue);

		return compareValue.equalsIgnoreCase(columnValue);
	}
}
