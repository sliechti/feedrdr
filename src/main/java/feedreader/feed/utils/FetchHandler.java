package feedreader.feed.utils;

import feedreader.entities.FeedSourceEntry;
import feedreader.entities.XmlChannelData;
import feedreader.entities.XmlChannelImage;
import feedreader.log.Logger;
import feedreader.parser.XmlFeedEntry;
import feedreader.parser.XmlFeedParser;
import feedreader.store.FeedEntriesTable;
import feedreader.store.FeedSourcesTable;
import feedreader.time.CurrentTime;

public class FetchHandler implements Fetch.FetchCallback {

    static final Class<?> clz = FetchHandler.class;

    FeedSourceEntry sourceEntry = null;
    XmlFeedParser parser;

    int saved = 0;
    int notUpdated = 0;
    int notChanged = 0;

    int maxCount = -1; // Used for testing.
    int found = 0;

    long nextCheck = 0L;

    private boolean dryRun = false;

    private int valid = 0;

    private boolean forceDelete = false;

    String totalCount = "";

    public String getTotalCount() {
        return totalCount;
    }

    public void reset() {
        saved = 0;
        notChanged = 0;
        notUpdated = 0;
        found = 0;
        totalCount = "";
    }

    public void setForceDelete(boolean forceDelete) {
        this.forceDelete = forceDelete;
    }

    public int getSavedCount() {
        return saved;
    }

    public int getNotUpdated() {
        return notUpdated;
    }

    public int getNotChanged() {
        return notChanged;
    }

    public int getFound() {
        return found;
    }

    public int getValid() {
        return valid;
    }

    public FeedSourceEntry getSourceEntry() {
        return sourceEntry;
    }

    public XmlFeedParser getParser() {
        return parser;
    }

    @Override public void onFetchSourceEntryFound(FeedSourceEntry entry) {
        reset();
        this.sourceEntry = entry;
    }

    @Override public void onFetchEntryWait(long nextCheck) {
        this.nextCheck = nextCheck;
    }

    public long getNextCheck() {
        return nextCheck;
    }

    @Override public void onFetchParserCreated(XmlFeedParser parser) {
        this.parser = parser;
    }

    /*
     * TODO: Report common errors: - Date not parsed, - Date outside range - Empty strings.
     */
    @Override public void onXmlEntryFound(XmlFeedEntry news) {
        found++;

        if (maxCount > 0 && found > maxCount) {
            return;
        }

        XmlChannelData data = parser.getChannelData();
        @SuppressWarnings("unused") XmlChannelImage img = parser.getChannelImage();

        // 1. Try the article's publication date.
        long pubDate = news.getEntryDate();
        if (pubDate == -1) {
            Logger.notice(clz).log(news.getSourceUrl()).log(" has an article without publication date ")
                    .log(news.getLink()).end();

            pubDate = data.getPublicationDate(); // channel's publication date.

            // If the article doesn't have a publication date, try to fallback to the sources' pub. date.
            if (pubDate == -1) {
                Logger.notice(clz).log("source channel and item have no publication date, using GMT time ")
                        .log(news.getSourceUrl()).end();
                pubDate = CurrentTime.inGMT();
            }

            news.setEntryDate(pubDate);
        }

        // TODO: Another fallback for pubDate could be the HTTP modification date.
        if (pubDate == -1) {
            Logger.notice("Ignoring article").log(news.getLink()).log(" from source ").log(news.getSourceUrl()).end();
            return;
        }

        /*
         * This doesn't work, because a new item inside the XML source may have an older publication date since we last
         * checked, causing the article to be ignored.
         */
        /*
         * if (!force && pubDate < sourceLastChecked) { notChanged++; return; }
         */

        // Publication's date is in the future. This happens when date times in sources don't use a timezone.
        if (pubDate > CurrentTime.inGMT()) {
            pubDate = CurrentTime.inGMT();
            Logger.notice(clz).log("publication date appears to be in the future, moving to now in GMT. ")
                    .log(news.getSourceUrl()).end();
            // We also need to change the news date.
            news.setEntryDate(pubDate);
        }

        valid++;

        if (dryRun) {
            return;
        }

        int id = FeedEntriesTable.save(sourceEntry.getId(), news, forceDelete);
        switch (id) {
        case -1:
            Logger.error(clz).log("Error saving ").log(news).end();
            break;

        case 1:
            saved++;
            break;

        // Nothing happened.
        case 0:
            notUpdated++;
            break;

        default:
            Logger.error(clz).log("FeedEntries.save retVal unknown. ").end();
        }
    }

    @Override public void onEndDocument() {
        totalCount = FeedSourcesTable.updateCount(sourceEntry.getId());
    }
}
