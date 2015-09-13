package feedreader.parser.test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import feedreader.parser.XmlFeedEntry;
import feedreader.parser.XmlFeedParser;
import feedreader.utils.test.LoadTestResourceFile;

public class XmlFeedParserTest {

    @Ignore
    public void fetchUrl() throws Exception {
        URL url = new URL("http://www.adafruit.com/blog/feed/");

        InputStream is = url.openStream();

        int c;
        while ((c = is.read()) != -1) {
            System.out.print((char) c);
        }
    }

    @Ignore
    protected class NewsCallback implements XmlFeedParser.XmlFeedParserCallback {

        public XmlFeedParser parser;

        @Override
        public void onXmlEntryFound(XmlFeedEntry entry) {
            /*
             * Logger.debug("title ").log("[").log(entry.getTitle()).log("]").end();
             * Logger.debug("link  ").log("[").log(entry.getLink()).log("]").end();
             * 
             * long entryDate = entry.getEntryDate(); if (entryDate <= 0) {
             * Logger.warning(this.getClass().getSimpleName())
             * .log("Article's publication date not found. Trying with source's date.").end();
             * 
             * entryDate = parser.getSourceDate(); }
             * 
             * if (entryDate <= 0) { Logger.error(this.getClass().getSimpleName())
             * .log("Error parsing source's publication date.").end(); Assert.fail("No publication date."); }
             * 
             * Logger.debug("date   ").log(new Date(entryDate)).end();
             * Logger.debug("author ").log("[").log(entry.getAuthor()).log("]").end();
             * Logger.debug("csize  ").log("[").log(entry.getContentSize()).log("]").end();
             * Logger.debug(" ---------------------------------------").end();
             */
        }

        @Override
        public void onEndDocument() {
        }
    }

    /*
     * TODO: Create unit tests of: - Different feeds, atom, rss. Different types. - Parsing atom, rss date times.
     */
    @Test
    public void parseXmlNews() throws Exception {
        String sourceUrl = "rss/test.channel.xml";

        FileInputStream fis = LoadTestResourceFile.asFileStream(sourceUrl);

        XmlFeedParser parser = new XmlFeedParser(sourceUrl, new NewsCallback());

        Assert.assertTrue("Error instantiating xml news parser.", (parser != null));

        parser.parse(fis);

        System.out.println(parser.getChannelData());
        System.out.println(parser.getChannelImage());
    }

}
