package feedreader.api.v1;

import feedreader.config.Constants;
import feedreader.config.FeedAppConfig;
import feedreader.log.Logger;
import feedreader.security.Session;
import feedreader.store.DBFields;
import feedreader.store.Database;
import feedreader.store.FeedEntriesTable;
import feedreader.store.UserFeedEntries;
import feedreader.store.UserStreamGroupsTable;
import feedreader.utils.JSONUtils;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/v1/entries")
public class FeedEntriesAPI {

    static final Class<?> clz = StreamsAPI.class;

    static final String SAVE_ACTION = "s";
    static final String REMOVE_ACTION = "r";

    @GET
    @Path("/entry")
    @Produces(MediaType.APPLICATION_JSON)
    public String entry(@Context HttpServletRequest req, @QueryParam("id") long entryId, @QueryParam("act") String action) {
        StringBuilder sb = new StringBuilder();

        if (entryId == 1337) {
            return "{\"count\": 1}";
        }
        if (entryId == 0) {
            return JSONUtils.empty();
        }

        long userId = Session.asLong(req.getSession(), Constants.SESSION_USERID_FIELD, 0);
        long profileId = Session.asLong(req.getSession(), Constants.SESSION_SELECTED_PROFILE_ID, 0);

        if (userId == 0 || profileId == 0) {
            return JSONErrorMsgs.getAccessDenied();
        }

        if (action.equals(SAVE_ACTION)) {
            sb.append("{\"count\": ").append(UserFeedEntries.userSaved(userId, profileId, entryId)).append("}");
        } else {
            sb.append("{\"count\": ").append(UserFeedEntries.removeFromSave(userId, profileId, entryId)).append("}");
        }

        return sb.toString();
    }

    @GET
    @Path("/saved")
    @Produces(MediaType.APPLICATION_JSON)
    public String savedEntries(@Context HttpServletRequest req, @QueryParam("paging") long paging) {
        long userId = Session.asLong(req.getSession(), Constants.SESSION_USERID_FIELD, 0);
        long profileId = Session.asLong(req.getSession(), Constants.SESSION_SELECTED_PROFILE_ID, 0);

        if (userId == 0 || profileId == 0) {
            return JSONErrorMsgs.getAccessDenied();
        }

        StringBuilder sb = new StringBuilder();

        sb.append("{\"entries\" : [");

        String query = String.format("SELECT %s, t0.%s FROM %s AS t0 INNER JOIN %s AS t1 ON t0.%s = t1.%s WHERE t0.%s = %d "
                + "ORDER BY t0.%s DESC LIMIT %d OFFSET %d",
                ConfigAPI.defaulEntryColumns("t1."),
                DBFields.TIME_SAVED_AT,
                UserFeedEntries.TABLE_SAVED, FeedEntriesTable.TABLE,
                DBFields.LONG_ENTRY_ID, DBFields.LONG_ENTRY_ID,
                DBFields.LONG_PROFILE_ID, profileId,
                DBFields.TIME_SAVED_AT,
                FeedAppConfig.DEFAULT_API_FETCH_ARTICLES,
                paging * FeedAppConfig.DEFAULT_API_FETCH_ARTICLES);
        Logger.debugSQL(query).end();

        try {
            ResultSet rs = Database.rawQuery(query);
            APIUtils.wrapObject(sb, rs);
        } catch (SQLException ex) {
            Logger.error(clz).log("savedEntries ").log(ex.getMessage()).end();
        }

        sb.append("]}");

        return sb.toString();
    }

    @GET
    @Path("/recently_read")
    @Produces(MediaType.APPLICATION_JSON)
    public String recentlyRead(@Context HttpServletRequest req, @QueryParam("page") long paging) {
        long profileId = Session.asLong(req.getSession(), Constants.SESSION_SELECTED_PROFILE_ID, 0);

        if (profileId == 0) {
            return JSONErrorMsgs.getAccessDenied();
        }

        StringBuilder sb = new StringBuilder();

        sb.append("{\"entries\" : [");

        String query = String.format("SELECT %s FROM %s AS t0 INNER JOIN %s AS t1 ON t0.%s = t1.%s "
                + "WHERE t0.%s = %d AND t0.%s != %d ORDER BY t0.%s %s LIMIT %d OFFSET %d",
                ConfigAPI.defaulEntryColumns("t1."),
                UserFeedEntries.TABLE_INFO, FeedEntriesTable.TABLE,
                DBFields.LONG_ENTRY_ID, DBFields.LONG_ENTRY_ID,
                DBFields.LONG_PROFILE_ID, profileId,
                DBFields.INT_CLICKED, 0,
                DBFields.TIME_READ_ON, "DESC",
                FeedAppConfig.DEFAULT_API_FETCH_ARTICLES,
                paging * FeedAppConfig.DEFAULT_API_FETCH_ARTICLES);
        Logger.debugSQL(query).end();
        try {
            ResultSet rs = Database.rawQuery(query);
            APIUtils.wrapObject(sb, rs);
        } catch (SQLException ex) {
            Logger.error(clz).log("recentlyRead ").log(ex.getMessage()).end();
        }

        sb.append("]}");

        return sb.toString();
    }

    @GET
    @Path("/clear_recently_read")
    @Produces(MediaType.APPLICATION_JSON)
    public String clearRecentlyRead(@Context HttpServletRequest req) {
        long userId = Session.asLong(req.getSession(), Constants.SESSION_USERID_FIELD, 0);
        long profileId = Session.asLong(req.getSession(), Constants.SESSION_SELECTED_PROFILE_ID, 0);

        if (userId == 0 || profileId == 0) {
            return JSONErrorMsgs.getAccessDenied();
        }

        return JSONUtils.count(UserFeedEntries.removeAllEntriesInfo(userId, profileId));
    }

    @GET
    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    public String update(@Context HttpServletRequest req, @QueryParam("e") long eventId, @QueryParam("id") long entryId) {
        if (entryId == 0) {
            return JSONUtils.error(0, "need an entry id.");
        }

        long userId = Session.getUserId(req.getSession());
        long profileId = Session.getProfileId(req.getSession());

        if (userId == 0 || profileId == 0) {
            return JSONUtils.error(0, JSONErrorMsgs.NO_USERID_PROFILE_ID);
        }

        UserFeedEntries.clicked(userId, profileId, entryId);

        return JSONUtils.count(FeedEntriesTable.clicked(entryId));
    }

    @GET
    @Path("/read")
    @Produces(MediaType.APPLICATION_JSON)
    public String read(@Context HttpServletRequest req, @QueryParam("e") String entries,
            @QueryParam("sid") long streamId, @QueryParam("c") int count) {

        long userId = Session.asLong(req.getSession(), Constants.SESSION_USERID_FIELD, 0);
        long profileId = Session.asLong(req.getSession(), Constants.SESSION_SELECTED_PROFILE_ID, 0);

        if (userId == 0 || profileId == 0) {
            return JSONErrorMsgs.getAccessDenied();
        }

        int r = 0;

        if (entries.contains(",")) {
            String[] eall = entries.split(",");

            for (String e : eall) {
                long entryId = Long.parseLong(e);
                r += (UserFeedEntries.setRead(true, userId, profileId, entryId)) ? 1 : 0;
            }
        } else {
            long entryId = Long.parseLong(entries);
            r += (UserFeedEntries.setRead(true, userId, profileId, entryId)) ? 1 : 0;
        }

        UserStreamGroupsTable.updateUnreadCount(userId, streamId, count);

        return JSONUtils.count(r);
    }

}
