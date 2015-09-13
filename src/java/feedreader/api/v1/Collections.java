package feedreader.api.v1;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import feedreader.entities.OPMLEntry;
import feedreader.entities.ProfileData;
import feedreader.log.Logger;
import feedreader.security.Session;
import feedreader.store.CollectionsTable;
import feedreader.store.DBFields;
import feedreader.store.Database;
import feedreader.store.UserFeedSubscriptionsTable;
import feedreader.store.UserProfilesTable;
import feedreader.store.UserStreamGroupsTable;
import feedreader.utils.JSONUtils;
import feedreader.utils.SQLUtils;

@Path("/v1/collections")
public class Collections {

    static final Class<?> clz = Collections.class;

    static final String SAVE_ACTION = "s";
    static final String REMOVE_ACTION = "r";

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public String list(@Context HttpServletRequest req, @QueryParam("page") long page) {
        StringBuilder sb = new StringBuilder();
        String query = CollectionsTable.getQueryList();
        try {
            sb.append("{\"entries\" : [");
            ResultSet rs = Database.rawQuery(query);
            APIUtils.wrapObject(sb, rs);
            sb.append("]}");
        } catch (SQLException e) {
            Logger.error(clz).log("list ").log(e.getMessage()).end();
            return JSONUtils.error(0, "collections list error", e);
        }
        
        return sb.toString();
    }
    
    @GET
    @Path("/entries")
    @Produces(MediaType.APPLICATION_JSON)
    public String entries(@Context HttpServletRequest request, @QueryParam("ids") String idList) {
        
        StringBuilder sb = new StringBuilder();
        String[] arIds = idList.split(",");
        
        sb.append("{\"entries\" : [");
        if (arIds.length > 0) {
            StringBuilder ids = new StringBuilder();
            for (String str : arIds) {
                ids.append(str).append(",");
            }
            if (ids.length() > 0) { 
                ids.setLength(ids.length()-1);
            }
            
            String query = CollectionsTable.getQueryCollectionEntriesByCollectionIds(ids.toString());
            try {
                ResultSet collections = Database.rawQuery(query);
                APIUtils.wrapObject(sb, collections);
            } catch (Exception e) {
                Logger.error(clz).log("entries ").log(e.getMessage()).end();
                return JSONUtils.error(0, "collections entries error", e);
            }
        }
        sb.append("]}");
        return sb.toString();
    }
    
    @POST
    @Path("/share")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public String share(@Context HttpServletRequest req, @FormParam("id") long streamId, @FormParam("name") String name, 
                @FormParam("description") String desc) {
        long userId = Session.getUserId(req.getSession());
        if (userId == Session.INVALID_USER_ID) {
            return JSONErrorMsgs.getAccessDenied();
        }
        
        if (streamId == 0) {
            return JSONUtils.error(0, "missing id");
        }
        
        try {
            
            String insert = String.format("INSERT INTO feedreader.sourcecollections (l_created_by, s_name, s_description) " + 
                    "VALUES (%d, '%s', '%s') RETURNING l_collection_id;",
                    userId, SQLUtils.asSafeString(name),
                    SQLUtils.asSafeString(desc));
            ResultSet rs = Database.rawQuery(insert);
            if (!rs.next()) {
                return JSONUtils.error(0, "collection id couldn't be created ");
            }
            
            long collectionId = rs.getLong(DBFields.LONG_COLLECTION_ID);
            
            String query = "SELECT t2.s_subs_name, t3.l_xml_id FROM feedreader.userstreamgroups AS t0 " +
                    "LEFT JOIN feedreader.userstreamgroupfeedsubscription AS t1 " +
                    "ON t0.l_stream_id = t1.l_stream_id " +
                    "LEFT JOIN feedreader.userfeedsubscriptions AS t2 " +
                    "ON t1.l_subs_id = t2.l_subs_id " +
                    "LEFT JOIN feedreader.feedsources AS t3 " +
                    "ON t2.l_xml_id = t3.l_xml_id " +
                    "WHERE t0.l_stream_id = " + streamId;
            
            rs = Database.rawQuery(query);
            while (rs.next()) {
                String insertRow = String.format("INSERT INTO feedreader.sourcecollectionslist " +
                        "(s_feed_name, l_xml_id, l_collection_id) " +
                        "VALUES ('%s', %d, %d);",
                        SQLUtils.asSafeString(rs.getString(DBFields.STR_SUBSCRIPTION_NAME)),
                        rs.getLong(DBFields.LONG_XML_ID),
                        collectionId);
                Logger.error(clz).log(insertRow).end();
                Database.getStatement().executeUpdate(insertRow);
            }
            
            return JSONUtils.success(""+collectionId);
            
        } catch (SQLException e) {
            Logger.error(clz).log("share ").log(e.getMessage()).end();
            return JSONUtils.error(0, "collections.share error", e);
        }
    }
 
    @GET
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    public String add(@Context HttpServletRequest req,  @QueryParam("id") long collectionId,
            @QueryParam("name") String name, @QueryParam("profiles") String profiles) {
        
        long userId = Session.getUserId(req.getSession());
        if (userId == Session.INVALID_USER_ID) {
            return JSONErrorMsgs.getAccessDenied();
        }
        
        List<Long> validProfiles;
        if (profiles.isEmpty()) {
            ProfileData data = UserProfilesTable.getFirstProfile(userId);
            validProfiles = new ArrayList<Long>(Arrays.asList(data.getProfileId()));
        } else {
            validProfiles = UserProfilesTable.validate(userId, profiles);
            if (validProfiles.isEmpty()) {
                return JSONUtils.error(0, "no valid profiles found.");
            }
        }
        
        long streamId = UserStreamGroupsTable.save(userId, name);
        if (streamId == 0) {
            return JSONUtils.error(0, "couldn't create stream group " + name);
        }
        
        int subsErrors = 0;
        int subsOk = 0;
        int profilesOk = 0;
        
        try {
            String query = CollectionsTable.getQueryCollectionEntriesByCollectionIds("" + collectionId);
            ResultSet rs = Database.getStatement().executeQuery(query);

            while (rs.next()) {

                String feedName = rs.getString(DBFields.STR_FEED_NAME);
                OPMLEntry entry = new OPMLEntry(feedName, rs.getString(DBFields.STR_XML_URL));
                long xmlId = rs.getLong(DBFields.LONG_XML_ID);

                long subsId = UserFeedSubscriptionsTable.save(userId, xmlId, entry);

                if (subsId == -1) {
                    subsErrors++;
                    Logger.error(clz).log("Error adding subscription " + xmlId + ", " + entry).end();
                    continue;
                }

                subsOk++;

                UserStreamGroupsTable.addSubscriptionToStream(streamId, subsId);

                for (Long id : validProfiles) {
                    if (UserProfilesTable.addStreamToProfile(streamId, id)) {
                        profilesOk++;
                    }
                }
            }
        } catch (SQLException e) {
            Logger.error(clz).log("add ").log(e.getMessage()).end();
            return JSONUtils.error(0, "collections add error", e);
        }
        
        if (subsOk > 0 || profilesOk > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            JSONUtils.append(sb, "streamId", streamId).append(",");
            JSONUtils.append(sb, "success", true).append(",");
            JSONUtils.append(sb, "collectionId", collectionId).append("}");
            return sb.toString();
        }
        
        return JSONUtils.error(0, "not implemented yet.");
        
    }
    
}
