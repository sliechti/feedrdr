package feedreader.entities;

import feedreader.parser.XmlFeedParser;
import feedreader.time.DateUtils;

import java.util.Date;

public class XmlChannelData
{
    public XmlFeedParser.NewsFlavor flavor = XmlFeedParser.NewsFlavor.UNKNOWN;

    StringBuilder title = new StringBuilder();
    StringBuilder link = new StringBuilder();
    StringBuilder description = new StringBuilder();
    StringBuilder language = new StringBuilder();
    // TODO: Not implemented?
    StringBuilder pubDate = new StringBuilder();
    long pubdateTs = 0; // Placeholder when fetching from the database.
    // TODO: Implement.
    int intervalInMinutes = 0;

    public String getLink()
    {
        return link.toString().toLowerCase().trim();
    }

    public String getTitle()
    {
        return title.toString().trim();
    }

    public String getDescription()
    {
        return description.toString().trim();
    }

    public String getLanguage()
    {
        return language.toString().trim();
    }

    public String getPubDateString()
    {
        return pubDate.toString().trim();
    }

    public long getPublicationDate()
    {
        if (pubdateTs > 0) return pubdateTs;
        if (getPubDateString().isEmpty()) return -1;
        return DateUtils.asLongDate(getPubDateString(), flavor, -1);
    }
    
    public void setPublicationDateTimestamp(long ts)
    {
        pubdateTs = ts;
    }
    
    public static boolean isTitleNode(String name)
    {
        return name.equalsIgnoreCase("title");
    }

    public static boolean isLinkNode(String name)
    {
        return name.equalsIgnoreCase("link");
    }

    private static boolean isLanguageNode(String node)
    {
        return (node.equalsIgnoreCase("language"));
    }

    public static boolean isDescriptionNode(String name)
    {
        return name.equalsIgnoreCase("description");
    }

    public static boolean isPublicationDateNode(String node)
    {
        return (node.equalsIgnoreCase("pubdate") ||
                node.equalsIgnoreCase("published")) ||
                node.equalsIgnoreCase("dc:date") ||
                node.equalsIgnoreCase("lastBuildDate");
    }

    // TODO: Move out of this class into a helper class.
    // This class should expose only get/set and a constructor to set default values.
    public void process(String node, String value)
    {
        if (isTitleNode(node)) {
            title.append(value);
            return;
        }

        if (isLinkNode(node)) {
            link.append(value);
            return;
        }

        if (isDescriptionNode(node)) {
            description.append(value);
            return;
        }

        if (isLanguageNode(node)) {
            language.append(value);
            return;
        }

        if (isPublicationDateNode(node)) {
            pubDate.append(value);
            return;
        }
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        return sb.append(this.getClass().getSimpleName())
                .append(" : lang=[").append(getLanguage()).append("], ")
                .append(" pubDate: [").append(new Date(getPublicationDate())).append("], ")
                .append(" link=[").append(getLink()).append("], ")
                .append(" name=[").append(getTitle()).append("], description=[")
                .append(getDescription()).append("]").toString();
    }
}
