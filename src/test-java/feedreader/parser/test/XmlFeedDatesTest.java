package feedreader.parser.test;

import org.junit.Assert;
import org.junit.Test;

import feedreader.parser.XmlFeedParser;
import feedreader.time.DateUtils;

public class XmlFeedDatesTest
{

    @Test
    public void testRSSDates()
    {
        String date = "Fri, 26 Apr 2013 16:44:06 +0000";
        long parsedDate = DateUtils.asLongDate(date, XmlFeedParser.NewsFlavor.RSS2);
        Assert.assertEquals(1366994646000L, parsedDate);

        // Engadget. Without timezone.
        date = "Sat, 27 Apr 2013 19:24:00";
        parsedDate = DateUtils.asLongDate(date, XmlFeedParser.NewsFlavor.RSS2);
        Assert.assertEquals(1367105040000L, parsedDate);
    }

    @Test
    public void testRDFDates()
    {
        String date = "2013-04-25T20:30:00-05:00";
        long parsedDate1 = DateUtils.asLongDate(date, XmlFeedParser.NewsFlavor.RDF);
        Assert.assertEquals(1366939800000L, parsedDate1);
    }

    @Test
    public void testATOMDates()
    {
        String date = "2012-11-23T03:29:00.001-08:00";
        long parsedDate1 = DateUtils.asLongDate(date, XmlFeedParser.NewsFlavor.ATOM1);
        Assert.assertEquals(1353670140001L, parsedDate1);
    }

}
