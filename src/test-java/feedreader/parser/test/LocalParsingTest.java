package feedreader.parser.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import feedreader.config.FeedAppConfig;
import feedreader.log.Logger;
import feedreader.parser.XmlFeedEntry;
import feedreader.parser.XmlFeedParser;
import feedreader.time.CurrentTime;
import feedreader.utils.test.LoadTestResourceFile;

public class LocalParsingTest {

    private static final String DOWNLOADED_XML = "downloaded/20132.f.xml";

    static {
        FeedAppConfig.DEFAULT_LOG_LEVEL = Logger.LogLevels.DEBUG.getVal();
    }

    static final Class<?> clz = LocalParsingTest.class;

    @Ignore
    protected static class DebugAllCallback implements XmlFeedParser.XmlFeedParserCallback {

        public XmlFeedParser parser;

        @Override
        public void onXmlEntryFound(XmlFeedEntry entry) {
            Logger.debug(clz).log(" --> ").end();
            long entryDate = entry.getEntryDate();
            if (entryDate <= 0) {
                Logger.warning(clz).log("Article's publication date not found. Trying with source's date.").end();
            }

            if (entryDate <= 0) {
                entryDate = CurrentTime.inGMT();
                entry.setEntryDate(entryDate);
//                Logger.error(clz).log("Error parsing source's publication date.").end();
//                Assert.fail("No publication date.");
            }

            // Logger.debug(entry.toString(true)).end();
            System.out.println(entry.getDescriptionSize());
            System.out.println(entry.getContentSize());
            // System.out.println("clean content: " + entry.getCleanContent());
            System.out.println("links " + entry.getContentLinks().size());
            System.out.println("link " + entry.getLink());
            System.out.println("date " + entry.getEntryDate());
            System.out.println("content images " + entry.getContentImages().size());
            System.out.println("content images " + Arrays.toString(entry.getContentImages().toArray()));
            System.out.println("thumb " + entry.getThumbImg());
            Logger.debug(clz).log(" <-- ").end();
        }

        @Override
        public void onEndDocument() {
        }
    }

    @Ignore
    protected class DebugImageCallback implements XmlFeedParser.XmlFeedParserCallback {

        @Override
        public void onXmlEntryFound(XmlFeedEntry entry) {
            Logger.debug(clz).log(entry.getTitle()).end();
            Logger.debug(clz).log("IMG \n[").log(entry.getImage()).log("]").end();
            // Logger.debug(clz).log(entry.getDescription()).end();
        }

        @Override
        public void onEndDocument() {
        }
    }

    @Test
    public static void testOne() throws IOException, FileNotFoundException, SAXException {
        // test("downloaded/14403.xml", new DebugAllCallback());
        // test("downloaded/3.xml", new DebugAllCallback());
//        test("xml/androidcentral.f.xml", new DebugAllCallback());
        test(DOWNLOADED_XML, new DebugAllCallback());
    }

    // @Test
    // public void testTwo() throws IOException, FileNotFoundException, SAXException {
    // test("downloaded/14420.xml", new DebugImageCallback());
    // }

    @Ignore
    public static void test(String sourceUrl, XmlFeedParser.XmlFeedParserCallback cb) throws FileNotFoundException,
            IOException, SAXException {
        Logger.debug(clz).log("testing ").log(sourceUrl).end();

        FileInputStream fis = LoadTestResourceFile.asFileStream(sourceUrl);

        XmlFeedParser parser = new XmlFeedParser(sourceUrl, cb);
        parser.setGatherInfo(true);
        parser.parse(fis);

        Logger.debug(clz).log(parser.getFlavor()).end();
        Logger.debug(clz).log(parser.getChannelData()).end();
        Logger.debug(clz).log(parser.getChannelImage()).end();

        System.out.println(parser.getFlavor());
    }
    
    public static void main(String args[]) throws FileNotFoundException, IOException, SAXException {
        testOne();
    }

}
