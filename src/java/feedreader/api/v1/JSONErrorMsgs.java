package feedreader.api.v1;

import feedreader.utils.JSONUtils;
import java.sql.SQLException;

class JSONErrorMsgs {

    public static final String NO_USERID_PROFILE_ID = "Access denied.";
    public static String ERROR_WITH_ONE_OR_MORE_PARAM = "One or more of the expected parameters is invalid.";
    public static String ERROR_REQUESTING_DATA_FROM_DB
            = "There was an error while requesting data from the database. We were informed.";

    static String getAccessDenied() {
        return JSONUtils.error(0, JSONErrorMsgs.NO_USERID_PROFILE_ID);
    }

    static String getErrorParams() {
        return JSONUtils.error(0, ERROR_WITH_ONE_OR_MORE_PARAM);
    }

    static String getRequestingDataError(SQLException ex) {
        return JSONUtils.error(0, JSONErrorMsgs.ERROR_REQUESTING_DATA_FROM_DB, ex);
    }
}
