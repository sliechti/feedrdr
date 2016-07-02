package feedreader.parser.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import feedreader.log.Logger;
import feedreader.parser.XmlFeedEntry;
import feedreader.parser.XmlFeedParser;
import feedreader.utils.test.LoadTestResourceFile;

public class ImageParserTest {

    private static final String IMAGE_TEST_CASES = "tests/imageTestCases.xml";
    private static final Class<?> clz = ImageParserTest.class;

    private static class DebugImageCallback implements XmlFeedParser.XmlFeedParserCallback {

        public List<String> thumbImages = new ArrayList<String>();
        int imgCount = 0;

        @Override public void onXmlEntryFound(XmlFeedEntry entry) {
            String img = entry.getImage();

            if (!img.isEmpty()) {
                imgCount++;
            }

            imgCount += entry.getContentImages().size();

            String imgUrl = entry.getThumbImg();
            thumbImages.add(imgUrl);
            Logger.debug(clz).log("thumb [").log(imgUrl).log("] image [").log(img).log("] ").log("content images [")
                    .log(entry.getContentImages().toString()).log("]").end();
        }

        private int getImageCount() {
            return imgCount;
        }

        @Override public void onEndDocument() {
        }

        public List<String> getThumbImages() {
            return thumbImages;
        }
    }

    @Test public static void runTest() throws IOException, FileNotFoundException, SAXException {
        Logger.get().setLevel(Logger.LogLevels.DEBUG);
        DebugImageCallback cb = new DebugImageCallback();
        test(IMAGE_TEST_CASES, cb);
        Assert.assertEquals(5, cb.getImageCount());

        int x = 0;
        for (String imgThumb : cb.getThumbImages()) {
            Assert.assertEquals("http://link.com/img" + (++x) + ".jpg", imgThumb);
            
        }
    }

    @Ignore public static void test(String sourceUrl, XmlFeedParser.XmlFeedParserCallback cb) throws FileNotFoundException,
            IOException, SAXException {
        Logger.debug(clz).log("image test ").log(sourceUrl).end();
        XmlFeedParser parser = new XmlFeedParser(sourceUrl, cb);
        parser.parse(LoadTestResourceFile.asFileStream(sourceUrl));
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException, SAXException {
        runTest();
    }
}
