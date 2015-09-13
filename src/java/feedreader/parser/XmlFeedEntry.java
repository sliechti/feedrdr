package feedreader.parser;

import feedreader.log.Logger;
import feedreader.time.DateUtils;
import java.util.ArrayList;
import java.util.Date;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*
 TODO: Do we really need two different classes XmlFeedEntry and entities.FeedEntry.
 All the strings manipulations in this class can be optimized by a lot.
 */
public class XmlFeedEntry {

    String sourceUrl = "";

    /*
     * **************************************************************
     * NOTICE: Any new member variables need to be cleared on reset().
     * **************************************************************
     */
    private final StringBuilder title = new StringBuilder();
    private final StringBuilder description = new StringBuilder();
    private final StringBuilder content = new StringBuilder();
    private final StringBuilder author = new StringBuilder();
    private final StringBuilder image = new StringBuilder();
    private final StringBuilder link = new StringBuilder();
    private String linkStr = "";
    private final StringBuilder entryDate = new StringBuilder();
    private long entryDateLong = 0L;

    Document doc = null;

    public XmlFeedParser.NewsFlavor flavor = XmlFeedParser.NewsFlavor.UNKNOWN;

    public XmlFeedEntry(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    /**
     * Resets all private member variables. We do this as a way of reusing the class instead of instantiating it every
     * time.
     *
     */
    private void reset() {
        title.setLength(0);
        author.setLength(0);
        description.setLength(0);
        content.setLength(0);
        link.setLength(0);
        image.setLength(0);
        entryDate.setLength(0);
        entryDateLong = 0;
        doc = null;
    }

    public void start() {
        reset();
    }

    public void end() {

    }

    void startElement(String qName) {

    }

    void endElement(String qName) {
        if (isLinkNode(qName) && link.length() > 0) {
            linkStr = link.toString().toLowerCase().trim();
            link.setLength(0);
        }
    }

    public String getTitle() {
        return title.toString().trim();
    }

    public String getLink() {
        return linkStr;
    }

    public String getImage() {
        return image.toString().trim();
    }

    public String getThumbImg() {
        String img = getImage();
        if (img.isEmpty()) {
            ArrayList<XmlImageDef> images = getContentImages();
            if (images.size() > 0) {
                XmlImageDef imgDef = images.iterator().next();
                img = imgDef.getSrc();
            }
        }

        return img;
    }

    public String getAuthor() {
        // TODO: For author: See commented code below.
        // boolean hasHtml = false;
        //
        // for (int x = 0; x < author.length(); x++)
        // {
        // if (author.charAt(x) == '<' || author.charAt(x) == '>')
        // {
        // hasHtml = true;
        // break;
        // }
        // }
        //
        // if (hasHtml)
        // {
        // String authors = author.toString();
        //
        // }
        return author.toString().trim();
    }

    /**
     * The entry in the feed might not have a publication date. In that case, you could try falling back to the source's
     * publication date. {@link XmlFeedParser} or to the HTTP's last modification date.
     *
     * @return Parsed time in milliseconds, -1 on error.
     *
     * @see XmlFeedParser#channelData
     * @see XmlFeedParser#channelData#getEntryDate()
     */
    public long getEntryDate() {
        /*
         * The entryDate may be overriden with setEntryDate by the job fetching news article. In the case the article's
         * publication date is in the future or the article/source has no publication date, the job will set the
         * publication's date to now.
         */
        if (entryDateLong != 0)
            return entryDateLong;
        String dateStr = getEntryDateStr();
        if (dateStr.isEmpty())
            return -1;
        if (dateStr.length() < 4)
            return -1; // TODO: Had a crash because of a string with ""
        try {
            return DateUtils.asLongDate(getEntryDateStr(), flavor);
        } catch (Exception e) {
            Logger.error(XmlFeedEntry.class).log(" getEntryDate ").log(dateStr).log(", error ").log(e.getMessage())
                    .end();
            return -1;
        }
    }

    /**
     * Override only if necessary.
     *
     * @param entryDate
     *            entry
     */
    public void setEntryDate(long entryDate) {
        this.entryDateLong = entryDate;
    }

    public String getEntryDateStr() {
        return entryDate.toString().trim();
    }

    public int getDescriptionSize() {
        return description.length();
    }

    public String getDescription() {
        return description.toString().trim();
    }

    public int getContentSize() {
        return content.length();
    }

    public String getContent() {
        return content.toString().trim();
    }

    void prepareDocument() {
        if (getContentSize() > getDescriptionSize()) {
            doc = Jsoup.parse(getContent());
        } else if (getDescriptionSize() > 0) {
            doc = Jsoup.parse(getDescription());
        } else {
            doc = Jsoup.parse("");
        }
    }

    public String getCleanContent() {
        if (doc == null) {
            prepareDocument();
        }

        return doc.text();
    }

    public ArrayList<XmlImageDef> getContentImages() {
        if (doc == null)
            prepareDocument();

        ArrayList<XmlImageDef> ret = new ArrayList<XmlImageDef>();

        Elements media = doc.select("img[src]");
        for (Element e : media) {
            XmlImageDef img = new XmlImageDef(e.attr("src"), e.attr("width"), e.attr("height"));
            ret.add(img);
        }

        return ret;
    }

    public ArrayList<XmlLinkDef> getContentLinks() {
        if (doc == null) {
            prepareDocument();
        }

        ArrayList<XmlLinkDef> ret = new ArrayList<XmlLinkDef>();

        Elements media = doc.select("a[href]");
        for (Element e : media) {
            XmlLinkDef link = new XmlLinkDef(e.attr("abs:href"), e.text());
            ret.add(link);
        }

        return ret;
    }

    public static boolean isTitleNode(String node) {
        return node.equalsIgnoreCase("title");
    }

    public static boolean isContentNode(String node) {
        return (node.contains("content"));
    }

    public static boolean isDescription(String node) {
        return (node.contains("description") || node.contains("summary"));
    }

    public static boolean isLinkNode(String node) {
        node = node.toLowerCase();
        return (node.matches("link") || node.contains("origlink"));
    }

    public static boolean isEntryDateNode(String node) {
        return (node.equalsIgnoreCase("pubdate") || node.equalsIgnoreCase("published"))
                || node.equalsIgnoreCase("dc:date") || node.equalsIgnoreCase("lastBuildDate")
                || node.equalsIgnoreCase("updated");
    }

    public static boolean isAuthorNode(String node) {
        return (node.equalsIgnoreCase("dc:creator") || node.equalsIgnoreCase("name"));
    }

    public static boolean isImageNode(String node) {
        node = node.toLowerCase();
        return (node.startsWith("media:thumbnail") || node.startsWith("thumbnail"));
    }

    /**
     * TODO: Trimming, concatenating here is the worst idea ever.
     *
     * What we should do is have a temp byte buffer in the XmlFeedParser class holding the char values. Then, once we we
     * have all characters for the node, call this method.
     *
     * @param node
     *            XML Node's name.
     * @param value
     *            XML Node's value.
     *
     */
    public void process(String node, String value) {
        if (value.isEmpty())
            return;

        if (isTitleNode(node)) {
            title.append(value);
            return;
        }

        if (isDescription(node)) {
            description.append(value);
            return;
        }

        if (isContentNode(node)) {
            content.append(value);
            return;
        }

        if (isLinkNode(node)) {
            link.append(value);
            return;
        }

        if (isEntryDateNode(node)) {
            value = value.replace("Z", "+0000"); // For atom dates.
            entryDate.append(value);
            return;
        }

        if (isAuthorNode(node)) {
            author.append(value);
            return;
        }

        if (isImageNode(node)) {
            if (image.length() > 0)
                image.append(",");
            image.append(value);
            return;
        }

    }

    /**
     * Use only for debugging and testing in dev. Never use in production.
     *
     * @return String object's data as a string.
     */
    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean withNl) {
        StringBuilder sb = new StringBuilder();

        char nl = ' ';
        if (withNl)
            nl = '\n';

        sb.append(this.getClass().getSimpleName()).append(nl).append(", source [").append(getSourceUrl()).append("]")
                .append(nl).append(", link [").append(getLink()).append("]").append(nl).append(", image [")
                .append(getImage()).append("]").append(nl).append(", title [").append(getTitle()).append("]")
                .append(nl).append(", date [").append(new Date(getEntryDate())).append("]").append(nl)
                .append(", description [").append(getDescriptionSize()).append("]").append(nl).append(", content [")
                .append(getContentSize()).append("]").append(nl);

        return sb.toString();
    }

}
