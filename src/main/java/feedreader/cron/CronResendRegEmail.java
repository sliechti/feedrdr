package feedreader.cron;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feedreader.config.FeedAppConfig;
import feedreader.store.DBFields;
import feedreader.store.Database;
import feedreader.store.UsersTable;
import feedreader.time.CurrentTime;
import feedreader.utils.ResourceUtils;
import feedreader.utils.SQLUtils;
import feedreader.utils.SimpleMail;

public class CronResendRegEmail implements Runnable {

	static final String encKey = "REGISTRATION";
	static final String MAIL_FROM = FeedAppConfig.MAIL_REG_FROM;
	static final String MAIL_FROM_NAME = "Registration " + FeedAppConfig.APP_NAME;
	private static final Logger logger = LoggerFactory.getLogger(CronResendRegEmail.class);
	private final String emailTmpl;
	private final SimpleMail mail = new SimpleMail();

	private final long FIRST_RESEND = CurrentTime.MILLIS_PER_DAY; //24 hours
	private final long SECOND_RESEND = (long) (CurrentTime.MILLIS_PER_DAY * 1.5); //36 hours
	private final long THIRD_RESEND = (long) (CurrentTime.MILLIS_PER_DAY * 1.75); //42 hours
	private final long DISABLE_ACCT = (long) (CurrentTime.MILLIS_PER_DAY * 2); //48 hours


	public CronResendRegEmail() throws Exception {
		logger.info("starting, running every: {} seconds", FeedAppConfig.DELAY_CHECK_REG_EMAIL);
		emailTmpl = ResourceUtils.loadResource("templates/newregistration.tmpl");
	}

	@Override
	public void run() {
		long currentTime = CurrentTime.inGMT();
		try {
			String query = String.format("SELECT %s, %s, %s, %s FROM %s WHERE %s = %b AND %s = %b AND %s = %b "
					+ "AND %s = %b AND (%d - %s) > %d",
					DBFields.LONG_USER_ID, DBFields.STR_EMAIL, DBFields.STR_SCREEN_NAME, DBFields.STR_REG_CODE,
					UsersTable.TABLE,
					DBFields.BOOL_REG_SENT, true,
					DBFields.BOOL_VERIFIED, false,
					DBFields.BOOL_ACCT_DISABLED, false,
					DBFields.BOOL_24_REG_SENT, false,
					currentTime, DBFields.TIME_SUBSCRIBED_AT, FIRST_RESEND
					);
			ResultSet rs = Database.rawQuery(query);
			int count = 0;
			while (rs.next()) {
				long userId = rs.getLong(DBFields.LONG_USER_ID);
				String email = rs.getString(DBFields.STR_EMAIL);
				String screenName = rs.getString(DBFields.STR_SCREEN_NAME);
				String regCode = rs.getString(DBFields.STR_REG_CODE);

				logger.info("resending registration for {} after 24 hours, sending email to {}", screenName, email);
				String error = resendEmail(screenName, regCode, email, userId);
				query = String.format("UPDATE %s SET %s = %b, %s = '%s' WHERE %s = %d",
						UsersTable.TABLE, DBFields.BOOL_24_REG_SENT, true,
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

		try {
			String query = String.format("SELECT %s, %s, %s, %s FROM %s WHERE %s = %b AND %s = %b AND %s = %b "
					+ "AND %s = %b AND (%d - %s) > %d",
					DBFields.LONG_USER_ID, DBFields.STR_EMAIL, DBFields.STR_SCREEN_NAME, DBFields.STR_REG_CODE,
					UsersTable.TABLE,
					DBFields.BOOL_REG_SENT, true,
					DBFields.BOOL_VERIFIED, false,
					DBFields.BOOL_ACCT_DISABLED, false,
					DBFields.BOOL_36_REG_SENT, false,
					currentTime, DBFields.TIME_SUBSCRIBED_AT, SECOND_RESEND
					);
			ResultSet rs = Database.rawQuery(query);
			int count = 0;
			while (rs.next()) {
				long userId = rs.getLong(DBFields.LONG_USER_ID);
				String email = rs.getString(DBFields.STR_EMAIL);
				String screenName = rs.getString(DBFields.STR_SCREEN_NAME);
				String regCode = rs.getString(DBFields.STR_REG_CODE);

				logger.info("resending registration for {} after 36 hours, sending email to {}", screenName, email);
				String error = resendEmail(screenName, regCode, email, userId);
				query = String.format("UPDATE %s SET %s = %b, %s = '%s' WHERE %s = %d",
						UsersTable.TABLE, DBFields.BOOL_36_REG_SENT, true,
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

		try {
			String query = String.format("SELECT %s, %s, %s, %s FROM %s WHERE %s = %b AND %s = %b AND %s = %b "
					+ "AND %s = %b AND (%d - %s) > %d",
					DBFields.LONG_USER_ID, DBFields.STR_EMAIL, DBFields.STR_SCREEN_NAME, DBFields.STR_REG_CODE,
					UsersTable.TABLE,
					DBFields.BOOL_REG_SENT, true,
					DBFields.BOOL_VERIFIED, false,
					DBFields.BOOL_ACCT_DISABLED, false,
					DBFields.BOOL_42_REG_SENT, false,
					currentTime, DBFields.TIME_SUBSCRIBED_AT, THIRD_RESEND
					);
			ResultSet rs = Database.rawQuery(query);
			int count = 0;
			while (rs.next()) {
				long userId = rs.getLong(DBFields.LONG_USER_ID);
				String email = rs.getString(DBFields.STR_EMAIL);
				String screenName = rs.getString(DBFields.STR_SCREEN_NAME);
				String regCode = rs.getString(DBFields.STR_REG_CODE);

				logger.info("resending registration for {} after 42 hours, sending email to {}", screenName, email);
				String error = resendEmail(screenName, regCode, email, userId);
				query = String.format("UPDATE %s SET %s = %b, %s = '%s' WHERE %s = %d",
						UsersTable.TABLE, DBFields.BOOL_42_REG_SENT, true,
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
		
		try {
			String query = String.format("SELECT %s, %s FROM %s WHERE %s = %b AND %s = %b AND %s = %b "
					+ "AND (%d - %s) > %d",
					DBFields.LONG_USER_ID, DBFields.STR_SCREEN_NAME,
					UsersTable.TABLE,
					DBFields.BOOL_REG_SENT, true,
					DBFields.BOOL_VERIFIED, false,
					DBFields.BOOL_ACCT_DISABLED, false,
					currentTime, DBFields.TIME_SUBSCRIBED_AT, DISABLE_ACCT
					);
			ResultSet rs = Database.rawQuery(query);
			int count = 0;
			while (rs.next()) {
				long userId = rs.getLong(DBFields.LONG_USER_ID);
				String screenName = rs.getString(DBFields.STR_SCREEN_NAME);

				logger.info("disabling account for {} after 48 hours", screenName);
				query = String.format("UPDATE %s SET %s = %b WHERE %s = %d",
						UsersTable.TABLE, DBFields.BOOL_ACCT_DISABLED, true,
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

	private String resendEmail(String screenName, String regCode, String email, long userId) {
		String error = "";
		try {
			String emailTxt = new String().concat(emailTmpl);
			emailTxt = emailTxt.replace("{NAME}", screenName);
			emailTxt = emailTxt.replace("{CODE}", regCode);
			String link = FeedAppConfig.BASE_APP_URL_EMAIL + "/verify?code=" + regCode;
			emailTxt = emailTxt.replace("{LINK}", link);
			mail.send(MAIL_FROM, MAIL_FROM_NAME, email, screenName,
					"Welcome to feedrdr, " + screenName, emailTxt);
		} catch (Exception e) {
			logger.error("failed to send email to: {}, error: {}", e, email, e.getMessage());
			error = e.getMessage();
		}
		return error;
	}

}
