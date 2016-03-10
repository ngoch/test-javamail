package net.fina.tec;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;

public class TestConnectionPOP3 {
	private static Session session = null;
	private static Store store = null;
	private static String folderName = "INBOX";

	public static void main(String[] args) {
		try {
			System.out.println("Start");

			if (args.length != 1) {
				throw new RuntimeException("Plase Set Parameter Properties Folde Name");
			}

			final String fileName = args[0];

			Properties prop = createProperties(fileName);

			String host = prop.getProperty("mail.pop3.host");
			String userName = prop.getProperty("mail.user");
			String pass = prop.getProperty("mail.password");

			Authenticator auth = new MailReaderAuthenticator(userName, pass);

			session = Session.getInstance(prop, auth);
			session.setDebug(true);

			store = session.getStore("pop3");

			store.connect(host, Integer.parseInt(prop.getProperty("mail.pop3.port")), userName, pass);

			Folder folder = openInboxFolder(store);

			Message[] m = null;

			m = folder.getMessages();

			System.out.println("Message count : " + m.length);

			for (Message a : m) {
				System.err.println("=============Test Print============");
				System.out.println("From : " + Arrays.toString(a.getFrom()));
				System.out.println("Subject : " + a.getSubject());
				System.out.println("Sent Date : " + a.getSentDate());

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static Properties createProperties(String folderName) {
		Properties prop = new Properties();
		try {
			prop.load(new FileReader(folderName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;
	}

	private static Folder openInboxFolder(Store store) throws MessagingException {
		Folder folder = store.getFolder(folderName);
		if (folder == null) {
			throw new MessagingException("Invalid folder");
		}
		try {
			folder.open(Folder.READ_ONLY);
		} catch (MessagingException ex) {
			folder.open(Folder.READ_ONLY);
		}
		return folder;
	}

	private static class MailReaderAuthenticator extends Authenticator {
		private String userName;
		private String pass;

		MailReaderAuthenticator(String userName, String pass) {
			this.userName = userName;
			this.pass = pass;
		}

		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(userName, pass);
		}
	}

	// TODO Test Method
	public static int readMails(String fileName) throws Exception {
		int messageCount = 0;
		try {
			Properties prop = createProperties(fileName);

			String host = prop.getProperty("mail.pop3.host");
			String userName = prop.getProperty("mail.user");
			String pass = prop.getProperty("mail.password");

			Authenticator auth = new MailReaderAuthenticator(userName, pass);

			session = Session.getInstance(prop, auth);
			session.setDebugOut(new PrintStream("logs" + System.getProperty("file.separator") + "POP3Log.txt"));
			session.setDebug(true);

			store = session.getStore("pop3");

			store.connect(host, Integer.parseInt(prop.getProperty("mail.pop3.port")), userName, pass);

			Folder folder = openInboxFolder(store);

			messageCount = folder.getMessageCount();

			// Message[] m = null;
			// m = folder.getMessages();
			//
			// // m = folder.search(new MySearchTerm());
			//
			// for (Message a : m) {
			// list.add(a);
			// }
		} catch (Exception ex) {
			throw ex;
		}
		return messageCount;
	}
}
