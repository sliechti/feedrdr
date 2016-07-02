package feedreader.fetch.test;

import feedreader.config.FeedAppConfig;
import feedreader.entities.FeedSourceEntry;
import feedreader.entities.XmlChannelData;
import feedreader.log.Logger;
import feedreader.store.DBFields;
import feedreader.store.Database;
import feedreader.store.FeedSourceChannelDataTable;
import feedreader.store.FeedSourcesTable;
import feedreader.utils.test.LoadTestResourceFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.junit.Test;

public class FetchFavicoTest {

    static final Class<?> clz = FetchFavicoTest.class;

    static final String ICOFOLDER = "downloads/favico/";
    static long XMLID = 117;

    static {
        Database.start(new Properties());
    }

    @Test public void fetchFavico() throws MalformedURLException, IOException {
        // Logger.get().setDebugSql(true);

        FeedSourceEntry sourceEntry = FeedSourcesTable.getByField(DBFields.LONG_XML_ID, XMLID);

        if (sourceEntry.getId() == 0) {
            Logger.error(clz).log("No feed source with id ").log(XMLID).end();
            return;
        }

        XmlChannelData data = FeedSourceChannelDataTable.get(sourceEntry.getId());

        String httpLink = data.getLink();

        if (httpLink.isEmpty()) {
            Logger.error(clz).log("Feed has no main link. ").log(data).end();
        }

        HttpURLConnection conn = (HttpURLConnection) new URL(httpLink + "/favicon.ico").openConnection();

        conn.setConnectTimeout(FeedAppConfig.FETCH_CONNECTION_TIMEOUT);
        conn.setReadTimeout(FeedAppConfig.FETCH_READ_TIMEOUT);
        conn.addRequestProperty("User-Agent", FeedAppConfig.FETCH_USER_AGENT);

        if (conn.getResponseCode() != 200) {

            Logger.error(clz).log(" http error ").log(conn.getResponseCode()).end();
            return;
        }

        Logger.error(clz).log("content type: ").log(conn.getContentType()).end();
        Logger.error(clz).log("content len : ").log(conn.getContentLength()).end();
        Logger.error(clz).log("content len (long): ").log(conn.getContentLengthLong()).end();

        File ico = LoadTestResourceFile.asFile(ICOFOLDER + "/" + XMLID + "_favico.ico");

        FileOutputStream fos = new FileOutputStream(ico);

        InputStream io = conn.getInputStream();

        int c;
        while ((c = io.read()) != -1) {
            fos.write(c);
        }
        
        fos.close();
    }
    
}
