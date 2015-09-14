package feedreader.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import feedreader.oauth.OAuthType;
import feedreader.store.DBFields;

/**
 * @see UserDataTest
 */
public class UserData {

    public static UserData NULL = new UserData();

    private List<ProfileData> profileData = new ArrayList<>();
    
    public enum UserType {
        FREE(0), BASIC(1), PRO(2);

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
    };

    private long userId = 0;
    private long selectedProfileId = 0;

    private String email;
    private String screenName;
    private String pwd;
    private boolean verified;
    private String locale;
    private UserType userType;
    private OAuthType authType = OAuthType.NONE;
    private boolean isAdmin = false;
    private long subscribedAt = 0;
    
    private UserData() {};
    
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
        
        try {
            data.profileData.add(ProfileData.fromRs(rs));
            while (rs.next()) {
                data.profileData.add(ProfileData.fromRs(rs));
            }
        } catch (SQLException ex) {
            // query was for userdata only, not profile data, e.g UserData.get(email)
        }
        
        return data;
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
    
    public long getUserId() {
        return userId;
    }

    public String getEmail() {
        return (email != null) ? email.toLowerCase() : "";
    }

    public String getPwd() {
        return pwd;
    }

    public String getScreenName() {
        return screenName;
    }

    public long getSubscribedAt() {
        return subscribedAt;
    }

    public boolean isVerified() {
        return verified;
    }
    
    public boolean isAdmin() {
        return isAdmin;
    }

    public void setSelectedProfileId(long lastProfileId) {
        this.selectedProfileId = lastProfileId;
    }

    public long getSelectedProfileId() {
        return selectedProfileId;
    }

    // TODO: Implement.
    public String getLocale() {
        return locale;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public OAuthType getAuthType() {
        return authType;
    }

    public UserType getUserType() {
        return userType;
    }

    public boolean isOauthUser() {
        return getAuthType() != OAuthType.NONE && getAuthType() != OAuthType.UNDEFINED;
    }

    @Override
    public String toString() {
        return "UserData{" + "userId=" + userId + ", profileId=" + selectedProfileId + ", email=" + email
                + ", screenName=" + screenName + ", pwd=" + pwd + ", verified=" + verified + ", locale=" + locale
                + ", userType=" + userType + ", authType=" + authType + ", profileData = "+ profileData.size() +"}";
    }

}
