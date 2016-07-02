package feedreader.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import feedreader.entities.XmlChannelData;
import feedreader.entities.XmlChannelImage;
import feedreader.log.Logger;

/**
 * Xml News Parser class
 * 
 * See list of features {@see <a href='http://xerces.apache.org/xerces2-j/features.html'>apache.org/xerces2-j</a>}
 * 
 */
public class XmlFeedParser {

    static final Class<?> clz = XmlFeedParser.class;

    /** The owner XML, all news entries will have this as parent */
    private String xmlUrl;

    /** The internal parser. */
    private SAXParser parser;

    /** Callback */
    private XmlFeedParserCallback cb;

    /**
     * The recognized XML flavor for the news source.
     *
     * @see #getFlavor()
     */
    public enum NewsFlavor {
        UNKNOWN, RSS2, ATOM1, RDF
    };

    NewsFlavor flavor = NewsFlavor.UNKNOWN;

    XmlChannelData channelData = new XmlChannelData();

    XmlChannelImage channelImage = new XmlChannelImage();

    /**
     * Used as temporary buffer for when we want to pass the value of a node's attribute in cases where the node's value
     * is empty, e.g &lt;link href="http://.." &gt;EMPTY&lt;/..
     */
    String attrBuff = "";

    /**
     * Temporary object to hold found xml entries. Passed to the calling class via callback interface.
     *
     * @see XmlFeedParserCallback
     */
    XmlFeedEntry xmlFeedEntry;

    boolean gatherInfo = false;

    public HashMap<String, XmlQnode> nodeList = new HashMap<String, XmlQnode>();

    // TODO: Implement. Right now when gatherinfo is set to true all nodes are recorded into the nodelist.
    // add a explicit include rule with filter nodes, where only those nodes are recorded.
    HashSet<String> filterNodes = new HashSet<>();

    public interface XmlFeedParserCallback {

        /**
         * Callback interface to receive the news item just parsed. Be careful, the news entry will be cleared after you
         * give back control to the xml news parser class.
         *
         * @param news
         */
        public void onXmlEntryFound(XmlFeedEntry news);

        public void onEndDocument();
    }

    public class XmlFeedParserException extends SAXException {
        private static final long serialVersionUID = 1L;
        public XmlFeedParserException(String message) {
            super(message);
        }
    }

    public XmlFeedParser(String xmlUrl, XmlFeedParserCallback cb) {
        this.xmlUrl = xmlUrl;
        this.cb = cb;

        xmlFeedEntry = new XmlFeedEntry(xmlUrl);

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            // factory.setValidating(false);
            // factory.setNamespaceAware(false);
            // factory.setXIncludeAware(false);
            // SchemaFactory sFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            // factory.setSchema(sFactory.newSchema());
            parser = factory.newSAXParser();
            parser.getXMLReader().setFeature("http://apache.org/xml/features/continue-after-fatal-error", true);
            parser.getXMLReader().setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    public String getXmlUrl() {
        return xmlUrl;
    }

    public XmlChannelData getChannelData() {
        return channelData;
    }

    public XmlChannelImage getChannelImage() {
        return channelImage;
    }

    public NewsFlavor getFlavor() {
        return flavor;
    }

    private void setFlavor(NewsFlavor flavor) {
        this.flavor = flavor;
        xmlFeedEntry.flavor = flavor;
        channelData.flavor = flavor;
    }

    public void setGatherInfo(boolean yes_no) {
        this.gatherInfo = yes_no;
    }

    public void recordOnlyNode(String node) {
        filterNodes.add(node);
    }

    public void parse(InputStream is) throws IOException, SAXException, RuntimeException {
        InputSource source = new InputSource(is);
        parser.parse(source, new XmlHandler());
    }

    private String getAttrValue(Attributes att, String name, String retVal) {
        String s = att.getValue(name);
        return (s == null) ? retVal : s;
    }

    private class XmlHandler extends DefaultHandler {

        private String currentNode = "";

        private boolean insideEntry = false;
        private boolean insideImage = false;

        @Override public void endDocument() throws SAXException {
            super.endDocument();

            cb.onEndDocument();
        }

        @Override public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
            Logger.debug(clz).log("resolveEntity publicId: ").log(publicId).log(", systemId: ").log(systemId).end();
            return super.resolveEntity(publicId, systemId);
        }

        private boolean isItemNode(String node) {
            return (node.equalsIgnoreCase("item") || node.equalsIgnoreCase("entry"));
        }

        private boolean isImageNode(String qName) {
            return (qName.equalsIgnoreCase("image"));
        }

        @Override public void characters(char[] ch, int s, int l) throws SAXException {
            boolean foundNonEmptyChar = false;

            for (int x = s; x < (s + l); x++) {
                if (ch[x] > 32) {
                    foundNonEmptyChar = true;
                    break;
                }
            }

            if (!foundNonEmptyChar)
                return;

            String val = new String(ch, s, l);

            if (gatherInfo) {
                XmlQnode n = nodeList.get(currentNode);
                if (n != null && n.getVal().isEmpty()) {
                    n.setVal(val);
                }
            }

            if (!insideEntry && !insideImage) {
                channelData.process(currentNode, val);
                return;
            }

            if (!insideEntry && insideImage) {
                channelImage.process(currentNode, val);
                return;
            }

            if (insideEntry) {
                xmlFeedEntry.process(currentNode, val);
                return;
            }
        }

        @Override public void endElement(String uri, String localName, String qName) throws SAXException {

            if (insideEntry && !attrBuff.isEmpty()) {
                xmlFeedEntry.process(currentNode, attrBuff);
                attrBuff = "";
            }

            if (isItemNode(qName)) {
                cb.onXmlEntryFound(xmlFeedEntry);
                xmlFeedEntry.end();
                insideEntry = false;
                return;
            }

            if (isImageNode(qName)) {
                insideImage = false;
            }

            xmlFeedEntry.endElement(qName);
        }

        @Override public void startElement(String uri, String localName, String qName, Attributes attr)
                throws SAXException {
            xmlFeedEntry.startElement(qName);

            if (gatherInfo) {
                XmlQnode n = nodeList.get(qName);
                if (n == null) {
                    n = new XmlQnode(qName, attr);
                    nodeList.put(qName, n);
                }
            }

            if (qName.equalsIgnoreCase("html")) {
                throw new XmlFeedParserException("HTML"); // TODO: Validate with a SCHEMA.
            }

            if (qName.equalsIgnoreCase("rss")) {
                setFlavor(NewsFlavor.RSS2);
                return;

                /*
                 * String version = attr.getValue("version"); if (version.equalsIgnoreCase("2.0")) {
                 * 
                 * }
                 * 
                 * Logger.error(this.getClass()) .log("Unknown news flavor in attribute [").log(qName).log("]").end();
                 * return;
                 */
            }

            if (qName.equalsIgnoreCase("rdf:rdf")) {
                setFlavor(NewsFlavor.RDF);
                return;
            }

            if (qName.equalsIgnoreCase("feed")) {
                String atom = attr.getValue("xmlns");
                if (atom != null && atom.endsWith("tom")) // TODO: Do better.
                {
                    setFlavor(NewsFlavor.ATOM1);
                    return;
                }

                if (atom == null) {
                    atom = "null";
                }

                Logger.error(this.getClass()).log("Unknown news flavor in attribute [").log(qName).log("], atom [")
                        .log(atom).log("]").end();
            }

            if (flavor == NewsFlavor.UNKNOWN) {
                Logger.error(this.getClass()).log("Unknown news flavor [").log(qName).log("]").end();
                return;
            }

            if (isImageNode(qName)) {
                insideImage = true;
            }
            if (isItemNode(qName)) {
                xmlFeedEntry.start();
                insideEntry = true;
            }

            currentNode = qName;

            if (XmlFeedEntry.isImageNode(currentNode) && attr.getLength() >= 1) {
                attrBuff = getAttrValue(attr, "url", "");
            } else if (XmlFeedEntry.isLinkNode(currentNode) && attr.getLength() >= 2) {
                if (getAttrValue(attr, "rel", "").equalsIgnoreCase("enclosure")) {
                    return;
                }

                // It is possible for an article to have multiple links. for comments, replies, etc.
                if (getAttrValue(attr, "rel", "").equalsIgnoreCase("alternate")) {
                    attrBuff = getAttrValue(attr, "href", "");
                }
            } else if (XmlFeedEntry.isLinkNode(currentNode) && attr.getLength() == 1) {
                attrBuff = getAttrValue(attr, "href", "");
            }
        }

    }

}
