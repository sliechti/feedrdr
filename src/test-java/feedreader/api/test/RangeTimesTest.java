package feedreader.api.test;

import feedreader.utils.RangeTimes;
import feedreader.utils.RangeTimes.Range;

import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("deprecation")
public class RangeTimesTest 
{
    @Test
    public void simeplTest()
    {
        String timeFilter = "s:1419860380000,e:1419859720000";
        RangeTimes rt = new RangeTimes(timeFilter);
        Assert.assertEquals(1, rt.getRangeCount());
    }
    
    @Test
    public void overLapTest()
    {
        String startFilter = "s:1419860380000,e:1419859720000";
        String newFilter = "s:1419860380000,e:1419681330000";
        
        RangeTimes rt = new RangeTimes(startFilter);
        
        rt.add(newFilter);
        
        TreeSet<Range> ranges = rt.getRanges();
        Assert.assertEquals(1, ranges.size());
        
        Assert.assertEquals(1419860380000L, ranges.first().getStart());
        Assert.assertEquals(1419681330000L, ranges.first().getEnd());
    }
    
    @Test
    public void noOverlapTest()
    {
        // Mon 29 Dec 2014 08:39:40 AM EST GMT-5:00
        // Mon 29 Dec 2014 08:28:40 AM EST GMT-5:00
        // Starts first, ends last.
        String startFilter = "s:1419860380000,e:1419859720000";
        // 1419872400000 Mon 29 Dec 2014 12:00:00 AM EST
        // 1419868800000 Mon 29 Dec 2014 11:00:00 AM EST
        // Starts last, ends first.
        String newFilter = "s:1419872400000,e:1419868800000";
        
        RangeTimes rt = new RangeTimes(startFilter);
        
        rt.add(newFilter);
        
        TreeSet<Range> ranges = rt.getRanges();
        Assert.assertEquals(2, ranges.size());
        
        Assert.assertEquals(1419872400000L, ranges.first().getStart());
        Assert.assertEquals(1419868800000L, ranges.first().getEnd());
        Assert.assertEquals(newFilter, ranges.first().serialize());
        
        Assert.assertEquals(1419860380000L, ranges.last().getStart());
        Assert.assertEquals(1419859720000L, ranges.last().getEnd());
        Assert.assertEquals(startFilter, ranges.last().serialize());
        
        // s:1419872400000,e:1419868800000|s:1419860380000,e:1419859720000
        Assert.assertEquals(newFilter + "|" + startFilter, rt.serialize());
    }    
    
    @Test
    public void toSqlTest()
    {
        // Mon 29 Dec 2014 08:39:40 AM EST GMT-5:00
        // Mon 29 Dec 2014 08:28:40 AM EST GMT-5:00
        // Starts first, ends last.
        String startFilter = "s:1419860380000,e:1419859720000";
        RangeTimes rt = new RangeTimes(startFilter);
        
        for (Range r : rt.getRanges()) {
            Assert.assertEquals("t_pub_date<=1419860380000 AND t_pub_date>=1419859720000",
                    r.toSqlString("t_pub_date"));
        }
    }    
    
    @Test
    public void removeEmptyEntries()
    {
        String filter = "s:1419860380000,e:1419859720000|s:0,e:0";
        RangeTimes rt = new RangeTimes(filter);
        Assert.assertEquals(1, rt.getRangeCount());
    }
    
}
