package feedreader.entities;

public class XmlChannelImage
{
    StringBuilder title = new StringBuilder();
    StringBuilder url = new StringBuilder();
    StringBuilder link = new StringBuilder();
    StringBuilder description = new StringBuilder();

    int height = 0;
    int width = 0;

    public String getTitle()
    {
        return title.toString().trim();
    }

    public String getUrl()
    {
        return url.toString().trim();
    }

    public String getLink()
    {
        return link.toString().trim();
    }

    public String getDescription()
    {
        return description.toString().trim();
    }

    public int getHeight()
    {
        return height;
    }

    public int getWidth()
    {
        return width;
    }

    public static boolean isTitleNode(String name)
    {
        return name.equalsIgnoreCase("title");
    }

    public static boolean isUrlNode(String name)
    {
        return name.equalsIgnoreCase("url");
    }

    public static boolean isLinkNode(String name)
    {
        return name.equalsIgnoreCase("link");
    }

    public static boolean isWidthNode(String name)
    {
        return name.equalsIgnoreCase("width");
    }

    public static boolean isHeightNode(String name)
    {
        return name.equalsIgnoreCase("height");
    }

    public static boolean isDescriptionNode(String name)
    {
        return name.equalsIgnoreCase("description");
    }

    public static int asInt(String value, int def)
    {
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {}

        return def;
    }

    public void process(String node, String value)
    {
        if (isTitleNode(node)) {
            title.append(value);
            return;
        }

        if (isUrlNode(node)) {
            url.append(value);
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

        if (isWidthNode(node)) {
            width = asInt(value, 0);
            return;
        }

        if (isHeightNode(node)) {
            height = asInt(value, 0);
            return;
        }
    }

    @Override
    public String toString()
    {
        return new StringBuilder()
                .append(this.getClass().getSimpleName())
                .append(" : url=[").append(getUrl()).append("], size=")
                .append(width).append("x").append(height).toString();
    }

}
