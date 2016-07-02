package feedreader.feed.utils;

import java.text.SimpleDateFormat;

public class FetchConstants
{

    /**
     * Possible RDF date formats.
     */
    public static final SimpleDateFormat[] RDF_DATE_FORMATS =
    {
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz"),
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSz")
    };
    /**
     * Possible ATOM date formats.
     */
    public static final SimpleDateFormat[] ATOM_DATE_FORMATS =
    {
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz"),
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSz")
    };
    /**
     * Possible RSS date formats.
     */
    public static final SimpleDateFormat[] RSS_DATE_FORMATS =
    {
        new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z"), 
        new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss")
    };

}
