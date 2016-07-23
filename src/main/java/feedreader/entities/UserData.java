package feedreader.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import feedreader.oauth.OAuthType;
import feedreader.store.DBFields;

/**
 * @see UserDataTest
 */
public class UserData {

    public static UserData NULL = new UserData();

    private OAuthType authType = OAuthType.NONE;

    private String email;

    private boolean generated = false;
    private boolean isAdmin = false;

    private boolean isSubscribedForNewsletter;
    private boolean isSubscribedToUpdates;
    private String locale;
    private List<ProfileData> profileData = new ArrayList<>();
    private String pwd;
    private String screenName;
    private long selectedProfileId = 0;
    private long subscribedAt = 0;
    private long userId = 0;
    private UserType userType;
    private boolean verified;

    private UserData() {
    }

    public OAuthType getAuthType() {
        return authType;
    }

    public String getEmail() {
        return (email != null) ? email.toLowerCase() : "";
    }

    public String getLocale() {
        return locale;
    }

    public List<ProfileData> getProfileData() {
        return profileData;
    }

    public ProfileData getProfileData(long profileId) {
        for (ProfileData p : profileData) {
            if (p.getProfileId() == profileId) {
                return p;
            }
        }
        return ProfileData.NULL;
    }

    public String getPwd() {
        return pwd;
    }

    public String getScreenName() {
        return screenName;
    }

    public long getSelectedProfileId() {
        return selectedProfileId;
    }

    public long getSubscribedAt() {
        return subscribedAt;
    }

    public long getUserId() {
        return userId;
    }

    public UserType getUserType() {
        return userType;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public boolean isGenerated() {
        return generated;
    }

    public boolean isNull() {
        return this == NULL;
    }

    public boolean isOauthUser() {
        return getAuthType() != OAuthType.NONE && getAuthType() != OAuthType.UNDEFINED;
    }

    public boolean isSubscribedForNewsletter() {
        return isSubscribedForNewsletter;
    }

    public boolean isSubscribedToUpdates() {
        return isSubscribedToUpdates;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
        this.generated = false;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setSelectedProfileId(long lastProfileId) {
        this.selectedProfileId = lastProfileId;
    }

    public void setSubscribedForNewsletter(boolean b) {
        this.isSubscribedForNewsletter = b;
    }

    public void setSubscribedToProductUpdates(boolean b) {
        this.isSubscribedToUpdates = b;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toStringExclude(this, "pwd");
    }

    public static UserData fromRs(ResultSet rs) throws SQLException {
        UserData data = new UserData();
        if (!rs.next()) {
            return NULL;
        }

        data.userId = rs.getLong(DBFields.LONG_USER_ID);
        data.email = rs.getString(DBFields.STR_EMAIL);
        data.screenName = rs.getString(DBFields.STR_SCREEN_NAME);
        data.pwd = rs.getString(DBFields.STR_PASSWORD);
        data.verified = rs.getBoolean(DBFields.BOOL_VERIFIED);
        data.authType = OAuthType.fromInt(rs.getInt(DBFields.ENUM_MAIN_OAUTH));
        data.userType = UserData.UserType.fromInt(rs.getInt(DBFields.ENUM_USER_TYPE));
        data.isAdmin = rs.getBoolean(DBFields.BOOL_IS_ADMIN);
        data.selectedProfileId = rs.getLong(DBFields.LONG_SELECTED_PROFILE_ID);
        data.subscribedAt = rs.getLong(DBFields.TIME_SUBSCRIBED_AT);
        data.generated = rs.getBoolean(DBFields.BOOL_GENERATED);
        data.isSubscribedToUpdates = rs.getBoolean(DBFields.BOOL_RECEIVE_NEWSLETTER);
        data.isSubscribedForNewsletter = rs.getBoolean(DBFields.BOOL_RECEIVE_PRODUCT_UPDATES);

        try {
            data.profileData.add(ProfileData.fromRs(rs));
            while (rs.next()) {
                data.profileData.add(ProfileData.fromRs(rs));
            }
        } catch (SQLException ex) {
            // query was for userdata only, not profile data, e.g
            // UserData.get(email)
        }

        return data;
    }

    public enum UserType {
        BASIC(1), FREE(0), PRO(2);

        int type = 0;

        private UserType(int type) {
            this.type = type;
        }

        public int val() {
            return type;
        }

        public static UserType fromInt(int aInt) {
            switch (aInt) {
                case 1:
                    return UserType.BASIC;
                case 2:
                    return UserType.PRO;
                default:
                case 0:
                    return UserType.FREE;
            }
        }
    }

}
