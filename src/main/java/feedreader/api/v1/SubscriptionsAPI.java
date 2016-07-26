package feedreader.api.v1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
import feedreader.entities.FeedSourceEntry;
import feedreader.entities.OPMLEntry;
import feedreader.feed.utils.Fetch;
import feedreader.security.Session;
import feedreader.security.UserSession;
import feedreader.store.DBFields;
import feedreader.store.Database;
import feedreader.store.FeedSourcesTable;
import feedreader.store.UserFeedSubscriptionsTable;
import feedreader.store.UserProfilesTable;
import feedreader.store.UserStreamGroupsTable;
import feedreader.utils.JSONUtils;
import feedreader.utils.SQLUtils;

@Path("/v1/user/subscriptions")
public class SubscriptionsAPI {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionsAPI.class);
    private static final String streamIdQuery = String
            .format("SELECT t1.%s, t1.%s, t1.%s, t2.%s, t2.%s, t2.%s FROM %s AS t0 "
                    + " RIGHT JOIN %s as t1 ON t0.%s = t1.%s " + " INNER JOIN %s as t2 ON t1.%s = t2.%s "
                    + " WHERE t0.%s = ? ORDER BY t1.%s %s", DBFields.LONG_SUBS_ID, DBFields.LONG_XML_ID,
                    DBFields.STR_SUBSCRIPTION_NAME, DBFields.BOOL_GAVE_UP, DBFields.STR_LAST_ERROR,
                    DBFields.INT_TOTAL_ENTRIES, UserStreamGroupsTable.TABLE_STREAM_SUBSCRIPTIONS,
                    UserFeedSubscriptionsTable.TABLE, DBFields.LONG_SUBS_ID, DBFields.LONG_SUBS_ID,
                    FeedSourcesTable.TABLE, DBFields.LONG_XML_ID, DBFields.LONG_XML_ID,
                    DBFields.LONG_STREAM_ID, DBFields.STR_SUBSCRIPTION_NAME,
                    FeedAppConfig.DEFAULT_API_SORT_USER_SUBSCRIPTIONS_LIST);
    private static final String listAllQuery = String
            .format("SELECT t0.%s, t0.%s, t0.%s, t1.%s, t1.%s, t1.%s FROM %s as t0\n"
                    + "    INNER JOIN %s as t1 \n" + "        ON t0.%s = t1.%s\n"
                    + "    WHERE %s = ? ORDER BY t0.%s %s", DBFields.LONG_SUBS_ID, DBFields.LONG_XML_ID,
                    DBFields.STR_SUBSCRIPTION_NAME, DBFields.BOOL_GAVE_UP, DBFields.STR_LAST_ERROR,
                    DBFields.INT_TOTAL_ENTRIES, UserFeedSubscriptionsTable.TABLE, FeedSourcesTable.TABLE,
                    DBFields.LONG_XML_ID, DBFields.LONG_XML_ID, DBFields.LONG_USER_ID,
                    DBFields.STR_SUBSCRIPTION_NAME, FeedAppConfig.DEFAULT_API_SORT_USER_SUBSCRIPTIONS_LIST);

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public String list(@Context HttpServletRequest req, @QueryParam("sid") long streamId) {
        long userId = UserSession.getUserId(req);
        if (userId == 0) {
            return JSONErrorMsgs.getAccessDenied();
        }

        StringBuilder sb = new StringBuilder();

        ResultSet rs;
        try (Connection conn = Database.getConnection()) {
            if (streamId == 0) {
                PreparedStatement stmt = conn.prepareStatement(listAllQuery);
                stmt.setLong(1, userId);
                rs = stmt.executeQuery();
            } else {
                PreparedStatement stmt = conn.prepareStatement(streamIdQuery);
                stmt.setLong(1, streamId);
                rs = stmt.executeQuery();
            }
            sb.append("{\"entries\" : [");
            int count = 0;
            while (rs.next()) {
                sb.append("{").append(JSONUtils.getNumber(rs, DBFields.LONG_SUBS_ID)).append(",")
                        .append(JSONUtils.getNumber(rs, DBFields.LONG_XML_ID)).append(",")
                        .append(JSONUtils.getBoolean(rs, DBFields.BOOL_GAVE_UP)).append(",")
                        .append(JSONUtils.getString(rs, DBFields.STR_LAST_ERROR)).append(",")
                        .append(JSONUtils.getNumber(rs, DBFields.INT_TOTAL_ENTRIES)).append(",")
                        .append(JSONUtils.getString(rs, DBFields.STR_SUBSCRIPTION_NAME)).append("},");
                count++;
            }
            if (count > 0) {
                sb.setLength(sb.length() - 1);
            }
        } catch (SQLException e) {
            logger.error("/list error: {}", e, e.getMessage());
            return JSONErrorMsgs.getRequestingDataError(e);
        }

        sb.append("]}");

        return sb.toString();
    }

    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    public String get(@Context HttpServletRequest req, @QueryParam("id") long subscriptionId,
            @QueryParam("sid") long sourceId) {
        long userId = Session.getUserId(req.getSession());
        if (userId == 0) {
            return JSONErrorMsgs.getAccessDenied();
        }

        StringBuilder sb = new StringBuilder();

        String rawQuery;

        if (subscriptionId == 0 && sourceId == 0) {
            return JSONErrorMsgs.getErrorParams();
        }
        rawQuery = String.format("SELECT * FROM %s AS t0" + " INNER JOIN %s AS t1"
                + " ON t0.%s = t1.%s WHERE %s = %s", UserFeedSubscriptionsTable.TABLE, FeedSourcesTable.TABLE,
                DBFields.LONG_XML_ID, DBFields.LONG_XML_ID, DBFields.LONG_USER_ID, userId);

        if (subscriptionId > 0) {
            rawQuery += " AND t0." + DBFields.LONG_SUBS_ID + " = " + subscriptionId;
        } else if (sourceId > 0) {
            rawQuery += " AND t1." + DBFields.LONG_XML_ID + " = " + sourceId;
        }

        try (Connection conn = Database.getConnection()) {
            ResultSet rs = Database.rawQuery(conn, rawQuery);
            APIUtils.wrapObject(sb, rs);
        } catch (SQLException e) {
            logger.error("/get error: {}", e, e.getMessage());
            return JSONErrorMsgs.getRequestingDataError(e);
        }

        return sb.toString();
    }

    @GET
    @Path("/removefromstream")
    @Produces(MediaType.APPLICATION_JSON)
    public String removeFromStream(@Context HttpServletRequest req, @QueryParam("sid") long streamId,
            @QueryParam("sui") long subscriptionId) {
        long userId = Session.getUserId(req.getSession());
        if (userId == 0) {
            return JSONErrorMsgs.getAccessDenied();
        }

        String rawQuery;

        if (streamId == 0 || subscriptionId == 0) {
            return JSONErrorMsgs.getErrorParams();
        }
        rawQuery = String.format("DELETE FROM %s WHERE %s = %d AND %s = %d",
                UserStreamGroupsTable.TABLE_STREAM_SUBSCRIPTIONS, DBFields.LONG_STREAM_ID, streamId,
                DBFields.LONG_SUBS_ID, subscriptionId);

        try (Connection conn = Database.getConnection()) {
            return JSONUtils.count(conn.createStatement().executeUpdate(rawQuery));
        } catch (SQLException e) {
            logger.error("removefromstream failed: {}", e, e.getMessage());
            return JSONErrorMsgs.getRequestingDataError(e);
        }
    }

    @GET
    @Path("/addtostream")
    @Produces(MediaType.APPLICATION_JSON)
    public String addToStream(@Context HttpServletRequest req, @QueryParam("sid") long streamId,
            @QueryParam("sui") long subscriptionId) {
        long userId = Session.getUserId(req.getSession());
        if (userId == 0) {
            return JSONErrorMsgs.getAccessDenied();
        }

        if (streamId == 0 || subscriptionId == 0) {
            return JSONErrorMsgs.getErrorParams();
        }

        UserStreamGroupsTable.setMaxTime(userId, streamId, 0); // invalidates.
        return JSONUtils.count(UserStreamGroupsTable.addSubscriptionToStream(streamId, subscriptionId));
    }

    @GET
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    public String add(@Context HttpServletRequest req, @QueryParam("sid") long streamId,
            @QueryParam("n") String subsName, @QueryParam("u") String subsUrl) {
        long userId = Session.getUserId(req.getSession());
        if (userId == 0) {
            return JSONErrorMsgs.getAccessDenied();
        }

        FeedSourceEntry entry = FeedSourcesTable.getByField(DBFields.STR_XML_URL, subsUrl);
        if (entry.getId() == 0) {
            String code = Fetch.validFeed(subsUrl);

            if (code == null || !code.isEmpty()) {
                return JSONUtils.error(0, "Feed error: " + code);
            }

            FeedSourcesTable.RetCodes r = FeedSourcesTable.addNewSource(subsUrl);
            if (r == FeedSourcesTable.RetCodes.ERROR) {
                return JSONUtils.error(0,
                        "There was an error adding the requested feed. We were informed and will have a look.");
            }

            entry = FeedSourcesTable.getByField(DBFields.STR_XML_URL, subsUrl);
            if (entry.getId() == 0) {
                return JSONUtils.error(0, "The feed was added but we couldn't fetch its current status.");
            }
        }

        long subsId = UserFeedSubscriptionsTable.save(userId, entry.getId(), new OPMLEntry(subsName, subsUrl));
        if (subsId == -1) {
            return JSONUtils.error(0, "Error adding subscription.");
        }

        if (!UserStreamGroupsTable.addSubscriptionToStream(streamId, subsId)) {
            JSONUtils.error(0, "Error adding subscription to stream.");
        }

        return JSONUtils.success("Subscription added.");
    }

    @GET
    @Path("/remove")
    @Produces(MediaType.APPLICATION_JSON)
    public String remove(@Context HttpServletRequest req, @QueryParam("sid") long subId) {
        long userId = Session.getUserId(req.getSession());
        if (userId == 0) {
            return JSONErrorMsgs.getAccessDenied();
        }

        return JSONUtils.count(UserFeedSubscriptionsTable.removeSubscription(userId, subId));
    }

    @GET
    @Path("/set")
    @Produces(MediaType.APPLICATION_JSON)
    public String set(@Context HttpServletRequest req, @QueryParam("id") long subscriptionId,
            @QueryParam("n") String name) {
        long userId = Session.getUserId(req.getSession());
        if (userId == 0) {
            return JSONErrorMsgs.getAccessDenied();
        }

        String rawQuery;

        if (subscriptionId == 0) {
            return JSONErrorMsgs.getErrorParams();
        }
        rawQuery = String.format("UPDATE %s SET %s = '%s' WHERE %s = %d", UserFeedSubscriptionsTable.TABLE,
                DBFields.STR_SUBSCRIPTION_NAME, SQLUtils.asSafeString(name), DBFields.LONG_SUBS_ID, subscriptionId);

        try (Connection conn = Database.getConnection()) {
            return JSONUtils.count(conn.createStatement().executeUpdate(rawQuery));
        } catch (SQLException e) {
            logger.error("/set failed: {}", e, e.getMessage());
            return JSONErrorMsgs.getRequestingDataError(e);
        }
    }

    /**
     * Just a convenience interface. If this query is causing performance issues, another approach is to start querying
     * from the profiles to the subscriptions, select * from profiles, select * from streamgroups, select * from
     * feedsubscriptions and do everything on the front end.
     *
     * @param req
     * @param subscriptionId
     * @return
     */
    @GET
    @Path("/withprofile")
    @Produces(MediaType.APPLICATION_JSON)
    public String withProfile(@Context HttpServletRequest req, @QueryParam("id") long subscriptionId) {
        long userId = Session.getUserId(req.getSession());
        if (userId == 0) {
            return JSONErrorMsgs.getAccessDenied();
        }

        StringBuilder sb = new StringBuilder();

        String rawQuery;

        if (subscriptionId == 0) {
            return JSONErrorMsgs.getErrorParams();
        }
        rawQuery = String.format("SELECT * FROM %s AS t0\n" + "    INNER JOIN %s AS t1 ON t0.%s = t1.%s\n"
                + "    INNER JOIN %s AS t2 ON t1.%s = t2.%s\n" + "    INNER JOIN %s AS t3 ON t3.%s = t2.%s\n"
                + "    WHERE t0.%s = %d", UserStreamGroupsTable.TABLE_STREAM_SUBSCRIPTIONS,
                UserStreamGroupsTable.TABLE, DBFields.LONG_STREAM_ID, DBFields.LONG_STREAM_ID,
                UserProfilesTable.TABLE_STREAM_GROUPS, DBFields.LONG_STREAM_ID, DBFields.LONG_STREAM_ID,
                UserProfilesTable.TABLE, DBFields.LONG_PROFILE_ID, DBFields.LONG_PROFILE_ID, DBFields.LONG_SUBS_ID,
                subscriptionId);

        sb.append("[");
        try (Connection conn = Database.getConnection()) {
            ResultSet rs = Database.rawQuery(conn, rawQuery);
            int count = 0;
            while (rs.next()) {
                sb.append("{").append(JSONUtils.getNumber(rs, DBFields.LONG_SUBS_ID)).append(",")
                        .append(JSONUtils.getNumber(rs, DBFields.LONG_STREAM_ID)).append(",")
                        .append(JSONUtils.getString(rs, DBFields.STR_STREAM_NAME)).append(",")
                        .append(JSONUtils.getNumber(rs, DBFields.LONG_PROFILE_ID)).append(",")
                        .append(JSONUtils.getString(rs, DBFields.STR_PROFILE_NAME)).append("},");
                count++;
            }
            if (count > 0 && sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
        } catch (SQLException ex) {
            logger.error("/withprofile failed: {}", ex, ex.getMessage());
            return JSONErrorMsgs.getRequestingDataError(ex);
        }

        sb.append("]");

        return sb.toString();
    }

}
