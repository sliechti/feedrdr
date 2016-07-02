package feedreader.entities;

import java.sql.ResultSet;
import java.sql.SQLException;

import feedreader.store.DBFields;

public class ProfileData {
    public static final int INVALID_PROFILE_ID = 0;
    public static final ProfileData NULL = new ProfileData(INVALID_PROFILE_ID, "", "");
    
    long profileId;
    String name;
    String color;
    boolean isDefault = false;
    
    private ProfileData(long profileId, String name, String color) {
        this.profileId = profileId;
        this.name = name;
        this.color = color;
    }

    public long getProfileId() {
        return profileId;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public boolean isIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean yesNo) {
        this.isDefault = yesNo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isEmpty() {
        return profileId == INVALID_PROFILE_ID;
    }

    @Override
    public String toString() {
        return "ProfileData{" + "profileId=" + profileId + ", name=" + name + ", color=" + color + ", " + "isDefault="
                + isDefault + '}';
    }

    public static ProfileData fromRs(ResultSet rs) throws SQLException {
        ProfileData ret = new ProfileData(rs.getLong(DBFields.LONG_PROFILE_ID), 
                rs.getString(DBFields.STR_PROFILE_NAME), 
                rs.getString(DBFields.STR_COLOR));
        ret.setIsDefault(rs.getBoolean(DBFields.BOOL_IS_DEFAULT));
        return ret;
    }

}
