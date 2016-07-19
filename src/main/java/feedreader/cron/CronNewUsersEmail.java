package feedreader.cron;

import java.io.InputStream;
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
import feedreader.utils.SQLUtils;
import feedreader.utils.SimpleMail;

public class CronNewUsersEmail implements Runnable {

    static final String encKey = "REGISTRATION";
    static final String MAIL_FROM = FeedAppConfig.MAIL_REG_FROM;
    static final String MAIL_FROM_NAME = "Registration " + FeedAppConfig.APP_NAME;
    private static final Logger logger = LoggerFactory.getLogger(CronNewUsersEmail.class);
    private final String emailTmpl;
    private final SimpleMail mail = new SimpleMail();

    public CronNewUsersEmail() throws Exception {
        logger.info("starting, running every: {} seconds", FeedAppConfig.DELAY_CHECK_NEW_USERS_EMAIL);
        InputStream is = ClassUtils.loadResource(this, "templates/newregistration.tmpl");
        emailTmpl = IOUtils.toString(is,  "UTF-8");
    }

    @Override
    public void run() {
        try {
            String query = String.format("SELECT %s, %s, %s, %s FROM %s WHERE %s = %b",
                    DBFields.LONG_USER_ID, DBFields.STR_EMAIL, DBFields.STR_SCREEN_NAME, DBFields.STR_REG_CODE,
                    UsersTable.TABLE,
                    DBFields.BOOL_REG_SENT, false);
            ResultSet rs = Database.rawQuery(query);
            int count = 0;
            while (rs.next()) {
                long userId = rs.getLong(DBFields.LONG_USER_ID);
                String email = rs.getString(DBFields.STR_EMAIL);
                String screenName = rs.getString(DBFields.STR_SCREEN_NAME);
                String regCode = rs.getString(DBFields.STR_REG_CODE);

                logger.info("new registration {}, sending email to {}", screenName, email);
                String error = "";
                try {
                    String emailTxt = new String().concat(emailTmpl);
                    emailTxt = emailTxt.replace("{NAME}", screenName);
                    emailTxt = emailTxt.replace("{CODE}", regCode);
                    String link = FeedAppConfig.BASE_APP_URL_EMAIL + "/verify.jsp?code=" + regCode;
                    emailTxt = emailTxt.replace("{LINK}", link);
                    mail.send(MAIL_FROM, MAIL_FROM_NAME, email, screenName,
                            "Welcome to feedrdr, " + screenName, emailTxt);
                    query = String.format("UPDATE %s SET %s = true WHERE %s = %d",
                            UsersTable.TABLE, DBFields.BOOL_REG_SENT, DBFields.LONG_USER_ID, userId);
                    Database.getStatement().execute(query);
                } catch (Exception e) {
                    logger.error("failed to send email to: {}, error: {}", e, email, e.getMessage());
                    error = e.getMessage();
                }

                query = String.format("UPDATE %s SET %s = %b, %s = '%s' WHERE %s = %d",
                        UsersTable.TABLE, DBFields.BOOL_REG_SENT, true,
                        DBFields.STR_REG_ERROR, SQLUtils.asSafeString(error),
                        DBFields.LONG_USER_ID, userId);

                Database.getStatement().execute(query);

                if (++count >= FeedAppConfig.MAX_REG_EMAILS_PER_RUN) {
                    break;
                }
            }
        } catch (SQLException e) {
            logger.error("sql error: {}", e, e.getMessage());
        }
    }

}
