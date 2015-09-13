package feedreader.cron;

import feedreader.config.FeedAppConfig;
import feedreader.log.Logger;
import feedreader.store.DBFields;
import feedreader.store.Database;
import feedreader.store.UsersTable;
import feedreader.utils.SimpleMail;
import feedreader.utils.TextUtils;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletContext;

public class CronForgotPasswordEmail implements Runnable
{
    static final Class<?> clz = CronForgotPasswordEmail.class;
    long createdAt = 0;

    SimpleMail mail = new SimpleMail();
    
    static final String MAIL_FROM = FeedAppConfig.MAIL_REG_FROM;
    static final String MAIL_FROM_NAME = "Registration " + FeedAppConfig.APP_NAME;
    
    String emailTmpl = "";
    static final String encKey = "REGISTRATION";
    
    public CronForgotPasswordEmail(long time, ServletContext ctx)
    {
        Logger.info(clz).log("starting: running every ").log(FeedAppConfig.DELAY_CHECK_FORGOT_PASSWORD).log(" seconds. ").end();
        createdAt = time;
        
        InputStream fis = ctx.getResourceAsStream("/WEB-INF/tmpl/forgotpwd.tmpl");

        StringBuilder sb = new StringBuilder();
        try
        {
            sb = TextUtils.toStringBuilder(fis, sb, true);
        } catch (IOException ex)
        {
            Logger.error(clz).log(ex.getMessage()).end();
        }

        emailTmpl = sb.toString();              
    }
    
    @Override
    public void run()
    {
        try
        {
            String query = String.format("SELECT %s, %s, %s, %s FROM %s WHERE %s = %b",
                    DBFields.LONG_USER_ID, DBFields.STR_EMAIL, DBFields.STR_SCREEN_NAME, DBFields.STR_FORGOT_CODE,
                    UsersTable.TABLE,
                    DBFields.BOOL_FORGOT_PWD, true);
            ResultSet rs = Database.rawQuery(query);
            
            int c = 0;
            while (rs.next())
            {
                long userId = rs.getLong(DBFields.LONG_USER_ID);
                String email = rs.getString(DBFields.STR_EMAIL);
                String screenName = rs.getString(DBFields.STR_SCREEN_NAME);
                String forgotCode = rs.getString(DBFields.STR_FORGOT_CODE);
                
                Logger.info(clz).log("Sending password forgotten email to '").log(screenName).log("', email '")
                        .log(email).log("'").end();
                
                try
                {
                    String emailTxt = new String().concat(emailTmpl);
                    emailTxt = emailTxt.replace("{NAME}", screenName);
                    emailTxt = emailTxt.replace("{CODE}", forgotCode);
                    String link = FeedAppConfig.BASE_APP_URL_EMAIL + "/reset.jsp?code=" + forgotCode;
                    emailTxt = emailTxt.replace("{LINK}", link);
                    
                    mail.send(MAIL_FROM, MAIL_FROM_NAME, email, screenName,
                            "Password reset", emailTxt);
                    
                    query = String.format("UPDATE %s SET %s = false WHERE %s = %d", 
                        UsersTable.TABLE, DBFields.BOOL_FORGOT_PWD, DBFields.LONG_USER_ID, userId);
                
                    Database.getStatement().execute(query);   
                }
                catch (Exception ex)
                {
                    Logger.error(clz).log("error sending message, ").log(ex.getMessage()).end();
                }
                
                if (++c >= FeedAppConfig.MAX_FORGOTTEN_EMAILS_PER_RUN) {
                    return;
                }                
            }
        } 
        catch (SQLException ex)
        {
            Logger.error(clz).log("error, ").log(ex.getMessage()).end();
        }
    }

}
