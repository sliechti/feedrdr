package feedreader.parser.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import feedreader.log.Logger;
import feedreader.parser.XmlFeedEntry;
import feedreader.parser.XmlFeedParser;
import feedreader.parser.XmlLinkDef;
import feedreader.utils.test.LoadTestResourceFile;

public class LinkParserTest {

    private static final String TEST_CASES_XML = "tests/linkTestCases.xml";
    private static final String CHANNEL_LINK = "http://channellink.com/channellink/test";
    private static final Class<?> clz = ImageParserTest.class;

    private static class DebugLinkCallback implements XmlFeedParser.XmlFeedParserCallback {

        public ArrayList<String> links = new ArrayList<String>();
        public HashSet<XmlLinkDef> contentLinks = new HashSet<>();

        @Override public void onXmlEntryFound(XmlFeedEntry entry) {
            String link = entry.getLink();
            if (!link.isEmpty()) {
                links.add(link);
            }

            contentLinks.addAll(entry.getContentLinks());
            Logger.debug(clz).log("link [").log(link).log("] content links [")
                .log(entry.getContentLinks().toString()).log("]").end();
        }

        private int getLinkCount() {
            return links.size();
        }

        public int getContentLinkCount() {
            return contentLinks.size();
        }

        @Override public void onEndDocument() {
        }

    }

    public static void runTest() throws IOException, FileNotFoundException, SAXException {
        Logger.get().setLevel(Logger.LogLevels.DEBUG);

        DebugLinkCallback cb = new DebugLinkCallback();

        test(TEST_CASES_XML, cb);

        Assert.assertEquals(7, cb.getLinkCount());

        for (int x = 0; x < cb.links.size(); x++) {
            String l = cb.links.get(x);
            Assert.assertEquals(false, l.isEmpty());
            Assert.assertEquals(true, l.startsWith("http://link.com/"));
            Assert.assertEquals(true, l.endsWith("#" + (x + 1)));
        }

        Assert.assertEquals(6, cb.getContentLinkCount());
    }

    @Test public static void test(String sourceUrl, XmlFeedParser.XmlFeedParserCallback cb)
            throws FileNotFoundException, IOException, SAXException {
        Logger.debug(clz).log("links test ").log(sourceUrl).end();
        XmlFeedParser parser = new XmlFeedParser(sourceUrl, cb);
        parser.parse(LoadTestResourceFile.asFileStream(sourceUrl));

        Assert.assertEquals(CHANNEL_LINK, parser.getChannelData().getLink());
    }

    public static void main(String args[]) throws FileNotFoundException, IOException, SAXException {
        runTest();
    }
    
}
