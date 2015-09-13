package feedreader.store;

import feedreader.config.Constants;
import feedreader.log.Logger;
import feedreader.utils.SQLUtils;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class Database {

    public static final String JDBC_DRIVER_PROP_KEY = "jdbc_driver";
    public static final String USERNAME_PROP_KEY = "username";
    public static final String PASSWORD_PROP_KEY = "password";
    public static final String URL_PROP_KEY = "jdbc_url";

    public static final String DEFAULT_KEYWORD = "DEFAULT";
    public static final String COUNT_KEYWORD = "count";
    public static final String IS_NULL_KEYWORD = "is null";
    static Connection conn;
    static Class<?> clz = Database.class;
    static Statement stmt;
    private static String loadedJdbcDriver = null;

    public static class Functions {

        public static final String UPDATE_SOURCE_COUNT = Constants.FEED_APP_SCHEMA + ".updatesourcecount";

    }

    public static Connection getConnection() {
        return conn;
    }

    public static Statement getStatement() {
        try {
            return conn.createStatement();
        } catch (SQLException ex) {
            Logger.error(clz).log("getStatement error, ").log(ex.getMessage()).end();
            return null;
        }
    }

    public static void start(Properties properties) {
        try {
            Logger.info(clz).log("start database with ").log(properties.toString()).end();
            loadedJdbcDriver = properties.getProperty(JDBC_DRIVER_PROP_KEY);
            Class.forName(loadedJdbcDriver);
            conn = DriverManager.getConnection(properties.getProperty(URL_PROP_KEY),
                    properties.getProperty(USERNAME_PROP_KEY), properties.getProperty(PASSWORD_PROP_KEY));

            stmt = conn.createStatement();
            Logger.info(clz).log("using driver ").log(loadedJdbcDriver).end();
            initTables();
        } catch (ClassNotFoundException ex) {
            Logger.error(clz).log("error loading database driver. ").log(properties.getProperty(JDBC_DRIVER_PROP_KEY))
                    .log(", error ").log(ex.getMessage()).end();
            throw new RuntimeException(ex.getMessage());
        } catch (SQLException ex) {
            Logger.error(clz).log("sql error ").log(ex.getMessage()).end();
        }
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

    public static ResultSet checkEntry(String table, String name, String val) throws SQLException {
        String query = String
                .format("SELECT %s FROM %s WHERE %s = '%s'", name, table, name, SQLUtils.asSafeString(val));
        Logger.debugSQL(clz).log("checkEntry ").log(query).end();
        return stmt.executeQuery(query);
    }

    public static void stop() {
        try {
            if (loadedJdbcDriver != null) {
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
                
                Driver driver = DriverManager.getDriver(loadedJdbcDriver);
                Logger.info(clz).log("closing driver ").log(driver.toString()).end();
                DriverManager.deregisterDriver(driver);
            }
        } catch (SQLException ex) {
            Logger.error(clz).log("deregistering driver ").log(ex.getMessage()).end();
        }
    }

    public static ResultSet rawQuery(String sql) throws SQLException {
        return stmt.executeQuery(sql);
    }

    public static long hasRecord(String table, String fieldName, long fieldValue) {
        try {
            String query = String.format("SELECT %s FROM %s WHERE %s = %d RETURNING %s", fieldName, table, fieldName,
                    fieldValue, fieldName);
            Logger.debugSQL(clz).log("hasRecord ").log(query).end();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                return rs.getLong(fieldName);
            }
        } catch (SQLException ex) {
            Logger.error(clz).log("hasRecord error ").log(ex.getMessage()).end();
        }

        return 0;
    }

    public static ResultSet getEntry(String TABLE, String field, long val) throws SQLException {
        String query = String.format("SELECT * FROM %s WHERE %s = %d", TABLE, field, val);
        Logger.debugSQL(clz).log("getEntry ").log(query).end();
        return stmt.executeQuery(query);
    }

    public static ResultSet getEntry(String TABLE, String field, String val) throws SQLException {
        String query = String.format("SELECT * FROM %s WHERE %s = '%s'", TABLE, field, SQLUtils.asSafeString(val));
        Logger.debugSQL(clz).log("getEntry ").log(query).end();
        return stmt.executeQuery(query);
    }

    public static void deleteEntry(String TABLE, String field, long val) throws SQLException {
        String query = String.format("DELETE FROM %s WHERE %s = %d", TABLE, field, val);
        Logger.debugSQL(clz).log("deleteEntry ").log(query).end();
        stmt.execute(query);
    }

    public static void execute(String query) throws SQLException {
        stmt.executeUpdate(query);
    }

    public static int executeUpdate(String query) throws SQLException {
        return stmt.executeUpdate(query);
    }

    static void
            setBoolean(String table, String boolSetName, boolean boolSetVal, String longWhereName, long longWhereVal)
                    throws SQLException {
        String query = String.format("UPDATE %s SET %s = %b WHERE %s = %d", table, boolSetName, boolSetVal,
                longWhereName, longWhereVal);
        Database.execute(query);
    }
}
