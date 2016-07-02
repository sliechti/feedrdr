package feedreader.api.v1;

import feedreader.store.DBFields;

public class ConfigAPI {

    public static String defaulEntryColumns(String table) {
        return table + "" + DBFields.LONG_ENTRY_ID + ", " + table + DBFields.LONG_XML_ID + ", "
                + table + DBFields.STR_LINK + ", " + table + DBFields.STR_TITLE + ", "
                + table + DBFields.TIME_PUBLICATION_DATE;
    }

}
