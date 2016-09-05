package feedreader.api.v1;

import java.sql.Connection;
import java.sql.PreparedStatement;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feedreader.config.Constants;
import feedreader.config.FeedAppConfig;
import feedreader.security.Session;
import feedreader.store.DBFields;
import feedreader.store.Database;
import feedreader.store.FeedSourceChannelDataTable;
import feedreader.store.FeedSourceChannelImageTable;
import feedreader.store.FeedSourcesTable;
import feedreader.utils.JSONUtils;

@Path("/v1/sources")
public class SourcesAPI {

    private static final Logger logger = LoggerFactory.getLogger(SourcesAPI.class);

    static final HashMap<String, String> maps0 = new HashMap<>();
    static final HashMap<String, String> maps1 = new HashMap<>();
    static final HashMap<String, String> maps2 = new HashMap<>();

    private static final String selectSourceByTitle = "SELECT fs.l_xml_id,"
            + "fs.s_title, coalesce (fs.s_link, f.s_xml_url) as s_link, fs.b_hasico, fi.s_img_url "
            + " FROM feedreader.feedsourcechanneldata AS fs "
            + " LEFT JOIN feedreader.feedsourcechannelimage fi "
            + " ON fs.l_xml_id = fi.l_xml_id "
            + "LEFT JOIN feedreader.feedsources AS f ON fs.l_xml_id = f.l_xml_id "
            + " WHERE fs.s_title ILIKE ? LIMIT ? OFFSET ?";

    static {
        maps0.put("i_count_0", "count");
        maps1.put("i_count_1", "count");
        maps2.put("i_count_2", "count");
    }

    @GET
    @Path("/find")
    @Produces(MediaType.APPLICATION_JSON)
    public String find(@Context HttpServletRequest req, @QueryParam("title") String title) {
        long userId = Session.getUserId(req.getSession());
        if (userId == 0) {
            return JSONErrorMsgs.getAccessDenied();
        }
        if (title == null || title.isEmpty()) {
            return JSONUtils.error(0, "expecting source title");
        }
        logger.info("/find {}", title);
        StringBuilder sb = new StringBuilder(1000);
        sb.append("{ \"entries\" : [");
        try (Connection conn = Database.getConnection()) {
            PreparedStatement selSourceByTitle = conn.prepareStatement(selectSourceByTitle);
            selSourceByTitle.setString(1, "%" + title + "%");
            selSourceByTitle.setInt(2, 10);
            selSourceByTitle.setInt(3, 0);
            APIUtils.wrapObject(sb, selSourceByTitle.executeQuery());
        } catch (Exception e) {
            logger.error("/find failed: {}", e, e.getMessage());
        }
        sb.append("]}");
        return sb.toString();
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

        String rawQuery = getSelectQuery(queryType);

        if (ids != null) {
            sb.append("{ \"entries\" : [");
            rawQuery += " in (" + ids + ")";
        } else if (sourceId != null) {
            rawQuery += " = " + sourceId;
        } else {
            return JSONUtils.empty(); // shouldn't be able to reach this case, but ...
        }

        // TODO: Don't query ALL fields. This query can be optimized. See anaylze describe.
        try (Connection conn = Database.getConnection()) {
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
            ResultSet rs = Database.rawQuery(conn, rawQuery);
            APIUtils.wrapObject(sb, rs, false, maps);
        } catch (SQLException ex) {
            logger.error("source id error: {}", ex, ex.getMessage());
        }

        if (ids != null) {
            sb.append("]}");
        }

        return sb.toString();
    }

    public static String getSelectQuery(int queryType) {
        // TODO: We should be using prepared call statements.
        String rawQuery = "SELECT ";

        switch (queryType) {
            case 1:
                rawQuery += "t0." + DBFields.LONG_XML_ID + ", t1." + DBFields.STR_LINK + ", "
                        + "t0." + DBFields.STR_XML_URL + " " + " , t0." + DBFields.INT_TOTAL_ENTRIES + ", "
                        + "t0." + DBFields.INT_COUNT_0 + ", t0." + DBFields.INT_COUNT_1 + ", t0." + DBFields.INT_COUNT_2
                        + " ";
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
        return rawQuery;
    }
}
