package common;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import org.apache.log4j.Logger;
import com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.ChannelSftp.LsEntry;

public class SSHUtil {
	static String host = "**Linux Machine IP or Hostname***";
	static String user = "**Linux machine User***";
	static String password = "** Linux machine password***";
	static int port = 22;
	static String filePath = "** Linux file Path***";
	private static String path = System.getProperty("user.dir");
	static final Logger log = Logger.getLogger(SSHUtil.class);

	public static void LoadConnectionDeatils() {
		try {
			host = CommonUtil.getXMLData(Paths.get(path, "src", "test", "java", "SSHSettings.xml").toString(),
					"SSH_Host");
			user = CommonUtil.getXMLData(Paths.get(path, "src", "test", "java", "SSHSettings.xml").toString(),
					"SSH_User");
			password = CommonUtil.getXMLData(Paths.get(path, "src", "test", "java", "SSHSettings.xml").toString(),
					"SSH_Password");
			ExtentCucumberAdapter.addTestStepLog(
					"************************************************" + host + "  " + user + "  " + password);
		} catch (Exception ex) {
			throw new CustomException("DBSettings.xml file does not exist");
		}
	}

	public static String GetListOfFiles(String filepath) {
		filePath = filepath;
		LoadConnectionDeatils();
		Session session = null;
		Channel channel = null;
		ChannelSftp channelSftp = null;
		String list = "";
		try {
			JSch jsch = new JSch();
			session = jsch.getSession(user, host, port);
			session.setPassword(password);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			ExtentCucumberAdapter.addTestStepLog("Connected to the host: '" + host + "'");
			channelSftp = (ChannelSftp) channel;
			channelSftp.cd(filePath);
			Vector filelist = channelSftp.ls("*");
			for (int i = 0; i < filelist.size(); i++) {
				LsEntry entry = (LsEntry) filelist.get(i);
				list = list + entry.getFilename() + ",";
				System.out.println(entry.getFilename());
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
		} finally {
			if (session != null)
				session.disconnect();
			if (channel != null)
				channel.disconnect();
		}
		return list;
	}

	public static boolean runCommands(String commands) {
		String result = "";
		boolean res = false;
		StringBuilder outputBuffer = new StringBuilder();
		try {
			System.out.println("COMMANDS##################-->" + commands);
			String[] splitValue = commands.split("---");
			if (splitValue.length > 1) {
				log.info("SPLIT@@@@@@@@@@@@@@ VALUE1*******" + splitValue[0]);
				log.info("SPLIT@@@@@@@@@@@@@@ VALUE2*******" + splitValue[1]);
				log.info("SPLIT@@@@@@@@@@@@@@ VALUE3*******" + splitValue[2]);
				log.info("SPLIT@@@@@@@@@@@@@@ VALUE3*******" + splitValue[3]);
				if (splitValue.length > 4) {
					log.info("SPLIT@@@@@@@@@@@@@@ VALUE1*******" + splitValue[4]);
				}
				JSch jsch = new JSch();
				Session session = setupSshSession();
				session.connect();
				Channel channel = session.openChannel("shell");// only shell
				channel.setOutputStream(System.out);
				PrintStream shellStream = new PrintStream(channel.getOutputStream()); // printStream for convenience
				channel.connect();

				String[] commands1 = splitValue[0].split("&&");

				for (String cmd : commands1) {
					System.out.println("FOR_EACH-------CMD" + cmd);
					if (cmd.contains("_delay_")) {
						String[] cmds = cmd.split("_delay_");
						shellStream.println(cmds[0]);
						ExtentCucumberAdapter.addTestStepLog("Executed command : " + cmds[0]);

						shellStream.flush();
						Thread.sleep(Long.parseLong(cmds[1].trim()));

					} else {
						shellStream.println(cmd.trim());
						ExtentCucumberAdapter.addTestStepLog("Executed command : " + cmd.trim());
						shellStream.flush();
						Thread.sleep(2000);
					}
				}

				String verifyText = DbHelper.queryCopiedText(splitValue[2]).toUpperCase().trim();
				log.info("COPIED@@@@@@@@@@@@@@ VALUE*******" + verifyText);
				String fileoutput = "";
				if (splitValue.length > 4) {
					fileoutput = GetListOfFiles(splitValue[1]).toUpperCase();
				} else {
					fileoutput = GetDataFromFile(splitValue[1]).toUpperCase();
				}
				log.info("FILEOUTPUT DATA_______-------" + fileoutput);
				if (splitValue[3].equals("verifydisplayed")) {
					log.info("Copied text is" + verifyText + " verified" + fileoutput.contains(verifyText.trim()));
					if (fileoutput.contains(verifyText.trim())) {
						log.info("Inside VERIFIED DISPLAYED________****true****");
						res = true;
					} else {
						log.info("Inside VERIFIED DISPLAYED________****false****");
						res = false;
					}
				} else if (splitValue[3].equals("verifynotdisplayed")) {
					if (fileoutput.contains(verifyText.toUpperCase())) {
						log.info("Inside VERIFIED NOT DISPLAYED________****false****");
						res = false;
					} else {
						log.info("Inside VERIFIED NOT DISPLAYED________****true****");
						res = true;
					}
				}

				ExtentCucumberAdapter
						.addTestStepLog("Value read from file : '" + fileoutput + "' , Copied value to be verified : '"
								+ verifyText + "' , Verification type : '" + splitValue[3] + "'");
				channel.disconnect();
				session.disconnect();
			} else {
				log.info("------------------Run only command ------------------------");

				JSch jsch2 = new JSch();
				Session session2 = setupSshSession();
				session2.connect();

				Channel channel2 = session2.openChannel("shell");// only shell
				channel2.setOutputStream(System.out);
				PrintStream shellStream2 = new PrintStream(channel2.getOutputStream()); // printStream for convenience
				channel2.connect();

				String[] commands1 = commands.split("&&");
				for (String cmd : commands1) {
					log.info("FOR_EACH---------------------------------CMD" + cmd);
					if (cmd.contains("_delay_")) {
						String[] cmds = cmd.split("_delay_");
						shellStream2.println(cmds[0]);
						ExtentCucumberAdapter.addTestStepLog("Executed command : " + cmds[0]);
						shellStream2.flush();
						Thread.sleep(Long.parseLong(cmds[1].trim()));
					} else {
						shellStream2.println(cmd.trim());
						ExtentCucumberAdapter.addTestStepLog("Executed command : " + cmd.trim());
						shellStream2.flush();
						Thread.sleep(2000);
					}
					channel2.disconnect();
					session2.disconnect();
				}
				res = true;
			}

		} catch (Exception e) {
			log.info("EXCEPTION BLOCK-------######********--------" + e);
		}
		return res;
	}

	public static String GetDataFromFile(String filepath) {
		filePath = filepath;
		LoadConnectionDeatils();
		String list = "";
		String value = "";
		try {
			JSch jsch = new JSch();
			Session session = jsch.getSession(user, host, port);
			session.setPassword(password);
			session.setConfig("StrictHostKeyChecking", "no");
			System.out.println("Establishing Connection...");
			session.connect();
			log.info("Connection established.");
			log.info("Crating SFTP Channel.");
			// filePath="ibc/sites/ATT03/lhost.yml";
			ExtentCucumberAdapter.addTestStepLog("Connected to the host: '" + filePath + "'");
			log.info("Connected to the host: '" + filePath + "'");
			ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
			sftpChannel.connect();
			log.info("SFTP Channel created.");
			ExtentCucumberAdapter.addTestStepLog("Connected to the host: '" + host + "'");
			InputStream inputStream = sftpChannel.get(filePath);
			ExtentCucumberAdapter.addTestStepLog("Reading the data from file: '" + filePath + "'");
			try (Scanner scanner = new Scanner(new InputStreamReader(inputStream))) {
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					list = list + line;
					log.info(line);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return list;
	}

	public static String runCommand(String command) {
		Session session = null;
		ChannelExec channel = null;
		try {
			session = setupSshSession();
			session.connect();
			ExtentCucumberAdapter.addTestStepLog("Connected to the host: '" + host + "'");
			StringBuilder outputBuffer = new StringBuilder();
			channel = (ChannelExec) session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
			InputStream commandOutput = channel.getInputStream();
			channel.connect();
			int readByte = commandOutput.read();
			ExtentCucumberAdapter.addTestStepLog("Executed the command on Remote machine");
			while (readByte != 0xffffffff) {
				outputBuffer.append((char) readByte);
				readByte = commandOutput.read();
			}
			ExtentCucumberAdapter.addTestStepLog("Command output: '" + outputBuffer.toString() + "'");
			return outputBuffer.toString();
		} catch (Exception ex) {
			closeConnection(channel, session);
			throw new RuntimeException(ex);
		} finally {
			closeConnection(channel, session);
		}
	}

	private static Session setupSshSession() {
		Session session = null;
		try {
			LoadConnectionDeatils();
			session = new JSch().getSession(user, host, port);
			session.setPassword(password);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		return session;
	}

	private static void closeConnection(ChannelExec channel, Session session) {
		try {
			channel.disconnect();
		} catch (Exception ignored) {
			log.error(ignored.getMessage());
		}
		session.disconnect();
	}

}
