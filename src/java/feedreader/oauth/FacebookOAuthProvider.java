package feedreader.oauth;

import com.restfb.json.JsonObject;

public class FacebookOAuthProvider extends BaseOauthProvider {

    public FacebookOAuthProvider() {
        super("https://graph.facebook.com/me?access_token=");
    }

    @Override
    public boolean onResponse(String response) {
        JsonObject o = new JsonObject(response);

        try {
            email = o.getString("email");
            name = o.getString("name");
            locale = o.getString("locale");
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    @Override
    protected String getDefaultName() {
        return "Facebook-Account";
    }

    @Override
    public OAuthType getAuthType() {
        return OAuthType.FACEBOOK;
    }

}
