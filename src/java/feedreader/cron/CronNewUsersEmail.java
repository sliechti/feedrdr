package feedreader.cron;

import feedreader.config.FeedAppConfig;
import feedreader.log.Logger;
import feedreader.store.DBFields;
import feedreader.store.Database;
import feedreader.store.UsersTable;
import feedreader.utils.SQLUtils;
import feedreader.utils.SimpleMail;
import feedreader.utils.TextUtils;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletContext;

public class CronNewUsersEmail implements Runnable
{
    static final Class<?> clz = CronNewUsersEmail.class;
            
    long createdAt = 0;

    SimpleMail mail = new SimpleMail();
    
    static final String MAIL_FROM = FeedAppConfig.MAIL_REG_FROM;
    static final String MAIL_FROM_NAME = "Registration " + FeedAppConfig.APP_NAME;
    
    String emailTmpl = "";
    static final String encKey = "REGISTRATION";
    
    public CronNewUsersEmail(long time, ServletContext ctx)
    {
        Logger.info(clz).log("starting. running every ").log( FeedAppConfig.DELAY_CHECK_NEW_USERS_EMAIL).log(" seconds.").end();
        createdAt = time;
        
        InputStream fis = ctx.getResourceAsStream("/WEB-INF/tmpl/newregistration.tmpl");

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
                    DBFields.LONG_USER_ID, DBFields.STR_EMAIL, DBFields.STR_SCREEN_NAME, DBFields.STR_REG_CODE,
                    UsersTable.TABLE,
                    DBFields.BOOL_REG_SENT, false);
            ResultSet rs = Database.rawQuery(query);
            
            int c = 0;
            
            while (rs.next())
            {
                long userId = rs.getLong(DBFields.LONG_USER_ID);
                String email = rs.getString(DBFields.STR_EMAIL);
                String screenName = rs.getString(DBFields.STR_SCREEN_NAME);
                String regCode = rs.getString(DBFields.STR_REG_CODE);
                
                Logger.info(clz).log("Sending new registration email to '").log(screenName).log("', email '")
                        .log(email).log("'").end();
                
                String error = "";
                
                try
                {
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
                }
                catch (Exception ex)
                {
                    Logger.error(clz).log("error sending message, ").log(ex.getMessage()).end();
                    error = ex.getMessage();
                }
                
                query = String.format("UPDATE %s SET %s = %b, %s = '%s' WHERE %s = %d", 
                        UsersTable.TABLE, DBFields.BOOL_REG_SENT, true,
                        DBFields.STR_REG_ERROR, SQLUtils.asSafeString(error),
                        DBFields.LONG_USER_ID, userId);

                Database.getStatement().execute(query);            
                
                if (++c >= FeedAppConfig.MAX_REG_EMAILS_PER_RUN) 
                {
                    break;
                }       
            }
        } 
        catch (SQLException ex)
        {
            Logger.error(clz).log("error, ").log(ex.getMessage()).end();
        }        
    }

}
