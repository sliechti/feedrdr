package feedreader.fetch.test;

import feedreader.log.Logger;
import feedreader.parser.XmlFeedEntry;
import feedreader.parser.XmlFeedParser;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

public class FetchLiveURLTest {

    static Class<?> clz = FetchLiveURLTest.class;

    @Ignore
    protected class NewsCallback implements XmlFeedParser.XmlFeedParserCallback {

        public XmlFeedParser parser;

        @Override
        public void onXmlEntryFound(XmlFeedEntry entry) {
            Logger.debug(clz).log("entry found.").end();
        }

        @Override
        public void onEndDocument() {
        }
    }

    @Test
    public void testLive() throws MalformedURLException, IOException, SAXException, URISyntaxException {
        URL url = new URL("http://www.mmsonline.com/rss/home/latest");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(1000);

        XmlFeedParser parser = new XmlFeedParser(url.toURI().toString(), new NewsCallback());

        parser.parse(conn.getInputStream());
    }
}
