package feedreader.api.v1;

import com.restfb.json.JsonObject;

import feedreader.security.Session;
import feedreader.store.UserKeyValuesTable;
import feedreader.utils.JSONUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/v1/user/settings")
public class UserSettings {

    static final Class<?> clz = UserSettings.class;

    @GET
    @Path("/reader")
    @Produces(MediaType.APPLICATION_JSON)
    public String reader(@Context HttpServletRequest req, @QueryParam("showUnreadOnly") boolean unreadOnly,
            @QueryParam("sortByAlphabet") int sortAz, @QueryParam("sortByUnread") int sortUnread) {
        long userId = Session.getUserId(req.getSession());
        long profileId = Session.getProfileId(req.getSession());
        if (userId == 0 || profileId == 0) {
            return JSONErrorMsgs.getAccessDenied();
        }

        JsonObject obj = new JsonObject();
        obj.put(JSONFields.BOOL_SHOW_UNREAD_ONLY, unreadOnly);
        obj.put(JSONFields.INT_SORT_AZ, sortAz);
        obj.put(JSONFields.INT_SORT_UNREAD, sortUnread);

        return JSONUtils.count(UserKeyValuesTable.save(userId, profileId, UserKeyValuesTable.READER_SETTINGS_KEY, obj));
    }

    @GET
    @Path("/modules")
    @Produces(MediaType.APPLICATION_JSON)
    public String modules(@Context HttpServletRequest req, @QueryParam("tmpl") String tmpl,
            @QueryParam("v") int viewMode) {
        long userId = Session.getUserId(req.getSession());
        long profileId = Session.getProfileId(req.getSession());
        if (userId == 0 || profileId == 0) {
            return JSONErrorMsgs.getAccessDenied();
        }

        int k = -1;
        switch (tmpl) {
        case "saved":
            k = UserKeyValuesTable.VIEW_SAVED_SETTINGS;
            break;

        case "all":
            k = UserKeyValuesTable.VIEW_ALL_SETTINGS;
            break;

        case "rr":
            k = UserKeyValuesTable.VIEW_RECENTLY_READ_SETTINGS;
            break;

        default:
            return JSONErrorMsgs.getErrorParams();
        }

        JsonObject obj = new JsonObject();
        obj.put(JSONFields.INT_VIEW_MODE, viewMode);

        return JSONUtils.count(UserKeyValuesTable.save(userId, profileId, k, obj));
    }

}
