package feedreader.store;

import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feedreader.config.Constants;
import feedreader.utils.SQLUtils;

public final class HttpErrorsTable {

    public static final String TABLE = Constants.HTTP_ERRORS_TABLE;
    private static final Logger logger = LoggerFactory.getLogger(HttpErrorsTable.class);

    public static boolean init() {
        logger.info("init");
        return true;
    }

    public static void close() {
        logger.info("close");
    }

    public static int addError(String httpCode, String error) {
        try (Connection conn = Database.getConnection()) {
            String query = String.format("INSERT INTO %s (%s, %s) VALUES (%s, %s)",
                    TABLE, DBFields.STR_HTTP_CODE, DBFields.STR_HTTP_ERROR,
                    httpCode, SQLUtils.asSafeString(error));
            return conn.createStatement().executeUpdate(query);
        } catch (Exception e) {
            logger.error("add error failed: {}", e, e.getMessage());
            return -1;
        }
    }

}
