package common;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

public class RedShiftUtil {
	static String dbredshiftURL = "***jdbc cluster connection string ****";
	static String MasterRSUsername = "***master user name***";
	static String MasterRSUserPassword = "***master user password***";
	private static String path = System.getProperty("user.dir");
	static String query = null;
	static final Logger log = Logger.getLogger(RestAssuredUtil.class);

	public List getData(String queryParams) {
		Connection conns = null;
		Statement stmt = null;
		List resultsList = null;
		try {
			query = CommonUtil.getXMLData(
					Paths.get(path, "src", "test", "java", "DBSettings.xml").toString(), queryParams);
			dbredshiftURL = CommonUtil.getXMLData(
					Paths.get(path, "src", "test", "java", "DBSettings.xml").toString(), "RedShift_DbURL");
			MasterRSUsername = CommonUtil.getXMLData(
					Paths.get(path, "src", "test", "java", "DBSettings.xml").toString(),
					"RedShift_DbUsername");
			MasterRSUserPassword = CommonUtil.getXMLData(
					Paths.get(path, "src", "test", "java", "DBSettings.xml").toString(),
					"RedShift_DbPassword");
		} catch (Exception exc) {
			throw new CustomException("DBSettings.xml file does not exists.." + exc.toString());
		}
		try {
			Class.forName("com.amazon.redshift.jdbc41.Driver");
			System.out.println("Connecting to database...");
			Properties prop = new Properties();
			// Uncomment the following line if using a keystore.
			prop.setProperty("sslfactory", "com.amazon.redshift.ssl.NonValidatingFactory");
			prop.setProperty("ssl", "true");
			prop.setProperty("user", MasterRSUsername);
			prop.setProperty("password", MasterRSUserPassword);
			conns = DriverManager.getConnection(dbredshiftURL, prop);
			// Try a simple query.
			log.info("Listing system tables...");
			stmt = conns.createStatement();
			String sql;
			// sql = "select * from information_schema.tables;";
			ResultSet rs1 = stmt.executeQuery(query);
			resultsList = CommonUtil.resultSetToArrayList(rs1);
			rs1.close();
			stmt.close();
			conns.close();
		} catch (Exception ex) {
			log.error(ex.getMessage());
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception ex) {
				log.error(ex.getMessage());
			}
			try {
				if (conns != null)
					conns.close();
			} catch (Exception ex) {
				log.error(ex.getMessage());
			}
		}
		log.info("Finished connectivity test.");
		return resultsList;
	}

	public List getExcelData(String queryParam) {
		Connection conn = null;
		Statement stmt = null;
		List resultList = null;
		try {
			dbredshiftURL = CommonUtil.getXMLData(
					Paths.get(path, "src", "test", "java", "DBSettings.xml").toString(), "RedShift_DbURL");
			// MasterRSUsername
			MasterRSUsername = CommonUtil.getXMLData(
					Paths.get(path, "src", "test", "java", "DBSettings.xml").toString(),
					"RedShift_DbUsername");
			// MasterRSUserPassword
			MasterRSUserPassword = CommonUtil.getXMLData(
					Paths.get(path, "src", "test", "java", "DBSettings.xml").toString(),
					"RedShift_DbPassword");
		} catch (Exception ex) {
			throw new CustomException("DBSettings.xml file does not exist");
		}
		try {
			Class.forName("com.amazon.redshift.jdbc41.Driver");
			// Open a connection and define properties.
			log.info("Connecting to database...");
			Properties props = new Properties();

			// Uncomment the following line if using a keystore.
			props.setProperty("sslfactory", "com.amazon.redshift.ssl.NonValidatingFactory");
			props.setProperty("ssl", "true");
			props.setProperty("user", MasterRSUsername);
			props.setProperty("password", MasterRSUserPassword);
			conn = DriverManager.getConnection(dbredshiftURL, props);

			// Try a simple query.
			log.info("Listing system tables...");
			stmt = conn.createStatement();
			String sql;
			ResultSet rs = stmt.executeQuery(queryParam);
			resultList = CommonUtil.resultSetToArrayList(rs);

			rs.close();
			stmt.close();
			conn.close();

		} catch (Exception ex) {
			log.error(ex.getMessage());
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception ex) {
				log.error(ex.getMessage());
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception ex) {
				log.error(ex.getMessage());
			}
		}
		log.info("Finished connectivity test.");
		return resultList;
	}
}
