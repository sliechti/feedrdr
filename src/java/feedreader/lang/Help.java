package feedreader.lang;

public class Help
{
    public static String MouseOver(String topic)
    {
        return "<a href=\"#\" onMouseOver=\"console.log('help: "+ topic +"')\">help</a>";
    }

    public static String MouseOver(String hrefText, String topic)
    {
        return "<a href=\"#\" onMouseOver=\"console.log('help: "+ topic +"')\">"+ hrefText +"</a>";
    }

}
