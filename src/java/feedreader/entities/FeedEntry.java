package feedreader.entities;

public class FeedEntry
{
    public static final String EMPTY = "";
    public static final long ZERO = 0L;

    public String getTitle()
    {
        throw new RuntimeException(EMPTY);
    }

    public String getLink()
    {
        throw new RuntimeException(EMPTY);
    }

    public String getXmlUrl()
    {
        throw new RuntimeException(EMPTY);
    }

    public long getPublicationDate()
    {
        throw new RuntimeException(EMPTY);
    }

    public long getDiscoveryDate()
    {
        throw new RuntimeException(EMPTY);
    }

    public String getContent()
    {
        throw new RuntimeException(EMPTY);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        return sb.toString();
    }

}

