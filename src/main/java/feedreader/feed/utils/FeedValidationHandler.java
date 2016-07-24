package feedreader.feed.utils;

import feedreader.entities.FeedSourceEntry;
import feedreader.parser.XmlFeedEntry;
import feedreader.parser.XmlFeedParser;

public class FeedValidationHandler implements Fetch.FetchCallback {
    XmlFeedParser parser;
    int entries = 0;

    public XmlFeedParser getParser() {
        return parser;
    }

    public int getEntries() {
        return entries;
    }

    @Override
    public void onFetchParserCreated(XmlFeedParser p) {
        this.parser = p;
    }

    @Override
    public void onXmlEntryFound(XmlFeedEntry news) {
        entries++;
    }

    @Override
    public void onFetchSourceEntryFound(FeedSourceEntry entry) {
        // noop
    }

    @Override
    public void onFetchEntryWait(long nextCheck) {
        // noop
    }

    @Override
    public void onEndDocument() {
        // noop
    }
}
