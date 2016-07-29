package feedreader.cron;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feedreader.config.FeedAppConfig;
import feedreader.store.DBFields;
import feedreader.store.Database;
import feedreader.store.UsersTable;
import feedreader.utils.SQLUtils;
import feedreader.utils.SimpleEmail;

public class CronResendRegEmail implements Runnable {

    public static final Logger logger = LoggerFactory.getLogger(CronResendRegEmail.class);
    private static final List<Integer> attemps = Arrays.asList(24, 36, 42, 48);

    public CronResendRegEmail() throws Exception {
        logger.info("starting, running every: {} seconds", FeedAppConfig.DELAY_CHECK_REG_EMAIL);
    }

    @Override
    public void run() {
        for (int x = 0; x < attemps.size(); x++) {
            checkVerification(attemps.get(x), x);
        }
    }

    private void checkVerification(long hoursElapsed, int attempt) {
        try (Connection conn = Database.getConnection()) {
            String query = String.format("SELECT user_id, s_email, t_subscribed_at "
                    + " FROM feedreader.users u "
                    + "   WHERE     u.b_verified = FALSE "
                    + " AND verify_attempt > %s"
                    + " AND TO_TIMESTAMP (t_subscribed_at / 1000) > (current_timestamp - INTERVAL '%s hours')",
                    attempt,
                    hoursElapsed);
            ResultSet rs = Database.rawQuery(conn, query);
            int count = 0;
            while (rs.next()) {
                long userId = rs.getLong(DBFields.LONG_USER_ID);
                String email = rs.getString(DBFields.STR_EMAIL);
                String regCode = rs.getString(DBFields.STR_REG_CODE);

                logger.info("resending registration to {} after {} hours", email, hoursElapsed);
                String error = SimpleEmail.getInstance().sendVerificationEmail(email, regCode, email);
                query = String.format("UPDATE feedreader.users "
                        + "SET verify_attempt = %d, s_reg_error = '%s' WHERE l_user_id = %d",
                        attempt,
                        SQLUtils.asSafeString(error),
                        userId);

                conn.createStatement().execute(query);

                if (attempt == attemps.size()) {
                    UsersTable.disableAccount(userId, "No verification after " + hoursElapsed + " hours");
                }
                if (++count >= FeedAppConfig.MAX_REG_EMAILS_PER_RUN) {
                    break;
                }
            }
        } catch (SQLException e) {
            logger.error("checkVerification {}, {} error: {}", e, hoursElapsed, attempt, e.getMessage());
        }
    }

}
