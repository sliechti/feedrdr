package feedreader.api.v1;

import feedreader.config.Constants;
import feedreader.config.FeedAppConfig;
import feedreader.log.Logger;
import feedreader.security.Session;
import feedreader.store.DBFields;
import feedreader.store.Database;
import feedreader.store.FeedSourceChannelDataTable;
import feedreader.store.FeedSourceChannelImageTable;
import feedreader.store.FeedSourcesTable;
import feedreader.utils.JSONUtils;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/v1/sources")
public class SourcesAPI {

    static final HashMap<String, String> maps0 = new HashMap<>();
    static final HashMap<String, String> maps1 = new HashMap<>();
    static final HashMap<String, String> maps2 = new HashMap<>();

    static {
        maps0.put("i_count_0", "count");
        maps1.put("i_count_1", "count");
        maps2.put("i_count_2", "count");
    }

    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    public String get(@Context HttpServletRequest req, @QueryParam("id") String sourceId,
            @QueryParam("ids") String ids, @QueryParam("qtype") int queryType) {
        long userId = Session.getUserId(req.getSession());
        if (userId == 0) {
            return JSONErrorMsgs.getAccessDenied();
        }
        if (sourceId == null && (ids == null || ids.isEmpty())) {
            return JSONErrorMsgs.getErrorParams();
        }

        StringBuilder sb = new StringBuilder();

        // TODO: We should be using prepared call statements.
        String rawQuery = "SELECT ";

        switch (queryType) {
            case 1:
                rawQuery += "t0." + DBFields.LONG_XML_ID + ", t1." + DBFields.STR_LINK + ", "
                        + "t0." + DBFields.STR_XML_URL + " " + " , t0." + DBFields.INT_TOTAL_ENTRIES + ", "
                        + "t0." + DBFields.INT_COUNT_0 + ", t0." + DBFields.INT_COUNT_1 + ", t0." + DBFields.INT_COUNT_2 + " ";
                break;

            default:
                rawQuery += " * ";
        }

        rawQuery += String.format("FROM %s AS t0 "
                + "LEFT JOIN %s AS t1 ON t0.%s = t1.%s "
                + "LEFT JOIN %s AS t2 ON t0.%s = t2.%s "
                + "WHERE t0.%s",
                FeedSourcesTable.TABLE,
                FeedSourceChannelImageTable.TABLE,
                DBFields.LONG_XML_ID, DBFields.LONG_XML_ID,
                FeedSourceChannelDataTable.TABLE,
                DBFields.LONG_XML_ID, DBFields.LONG_XML_ID,
                DBFields.LONG_XML_ID);

        if (ids != null) {
            sb.append("{ \"entries\" : [");
            rawQuery += " in (" + ids + ")";
        } else if (sourceId != null) {
            rawQuery += " = " + sourceId;
        } else {
            return JSONUtils.empty(); // shouldn't be able to reach this case, but ...
        }

        // TODO: Don't query ALL fields. This query can be optimized. See anaylze describe.
        Logger.debugSQL(FeedsAPI.class).log(rawQuery).end();

        try {
            int userType = Session.asInt(req.getSession(), Constants.SESSION_USER_TYPE, 0);
            HashMap<String, String> maps;
            switch (userType) {
                case FeedAppConfig.USER_1_VAL:
                    maps = maps1;
                    break;
                case FeedAppConfig.USER_2_VAL:
                    maps = maps2;
                    break;
                default:
                case FeedAppConfig.USER_0_VAL:
                    maps = maps0;
                    break;
            }
            ResultSet rs = Database.rawQuery(rawQuery);
            APIUtils.wrapObject(sb, rs, false, maps);
        } catch (SQLException ex) {
            Logger.error(SourcesAPI.class).log("sourceId error ").log(ex.getMessage()).end();
        }

        if (ids != null) {
            sb.append("]}");
        }

        return sb.toString();
    }
}
