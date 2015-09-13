package feedreader.scope;

import feedreader.oauth.OAuthType;


public final class UserScope {

    private long profileId;
    private OAuthType oAuthType;
    private long userType;

    public long getUserType() {
        return userType;
    }
    
    public long getProfileId() {
        return profileId;
    }
    
    public OAuthType getAuthType() {
        return oAuthType;
    }
}

