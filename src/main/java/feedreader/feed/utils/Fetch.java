package feedreader.feed.utils;

import feedreader.config.FeedAppConfig;
import feedreader.entities.FeedSourceEntry;
import feedreader.log.Logger;
import feedreader.parser.XmlFeedEntry;
import feedreader.parser.XmlFeedParser;
import feedreader.store.DBFields;
import feedreader.store.FeedSourcesTable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;

import org.xml.sax.SAXException;

/**
 * This class should be split in fetch functionality and functionality used for run fetching jobs, like run.
 * 
 * Fetch should do only that. Fetch stuff. The run part could be added to a different class like FetchRunner.
 * 
 */
public class Fetch {

    /** Set to timestamp in millis, when returned code is {@link RetCode#CHECKING_NEXT_SOURCE_IN} */
    public static volatile long nextCheckAt = 0;

    /**
     * How long should the feedSourceEntry be blocked when fetched by
     * {@link co.fusr.apps.feeds.store.FeedSourcesFetchInfo#getNext(int)}
     */
    public static int blockForMs = 60 * 1000;

    static final Class<?> clz = Fetch.class;

    static String downloadPath = null;

    /**
     * Possible return codes from the functions: <br>
     * {@link #validate(ValidateCallback, String)} and <br>
     * {@link #run(FetchCallback, String, boolean)}
     */
    public class RetCode {

        public static final int VALID = 4;
        public static final int NEXT_CHECK_IN = 3;
        public static final int CHECKING_NEXT_SOURCE_IN = 2;
        public static final int NO_SOURCES_FOUND = 1;
        public static final int FINISHED = 0;
        public static final int SAX_EXCEPTION = -1;
        public static final int CONNECTION_TIMEOUT = -2;
        public static final int URL_NOT_FOUND = -3;
        public static final int MALFORMED_URL = -4;
        public static final int UNKNOWN_HOST = -5;
        // We should add more return codes, for each HTTP codes.
        public static final int IO_EXCEPTION = -6;
        public static final int INVALID = -7;
        public static final int FETCH_EXCEPTION = -8;
    };

    public static class FetchException extends Exception {
        private static final long serialVersionUID = 1L;
        private int code;

        public FetchException(String message, int code) {
            super(message);
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    public interface ValidateCallback extends XmlFeedParser.XmlFeedParserCallback {

        public void onFetchParserCreated(XmlFeedParser parser);
    }

    public interface FetchCallback extends ValidateCallback {

        /**
         * Will be called when the Fetch class finds a {@link FeedSourceEntry}
         *
         * @param entry
         */
        public void onFetchSourceEntryFound(FeedSourceEntry entry);

        /**
         * Will be called when a {@link FeedSourceEntry} was found, but the fetcher still needs to wait until nextCheck.
         *
         * @param nextCheck
         */
        public void onFetchEntryWait(long nextCheck);
    }

    public static String stringCode(int retCode) {
        for (Field f : RetCode.class.getFields()) {

            try {
                if (f.getInt(f) == retCode)
                    return f.getName();
            } catch (IllegalAccessException e) {
                return "ERROR";
            }
        }

        return "UNKNOWN";
    }

    private static void updateCheckedTime(FeedSourceEntry entry) {
        FeedSourcesTable.updateCheckedTime(entry);
    }

    public static HttpURLConnection getHttpConnection(String xmlUrl) throws MalformedURLException, IOException,
            FetchException {
        HttpURLConnection conn = (HttpURLConnection) new URL(xmlUrl).openConnection();

        conn.setConnectTimeout(FeedAppConfig.FETCH_CONNECTION_TIMEOUT);
        conn.setReadTimeout(FeedAppConfig.FETCH_READ_TIMEOUT);
        conn.addRequestProperty("User-Agent", FeedAppConfig.FETCH_USER_AGENT);

        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new FetchException("HTTP status code not OK.", conn.getResponseCode());
        }

        return conn;
    }

    public static String validFeed(String subsUrl) {
        XmlFeedParser parser = new XmlFeedParser(subsUrl, new XmlFeedParser.XmlFeedParserCallback() {

            @Override
            public void onEndDocument() {
            }

            @Override
            public void onXmlEntryFound(XmlFeedEntry news) {
            }
        });

        try {
            InputStream io = getHttpConnection(subsUrl).getInputStream();
            parser.parse(io);
        } catch (Exception e) {
            return e.getMessage();
        }

        return "";
    }

    private static boolean processUrl(ValidateCallback callback, FeedSourceEntry entry) throws IOException,
            MalformedURLException, FileNotFoundException, UnknownHostException, SAXException, FetchException {

        XmlFeedParser parser = new XmlFeedParser(entry.getXmlUrl(), callback);
        callback.onFetchParserCreated(parser);
        parser.setGatherInfo(FeedAppConfig.XML_GATHER_INFO);

        InputStream io = getHttpConnection(entry.getXmlUrl()).getInputStream();

        if (downloadPath != null) {
            String saveTo = downloadPath + "/" + Long.toString(entry.getId()) + ".xml";
            File f = new File(saveTo);
            FileOutputStream fos = new FileOutputStream(f);

            int c;
            while ((c = io.read()) != -1) {
                fos.write(c);
            }

            fos.close();

            FileInputStream fis = new FileInputStream(f);

            try {
                parser.parse(fis);
            } catch (Exception e) {
                f.renameTo(new File(f.toString() + ".ex"));
                throw e;
            }
        } else {
            parser.parse(io);
        }

        return true;
    }

    public static void setDownloadPath(String p) {
        if (!p.isEmpty()) {
            downloadPath = p;
        }
    }

    private static int setError(long id, int retCode) {
        return setError(id, retCode, retCode);
    }

    private static int setError(long id, int code, int retCode) {
        int ret = FeedSourcesTable.setError(id, code, Fetch.stringCode(retCode));
        if (ret >= FeedAppConfig.FETCH_RETRY_AMOUNT) {
            Logger.notice(clz).log("disabling source id ").log(id).end();
            FeedSourcesTable.disable(id);
        }
        return ret;
    }

    public static int validationRun(FetchCallback callback) {
        FeedSourceEntry entry = FeedSourcesTable.getNextInvalid(blockForMs);
        if (entry.getId() == 0) {
            return RetCode.NO_SOURCES_FOUND;
        }

        int code = run(callback, entry, false);
        if (code == RetCode.FINISHED)
            return RetCode.VALID;

        return code;
    }

    public static int run(FetchCallback callback, long xmlId, boolean force) {
        FeedSourceEntry entry = FeedSourcesTable.getByField(DBFields.LONG_XML_ID, xmlId);
        if (entry.getId() == 0)
            return RetCode.NO_SOURCES_FOUND;

        return run(callback, entry, force);
    }

    public static int run(FetchCallback callback, String xmlUrl, boolean force) {
        FeedSourceEntry entry = FeedSourcesTable.getByField(DBFields.STR_XML_URL, xmlUrl);
        if (entry.getId() == 0)
            return RetCode.NO_SOURCES_FOUND;

        return run(callback, entry, force);
    }

    public static int run(FetchCallback callback) {
        FeedSourceEntry entry = FeedSourcesTable.getNextFetch(blockForMs);
        return run(callback, entry, false);
    }

    public static int run(FetchCallback callback, FeedSourceEntry entry, boolean force) {
        Logger.debug(clz).log("run, force ").log(force).log(" ").log(entry).end();

        callback.onFetchSourceEntryFound(entry);

        if (entry.getXmlUrl().isEmpty()) {
            return RetCode.NO_SOURCES_FOUND;
        }

        updateCheckedTime(entry);

        try {
            processUrl(callback, entry);
        } catch (SocketTimeoutException e) {
            Logger.error(Fetch.class).log(entry.getXmlUrl()).log(" SocketTimeoutException ").log(e.getMessage()).end();
            setError(entry.getId(), RetCode.CONNECTION_TIMEOUT);
            return RetCode.CONNECTION_TIMEOUT;
        } catch (UnknownHostException e) {
            Logger.error(Fetch.class).log(entry.getXmlUrl()).log(" UnknownHostException ").log(e.getMessage()).end();
            setError(entry.getId(), RetCode.UNKNOWN_HOST);
            return RetCode.UNKNOWN_HOST;
        } catch (FileNotFoundException e) {
            Logger.error(Fetch.class).log(entry.getXmlUrl()).log(" FileNotFoundException ").log(e.getMessage()).end();
            setError(entry.getId(), RetCode.URL_NOT_FOUND);
            return RetCode.URL_NOT_FOUND;
        } catch (MalformedURLException e) {
            Logger.error(Fetch.class).log(entry.getXmlUrl()).log(" MalformedURLException ").log(e.getMessage()).end();
            setError(entry.getId(), RetCode.MALFORMED_URL);
            return RetCode.MALFORMED_URL;
        } catch (IOException e) {
            Logger.error(Fetch.class).log(entry.getXmlUrl()).log(" IOException ").log(e.getMessage()).end();
            setError(entry.getId(), RetCode.CONNECTION_TIMEOUT);
            return RetCode.CONNECTION_TIMEOUT;
        } catch (SAXException e) {
            // TODO: Save document to database.
            Logger.error(Fetch.class).log(entry.getXmlUrl()).log(" SAXException ").log(e.getMessage()).end();
            setError(entry.getId(), RetCode.SAX_EXCEPTION);
            return RetCode.SAX_EXCEPTION;
        } catch (FetchException e) {
            // This shouldn't happen. Unless the source url changed to a redirect or is not found. 301, 404, etc.
            Logger.error(Fetch.class).log(entry.getXmlUrl()).log(" FetchException ").log(e.getCode()).log(" ")
                    .log(e.getMessage()).end();
            setError(entry.getId(), e.getCode(), RetCode.FETCH_EXCEPTION);
            return RetCode.FETCH_EXCEPTION;
        }

        return RetCode.FINISHED;
    }

}
