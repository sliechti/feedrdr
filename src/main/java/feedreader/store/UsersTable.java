package feedreader.store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import feedreader.config.Constants;
import feedreader.entities.UserData;
import feedreader.log.Logger;
import feedreader.oauth.OAuthType;
import feedreader.time.CurrentTime;
import feedreader.utils.PwdUtils;
import feedreader.utils.SQLUtils;
import feedreader.utils.SimpleEncryption;

public class UsersTable {

    public static final String TABLE = Constants.USERS_TABLE;
    public static final String TABLE_TOKENS = Constants.USER_AUTH_TOKENS;

    static PreparedStatement stmtSelUsers;
    static Statement stmt;

    static Class<?> clz = UsersTable.class;
    static Connection conn;

    static final String ENCKEY = "REGISTRATION";

    public static boolean init() {
        conn = Database.getConnection();

        try {
            stmtSelUsers = conn.prepareCall(String.format("SELECT * FROM %s WHERE %s = ? AND %s = ?", TABLE,
                    DBFields.STR_EMAIL, DBFields.STR_PASSWORD));
            stmt = Database.getStatement();
        } catch (SQLException ex) {
            Logger.error(clz).log("prepareCall failed ").log(ex.getMessage()).end();
        }

        Logger.info(clz).log("initialized.").end();
        return true;
    }

    public static void close() {
        Logger.info(clz).log("close()").end();

        try {
            stmtSelUsers.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.error(clz).log("closing sql objects ").log(ex.getMessage()).end();
        }
    }

    static String getRegistrationCode(String email, String password) {
        String fallbackCode = "0xD34DB33F" + Long.toString(CurrentTime.inGMT());
        byte[] code = fallbackCode.getBytes();

        try {
            code = SimpleEncryption.encrypt(ENCKEY, true, email + password);
        } catch (Exception e) {
            Logger.error(clz).log("error generating code ").log(e.getMessage()).end();
        }

        return new String(code);
    }

    public static int displayName(long userId, String displayName) {
        try {
            String query = String.format("UPDATE %s SET %s = '%s' WHERE %s = %d", TABLE, DBFields.STR_SCREEN_NAME,
                    SQLUtils.asSafeString(displayName), DBFields.LONG_USER_ID, userId);
            Logger.debugSQL(clz).log(query).end();
            return stmt.executeUpdate(query);
        } catch (Exception e) {
            Logger.error(clz).log("displayName ").log(displayName).log(", error ").log(e.getMessage()).end();
        }

        return -1;
    }

    public static int update(UserData data) {
        try {
            String query = String.format("UPDATE %s SET %s = '%s', %s = '%s', %s = '%s' WHERE %s = %d", TABLE,
                    DBFields.STR_SCREEN_NAME, SQLUtils.asSafeString(data.getScreenName()), DBFields.STR_PASSWORD,
                    SQLUtils.asSafeString(data.getPwd()), DBFields.STR_EMAIL, SQLUtils.asSafeString(data.getEmail()),
                    DBFields.LONG_USER_ID, data.getUserId());
            Logger.debugSQL(clz).log(query).end();
            return stmt.executeUpdate(query);
        } catch (Exception e) {
            Logger.error(clz).log("update ").log(data).log(", error ").log(e.getMessage()).end();
        }

        return -1;
    }

    public static UserData get(long userId) {
        try {
            String query = "SELECT * FROM feedreader.users as t0 " +
                    "LEFT JOIN feedreader.userprofiles as t1 " +
                    "ON t0.l_user_id = t1.l_user_id WHERE t0.l_user_id = " + userId;
            ResultSet rs = Database.rawQuery(query);
            return UserData.fromRs(rs);
        } catch (SQLException ex) {
            Logger.error(clz).log("get userId ").log(userId).log(" ").log(ex.getMessage()).end();
            return UserData.NULL;
        }
    };

    static UserData getFromCode(String fieldName, String code) {
        try {
            String query = String.format("SELECT * FROM %s WHERE %s like '%s'", TABLE, fieldName, code + "%");
            Logger.debugSQL(clz).log(query).end();
            ResultSet rs = stmt.executeQuery(query);
            return UserData.fromRs(rs);
        } catch (SQLException ex) {
            Logger.error(clz).log("getFromCode ").log(fieldName).log("/").log(code).log(", error ")
                    .log(ex.getMessage()).end();
        }

        return UserData.NULL;
    }

    public static UserData getFromRegCode(String code) {
        return getFromCode(DBFields.STR_REG_CODE, code);
    }

    public static UserData getFromForgotCode(String code) {
        return getFromCode(DBFields.STR_FORGOT_CODE, code);
    }

    public static UserData get(String email) {
        return fromStringField(DBFields.STR_EMAIL, email);
    };

    public static UserData fromCookie(String cookieKey) {
        return fromStringField(DBFields.STR_COOKIE, cookieKey);
    }

    private static UserData fromStringField(String fieldName, String fieldVal) {
        try {
            ResultSet rs = Database.getEntry(TABLE, fieldName, fieldVal);
            return UserData.fromRs(rs);
        } catch (SQLException ex) {
            Logger.error(clz).log("fromStringField ").log(fieldName).log(", val ").log(fieldVal).log(", ex: ")
                    .log(ex.getMessage()).end();
            return UserData.NULL;
        }
    };

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
            Logger.debug(clz).log(stmtSelUsers.toString()).end();
            return UserData.fromRs(stmtSelUsers.executeQuery());
        } catch (SQLException e) {
            Logger.error(UsersTable.class).log("authenticate failed ").log(e.getMessage()).end();
            return UserData.NULL;
        }
    }

    public static void verify(UserData data) {
        try {
            String query = String.format("UPDATE %s SET %s = %b, %s = '%s' WHERE %s = %d", TABLE,
                    DBFields.BOOL_VERIFIED, true, DBFields.STR_REG_CODE, "", DBFields.LONG_USER_ID, data.getUserId());
            Logger.debugSQL(clz).log(query).end();
            stmt.execute(query);
        } catch (SQLException ex) {
            Logger.error(clz).log("verify ").log(data).log(", error ").log(ex.getMessage()).end();
        }
    }

    public static void unverify(UserData data) {
        try {
            String code = getRegistrationCode(data.getEmail(), data.getPwd());
            String query = String.format("UPDATE %s SET %s = %b,  %s = %b, %s = '%s' WHERE %s = %d", TABLE,
                    DBFields.BOOL_VERIFIED, false, DBFields.BOOL_REG_SENT, false, DBFields.STR_REG_CODE,
                    SQLUtils.asSafeString(code), DBFields.LONG_USER_ID, data.getUserId());
            Logger.debugSQL(clz).log(query).end();
            stmt.execute(query);
        } catch (SQLException ex) {
            Logger.error(clz).log("unverify ").log(data).log(", error ").log(ex.getMessage()).end();
        }
    }

    /**
     * TODO: To be implemented.
     *
     * Validates an user against the Use database.
     *
     * @param userId
     *            The user to validate
     * @param callerClzz
     *            The class or Application calling the function. (Will be used for reporting and monitoring)
     * @return true if user is known
     */
    public static boolean isValidUser(long userId, Class<?> callerClzz) {
        return true;
    }

    /**
     * User created with other services oauth. The email is mandatory, as password we generate something long.
     * 
     * @param email
     * @param oauth
     * @param authToken
     * @param password
     *            leave empty if oauth is different than none.
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
                return UserData.NULL;
            }

            if (password.isEmpty()) {
                password = PwdUtils.generate();
            }

            String code = getRegistrationCode(email, password);
            String query = String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s) "
                    + " VALUES ('%s', '%s', '%s', %d, '%s', %d, '%s')", 
                    TABLE, 
                    DBFields.STR_EMAIL, DBFields.STR_PASSWORD, DBFields.STR_LOCALE, 
                    DBFields.ENUM_MAIN_OAUTH, DBFields.STR_SCREEN_NAME,
                    DBFields.TIME_SUBSCRIBED_AT, DBFields.STR_REG_CODE, 
                    SQLUtils.asSafeString(email), password, SQLUtils.asSafeString(locale), 
                    oauth.getVal(), SQLUtils.asSafeString(screenName),
                    CurrentTime.inGMT(), SQLUtils.asSafeString(code));
            Logger.debugSQL(clz).log(query).end();
            if (stmt.executeUpdate(query) > 0) {
                return get(email);
            }
        } catch (SQLException ex) {
            Logger.error(clz).log("createNewUser ").log(email).log("/").log(oauth).log(", error ").log(ex.getMessage())
                    .end();
        }

        return UserData.NULL;
    }

    public static UserData createNewUser(String email, String screenName, String password, String locale) {
        return createNewUser(email, OAuthType.NONE, password, locale, screenName);
    }

    public static void setLastProfile(long userId, long profileId) {
        try {
            String query = String.format("UPDATE %s SET %s = %d WHERE %s = %d", TABLE,
                    DBFields.LONG_SELECTED_PROFILE_ID, profileId, DBFields.LONG_USER_ID, userId);
            stmt.execute(query);
        } catch (SQLException ex) {
            Logger.error(clz).log("setLastProfile ").log(userId).log("/").log(profileId).log(", error ")
                    .log(ex.getMessage()).end();
        }
    }

    public static int setNewPassword(UserData data, String pwd) {
        try {
            String query = String.format("UPDATE %s SET %s = '%s', %s = '%s' WHERE %s = %d", TABLE,
                    DBFields.STR_PASSWORD, pwd, DBFields.STR_FORGOT_CODE, "", DBFields.LONG_USER_ID, data.getUserId());
            Logger.debugSQL(clz).log(query).end();
            return stmt.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.debugSQL(clz).log("setNewPassword ").log(data).log("  ").log(pwd).log(", error ")
                    .log(ex.getMessage()).end();
        }

        return -1;
    }

    public static int setForgotPassword(long userId, String code) {
        try {
            String query = String.format("UPDATE %s SET %s = %b, %s = '%s' WHERE %s = %d", TABLE,
                    DBFields.BOOL_FORGOT_PWD, true, DBFields.STR_FORGOT_CODE, SQLUtils.asSafeString(code),
                    DBFields.LONG_USER_ID, userId);
            Logger.debugSQL(clz).log(query).end();
            return stmt.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.error(clz).log("setForgotPassword ").log(userId).log(", error ").log(ex.getMessage()).end();
        }

        return -1;
    }

    public static void saveToken(long userId, OAuthType type, String token) {
        try {
            String query = String.format("SELECT %s FROM %s WHERE %s = %d AND %s = %d", DBFields.LONG_USER_ID,
                    TABLE_TOKENS, DBFields.LONG_USER_ID, userId, DBFields.ENUM_OAUTH, type.getVal());
            Logger.debugSQL(clz).log("saveToken ").log(query).end();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                query = String.format("UPDATE %s SET %s = %d, %s = %d, %s = '%s' WHERE %s = %d", TABLE_TOKENS,
                        DBFields.LONG_USER_ID, userId, DBFields.ENUM_OAUTH, type.getVal(), DBFields.STR_AUTH_TOKEN,
                        SQLUtils.asSafeString(token), DBFields.LONG_USER_ID, userId);
                Logger.debugSQL(clz).log("saveToken ").log(query).end();
                stmt.execute(query);
            }

            query = String.format("INSERT INTO %s (%s, %s, %s) VALUES (%d, %d, '%s')", TABLE_TOKENS,
                    DBFields.LONG_USER_ID, DBFields.ENUM_OAUTH, DBFields.STR_AUTH_TOKEN, userId, type.getVal(),
                    SQLUtils.asSafeString(token));
            Logger.debugSQL(clz).log("saveToken ").log(query).end();
            stmt.execute(query);
        } catch (SQLException ex) {
            Logger.error(clz).log("saveToken ").log(userId).log("/").log(type).log("/").log(token).log(", error ")
                    .log(ex.getMessage()).end();
        }
    }

    public static void saveCookie(UserData userData, String cookieKey) {
        try {
            String query = String.format("UPDATE %s SET %s = '%s' WHERE %s = %d", TABLE, DBFields.STR_COOKIE,
                    SQLUtils.asSafeString(cookieKey), DBFields.LONG_USER_ID, userData.getUserId(), DBFields.ENUM_OAUTH);
            stmt.execute(query);
        } catch (SQLException e) {
            Logger.error(clz).log("saveCookie ").log(userData.getUserId()).log("/").log(cookieKey).log(e.getMessage())
                    .end();
        }
    }
    
}
