package feedreader.parser;


public class XmlLinkDef 
{
    String href;
    String text;

    public XmlLinkDef(String href, String text)
    {
        this.href = href;
        this.text = text;
    }

    public String getHref()
    {
        return href;
    }

    public String getText()
    {
        return text;
    }

    @Override
    public String toString()
    {
        return "XmlLinkDef{" + "href=" + href + ", text=" + text + '}';
    }
    
}
