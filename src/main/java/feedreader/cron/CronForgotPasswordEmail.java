package feedreader.cron;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feedreader.config.FeedAppConfig;
import feedreader.store.DBFields;
import feedreader.store.Database;
import feedreader.store.UsersTable;
import feedreader.utils.ClassUtils;
import feedreader.utils.SimpleEmail;

public class CronForgotPasswordEmail implements Runnable {

    static final String encKey = "REGISTRATION";
    static final String MAIL_FROM = FeedAppConfig.MAIL_REG_FROM;
    static final String MAIL_FROM_NAME = "Registration " + FeedAppConfig.APP_NAME;
    private static final Logger logger = LoggerFactory.getLogger(CronForgotPasswordEmail.class);
    private final String emailTmpl;

    public CronForgotPasswordEmail() throws Exception {
        logger.info("starting: running every {} seconds", FeedAppConfig.DELAY_CHECK_FORGOT_PASSWORD);
        InputStream is = ClassUtils.loadResource(this, "templates/forgotpwd.tmpl");
        emailTmpl = IOUtils.toString(is, "UTF-8");
    }

    @Override
    public void run() {
        try (Connection conn = Database.getConnection()) {
            String query = String.format("SELECT %s, %s, %s, %s FROM %s WHERE %s = %b",
                    DBFields.LONG_USER_ID, DBFields.STR_EMAIL, DBFields.STR_SCREEN_NAME, DBFields.STR_FORGOT_CODE,
                    UsersTable.TABLE, DBFields.BOOL_FORGOT_PWD, true);
            ResultSet rs = Database.rawQuery(conn, query);
            int count = 0;
            while (rs.next()) {
                long userId = rs.getLong(DBFields.LONG_USER_ID);
                String email = rs.getString(DBFields.STR_EMAIL);
                String forgotCode = rs.getString(DBFields.STR_FORGOT_CODE);

                logger.info("forgot sent to {}", email);

                try {
                    String emailTxt = new String().concat(emailTmpl);
                    emailTxt = emailTxt.replace("{CODE}", forgotCode);
                    String link = FeedAppConfig.BASE_APP_URL_EMAIL + "/password_reset?code=" + forgotCode;
                    emailTxt = emailTxt.replace("{LINK}", link);

                    SimpleEmail.getInstance().send(MAIL_FROM, MAIL_FROM_NAME, email, email, "Password reset", emailTxt);

                    query = String.format("UPDATE %s SET %s = false WHERE %s = %d",
                            UsersTable.TABLE, DBFields.BOOL_FORGOT_PWD, DBFields.LONG_USER_ID, userId);

                    conn.createStatement().execute(query);
                } catch (Exception ex) {
                    logger.error("error sending message: {}", ex, ex.getMessage());
                }

                if (++count >= FeedAppConfig.MAX_FORGOTTEN_EMAILS_PER_RUN) {
                    return;
                }
            }
        } catch (SQLException ex) {
            logger.error("sql error: {}", ex, ex.getMessage());
        }
    }

}
