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

public class SimpleMail {

    static String SMTP_HOST_NAME = "see mail.*.properties";
    static String SMTP_AUTH_USER = "see mail.*.properties";
    static String SMTP_AUTH_PWD = "see mail.*.properties";

    Authenticator auth;
    Session mailSession;
    
    public SimpleMail() {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", SMTP_HOST_NAME);
        props.put("mail.smtp.auth", "true");
        mailSession = Session.getInstance(props, new SMTPAuthenticator());
    }

    public static void configure(String host, String user, String pwd) {
        SMTP_HOST_NAME = host;
        SMTP_AUTH_USER = user;
        SMTP_AUTH_PWD = pwd;
    }
    
    public void send(String from, String fromName, String to, String toName, String subject, String plainText)
            throws Exception {
        mailSession.setDebug(FeedAppConfig.DEBUG_EMAIL);

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
            String username = SMTP_AUTH_USER;
            String password = SMTP_AUTH_PWD;
            return new PasswordAuthentication(username, password);
        }
    }

}
