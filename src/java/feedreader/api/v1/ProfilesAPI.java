package feedreader.api.v1;

import com.restfb.json.JsonObject;

import feedreader.config.FeedAppConfig;
import feedreader.entities.ProfileData;
import feedreader.log.Logger;
import feedreader.security.Session;
import feedreader.store.DBFields;
import feedreader.store.Database;
import feedreader.store.UserFeedEntries;
import feedreader.store.UserKeyValuesTable;
import feedreader.store.UserProfilesTable;
import feedreader.utils.JSONUtils;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/v1/user/profiles") public class ProfilesAPI {

    static final Class<?> clz = StreamsAPI.class;

    @GET @Path("/list") @Produces(MediaType.APPLICATION_JSON) public String list(@Context HttpServletRequest req,
            @DefaultValue("true") @QueryParam("rs") boolean readerSettings) throws IOException {
        long userId = Session.getUserId(req.getSession());
        if (userId == 0) {
            return JSONErrorMsgs.getAccessDenied();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{\"entries\" : [");

        try {
            String query = String.format("SELECT * FROM %s WHERE %s = %d", UserProfilesTable.TABLE,
                    DBFields.LONG_USER_ID, userId);

            ResultSet rs = Database.rawQuery(query);

            while (rs.next()) {
                long profileId = rs.getLong(DBFields.LONG_PROFILE_ID);

                sb.append("{").append(JSONUtils.getNumber(rs, DBFields.LONG_PROFILE_ID)).append(",")
                        .append(JSONUtils.getString(rs, DBFields.STR_PROFILE_NAME)).append(",")
                        .append(JSONUtils.getString(rs, DBFields.STR_COLOR)).append(",")
                        .append(JSONUtils.getBoolean(rs, DBFields.BOOL_IS_DEFAULT)).append(",");

                JsonObject obj = UserKeyValuesTable
                        .get(userId, profileId, UserKeyValuesTable.READER_SETTINGS_KEY, true);
                sb.append("\"settings\": ").append(obj.toString()).append(",");
                obj = UserKeyValuesTable.get(userId, profileId, UserKeyValuesTable.VIEW_ALL_SETTINGS, true);
                sb.append("\"all_settings\": ").append(obj.toString()).append(",");
                obj = UserKeyValuesTable.get(userId, profileId, UserKeyValuesTable.VIEW_SAVED_SETTINGS, true);
                sb.append("\"saved_settings\": ").append(obj.toString()).append(",");
                obj = UserKeyValuesTable.get(userId, profileId, UserKeyValuesTable.VIEW_RECENTLY_READ_SETTINGS, true);
                sb.append("\"rr_settings\": ").append(obj.toString()).append("},");
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
        } catch (SQLException ex) {
            Logger.error(StreamsAPI.class).log("error ").log(ex.getMessage()).end();
        }

        sb.append("]}");

        return sb.toString();
    }

    @GET @Path("/new") @Produces(MediaType.APPLICATION_JSON) public String add(@Context HttpServletRequest req,
            @QueryParam("n") String profileName, @QueryParam("c") String profileColor) {
        long userId = Session.getUserId(req.getSession());

        if (userId == 0) {
            return JSONErrorMsgs.getAccessDenied();
        }

        if (profileName == null || profileName.isEmpty()) {
            return JSONUtils.error(0, "No name passed.");
        }

        int r = UserProfilesTable.getProfileCount(userId);

        if (r >= FeedAppConfig.MAX_PROFILES_COUNT) {
            return JSONUtils.error(r, "Maximum number of profiles reached. " + FeedAppConfig.MAX_PROFILES_COUNT);
        }

        if (r == -1) {
            return JSONUtils.error(r, "couldn't execute query.");
        }

        if (profileColor == null || profileColor.isEmpty()) {
            profileColor = FeedAppConfig.DEFAULT_PROFILE_COLOR;
        }

        long profileId = UserProfilesTable.addProfile(userId, profileName, profileColor);
        if (profileId == -1) {
            return JSONUtils.error(0, "error adding new profile.");
        }

        return JSONUtils.success("profile created.", "\"profileid\" : " + profileId);
    }

    @GET @Path("/save") @Produces(MediaType.APPLICATION_JSON) public String
            save(@Context HttpServletRequest req, @QueryParam("pid") long profileId,
                    @QueryParam("n") String profileName, @QueryParam("c") String profileColor) {
        long userId = Session.getUserId(req.getSession());

        if (userId == 0 || profileId == 0) {
            return JSONErrorMsgs.getAccessDenied();
        }

        ProfileData data = UserProfilesTable.getProfile(userId, profileId);
        if (data.getProfileId() == 0) {
            return JSONUtils.error(0, "profile not found.");
        }

        if (profileName != null && !profileName.isEmpty()) {
            data.setName(profileName);
        }

        if (profileColor != null && !profileColor.isEmpty()) {
            data.setColor(profileColor);
        }

        return JSONUtils.count(UserProfilesTable.save(userId, data));
    }

    @GET @Path("/delete") @Produces(MediaType.APPLICATION_JSON) public String delete(@Context HttpServletRequest req,
            @QueryParam("pid") long pid) {
        long userId = Session.getUserId(req.getSession());
        if (userId == Session.INVALID_USER_ID) {
            return JSONErrorMsgs.getAccessDenied();
        }

        ProfileData pData = UserProfilesTable.getProfile(userId, pid);
        long profileId = pData.getProfileId();
        if (profileId == ProfileData.INVALID_PROFILE_ID) {
            return JSONErrorMsgs.getAccessDenied();
        }

        if (UserFeedEntries.removeAllSavedEntries(userId, profileId) == -1) {
            return JSONUtils.error(0, "Error deleting saved entries for profile.");
        }

        if (UserFeedEntries.removeAllEntriesInfo(userId, profileId) == -1) {
            return JSONUtils.error(0, "Error deleting entry infos for profile.");
        }

        if (UserProfilesTable.removeAllStreamGroupFromProfile(profileId) == -1) {
            return JSONUtils.error(0, "Error deleting stream groups for profile.");
        }

        if (UserKeyValuesTable.delete(userId, profileId) == -1) {
            return JSONUtils.error(0, "Error deleting user key values for profile.");
        }

        if (UserProfilesTable.delete(userId, profileId) == -1) {
            return JSONUtils.error(0, "Error deleting profile. We were informed about this"
                    + " issue and will take a look.");
        }

        int c = UserProfilesTable.getProfileCount(userId);
        if (c == 0) {
            UserProfilesTable.createDefaultProfile(userId);
        }

        return JSONUtils.count(1);
    }

}
