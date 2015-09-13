package feedreader.utils;

import feedreader.config.FeedAppConfig;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PermissionUtils {
    
    static Calendar c = Calendar.getInstance(Locale.getDefault());

    public static long getMaxHistoryTime(int userType) {
        c.setTime(new Date());

        switch (userType) {
        case 1:
            c.add(Calendar.DAY_OF_YEAR, FeedAppConfig.USER_1_MAX_DAYS_BACK);
            break;

        case 2:
            c.add(Calendar.DAY_OF_YEAR, FeedAppConfig.USER_2_MAX_DAYS_BACK);
            break;

        default:
        case 0:
            c.add(Calendar.DAY_OF_YEAR, FeedAppConfig.USER_0_MAX_DAYS_BACK);
            break;
        }

        return c.getTimeInMillis();
    }
}
