package feedreader.opml;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import feedreader.entities.OPMLEntry;

/**
 * TODO: Document
 */
public class OPMLParser {

    public interface Callback {

        public void onEntry(OPMLEntry entry);

        public void onDirectoryStart(String name);

        public void onDirectoryEnd();

        public void onBodyStart();

        public void onBodyEnd();
    }

    private SAXParser parser;
    private Callback cb;

    /**
     *
     * @param cb
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public OPMLParser(Callback cb) throws ParserConfigurationException, SAXException {
        this.parser = SAXParserFactory.newInstance().newSAXParser();
        this.cb = cb;
    }

    /**
     *
     * @param is
     * @throws java.io.IOException
     * @throws SAXException
     */
    public void parse(InputStream is) throws IOException, SAXException {
        parser.parse(is, new OPMLHandler());
    }

    class OPMLHandler extends DefaultHandler {

        private boolean inBody = false;
        private boolean inOutlineEntry = false;

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (!inOutlineEntry && qName.equalsIgnoreCase("body")) {
                cb.onBodyEnd();
            } else if (!inOutlineEntry && qName.equalsIgnoreCase("outline")) {
                cb.onDirectoryEnd();
            }

            inOutlineEntry = false;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attr) throws SAXException {
            if (qName.equalsIgnoreCase("body")) {
                inBody = true;
                cb.onBodyStart();
                return;
            }

            if (!inBody) {
                return;
            }

            // TODO: What if the outline has no title? What if the the entry has no xmlUrl.
            // Needs to catch more cases. log.error them.
            if (qName.equalsIgnoreCase("outline") && attr.getValue("xmlUrl") == null) {
                cb.onDirectoryStart(attr.getValue("title"));
            } else {
                inOutlineEntry = true;

                String title = attr.getValue("title");
                String xmlUrl = attr.getValue("xmlUrl");

                OPMLEntry entry = new OPMLEntry(title, xmlUrl);

                cb.onEntry(entry);
            }
        }
    }

}
