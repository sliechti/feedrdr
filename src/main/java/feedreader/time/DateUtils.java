package feedreader.time;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Locale;

import feedreader.feed.utils.FetchConstants;
import feedreader.parser.XmlFeedParser;

public class DateUtils {

    public static long asLongDate(String date, XmlFeedParser.NewsFlavor hint) {
        return asLongDate(date, hint, 0L);
    }

    public static String formatDate(long date, String langStr) {
        DateFormat df = SimpleDateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL,
                Locale.forLanguageTag(langStr));
        return df.format(date);
    }

    /**
     * Move to FeedDateUtils.
     * 
     * @param date
     *            date time as String.
     * @param hint
     * @param defVal
     * @return
     */
    public static long asLongDate(String date, XmlFeedParser.NewsFlavor hint, long defVal) {
        SimpleDateFormat[] fmts;

        switch (hint) {
        default:
        case RSS2:
            fmts = FetchConstants.RSS_DATE_FORMATS;
            break;

        case ATOM1:
            fmts = FetchConstants.ATOM_DATE_FORMATS;
            break;

        case RDF:
            fmts = FetchConstants.RDF_DATE_FORMATS;
            break;
        }

        for (int x = 0; x < fmts.length; x++) {
            SimpleDateFormat fmt = fmts[x];

            try {
                return fmt.parse(date).getTime();
            } catch (ParseException e) {
                // This is our fix for timezones with ":" in it.
                ParsePosition position = new ParsePosition(0);
                fmt.parse(date, position);

                int errIndex = position.getErrorIndex();

                if (errIndex > 10 && errIndex < date.length() && date.charAt(errIndex) == ':') {
                    StringBuilder sb = new StringBuilder();

                    for (int l = 0; l < date.length(); l++) {
                        if (l == errIndex)
                            continue;

                        sb.append(date.charAt(l));
                    }

                    date = sb.toString();
                    x--;
                }
            }
        }

        return defVal;
    }

}
