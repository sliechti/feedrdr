package feedreader.store;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import feedreader.config.Constants;
import feedreader.utils.ApplicationConfig;
import feedreader.utils.SQLUtils;

public class Database {

    private static final Logger logger = LoggerFactory.getLogger(Database.class);

    public static final String JDBC_DRIVER_PROP_KEY = "jdbc_driver";
    public static final String PASSWORD_PROP_KEY = "password";
    public static final String URL_PROP_KEY = "jdbc_url";
    public static final String USERNAME_PROP_KEY = "username";
    public static final String DEFAULT_KEYWORD = "DEFAULT";
    public static final String COUNT_KEYWORD = "count";
    public static final String IS_NULL_KEYWORD = "is null";
    private static HikariDataSource ds;

    public static ResultSet checkEntry(Connection conn, String table, String name, String val) throws SQLException {
        String query = String
                .format("SELECT %s FROM %s WHERE %s = '%s'", name, table, name, SQLUtils.asSafeString(val));
        return conn.createStatement().executeQuery(query);
    }

    public static void deleteEntry(Connection conn, String TABLE, String field, long val) throws SQLException {
        String query = String.format("DELETE FROM %s WHERE %s = %d", TABLE, field, val);
        conn.createStatement().execute(query);
    }

    public static void execute(Connection conn, String query) throws SQLException {
        conn.createStatement().executeUpdate(query);
    }

    public static int executeUpdate(Connection conn, String query) throws SQLException {
        return conn.createStatement().executeUpdate(query);
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public static ResultSet getEntry(Connection conn, String TABLE, String field, long val) throws SQLException {
        String query = String.format("SELECT * FROM %s WHERE %s = %d", TABLE, field, val);
        return conn.createStatement().executeQuery(query);
    }

    public static ResultSet getEntry(Connection conn, String TABLE, String field, String val) throws SQLException {
        String query = String.format("SELECT * FROM %s WHERE %s = '%s'", TABLE, field, SQLUtils.asSafeString(val));
        return conn.createStatement().executeQuery(query);
    }

    public static long hasRecord(String table, String fieldName, long fieldValue) {
        long ret = 0;
        try {
            String query = String.format("SELECT %s FROM %s WHERE %s = %d RETURNING %s", fieldName, table, fieldName,
                    fieldValue, fieldName);
            Connection conn = getConnection();
            ResultSet rs = conn.createStatement().executeQuery(query);

            if (rs.next()) {
                ret = rs.getLong(fieldName);
            }
            conn.close();
        } catch (SQLException e) {
            logger.error("has record failed: {}", e, e.getMessage());
        }

        return ret;
    }

    public static ResultSet rawQuery(Connection conn, String sql) throws SQLException {
        return conn.createStatement().executeQuery(sql);
    }

    public static void start() {
        ApplicationConfig appConfig = ApplicationConfig.instance();

        String jdbcDriver = appConfig.getString(JDBC_DRIVER_PROP_KEY);
        try {
            Class.forName(jdbcDriver);
        } catch (ClassNotFoundException e) {
            logger.error("failed to load jdbc driver: {}", e, jdbcDriver, e.getMessage());
        }

        // loadedJdbcDriver = appConfig.getString(JDBC_DRIVER_PROP_KEY);
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(appConfig.getString(URL_PROP_KEY));
        config.setUsername(appConfig.getString(USERNAME_PROP_KEY));
        config.setPassword(appConfig.getString(PASSWORD_PROP_KEY));
        config.setMaximumPoolSize(20);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        ds = new HikariDataSource(config);

        logger.info("loaded hikari cp: {}", config);
        initTables();
    }

    public static void stop() {
        if (!ds.isClosed()) {
            UsersTable.close();
            UserProfilesTable.close();
            UserKeyValuesTable.close();
            FeedSourcesTable.close();
            FeedSourceChannelDataTable.close();
            FeedSourceChannelImageTable.close();
            FeedEntriesTable.close();
            UserStreamGroupsTable.close();
            UserFeedSubscriptionsTable.close();
            UserFeedEntries.close();
            XmlAttrTable.close();
            JoinedTable.close();
            CollectionsTable.close();
            ds.close();
        }
    }

    static void setBoolean(String table, String boolSetName, boolean boolSetVal, String longWhereName,
            long longWhereVal)
            throws SQLException {
        String query = String.format("UPDATE %s SET %s = %b WHERE %s = %d", table, boolSetName, boolSetVal,
                longWhereName, longWhereVal);
        Connection conn = ds.getConnection();
        Database.execute(conn, query);
        conn.close();
    }

    private static void initTables() {
        UsersTable.init();
        UserProfilesTable.init();
        FeedSourcesTable.init();
        FeedSourceChannelDataTable.init();
        FeedSourceChannelImageTable.init();
        FeedEntriesTable.init();
        UserStreamGroupsTable.init();
        UserFeedSubscriptionsTable.init();
        UserFeedEntries.init();
        XmlAttrTable.init();
        JoinedTable.init();
        UserKeyValuesTable.init();
        CollectionsTable.init();
    }

    public static class Functions {

        public static final String UPDATE_SOURCE_COUNT = Constants.FEED_APP_SCHEMA + ".updatesourcecount";

    }
}
