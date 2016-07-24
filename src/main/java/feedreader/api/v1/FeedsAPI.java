package feedreader.api.v1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feedreader.config.FeedAppConfig;
import feedreader.cron.CronTimeUtils;
import feedreader.security.Session;
import feedreader.store.DBFields;
import feedreader.store.Database;
import feedreader.store.FeedEntriesTable;
import feedreader.time.CurrentTime;
import feedreader.utils.JSONUtils;
import feedreader.utils.SQLUtils;

@Path("/v1/user/feeds")
public class FeedsAPI {

    private static final Logger logger = LoggerFactory.getLogger(FeedsAPI.class);

    private static final String allQuery = "SELECT t3.l_entry_id, t3.l_xml_id, t3.s_link, t3.s_title, t3.t_discovered_at, t3.t_pub_date"
            + " FROM feedreader.userprofilestreamgroup AS t0 "
            + "    INNER JOIN feedreader.userstreamgroupfeedsubscription AS t1 "
            + "        ON t0.l_stream_id = t1.l_stream_id "
            + "    INNER JOIN feedreader.userfeedsubscriptions AS t2 "
            + "        ON t2.l_subs_id = t1.l_subs_id "
            + "    INNER JOIN feedreader.feedentries AS t3 " + "        ON t2.l_xml_id = t3.l_xml_id "
            + " WHERE t0.l_profile_id = ? " + " AND t3.t_pub_date > ?" + " GROUP BY t3.l_entry_id "
            + "ORDER BY t3.t_pub_date DESC LIMIT ? OFFSET ?";

    private static final String subscriptionsQeury = "select t0.s_group_time_filter, t1.l_subs_id, t2.l_xml_id from feedreader.userstreamgroups as t0 "
            + " inner join feedreader.userstreamgroupfeedsubscription as t1 "
            + "     on t0.l_stream_id = t1.l_stream_id "
            + " inner join feedreader.userfeedsubscriptions as t2 "
            + "     on t2.l_subs_id = t1.l_subs_id " + " where t0.l_stream_id = ?";

    public FeedsAPI() {
        logger.info("init");
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public String all(@Context HttpServletRequest req, @QueryParam("page") int page) {
        long start = CurrentTime.inGMT();

        long userId = Session.getUserId(req.getSession());
        if (userId == 0) {
            return JSONUtils.error(0, "user id unknown.");
        }

        long profileId = Session.getProfileId(req.getSession());
        if (profileId == 0) {
            return JSONUtils.error(0, "user id unknown.");
        }

        StringBuilder sb = new StringBuilder();

        if (profileId == 0) {
            return JSONUtils.empty();
        }

        sb.append("\"entries\" : [");

        try (Connection conn = Database.getConnection()) {
            long maxTime = new Date().getTime() - (60 * 60 * 48 * 1000); // max 48h?
            PreparedStatement stmt = conn.prepareStatement(allQuery);
            stmt.setLong(1, profileId);
            stmt.setLong(2, maxTime);
            stmt.setLong(3, FeedAppConfig.DEFAULT_API_FETCH_ARTICLES);
            stmt.setInt(4, FeedAppConfig.DEFAULT_API_FETCH_ARTICLES * page);
            ResultSet rs = stmt.executeQuery();
            APIUtils.wrapObject(sb, rs);
        } catch (SQLException e) {
            logger.error("list all error: {}", e, e.getMessage());
        }

        long end = CurrentTime.inGMT();
        sb.insert(0, "{\"time\":" + (end - start) + ",");

        sb.append("]}");
        return sb.toString();
    }

    @GET
    @Path("/listunread")
    @Produces(MediaType.APPLICATION_JSON)
    public String listunread(@Context HttpServletRequest req, @QueryParam("sid") String streamId) {
        // long userId = Session.asLong(req.getSession(), Constants.SESSION_USERID_FIELD, 0);
        //
        // String query = "select t2.l_xml_id, t2.i_unread, t2.s_time_filter from feedreader.userstreamgroups as t0 "
        // + "inner join feedreader.userstreamgroupfeedsubscription as t1 "
        // + " on t0.l_stream_id = t1.l_stream_id "
        // + " inner join feedreader.userfeedsubscriptions as t2 "
        // + " on t1.l_subs_id = t2.l_subs_id "
        // + " where t0.l_stream_id = " + streamId;
        //
        // ResultSet rs = Database.rawQuery(query);

        return "";
    }

    @GET
    @Path("/sourceid")
    @Produces(MediaType.APPLICATION_JSON)
    public String sourceId(@Context HttpServletRequest req, @QueryParam("id") String sourceId,
            @QueryParam("page") int page) {
        long userId = Session.getUserId(req.getSession());
        if (userId == 0) {
            return JSONErrorMsgs.getAccessDenied();
        }

        StringBuilder sb = new StringBuilder();

        if (sourceId == null) {
            return "";
        }

        long userMaxHistTime = CronTimeUtils.getMaxHistory(req.getSession());

        // TODO: Don't query ALL fields. This query can be optimized. See analyze describe.
        String rawQuery = String.format("SELECT * FROM %s WHERE %s = %s AND %s > %d ORDER BY %s %s LIMIT %d OFFSET %d",
                FeedEntriesTable.TABLE,
                // where
                DBFields.LONG_XML_ID, sourceId,
                DBFields.TIME_PUBLICATION_DATE, userMaxHistTime,
                // order
                DBFields.TIME_PUBLICATION_DATE,
                FeedAppConfig.DEFAULT_API_SORT_PUBLICATION_DATE,
                // limit
                FeedAppConfig.DEFAULT_API_FETCH_ARTICLES,
                page * FeedAppConfig.DEFAULT_API_FETCH_ARTICLES);

        sb.append("{\"entries\" : [");
        try (Connection conn = Database.getConnection()) {
            ResultSet rs = Database.rawQuery(conn, rawQuery);
            int count = 0;
            while (rs.next()) {
                sb.append("{").append(JSONUtils.getNumber(rs, DBFields.LONG_ENTRY_ID)).append(",")
                        .append(JSONUtils.getNumber(rs, DBFields.LONG_XML_ID)).append(",")
                        .append(JSONUtils.getString(rs, DBFields.STR_LINK)).append(",")
                        .append(JSONUtils.getString(rs, DBFields.STR_TITLE)).append(",")
                        .append(JSONUtils.getNumber(rs, DBFields.TIME_DISCOVERED_AT)).append(",")
                        .append(JSONUtils.getNumber(rs, DBFields.TIME_PUBLICATION_DATE)).append("},");
                count++;
            }
            if (count > 0 && sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
        } catch (SQLException e) {
            logger.error("query failed: {}", e, e.getMessage());
        }

        sb.append("]}");

        return sb.toString();
    }

    @GET
    @Path("/data")
    @Produces(MediaType.APPLICATION_JSON)
    public String data(@Context HttpServletRequest req, @QueryParam("entries") String entries,
            @QueryParam("img") boolean images, @QueryParam("cnt") boolean content, @QueryParam("ml") int maxLen) {
        long userId = Session.getUserId(req.getSession());
        if (userId == 0) {
            return JSONUtils.error(0, "access denied.");
        }

        StringBuilder sb = new StringBuilder();

        // TODO: Entries are supposed to be 1,2,3,4 ... they can be malformed. Check that.
        if (entries == null || entries.isEmpty()) {
            return JSONUtils.empty();
        }

        if (maxLen == 0) {
            maxLen = FeedAppConfig.DEFAULT_API_MAX_CONTENT_LEN;
        }

        String field = "";
        if (images && content) {
            field = DBFields.STR_THUMB_URL + ", substring(" + DBFields.STR_CLEAN_CONTENT + ", 0, " + maxLen
                    + ") as content";
        } else if (images && !content) {
            field = DBFields.STR_THUMB_URL;
        } else if (content && !images) {
            field = "substring(" + DBFields.STR_CLEAN_CONTENT + ", 0, " + maxLen + ") as content";
        }

        String rawQuery = String.format("SELECT %s, %s FROM %s WHERE %s IN (%s) ", DBFields.LONG_ENTRY_ID, field,
                FeedEntriesTable.TABLE, DBFields.LONG_ENTRY_ID, SQLUtils.asSafeString(entries));
        sb.append("{\"entries\" : [");
        try (Connection conn = Database.getConnection()) {
            ResultSet rs = Database.rawQuery(conn, rawQuery);
            APIUtils.wrapObject(sb, rs);
        } catch (SQLException ex) {
            logger.error("/data query failed: {}", ex, ex.getMessage());
        }

        sb.append("]}");

        return sb.toString();
    }
}
