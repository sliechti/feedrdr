package feedreader.cron;

import feedreader.config.Environment;
import feedreader.config.FeedAppConfig;
import feedreader.entities.FeedSourceEntry;
import feedreader.entities.XmlChannelData;
import feedreader.entities.XmlChannelImage;
import feedreader.feed.utils.Fetch;
import feedreader.feed.utils.FetchHandler;
import feedreader.log.Logger;
import feedreader.parser.XmlFeedParser;
import feedreader.store.FeedSourceChannelDataTable;
import feedreader.store.FeedSourceChannelImageTable;
import feedreader.store.FeedSourcesTable;
import feedreader.store.XmlAttrTable;
import feedreader.time.CurrentTime;
import feedreader.utils.SimpleMail;
import java.util.Arrays;

public class CronFetchNews implements Runnable {

    static final Class<?> clz = CronFetchNews.class;
    static final String fetcherEmail = FeedAppConfig.MAIL_FETCHER_EMAIL;
    static final String fetcherToEmail = FeedAppConfig.MAIL_FETCHER_TO;

    static CronFetchNews instance = null;
    static CronFetchNews validationInstance = null;

    public static CronFetchNews fetchInstance(boolean validate) {
        if (validate) {
            if (validationInstance == null) {
                validationInstance = new CronFetchNews(true);
            }
            return validationInstance;
        } else {
            if (instance == null) {
                instance = new CronFetchNews(false);
            }
            return instance;
        }

    }

    enum Status {
        STARTING, NO_SOURCES_FOUND, PROCESSING, ERROR
    };

    FetchHandler handler = new FetchHandler();
    SimpleMail mail = new SimpleMail();

    // class logic
    static boolean forceDelete = false;
    boolean validationRun = false;

    // stats
    int validSources = 0;
    int code;
    int fetchedSources = 0;
    int saved = 0;
    int notChanged = 0;
    long entriesFound = 0;
    String totalCount;

    // feedback
    long entryId = 0;
    String lastUrlProcessed = "";

    long lastStatusSent = 0;

    // error
    String error = "";
    Status status = Status.STARTING;

    public CronFetchNews(boolean validationRun) {
        Logger.info(clz).log("starting: is validate run=").log(validationRun).log(" running every ")
                .log(FeedAppConfig.DELAY_FETCH_IN_S).log(" seconds.").end();
        this.validationRun = validationRun;
        forceDelete = FeedAppConfig.FETCH_FORCE_DELETE;
        lastStatusSent = CurrentTime.inGMT();
    }

    public void resetStats() {
        validSources = 0;
        fetchedSources = 0;
        saved = 0;
        notChanged = 0;
        entriesFound = 0;
        totalCount = "";
    }

    @Override public String toString() {
        return "fetch every: "
                + FeedAppConfig.DELAY_FETCH_IN_S
                + " seconds. \n"
                + "fetch run status: "
                + Fetch.stringCode(code)
                + "\n"
                + "is validating: "
                + validationRun
                + "\n"
                + "fetched sources: "
                + fetchedSources
                + "\n"
                + "valid sources: "
                + validSources
                + "\n"
                + "entries found: "
                + entriesFound
                + "\n"
                + "new articles found: "
                + saved
                + "\n"
                + "articles that didn't change: "
                + notChanged
                + "\n"
                + "last xml id fetched: "
                + entryId
                + "\n"
                + "last url fetched: "
                + lastUrlProcessed
                + "\n"
                + "error: "
                + error
                + "\n"
                + "status: "
                + status
                + "\n"
                + "totalCount: "
                + totalCount
                + "\n"
                + "next email: "
                + (((lastStatusSent + (FeedAppConfig.FETCH_SEND_STATUS_EVERY_MINUTES * 60 * 1000)) - CurrentTime
                        .inGMT()) / 1000 / 60) + " min. \n";
    }

    private boolean sendMail(String subject, String text) {
        try {
            mail.send(fetcherEmail, fetcherEmail, fetcherToEmail, fetcherToEmail, Environment.name() + "-" + subject,
                    text);
            return true;
        } catch (Exception ex) {
            Logger.error(clz).log("error sending email ").log(ex.getMessage()).end();
        }

        return false;
    }

    @Override public void run() {
        if (status == Status.ERROR) {
            Logger.error(clz).log("fetch in error state ").log(error).end();

            if (!error.isEmpty() && sendMail("FetchError", error)) {
                error = "";
                status = Status.PROCESSING;
            }

            return;
        }

        try {
            Fetch.run(handler);

            handler.setForceDelete(forceDelete);

            if (FeedAppConfig.XML_SAVE) {
                Fetch.setDownloadPath(FeedAppConfig.DOWNLOAD_XML_PATH);
            }

            if (validationRun) {
                code = Fetch.validationRun(handler);
            } else {
                code = Fetch.run(handler);
            }

            FeedSourceEntry entry = handler.getSourceEntry();

            switch (code) {
            case Fetch.RetCode.NO_SOURCES_FOUND:
                status = Status.NO_SOURCES_FOUND;
                break;

            case Fetch.RetCode.VALID:
                FeedSourcesTable.setValid(entry.getId());
                validSources++;

            case Fetch.RetCode.FINISHED:
                XmlFeedParser parser = handler.getParser();

                if (FeedAppConfig.XML_GATHER_INFO) {
                    XmlAttrTable.save(entry.getXmlUrl(), parser.nodeList);
                }

                entryId = entry.getId();
                lastUrlProcessed = entry.getXmlUrl();

                XmlChannelData channelData = parser.getChannelData();
                XmlChannelImage channelImg = parser.getChannelImage();

                FeedSourceChannelDataTable.save(entry.getId(), channelData, true);
                FeedSourceChannelImageTable.save(entry.getId(), channelImg, true);

                saved += handler.getSavedCount();
                notChanged += handler.getNotChanged();
                entriesFound += handler.getFound();
                totalCount = handler.getTotalCount();

                fetchedSources++;

                if (lastStatusSent + (FeedAppConfig.FETCH_SEND_STATUS_EVERY_MINUTES * 60 * 1000) < CurrentTime.inGMT()) {
                    Logger.info(clz).log("sending status.").end();
                    sendMail("status ", this.toString());
                    resetStats();
                    lastStatusSent = CurrentTime.inGMT();
                }

                FeedSourcesTable.clearErrors(entry.getId());
            }

            status = Status.PROCESSING;
        } catch (Exception e) {
            error = Arrays.toString(e.getStackTrace());
            status = Status.ERROR;
        }

    }

}
