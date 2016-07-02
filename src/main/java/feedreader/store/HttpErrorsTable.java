package feedreader.store;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import feedreader.config.Constants;
import feedreader.log.Logger;
import feedreader.utils.SQLUtils;


public final class HttpErrorsTable {

    public static final String TABLE = Constants.HTTP_ERRORS_TABLE;

    static Class<?> clz = HttpErrorsTable.class; // Easier for logging.

    static Connection conn;
    static Statement stmt;

    public static boolean init() {
        conn = Database.getConnection();
        stmt = Database.getStatement();
        Logger.info(clz).log("initialized.").end();
        return true;
    }

    public static void close() {
        Logger.info(clz).log("close()").end();
        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.error(clz).log("closing sql objects ").log(ex.getMessage()).end();
        }
    }
    
    public static int addError(String httpCode, String error) {
        try {
            String query = String.format("INSERT INTO %s (%s, %s) VALUES (%s, %s)", 
                    TABLE, DBFields.STR_HTTP_CODE, DBFields.STR_HTTP_ERROR,
                    httpCode, SQLUtils.asSafeString(error));
            return stmt.executeUpdate(query);
        } catch (Exception e) {
            Logger.error(clz).log("addError ").log(e.getMessage()).end();
            return -1;
        }
    }
    
}
