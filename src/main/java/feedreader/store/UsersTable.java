package feedreader.store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feedreader.config.Constants;
import feedreader.entities.UserData;
import feedreader.oauth.OAuthType;
import feedreader.time.CurrentTime;
import feedreader.utils.PwdUtils;
import feedreader.utils.SQLUtils;
import feedreader.utils.SimpleEmail;

public class UsersTable {

    public static final String TABLE = Constants.USERS_TABLE;
    public static final String TABLE_TOKENS = Constants.USER_AUTH_TOKENS;
    private static final Logger log = LoggerFactory.getLogger(UsersTable.class);

    public static void close() {
        log.info("close()");
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

        try (Connection conn = Database.getConnection()) {
            ResultSet rs = Database.checkEntry(conn, TABLE, DBFields.STR_EMAIL, email);
            if (rs.next()) {
                log.info("email already known: {}", email);
                return UserData.NULL;
            }

            boolean generated = false;
            if (password == null || password.isEmpty()) {
                password = PwdUtils.generate();
                generated = true;
            }

            String code = getRegistrationCode();
            String query = String.format(
                    "INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s) "
                            + " VALUES ('%s', '%s', '%s', %d, '%s', %d, '%s', %s)",
                    TABLE, DBFields.STR_EMAIL, DBFields.STR_PASSWORD, DBFields.STR_LOCALE, DBFields.ENUM_MAIN_OAUTH,
                    DBFields.STR_SCREEN_NAME, DBFields.TIME_SUBSCRIBED_AT, DBFields.STR_REG_CODE,
                    DBFields.BOOL_GENERATED, SQLUtils.asSafeString(email), password, SQLUtils.asSafeString(locale),
                    oauth.getVal(), SQLUtils.asSafeString(screenName), CurrentTime.inGMT(), SQLUtils.asSafeString(code),
                    generated);
            log.debug("createNewUser SQL: {}", query);
            if (conn.createStatement().executeUpdate(query) > 0) {
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
        try (Connection conn = Database.getConnection()) {
            String query = String.format("UPDATE %s SET %s = '%s' WHERE %s = %d", TABLE, DBFields.STR_SCREEN_NAME,
                    SQLUtils.asSafeString(displayName), DBFields.LONG_USER_ID, userId);
            return conn.createStatement().executeUpdate(query);
        } catch (Exception e) {
            log.error("displayName {}, error {}", displayName, e);
        }
        return -1;
    }

    public static UserData fromCookie(String cookieKey) {
        return fromStringField(DBFields.STR_COOKIE, cookieKey);
    }

    public static UserData get(long userId) {
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT * FROM feedreader.users as t0 " + "LEFT JOIN feedreader.userprofiles as t1 "
                    + "ON t0.l_user_id = t1.l_user_id WHERE t0.l_user_id = " + userId;
            ResultSet rs = Database.rawQuery(conn, query);
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
        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    String.format("SELECT * FROM %s WHERE %s = ? AND %s = ?", TABLE,
                            DBFields.STR_EMAIL, DBFields.STR_PASSWORD));
            stmt.setString(1, SQLUtils.asSafeString(email));
            stmt.setString(2, SQLUtils.asSafeString(pwd));
            return UserData.fromRs(stmt.executeQuery());
        } catch (SQLException e) {
            log.error("authenticate failed: {}", e, e.getMessage());
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
        log.info("initialized.");
        return true;
    }

    /**
     * TODO: To be implemented.
     *
     * Validates an user against the User database.
     *
     * @param userId
     * The user to validate
     * @return true if user is known
     */
    public static boolean isValidUser(long userId) {
        return true;
    }

    public static void saveCookie(UserData userData, String cookieKey) {
        try (Connection conn = Database.getConnection()) {
            String query = String.format("UPDATE %s SET %s = '%s' WHERE %s = %d", TABLE, DBFields.STR_COOKIE,
                    SQLUtils.asSafeString(cookieKey), DBFields.LONG_USER_ID, userData.getUserId(), DBFields.ENUM_OAUTH);
            conn.createStatement().execute(query);
        } catch (SQLException e) {
            log.error("save cookie error: {}", e, e.getMessage());
        }
    }

    public static void saveToken(long userId, OAuthType type, String token) {
        try (Connection conn = Database.getConnection()) {
            String query = String.format("SELECT %s FROM %s WHERE %s = %d AND %s = %d", DBFields.LONG_USER_ID,
                    TABLE_TOKENS, DBFields.LONG_USER_ID, userId, DBFields.ENUM_OAUTH, type.getVal());
            ResultSet rs = conn.createStatement().executeQuery(query);

            if (rs.next()) {
                query = String.format("UPDATE %s SET %s = %d, %s = %d, %s = '%s' WHERE %s = %d", TABLE_TOKENS,
                        DBFields.LONG_USER_ID, userId, DBFields.ENUM_OAUTH, type.getVal(), DBFields.STR_AUTH_TOKEN,
                        SQLUtils.asSafeString(token), DBFields.LONG_USER_ID, userId);
                conn.createStatement().execute(query);
            }

            query = String.format("INSERT INTO %s (%s, %s, %s) VALUES (%d, %d, '%s')", TABLE_TOKENS,
                    DBFields.LONG_USER_ID, DBFields.ENUM_OAUTH, DBFields.STR_AUTH_TOKEN, userId, type.getVal(),
                    SQLUtils.asSafeString(token));
            conn.createStatement().execute(query);
        } catch (SQLException ex) {
            log.error("save token error: {}", ex, ex.getMessage());
        }
    }

    public static int setForgotPassword(long userId) {
        try (Connection conn = Database.getConnection()) {
            String code = RandomStringUtils.random(12, true, true);
            String query = String.format("UPDATE %s SET %s = %b, %s = '%s' WHERE %s = %d", TABLE,
                    DBFields.BOOL_FORGOT_PWD, true,
                    DBFields.STR_FORGOT_CODE, SQLUtils.asSafeString(code),
                    DBFields.LONG_USER_ID, userId);
            return conn.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            log.error("set forgot password: {}", ex, ex.getMessage());
        }

        return -1;
    }

    public static void setLastProfile(long userId, long profileId) {
        try (Connection conn = Database.getConnection()) {
            String query = String.format("UPDATE %s SET %s = %d WHERE %s = %d", TABLE,
                    DBFields.LONG_SELECTED_PROFILE_ID, profileId, DBFields.LONG_USER_ID, userId);
            conn.createStatement().execute(query);
        } catch (SQLException ex) {
            log.error("set last profile error: {}", ex, ex.getMessage());
        }
    }

    public static int setNewPassword(UserData data, String pwd) {
        try (Connection conn = Database.getConnection()) {
            String query = String.format("UPDATE %s SET %s = '%s', %s = '%s' WHERE %s = %d", TABLE,
                    DBFields.STR_PASSWORD, pwd,
                    DBFields.STR_FORGOT_CODE, "", // clear code so it can't be reused.
                    DBFields.LONG_USER_ID, data.getUserId());
            log.info("password changed for: {}, email {}", data.getUserId(), data.getEmail());
            return conn.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            log.error("set new password error: {}", ex, ex.getMessage());
        }

        return -1;
    }

    public static void unverify(UserData data) {
        try (Connection conn = Database.getConnection()) {
            String code = getRegistrationCode();
            String query = String.format("UPDATE %s SET %s = %b,  %s = %b, %s = '%s' WHERE %s = %d", TABLE,
                    DBFields.BOOL_VERIFIED, false, DBFields.BOOL_REG_SENT, false, DBFields.STR_REG_CODE,
                    SQLUtils.asSafeString(code), DBFields.LONG_USER_ID, data.getUserId());
            conn.createStatement().execute(query);
        } catch (SQLException ex) {
            log.error("unverify error: {}", ex, ex.getMessage());
        }
    }

    public static int update(UserData data) {
        try (Connection conn = Database.getConnection()) {
            String query = String.format(
                    "UPDATE %s SET %s = '%s', %s = '%s', %s = '%s', %s = %b, %s = '%b', %s = '%b' WHERE %s = %d",
                    TABLE,
                    DBFields.STR_SCREEN_NAME, SQLUtils.asSafeString(data.getScreenName()), DBFields.STR_PASSWORD,
                    SQLUtils.asSafeString(data.getPwd()), DBFields.STR_EMAIL,
                    SQLUtils.asSafeString(data.getEmail()),
                    DBFields.BOOL_RECEIVE_NEWSLETTER,
                    data.isSubscribedForNewsletter(),
                    DBFields.BOOL_RECEIVE_PRODUCT_UPDATES,
                    data.isSubscribedToUpdates(),
                    DBFields.BOOL_GENERATED,
                    data.isGenerated(),
                    DBFields.LONG_USER_ID,
                    data.getUserId());
            log.info("update user: {}", data);
            return conn.createStatement().executeUpdate(query);
        } catch (Exception e) {
            log.error("update {}, error {}", data, e);
        }

        return -1;
    }

    public static void verify(UserData data) {
        try (Connection conn = Database.getConnection()) {
            String query = String.format("UPDATE %s SET %s = %b, %s = '%s' WHERE %s = %d", TABLE,
                    DBFields.BOOL_VERIFIED, true, DBFields.STR_REG_CODE, "", DBFields.LONG_USER_ID, data.getUserId());
            conn.createStatement().execute(query);
        } catch (SQLException ex) {
            log.error("verify error: {}", ex, ex.getMessage());
        }
    }

    static String getRegistrationCode() {
        return RandomStringUtils.random(20, true, true);
    }

    private static UserData fromStringField(String fieldName, String fieldVal) {
        try (Connection conn = Database.getConnection()) {
            ResultSet rs = Database.getEntry(conn, TABLE, fieldName, fieldVal);
            return UserData.fromRs(rs);
        } catch (SQLException ex) {

            log.error("fromStringField  {}, val {}, error {}", fieldName, fieldVal, ex);

            return UserData.NULL;
        }
    }

    private static UserData getFromCode(String fieldName, String code) {
        try (Connection conn = Database.getConnection()) {
            String query = String.format("SELECT * FROM %s WHERE %s = '%s'", TABLE,
                    fieldName, code);
            log.debug(query);
            ResultSet rs = conn.createStatement().executeQuery(query);
            return UserData.fromRs(rs);
        } catch (SQLException ex) {
            log.error("getFromCode  {}, error {}", fieldName, ex);
        }

        return UserData.NULL;
    }

    public static void disableAccount(long userId, String reason) {
        log.info("disabling account {}, reason: {}", userId, reason);
        String query = String.format("UPDATE feedreader.users "
                + "SET b_acct_disabled = true "
                + "WHERE l_user_id = %d", userId);
        try (Connection conn = Database.getConnection()) {
            conn.createStatement().execute(query);
            UserData user = get(userId);
            SimpleEmail.getInstance().sendAccountDisabled(user.getEmail(), reason);
        } catch (Exception e) {
            log.error("disable account failed: {}", e, e.getMessage());
        }
    }

}
