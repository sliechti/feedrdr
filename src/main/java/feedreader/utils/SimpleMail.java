package feedreader.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feedreader.config.FeedAppConfig;

/**
 * https://javamail.java.net/nonav/docs/api/
 */
public class SimpleMail {

    private static final Logger logger = LoggerFactory.getLogger(SimpleMail.class);
    private static final String PROP_SMTP_PWD = "mail.smtp.password";
    private static final String PROP_SMTP_USER = "mail.smtp.user";
    private static final String TEXT_PLAIN = "text/plain";
    private final String authPwd;
    private final String authUser;
    private final Session session;

    public SimpleMail() {
        Properties props = new Properties();
        try {
            InputStream is = SimpleMail.class.getClassLoader().getResourceAsStream("mail.properties");
            props.load(is);
        } catch (IOException e) {
            logger.error("failed to init: {}", e, e.getMessage());
            throw new RuntimeException("mail.properties not found: failed to start " + SimpleMail.class.getName());
        }
        authUser = props.getProperty(PROP_SMTP_USER);
        authPwd = props.getProperty(PROP_SMTP_PWD);
        session = Session.getInstance(props, new SMTPAuthenticator());
    }

    public void send(String from,
            String fromName,
            String to,
            String toName,
            String subject,
            String plainText) throws Exception {
        Transport transport = session.getTransport();
        MimeMessage message = new MimeMessage(session);
        message.setContent(plainText, TEXT_PLAIN);
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
            return new PasswordAuthentication(authUser, authPwd);
        }
    }

}
