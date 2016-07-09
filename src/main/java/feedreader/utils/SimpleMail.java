package feedreader.utils;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import feedreader.config.FeedAppConfig;

/**
 * https://javamail.java.net/nonav/docs/api/
 */
public class SimpleMail {

	private static String SMTP_AUTH_USER = "see mail.*.properties";
	private static String SMTP_AUTH_PWD = "see mail.*.properties";
    private static final String PROP_SMTP_PWD = "mail.smtp.password";
    private static final String PROP_SMTP_USER = "mail.smtp.user";

	private final Session mailSession;
	private static Properties mailProperties;

	public SimpleMail() {
		mailSession = Session.getInstance(mailProperties, new SMTPAuthenticator());
	}

	public static void configure(Properties props) {
		SimpleMail.mailProperties = props;
        SMTP_AUTH_USER = mailProperties.getProperty(PROP_SMTP_USER);
		SMTP_AUTH_PWD = mailProperties.getProperty(PROP_SMTP_PWD);
	}

	public void send(String from, String fromName, String to, String toName, String subject, String plainText)
			throws Exception {
		Transport transport = mailSession.getTransport();
		MimeMessage message = new MimeMessage(mailSession);
		message.setContent(plainText, "text/plain");
		message.setSubject(subject);
		message.setFrom(new InternetAddress(from, fromName));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(to, toName));
		message.addRecipient(Message.RecipientType.BCC, new InternetAddress(FeedAppConfig.MAIL_BCC_ADDRESS));
		transport.connect();
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
	}

	private class SMTPAuthenticator extends Authenticator {
		@Override
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(SMTP_AUTH_USER, SMTP_AUTH_PWD);
		}
	}

}
