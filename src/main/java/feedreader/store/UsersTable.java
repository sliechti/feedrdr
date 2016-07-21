package feedreader.store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feedreader.config.Constants;
import feedreader.entities.UserData;
import feedreader.oauth.OAuthType;
import feedreader.time.CurrentTime;
import feedreader.utils.PwdUtils;
import feedreader.utils.SQLUtils;
import feedreader.utils.SimpleEncryption;

public class UsersTable {

    public static final String TABLE = Constants.USERS_TABLE;
    public static final String TABLE_TOKENS = Constants.USER_AUTH_TOKENS;
    private static Connection conn;
    private static final String ENCKEY = "REGISTRATION";
    private static final Logger log = LoggerFactory.getLogger(UsersTable.class);
    private static Statement stmt;
    private static PreparedStatement stmtSelUsers;

    public static void close() {
        log.info("close()");
        try {
            stmtSelUsers.close();
            conn.close();
        } catch (SQLException ex) {
            log.error("closing sql objects {}", ex.getMessage());
        }
    }

    /**
     * User created with other services oauth. The email is mandatory, as
     * password we generate something long.
     *
     * @param email
     * @param oauth
     * @param authToken
     * @param password
     * leave empty if oauth is different than none.
     * @param locale
     * @param screenName
     * @return
     */
    public static UserData createNewUser(String email, OAuthType oauth, String password, String locale,
            String screenName) {
        email = email.toLowerCase();

        try {
            ResultSet rs = Database.checkEntry(TABLE, DBFields.STR_EMAIL, email);
            if (rs.next()) {
                log.info("email already known: {}", email);
                return UserData.NULL;
            }

            boolean generated = false;
            if (password == null || password.isEmpty()) {
                password = PwdUtils.generate();
                generated = true;
            }

            String code = getRegistrationCode(email, password);
            String query = String.format(
                    "INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s) "
                            + " VALUES ('%s', '%s', '%s', %d, '%s', %d, '%s', %s)",
                    TABLE, DBFields.STR_EMAIL, DBFields.STR_PASSWORD, DBFields.STR_LOCALE, DBFields.ENUM_MAIN_OAUTH,
                    DBFields.STR_SCREEN_NAME, DBFields.TIME_SUBSCRIBED_AT, DBFields.STR_REG_CODE,
                    DBFields.BOOL_GENERATED, SQLUtils.asSafeString(email), password, SQLUtils.asSafeString(locale),
                    oauth.getVal(), SQLUtils.asSafeString(screenName), CurrentTime.inGMT(), SQLUtils.asSafeString(code),
                    generated);
            log.debug("createNewUser SQL: {}", query);
            if (stmt.executeUpdate(query) > 0) {
                return get(email);
            }
        } catch (SQLException ex) {
            log.error("create new user error: {}", ex, ex.getMessage());
        }

        return UserData.NULL;
    }

    public static UserData createNewUser(String email, String screenName, String password, String locale) {
        return createNewUser(email, OAuthType.NONE, password, locale, screenName);
    }

    public static int displayName(long userId, String displayName) {
        try {
            String query = String.format("UPDATE %s SET %s = '%s' WHERE %s = %d", TABLE, DBFields.STR_SCREEN_NAME,
                    SQLUtils.asSafeString(displayName), DBFields.LONG_USER_ID, userId);
            log.debug(query);
            return stmt.executeUpdate(query);
        } catch (Exception e) {
            log.error("displayName {}, error {}", displayName, e);
        }

        return -1;
    }

    public static UserData fromCookie(String cookieKey) {
        return fromStringField(DBFields.STR_COOKIE, cookieKey);
    }

    public static UserData get(long userId) {
        try {
            String query = "SELECT * FROM feedreader.users as t0 " + "LEFT JOIN feedreader.userprofiles as t1 "
                    + "ON t0.l_user_id = t1.l_user_id WHERE t0.l_user_id = " + userId;
            ResultSet rs = Database.rawQuery(query);
            log.debug(query);
            return UserData.fromRs(rs);
        } catch (SQLException ex) {
            log.error("get userId  {}, error {}", "", ex);
            return UserData.NULL;
        }
    }

    public static UserData get(String email) {
        return fromStringField(DBFields.STR_EMAIL, email);
    }

    /**
     * Standard authentication method when logging in through our form.
     *
     * @param email
     * @param pwd
     *
     * @return
     */
    public static UserData get(String email, String pwd) {
        email = email.toLowerCase();
        try {
            stmtSelUsers.setString(1, SQLUtils.asSafeString(email));
            stmtSelUsers.setString(2, SQLUtils.asSafeString(pwd));
            return UserData.fromRs(stmtSelUsers.executeQuery());
        } catch (SQLException e) {
            feedreader.log.Logger.error(UsersTable.class).log("authenticate failed ").log(e.getMessage()).end();
            return UserData.NULL;
        }
    }

    public static UserData getFromForgotCode(String code) {
        return getFromCode(DBFields.STR_FORGOT_CODE, code);
    }

    public static UserData getFromRegCode(String code) {
        return getFromCode(DBFields.STR_REG_CODE, code);
    }

    public static boolean init() {
        conn = Database.getConnection();

        try {
            stmtSelUsers = conn.prepareCall(String.format("SELECT * FROM %s WHERE %s = ? AND %s = ?", TABLE,
                    DBFields.STR_EMAIL, DBFields.STR_PASSWORD));
            stmt = Database.getStatement();
        } catch (SQLException ex) {
            log.error("prepareCall failed {}", ex);
        }

        log.info("initialized.");
        return true;
    }

    /**
     * TODO: To be implemented.
     *
     * Validates an user against the Use database.
     *
     * @param userId
     * The user to validate
     * @param callerClzz
     * The class or Application calling the function. (Will be used
     * for reporting and monitoring)
     * @return true if user is known
     */
    public static boolean isValidUser(long userId, Class<?> callerClzz) {
        return true;
    }

    public static void saveCookie(UserData userData, String cookieKey) {
        try {
            String query = String.format("UPDATE %s SET %s = '%s' WHERE %s = %d", TABLE, DBFields.STR_COOKIE,
                    SQLUtils.asSafeString(cookieKey), DBFields.LONG_USER_ID, userData.getUserId(), DBFields.ENUM_OAUTH);
            stmt.execute(query);
        } catch (SQLException e) {
            log.error("save cookie error: {}", e, e.getMessage());
        }
    }

    public static void saveToken(long userId, OAuthType type, String token) {
        try {
            String query = String.format("SELECT %s FROM %s WHERE %s = %d AND %s = %d", DBFields.LONG_USER_ID,
                    TABLE_TOKENS, DBFields.LONG_USER_ID, userId, DBFields.ENUM_OAUTH, type.getVal());
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                query = String.format("UPDATE %s SET %s = %d, %s = %d, %s = '%s' WHERE %s = %d", TABLE_TOKENS,
                        DBFields.LONG_USER_ID, userId, DBFields.ENUM_OAUTH, type.getVal(), DBFields.STR_AUTH_TOKEN,
                        SQLUtils.asSafeString(token), DBFields.LONG_USER_ID, userId);
                stmt.execute(query);
            }

            query = String.format("INSERT INTO %s (%s, %s, %s) VALUES (%d, %d, '%s')", TABLE_TOKENS,
                    DBFields.LONG_USER_ID, DBFields.ENUM_OAUTH, DBFields.STR_AUTH_TOKEN, userId, type.getVal(),
                    SQLUtils.asSafeString(token));
            stmt.execute(query);
        } catch (SQLException ex) {
            log.error("save token error: {}", ex, ex.getMessage());
        }
    }

    public static int setForgotPassword(long userId) {
        try {
            String code = RandomStringUtils.random(12, true, true);
            String query = String.format("UPDATE %s SET %s = %b, %s = '%s' WHERE %s = %d", TABLE,
                    DBFields.BOOL_FORGOT_PWD, true,
                    DBFields.STR_FORGOT_CODE, SQLUtils.asSafeString(code),
                    DBFields.LONG_USER_ID, userId);
            return stmt.executeUpdate(query);
        } catch (SQLException ex) {
            log.error("set forgot password: {}", ex, ex.getMessage());
        }

        return -1;
    }

    public static void setLastProfile(long userId, long profileId) {
        try {
            String query = String.format("UPDATE %s SET %s = %d WHERE %s = %d", TABLE,
                    DBFields.LONG_SELECTED_PROFILE_ID, profileId, DBFields.LONG_USER_ID, userId);
            stmt.execute(query);
        } catch (SQLException ex) {
            log.error("set last profile error: {}", ex, ex.getMessage());
        }
    }

    public static int setNewPassword(UserData data, String pwd) {
        try {
            String query = String.format("UPDATE %s SET %s = '%s', %s = '%s' WHERE %s = %d", TABLE,
                    DBFields.STR_PASSWORD, pwd,
                    DBFields.STR_FORGOT_CODE, "", // clear code so it can't be reused.
                    DBFields.LONG_USER_ID, data.getUserId());
            log.info("password changed for: {}, email {}", data.getUserId(), data.getEmail());
            return stmt.executeUpdate(query);
        } catch (SQLException ex) {
            log.error("set new password error: {}", ex, ex.getMessage());
        }

        return -1;
    }

    public static void unverify(UserData data) {
        try {
            String code = getRegistrationCode(data.getEmail(), data.getPwd());
            String query = String.format("UPDATE %s SET %s = %b,  %s = %b, %s = '%s' WHERE %s = %d", TABLE,
                    DBFields.BOOL_VERIFIED, false, DBFields.BOOL_REG_SENT, false, DBFields.STR_REG_CODE,
                    SQLUtils.asSafeString(code), DBFields.LONG_USER_ID, data.getUserId());
            stmt.execute(query);
        } catch (SQLException ex) {
            log.error("unverify error: {}", ex, ex.getMessage());
        }
    }

    public static int update(UserData data) {
        try {
            String query = String.format(
                    "UPDATE %s SET %s = '%s', %s = '%s', %s = '%s', %s = %b, %s = '%b' WHERE %s = %d",
                    TABLE,
                    DBFields.STR_SCREEN_NAME, SQLUtils.asSafeString(data.getScreenName()), DBFields.STR_PASSWORD,
                    SQLUtils.asSafeString(data.getPwd()), DBFields.STR_EMAIL,
                    SQLUtils.asSafeString(data.getEmail()),
                    DBFields.BOOL_RECEIVE_NEWSLETTER,
                    data.isSubscribedForNewsletter(),
                    DBFields.BOOL_RECEIVE_PRODUCT_UPDATES,
                    data.isSubscribedToUpdates(),
                    DBFields.LONG_USER_ID,
                    data.getUserId());
            log.info("{}", query);

            return stmt.executeUpdate(query);
        } catch (Exception e) {
            log.error("update {}, error {}", data, e);
        }

        return -1;
    }

    public static void verify(UserData data) {
        try {
            String query = String.format("UPDATE %s SET %s = %b, %s = '%s' WHERE %s = %d", TABLE,
                    DBFields.BOOL_VERIFIED, true, DBFields.STR_REG_CODE, "", DBFields.LONG_USER_ID, data.getUserId());
            stmt.execute(query);
        } catch (SQLException ex) {
            log.error("verify error: {}", ex, ex.getMessage());
        }
    }

    static String getRegistrationCode(String email, String password) {
        String fallbackCode = "0xD34DB33F" + Long.toString(CurrentTime.inGMT());
        byte[] code = fallbackCode.getBytes();

        try {
            code = SimpleEncryption.encrypt(ENCKEY, true, email + password);
        } catch (Exception e) {
            log.error("error generating code {}", e);
        }

        return new String(code);
    }

    private static UserData fromStringField(String fieldName, String fieldVal) {
        try {
            ResultSet rs = Database.getEntry(TABLE, fieldName, fieldVal);
            return UserData.fromRs(rs);
        } catch (SQLException ex) {

            log.error("fromStringField  {}, val {}, error {}", fieldName, fieldVal, ex);

            return UserData.NULL;
        }
    }

    private static UserData getFromCode(String fieldName, String code) {
        try {
            String query = String.format("SELECT * FROM %s WHERE %s = '%s'", TABLE,
                    fieldName, code);
            log.debug(query);
            ResultSet rs = stmt.executeQuery(query);
            return UserData.fromRs(rs);
        } catch (SQLException ex) {
            log.error("getFromCode  {}, error {}", fieldName, ex);
        }

        return UserData.NULL;
    }

}
