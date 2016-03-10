package net.fina.tec;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

public class TestConnectionSMPT {

	private static Logger log = Logger.getLogger(TestConnectionSMPT.class);

	public static void main(String[] args) throws Exception {

		if (args.length != 1) {
			throw new RuntimeException("Plase Set Parameter Properties Folde Name");
		}

		TestConnectionSMPT.sendMail(args[0], "gochiashvili@fina2.net");
	}

	public static void sendMail(String file, String... to) throws Exception {
		final Properties properties = createProperties(file);

		final String host = properties.getProperty("mail.smtp.host");
		final String user = properties.getProperty("mail.user");
		final String password = properties.getProperty("mail.password");
		final String address = properties.getProperty("mail.address");

		properties.put("mail.smtp.auth", "true");

		properties.put("mail.smtp.port", properties.getProperty("mail.smtp.port"));

		if (properties.getProperty("mail.smtp.ssl.enable").equals("true")) {
			properties.put("mail.smtp.socketFactory.port", properties.getProperty("mail.smtp.port"));
			properties.put("mail.smtp.socketFactory.fallback", "false");
			properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		} else {
			properties.put("mail.smtp.ssl.trust", host);
			properties.setProperty("mail.smtp.auth.plain.disable", "true");
		}

		if (properties.get("mail.smtp.starttls.enable").equals("false")) {

			// Remove Other Properties
			properties.remove("mail.smtp.socketFactory.port");
			properties.remove("mail.smtp.socketFactory.fallback");
			properties.remove("mail.smtp.socketFactory.class");

		}

		Session session = Session.getDefaultInstance(properties);
		session.setDebugOut(new PrintStream("logs" + System.getProperty("file.separator") + "SMTPLog.txt"));
		session.setDebug(true);

		try {

			log.info("Properties=" + properties);

			// SMTP CONNECT
			final Transport transport = session.getTransport("smtp");
			transport.connect(host, user, password);
			session.getDebugOut().println("SMTP Connect successful");

			log.info("session propertirs=" + session.getProperties());
			log.info("transport=" + transport);

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(address));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to[0]));
			message.setSubject("Testing Subject");
			Calendar c = Calendar.getInstance();
			String header = "This message was sent by \"Test Java Mail Connection\"\non " + c.getTime() + "\n";
			message.setContent(header, "text/plain; charset=UTF-8");

			log.info("Message=" + message);

			InternetAddress[] addresses = new InternetAddress[to.length];
			for (int i = 0; i < addresses.length; i++) {
				addresses[i] = new InternetAddress(to[i]);
			}

			log.info("address=" + Arrays.toString(addresses));

			transport.sendMessage(message, addresses);
			transport.close();

		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw ex;
		}
	}

	private static Properties createProperties(String fileName) {
		Properties prop = new Properties();
		try {
			prop.load(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;
	}
}
