package feedreader.cron;

import java.util.Arrays;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feedreader.config.Environment;
import feedreader.config.FeedAppConfig;
import feedreader.entities.FeedSourceEntry;
import feedreader.entities.XmlChannelData;
import feedreader.entities.XmlChannelImage;
import feedreader.feed.utils.Fetch;
import feedreader.feed.utils.FetchHandler;
import feedreader.main.AppContextInit;
import feedreader.parser.XmlFeedParser;
import feedreader.store.FeedSourceChannelDataTable;
import feedreader.store.FeedSourceChannelImageTable;
import feedreader.store.FeedSourcesTable;
import feedreader.store.XmlAttrTable;
import feedreader.time.CurrentTime;
import feedreader.utils.SimpleMail;

/**
 * Class responsible for fetching news. Instantiated on {@link AppContextInit}.
 */
public class CronFetchNews implements Runnable {

    private static final String fetcherEmail = FeedAppConfig.MAIL_FETCHER_EMAIL;
    private static final String fetcherToEmail = FeedAppConfig.MAIL_FETCHER_TO;
    private static boolean forceDelete = false;

    private static CronFetchNews instance = null;
    private static final Logger logger = LoggerFactory.getLogger(CronFetchNews.class);

    private static CronFetchNews validationInstance = null;

    private int code;

    private String error = "";

    private FetchHandler handler = new FetchHandler();
    private long lastStatusSent = 0;

    private SimpleMail mail = new SimpleMail();
    private final Stats stats = new Stats();
    private Status status = Status.STARTING;
    private boolean validationRun = false;

    public CronFetchNews(boolean validationRun) {
        logger.info("starting cron fetch news. running in validation mode {}, running every: {} seconds",
                validationRun, FeedAppConfig.DELAY_FETCH_IN_S);
        this.validationRun = validationRun;
        forceDelete = FeedAppConfig.FETCH_FORCE_DELETE;
        lastStatusSent = CurrentTime.inGMT();

        if (FeedAppConfig.DOWNLOAD_XML_FILES) {
            Fetch.setDownloadPath(FeedAppConfig.DOWNLOAD_XML_PATH);
        }
    }

    @Override
    public void run() {
        if (status == Status.ERROR) {
            logger.error("cron in error state: {}", error);

            if (!error.isEmpty() && sendMail("FetchError", error)) {
                error = "";
                status = Status.PROCESSING;
            }
            return;
        }

        try {
            Fetch.run(handler);
            handler.setForceDelete(forceDelete);

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
                    stats.validSources++;

                case Fetch.RetCode.FINISHED:
                    XmlFeedParser parser = handler.getParser();

                    if (FeedAppConfig.XML_GATHER_INFO) {
                        XmlAttrTable.save(entry.getXmlUrl(), parser.nodeList);
                    }

                    stats.entryId = entry.getId();
                    stats.lastUrlProcessed = entry.getXmlUrl();

                    XmlChannelData channelData = parser.getChannelData();
                    XmlChannelImage channelImg = parser.getChannelImage();

                    FeedSourceChannelDataTable.save(entry.getId(), channelData, true);
                    FeedSourceChannelImageTable.save(entry.getId(), channelImg, true);

                    stats.saved += handler.getSavedCount();
                    stats.notChanged += handler.getNotChanged();
                    stats.entriesFound += handler.getFound();
                    stats.totalCount = handler.getTotalCount();

                    stats.fetchedSources++;

                    if (lastStatusSent + (FeedAppConfig.FETCH_SEND_STATUS_EVERY_MINUTES * 60 * 1000) < CurrentTime
                            .inGMT()) {
                        logger.info("sending fetch status");
                        sendMail("status ", toString());
                        stats.resetStats();
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    private boolean sendMail(String subject, String text) {
        try {
            mail.send(fetcherEmail, fetcherEmail, fetcherToEmail, fetcherToEmail, Environment.name() + "-" + subject,
                    text);
            return true;
        } catch (Exception e) {
            logger.error("sending email due to fetch error: {}", e, e.getMessage());
        }
        return false;
    }

    /*
     * Fetch Instance
     */
    // TODO SLU: Very wrong.
    public static CronFetchNews fetchInstance(boolean validate) {
        if (validate) {
            if (validationInstance == null) {
                validationInstance = new CronFetchNews(true);
            }
            return validationInstance;
        }
        if (instance == null) {
            instance = new CronFetchNews(false);
        }
        return instance;
    }

    public enum Status {
        ERROR, NO_SOURCES_FOUND, PROCESSING, STARTING
    }

    @SuppressWarnings("unused") // reflection
    private static class Stats {

        private long entriesFound;
        private long entryId;
        private int fetchedSources;
        private String lastUrlProcessed;
        private int notChanged;
        private int saved;
        private String totalCount;
        private int validSources;

        public Stats() {
            resetStats();
        }

        final public void resetStats() {
            entriesFound = 0;
            entryId = 0;
            fetchedSources = 0;
            lastUrlProcessed = "";
            notChanged = 0;
            saved = 0;
            totalCount = "";
            validSources = 0;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }

}
