package feedreader.store.test;

import feedreader.store.FeedSourcesTable;

import org.junit.Ignore;
import org.junit.Test;

public class FeedSourcesTableTest {

    static final Class<?> clz = FeedSourcesTableTest.class;

    @Ignore
    public void testReturnId() {
        FeedSourcesTable.init();
        // long l = FeedSourcesTable.addNewSource("http://www.google.com", null, null);
        // Logger.debug(clz).log("Inserted id ").log(l).end();
    }

    @Test
    public void nothing() {
    }
}
