package feedreader.utils;


public class BooleanUtils 
{
    public static boolean isBoolean(String val) 
    {
        if (val.equalsIgnoreCase("true") || val.equalsIgnoreCase("1") || val.equals("on"))
            return true;

        if (val.equalsIgnoreCase("false") || val.equalsIgnoreCase("0") || val.equals("off"))
            return false;
        
        return false; // TODO: throw exception?
    }
    
}
