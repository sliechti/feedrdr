package feedreader.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Future;

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
import feedreader.cron.CronResendRegEmail;
import feedreader.utils.SimpleEmail.SendCallback;

/**
 * https://javamail.java.net/nonav/docs/api/
 */
public class SimpleEmail {

    public static String verificationTmpl;

    private static SimpleEmail instance;
    private static final Logger logger = LoggerFactory.getLogger(SimpleEmail.class);
    private static final String PROP_SMTP_PWD = "mail.smtp.password";
    private static final String PROP_SMTP_USER = "mail.smtp.user";
    private static final String TEXT_PLAIN = "text/plain";
    private final String authPwd;
    private final String authUser;
    private final Session session;

    private SimpleEmail() {
        Properties props = new Properties();
        try {
            InputStream is = ClassLoader.getSystemResourceAsStream("mail.properties");
            props.load(is);
        } catch (IOException e) {
            logger.error("failed to init: {}", e, e.getMessage());
            throw new RuntimeException("mail.properties not found: failed to start");
        }
        authUser = props.getProperty(PROP_SMTP_USER);
        authPwd = props.getProperty(PROP_SMTP_PWD);
        session = Session.getInstance(props, new SMTPAuthenticator());
        verificationTmpl = ResourceUtils.loadResource("templates/newregistration.tmpl");
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

    public void sendAccountDisabled(String email, String reason) {
        logger.warn("implement, send an email to {} explaining why the account was disabled. reason: {}",
                email, reason);
        // TODO SLU: Implement
    }

    public void sendAsync(EmailParamsBuilder builder) {
        sendAsync(builder, SendCallback.NULL);
    }

    public void sendAsync(EmailParamsBuilder params, SendCallback callback) {
        // TODO SLU: Implement
        callback.failed("not implemented yet");
    }

    /**
     * @deprecated use {@link #sendAsync} instead.
     */
    @Deprecated
    public String sendVerificationEmail(String screenName, String regCode, String email) {
        String error = "";
        try {
            String emailTxt = new String().concat(verificationTmpl);
            emailTxt = emailTxt.replace("{NAME}", screenName);
            emailTxt = emailTxt.replace("{CODE}", regCode);
            String link = FeedAppConfig.BASE_APP_URL_EMAIL + "/verify?code=" + regCode;
            emailTxt = emailTxt.replace("{LINK}", link);
            send(FeedAppConfig.MAIL_REG_FROM, FeedAppConfig.MAIL_REG_FROM,
                    email, screenName,
                    "Welcome to feedrdr, " + screenName, emailTxt);
        } catch (Exception e) {
            CronResendRegEmail.logger.error("failed to send email to: {}, error: {}", e, email, e.getMessage());
            error = e.getMessage();
        }
        return error;
    }

    public static SimpleEmail getInstance() {
        if (instance == null) {
            synchronized (SimpleEmail.class) {
                if (instance == null) {
                    instance = new SimpleEmail();
                }
            }
        }
        return instance;
    }

    public interface SendCallback {
        SendCallback NULL = new SendCallback() {
            @Override
            public void failed(String reason) {
                // noop
            }

            @Override
            public void sent() {
                // noop
            }
        };

        void failed(String reason);

        void sent();
    }

    private class SMTPAuthenticator extends Authenticator {
        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(authUser, authPwd);
        }
    }

}
