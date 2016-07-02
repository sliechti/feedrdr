package feedreader.api.v1;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import feedreader.config.Constants;
import feedreader.config.Environment;
import feedreader.config.FeedAppConfig;
import feedreader.cron.CronTimeUtils;
import feedreader.log.Logger;
import feedreader.security.Session;
import feedreader.store.DBFields;
import feedreader.store.Database;
import feedreader.store.FeedEntriesTable;
import feedreader.store.JoinedTable;
import feedreader.store.UserFeedSubscriptionsTable;
import feedreader.store.UserProfilesTable;
import feedreader.store.UserStreamGroupsTable;
import feedreader.time.CurrentTime;
import feedreader.utils.JSONUtils;
import feedreader.utils.SQLUtils;

@Path("/v1/user/streams")
public class StreamsAPI {

    private static final int FILTER_UNREAD = 1;
    private static final int FILTER_ALL = 0;
    static final Class<?> clz = StreamsAPI.class;

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public String list(@Context HttpServletRequest req, @QueryParam("views") boolean views) {
        long userId = Session.getUserId(req.getSession());
        long profileId = Session.getProfileId(req.getSession());

        if (userId == 0 || profileId == 0) {
            return JSONErrorMsgs.getAccessDenied();
        }

        try {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            APIUtils.wrapObject(sb, UserStreamGroupsTable.get(userId, profileId, views));
            return sb.append("]").toString();
        } catch (SQLException ex) {
            Logger.error(clz).log("streamsList ").log(ex.getMessage()).end();
            return JSONUtils.error(0, JSONErrorMsgs.ERROR_REQUESTING_DATA_FROM_DB, ex);
        }
    }

    @GET
    @Path("/unreadcount")
    @Produces(MediaType.APPLICATION_JSON)
    public String unreadCount(@Context HttpServletRequest req, @QueryParam("sid") String streamIds,
            @QueryParam("nc") int noCache) {
        long pageStart = CurrentTime.inGMT();

        long userId = Session.getUserId(req.getSession());
        long profileId = Session.getProfileId(req.getSession());

        if (userId == 0 || profileId == 0) {
            return JSONErrorMsgs.getAccessDenied();
        }

        ArrayList<String> arr = new ArrayList<>(Arrays.asList(streamIds.split(",")));
        StringBuilder sb = new StringBuilder();
        sb.append("{ \"entries\" : [");

        int userType = Session.asInt(req.getSession(), Constants.SESSION_USER_TYPE, 0);

        String query = "select l_stream_id, " + DBFields.LONG_GR_UNREAD
                + ", t_gr_unread from feedreader.userstreamgroups " + "where l_stream_id in ("
                + SQLUtils.asSafeString(streamIds) + ") " + "and l_user_id = " + userId;

        if (noCache == 0) {
            try {
                ResultSet rs = Database.rawQuery(query);

                int rows = 0;
                while (rs.next()) {
                    long t = rs.getLong(3);
                    long diff = (CurrentTime.inGMT() - t);

                    if (diff < FeedAppConfig.CACHE_UNREAD_TIME) {
                        long xmlId = rs.getLong(1);
                        sb.append("{\"id\":").append(xmlId).append(",\"count\":").append(rs.getLong(2))
                                .append(",\"cache\": true").append(",\"diff\": ").append(diff).append("},");
                        arr.remove(Long.toString(xmlId));
                    }
                    rows++;
                }

                if (rows == 0) {
                    return JSONErrorMsgs.getErrorParams();
                }
            } catch (SQLException ex) {
                Logger.error(clz).log("unreadCount ").log(query).log("/").log(userId).log(", error ")
                        .log(ex.getMessage()).end();
                if (Environment.isDev()) {
                    return JSONUtils.error(0, "couldn't get unread count " + ex.getMessage());
                }
                return JSONUtils.error(0, "couldn't get unread count.");
            }
        }

        for (String strStreamId : arr) {
            long streamId = Long.parseLong(strStreamId);
            // We get all xmlids from all subscriptions in this stream id.
            String xmlIds = JoinedTable.getAllXmlIds(streamId, userId);
            long maxTime = CronTimeUtils.getMaxHistory(userType);

            long callStart = System.currentTimeMillis();
            StringBuilder unreadEntries = new StringBuilder();
            long unreadEntriesCount = JoinedTable.allStreamUnreadEntries(streamId, userId, maxTime, unreadEntries);
            long unreadExec = System.currentTimeMillis() - callStart;

            sb.append("{\"id\":").append(streamId).append(",\"ids\": \"").append(xmlIds).append("\"")
                    .append(",\"count\":").append(unreadEntriesCount).append(",\"exec\":").append(unreadExec)
                    .append("},");

            UserStreamGroupsTable.updateUnreadCount(userId, streamId, unreadEntriesCount);
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }

        sb.append("]");

        sb.append(", \"pageTime\" : ").append(System.currentTimeMillis() - pageStart).append(",\"expires\": ")
                .append(FeedAppConfig.CACHE_UNREAD_TIME).append("}");

        return sb.toString();
    }

    @GET
    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    public String update(@Context HttpServletRequest req, @QueryParam("sid") long streamId,
            @QueryParam("v") int viewId, @QueryParam("filter") int filter, @QueryParam("sort") int sort,
            @DefaultValue("-1") @QueryParam("c") long count) {
        long userId = Session.asLong(req.getSession(), Constants.SESSION_USERID_FIELD, 0);
        if (userId == 0) {
            return JSONUtils.error(0, "access denied.");
        }

        if (streamId == 0 || viewId == 0) {
            return JSONUtils.error(0, "expecting stream id");
        }

        if (viewId == 0) {
            return JSONUtils.error(0, "invalid view.");
        }

        int c = UserStreamGroupsTable.saveView(userId, streamId, viewId, filter, sort, count);
        if (c == -1) {
            return JSONUtils.error(c, "error saving view.");
        }

        return JSONUtils.count(c);
    }

    @GET
    @Path("/allread")
    @Produces(MediaType.APPLICATION_JSON)
    public String allread(@Context HttpServletRequest req, @QueryParam("sid") long streamId) {
        long userId = Session.asLong(req.getSession(), Constants.SESSION_USERID_FIELD, 0);
        if (userId == 0) {
            return JSONErrorMsgs.getAccessDenied();
        }

        String xmlIds = JoinedTable.getAllXmlIds(streamId, userId);
        StringBuilder out = new StringBuilder();

        out.append("{ \"updates\": [");
        for (String xmlId : xmlIds.split(",")) {
            String query = "select t_pub_date from feedreader.feedentries as t0\n" + " where t0.l_xml_id = " + xmlId
                    + " order by t_pub_date desc limit 1";

            try {
                ResultSet rs = Database.rawQuery(query);

                long marker = 0;

                if (rs.next()) {
                    marker = rs.getLong(1);
                    out.append("{").append("\"id\" : ").append(xmlId).append(",").append("\"update\" : \"")
                            .append(marker).append("\",").append("\"str\" : \"").append(new Date(rs.getLong(1)))
                            .append("\",");
                } else {
                    out.append("{").append("\"id\" : ").append(xmlId).append(",").append("\"info\" : \"")
                            .append("no records").append("\",");
                }

                int r = UserFeedSubscriptionsTable.updateReadMarker(userId, Long.parseLong(xmlId), marker);
                out.append("\"updated\" : ").append(r).append("}");
            } catch (SQLException ex) {
                Logger.error(clz).log(query).end();

                out.append("{").append("\"id\" : ").append(xmlId).append(",").append("\"error\" : \'")
                        .append("database").append("\",").append("}");
            }

            out.append(",");
        }
        if (out.length() > 0) {
            out.setLength(out.length() - 1);
        }
        out.append("]");

        int r = UserStreamGroupsTable.updateUnreadCount(userId, streamId, 0);

        out.append(", \"readCount\" : ").append(r).append("}");
        //
        // return JSONUtils.count(UserStreamGroupsTable.setMaxTime(userId, streamId, time));

        return out.toString();
    }

    @GET
    @Path("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    public String delete(@Context HttpServletRequest req, @QueryParam("sid") long streamId,
            @QueryParam("dp") boolean delFromAllrofiles) {
        long userId = Session.getUserId(req.getSession());
        long profileId = Session.getProfileId(req.getSession());

        if (userId == 0 || profileId == 0) {
            return JSONErrorMsgs.getAccessDenied();
        }

        StringBuilder sb = new StringBuilder();

        if (delFromAllrofiles) {
            UserProfilesTable.removeStreamGroupFromUserId(streamId, userId);
        } else {
            UserProfilesTable.removeStreamGroupFromProfile(streamId, profileId);
        }

        if (!UserProfilesTable.streamGroupKnown(streamId)) {
            UserStreamGroupsTable.removeStreamGroup(streamId);
        }

        sb.append("{\"count\": ").append(1).append("}");

        return sb.toString();
    }

    @GET
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    public String addStream(@Context HttpServletRequest req, @QueryParam("sn") String streamName) {
        long userId = Session.getUserId(req.getSession());
        long profileId = Session.getProfileId(req.getSession());

        if (userId == 0 || profileId == 0) {
            return JSONErrorMsgs.getAccessDenied();
        }

        if (streamName == null || streamName.isEmpty()) {
            return JSONUtils.error(0, "Need a name. query.sn.");
        }

        long streamId = UserStreamGroupsTable.save(userId, streamName);
        if (streamId == -1) {
            return JSONUtils.error(0, "Error adding new stream group.");
        }

        if (profileId == 0) {
            UserProfilesTable.addStreamToAllProfiles(userId, streamId);
            return JSONUtils.success(JSONUtils.escapeQuotes(streamName) + " added to all profiles.", "\"id\":"
                    + streamId + "");
        } else {
            UserProfilesTable.addStreamToProfile(streamId, profileId);
            return JSONUtils.success(JSONUtils.escapeQuotes(streamName) + " added to profile.", "\"id\":" + streamId
                    + "");
        }
    }

    @GET
    @Path("/rename")
    @Produces(MediaType.APPLICATION_JSON)
    public String streamRename(@Context HttpServletRequest req, @QueryParam("sid") long streamId,
            @QueryParam("sn") String streamName) {
        long userId = Session.getUserId(req.getSession());
        if (userId == 0) {
            return JSONErrorMsgs.getAccessDenied();
        }

        return JSONUtils.count(UserStreamGroupsTable.rename(userId, streamId, streamName));
    }

    @GET
    @Path("/feed")
    @Produces(MediaType.APPLICATION_JSON)
    public String feed(@Context HttpServletRequest req, @QueryParam("id") long streamId,
            @QueryParam("filter") int filter, @QueryParam("page") long paging, @QueryParam("offset") int offset,
            @QueryParam("sort") int sort) {
        long userId = Session.getUserId(req.getSession());
        if (userId == 0) {
            return JSONErrorMsgs.getAccessDenied();
        }

        long userMaxHistTime = CronTimeUtils.getMaxHistory(req.getSession());

        String sortDir = ((sort == 0) ? "desc" : "asc");

        if (filter == FILTER_ALL) {
            // TODO: Don't query ALL fields. This query can be optimized. See anaylze describe.
            StringBuilder sb = new StringBuilder();

            String rawQuery = String.format("SELECT %s FROM %s WHERE %s in (" + "SELECT %s FROM %s AS t1 INNER JOIN "
                    + "    %s AS t2 ON t1.%s = t2.%s " + " WHERE t1.%s = %s " + "    AND t2.%s = %s"
                    + ") AND %s > %d ORDER BY %s %s LIMIT %d OFFSET %d", ConfigAPI.defaulEntryColumns(""),
                    FeedEntriesTable.TABLE, DBFields.LONG_XML_ID, DBFields.LONG_XML_ID,
                    UserFeedSubscriptionsTable.TABLE, UserStreamGroupsTable.TABLE_STREAM_SUBSCRIPTIONS,
                    DBFields.LONG_SUBS_ID, DBFields.LONG_SUBS_ID, DBFields.LONG_USER_ID, userId,
                    DBFields.LONG_STREAM_ID, streamId,
                    // 2 part of where
                    DBFields.TIME_PUBLICATION_DATE, userMaxHistTime,
                    // sort, limit
                    DBFields.TIME_PUBLICATION_DATE, sortDir, 
                    FeedAppConfig.DEFAULT_API_FETCH_ARTICLES,
                    paging * FeedAppConfig.DEFAULT_API_FETCH_ARTICLES);
            Logger.debugSQL(FeedsAPI.class).log(rawQuery).end();
            sb.append("{\"entries\" : [");
            try {
                ResultSet rs = Database.rawQuery(rawQuery);
                int count = 0;
                while (rs.next()) {
                    sb.append("{").append(JSONUtils.getNumber(rs, DBFields.LONG_ENTRY_ID)).append(",")
                            .append(JSONUtils.getNumber(rs, DBFields.LONG_XML_ID)).append(",")
                            .append(JSONUtils.getString(rs, DBFields.STR_LINK)).append(",")
                            .append(JSONUtils.getString(rs, DBFields.STR_TITLE)).append(",")
                            .append(JSONUtils.getNumber(rs, DBFields.TIME_PUBLICATION_DATE)).append("},");
                    count++;
                }
                if (count > 0 && sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
            } catch (SQLException ex) {
                Logger.error(FeedsAPI.class).log("list error ").log(ex.getMessage()).end();
            }

            sb.append("]}");

            return sb.toString();
        } else if (filter == FILTER_UNREAD) {
            try {
                StringBuilder sb = new StringBuilder();

                long pageStart = CurrentTime.inGMT();
                long profileId = Session.asLong(req.getSession(), Constants.SESSION_SELECTED_PROFILE_ID, 0);

                long callStart = System.currentTimeMillis();
                StringBuilder unreadEntries = new StringBuilder();
                long unreadEntriesCount = JoinedTable.allStreamUnreadEntries(streamId, userId, userMaxHistTime,
                        unreadEntries);
                long unreadExec = System.currentTimeMillis() - callStart;

                if (unreadEntries.length() == 0) {
                    unreadEntries.append("0");
                }

                String query = "select l_xml_id, l_entry_id, s_link, s_title, t_pub_date from feedreader.feedentries "
                        + "where l_entry_id in ( " + unreadEntries.toString() + ") " + " and t_pub_date > "
                        + userMaxHistTime + " order by t_pub_date " + sortDir + "" + " limit "
                        + FeedAppConfig.DEFAULT_API_FETCH_ARTICLES + " offset " + offset;
                callStart = System.currentTimeMillis();
                ResultSet rs = Database.rawQuery(query);
                long listExec = System.currentTimeMillis() - callStart;

                rs.setFetchSize(FeedAppConfig.DEFAULT_API_FETCH_ARTICLES);
                sb.append("{\"entries\" : [");
                int r = APIUtils.wrapObject(sb, rs);

                if (r < FeedAppConfig.DEFAULT_API_FETCH_ARTICLES) {
                    UserStreamGroupsTable.updateUnreadCount(userId, streamId, unreadEntriesCount);
                }
                sb.append("],");
                sb.append("\"maxTime\" : \"").append(new Date(userMaxHistTime)).append("\", ");
                sb.append("\"maxTimeRaw\" : \"").append(userMaxHistTime).append("\", ");
                sb.append("\"unread\" : ").append(unreadEntriesCount).append(", ");
                sb.append("\"unreadExec\" : ").append(unreadExec).append(", ");
                sb.append("\"listExec\" : ").append(listExec).append(", ");
                sb.append("\"pageTime\" : ").append(System.currentTimeMillis() - pageStart).append(", ");
                sb.append("\"profileId\" : ").append(profileId).append("}");

                return sb.toString();
            } catch (SQLException ex) {
                if (Environment.isDev()) {
                    return JSONUtils.error(0, ex.getMessage());
                }
                return JSONUtils.error(0, "database error.");
            }
        } else {
            return JSONUtils.error(0, "unknown filter mode.");
        }
    }

}
