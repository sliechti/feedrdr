package feedreader.cron;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpSession;

import feedreader.config.FeedAppConfig;
import feedreader.log.Logger;
import feedreader.security.Session;

/**
 * Document
 *
 */
public class CronTimeUtils implements Runnable {

    static final Class<?> clz = CronTimeUtils.class;

    Calendar c = Calendar.getInstance(TimeZone.getTimeZone(FeedAppConfig.DEFAULT_TIMEZONE));
    Date midnight = new Date();
    int currentDay = 0;
    static Date[] maxHistory = new Date[3];

    final void resetTime() {
        c.setTime(new Date());
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);

        int today = c.get(Calendar.DAY_OF_MONTH);
        if (today != currentDay) {
            midnight = c.getTime();
            Logger.info(clz).log("Midnight time: ").log(midnight).end();

            Calendar calc = Calendar.getInstance(TimeZone.getTimeZone(FeedAppConfig.DEFAULT_TIMEZONE));

            calc.setTimeInMillis(c.getTimeInMillis());
            calc.add(Calendar.DAY_OF_YEAR, FeedAppConfig.USER_0_MAX_DAYS_BACK);
            maxHistory[0] = calc.getTime();
            Logger.info(clz).log("maxHistory 0 changed: ").log(calc.getTime()).log(", ts ").log(calc.getTimeInMillis())
                    .end();

            calc.setTimeInMillis(c.getTimeInMillis());
            calc.add(Calendar.DAY_OF_YEAR, FeedAppConfig.USER_1_MAX_DAYS_BACK);
            maxHistory[1] = calc.getTime();
            Logger.info(clz).log("maxHistory 1 changed: ").log(calc.getTime()).log(", ts ").log(calc.getTimeInMillis())
                    .end();

            calc.setTimeInMillis(c.getTimeInMillis());
            calc.add(Calendar.DAY_OF_YEAR, FeedAppConfig.USER_2_MAX_DAYS_BACK);
            maxHistory[2] = calc.getTime();
            Logger.info(clz).log("maxHistory 2 changed: ").log(calc.getTime()).log(", ts ").log(calc.getTimeInMillis())
                    .end();

            currentDay = today;
            Logger.info(clz).log("current day changed: ").log(currentDay).end();
        }
    }

    /**
     * Use {@link #getMaxHistory(HttpSession)} instead. 
     */
    @Deprecated public static long getMaxHistory(int userType) {
        if (userType > maxHistory.length -1) {
            throw new IllegalArgumentException("UserType is outside of range, expecting 0-2");
        }

        return maxHistory[userType].getTime();
    }

    public static long getMaxHistory(HttpSession session) {
        int userType = Session.getUserType(session);
        if (userType > maxHistory.length -1) {
            throw new IllegalArgumentException("UserType is outside of range, expecting 0-2");
        }

        return maxHistory[userType].getTime();
    }
    
    public CronTimeUtils() {
        resetTime();
    }

    @Override
    public void run() {
        resetTime();
    }

}
