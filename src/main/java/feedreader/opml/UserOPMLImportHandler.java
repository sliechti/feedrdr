package feedreader.opml;

import feedreader.entities.FeedSourceEntry;
import feedreader.entities.OPMLEntry;
import feedreader.log.Logger;
import feedreader.store.DBFields;
import feedreader.store.FeedSourcesTable;
import feedreader.store.UserFeedSubscriptionsTable;
import feedreader.store.UserProfilesTable;
import feedreader.store.UserStreamGroupsTable;

import java.util.ArrayList;

public class UserOPMLImportHandler implements OPMLParser.Callback {

    long userId;
    long streamId; // Set to current stream id.

    int publishWithDate;

    // stats
    int subsOk = 0;
    int subsErrors = 0;

    int sourceQueued = 0;
    int sourceError = 0;
    int sourceKnown = 0;

    int streamsOk = 0;
    int streamsErrors = 0;

    /** Local array to hold the tree path. We also use the array's hash to identify the directory path. */
    ArrayList<String> dirTree = new ArrayList<String>();

    // option(s)
    ArrayList<Long> profiles = new ArrayList<>();
    ArrayList<Long> streams = new ArrayList<>();

    /*
     * if the options grow too much, separate classes might be needed, setSubscriptionOptions, setFolderOptions
     */
    public UserOPMLImportHandler(long userId) {
        this.userId = userId;
        // this.publishWithDate = publishWithDate;
    }

    public int getSourceKnown() {
        return sourceKnown;
    }

    public int getSourceError() {
        return sourceError;
    }

    public int getSourceQueued() {
        return sourceQueued;
    }

    public int getSubsOk() {
        return subsOk;
    }

    public int getSubsErrors() {
        return subsErrors;
    }

    public int getStreamsOk() {
        return streamsOk;
    }

    public int getStreamsErrors() {
        return streamsErrors;
    }

    public void addOnlyToProfile(ArrayList<String> ids) {
        for (String s : ids) {
            try {
                long l = Long.parseLong(s);
                addOnlyToProfile(l);
            } catch (NumberFormatException ex) {
                Logger.error(this.getClass()).log("addOnlyToProfile ignored ").log(s).log(", error ")
                        .log(ex.getMessage()).end();
            }
        }
    }

    public void addOnlyToProfile(long id) {
        if (!profiles.contains(id))
            profiles.add(id);
    }

    @Override
    public void onDirectoryEnd() {
        dirTree.remove(dirTree.size() - 1);
    }

    @Override
    public void onDirectoryStart(String dirName) {
        dirTree.add(dirName);

        String streamName = "";

        if (dirTree.size() > 1) {
            for (String s : dirTree) {
                streamName = streamName.concat(s + "_");
            }
            streamName = streamName.substring(0, streamName.length() - 1);
        } else {
            streamName = dirName;
        }

        streamId = UserStreamGroupsTable.save(userId, streamName);
        if (streamId == -1) {
            streamsErrors++;
            Logger.error("Error adding new stream group ").log(streamName).log(" for user ").log(userId).end();
            return;
        }

        streamsOk++;

        if (!streams.contains(streamId))
            streams.add(streamId);
    }

    @Override
    public void onEntry(OPMLEntry entry) {
        FeedSourcesTable.RetCodes retCode = FeedSourcesTable.addNewSource(entry.getXmlUrl());
        switch (retCode) {
        case QUEUED:
            sourceQueued++;
            break;

        case ERROR:
            sourceError++;
            break;

        case IN_QUEUE:
            sourceKnown++;
            break;

        default:
            Logger.error(this.getClass()).log("Unhandled ret code: ").log(retCode).end();
        }

        // TODO: Performance. The source id can be fetch in a subquery with the insert in subs.save.
        FeedSourceEntry sourceEntry = FeedSourcesTable.getByField(DBFields.STR_XML_URL, entry.getXmlUrl());
        long subsId = UserFeedSubscriptionsTable.save(userId, sourceEntry.getId(), entry);

        if (subsId == -1) {
            subsErrors++;
            Logger.error(this.getClass()).log("Error adding subscription.").end();
            return;
        }

        subsOk++;

        UserStreamGroupsTable.addSubscriptionToStream(streamId, subsId);

        Logger.debug(this.getClass()).log("Adding subscription ").log(subsId).log(" to streamgroupid ")
                .log(profiles.size()).end();

    }

    @Override
    public void onBodyStart() {
    }

    @Override
    public void onBodyEnd() {
        Logger.debug(this.getClass()).log("Body end. ").log(streams.size()).end();

        for (Long sId : streams) {
            if (profiles.isEmpty()) {
                UserProfilesTable.addStreamToAllProfiles(userId, sId);
            } else {
                // TODO: Check for userId.= otherwise it would be possible for someone else to add to other peoples
                // profiles.
                for (Long l : profiles) {
                    UserProfilesTable.addStreamToProfile(sId, l);
                }
            }
        }
    }

}
