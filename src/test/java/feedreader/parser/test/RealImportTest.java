package feedreader.parser.test;

import feedreader.parser.XmlFeedParser;
import feedreader.cron.CronTimeUtils;
import feedreader.entities.FeedSourceEntry;
import feedreader.feed.utils.Fetch;
import feedreader.feed.utils.Fetch.FetchException;
import feedreader.feed.utils.FetchHandler;
import feedreader.log.Logger;
import feedreader.store.DBFields;
import feedreader.store.Database;
import feedreader.store.FeedSourceChannelDataTable;
import feedreader.store.FeedSourceChannelImageTable;
import feedreader.store.FeedSourcesTable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.Properties;

import org.junit.Test;
import org.xml.sax.SAXException;

public class RealImportTest {

    /**
     * ------------> CHANGE XMLID!
     */
    public static long XMLID = 13867;
    public static boolean DEBUGSQL = true;

    static final Class<?> clz = RealImportTest.class;

    static {
        Properties p = new Properties();
        p.setProperty(Database.JDBC_DRIVER_PROP_KEY, "org.postgresql.Driver");
        p.setProperty(Database.USERNAME_PROP_KEY, "sliechti");
        p.setProperty(Database.PASSWORD_PROP_KEY, "sliechti");
        p.setProperty(Database.URL_PROP_KEY, "jdbc:postgresql://localhost/postgres");
        Database.start(p);
    }

    @Test public static void run() throws FileNotFoundException, IOException, SAXException, MalformedURLException,
            Fetch.FetchException {

        Logger.get().setLevel(Logger.LogLevels.DEBUG);
        Logger.get().setDebugSql(DEBUGSQL);

        FeedSourceEntry sourceEntry = FeedSourcesTable.getByField(DBFields.LONG_XML_ID, XMLID);

        Logger.debug(clz).log("running real import on").end();
        Logger.debug(clz).log(" -> id  : ").log(XMLID).end();
        Logger.debug(clz).log(" -> url : ").log(sourceEntry.getXmlUrl()).end();
        // FeedSourceEntry sourceEntry = FeedSourcesTable.getNextFetch(10);
        FetchHandler handler = new FetchHandler();
        handler.setForceDelete(true);
        XmlFeedParser parser = new XmlFeedParser(sourceEntry.getXmlUrl(), handler);

        Logger.notice(clz).log(sourceEntry).end();

        if (sourceEntry.getId() == 0)
            return;

        handler.onFetchSourceEntryFound(sourceEntry);
        handler.onFetchParserCreated(parser);
        parser.setGatherInfo(true);
        HttpURLConnection conn = Fetch.getHttpConnection(sourceEntry.getXmlUrl());
        parser.parse(conn.getInputStream());

        Logger.info(clz).log(parser.getChannelData()).end();
        Logger.info(clz).log(parser.getChannelImage()).end();

        FeedSourceChannelDataTable.save(sourceEntry.getId(), parser.getChannelData(), true);
        FeedSourceChannelImageTable.save(sourceEntry.getId(), parser.getChannelImage(), true);

        Logger.info(clz).log("Found       ").log(handler.getFound()).end();
        Logger.info(clz).log("Saved       ").log(handler.getSavedCount()).end();
        Logger.info(clz).log("Not changed ").log(handler.getNotChanged()).end();
        Logger.info(clz).log("Not updated ").log(handler.getNotUpdated()).end();
    }

    public static void main(String args[]) throws FileNotFoundException, MalformedURLException, IOException,
            SAXException, FetchException {
        
        new CronTimeUtils(); // so max history is built.
        run();
    }
}
