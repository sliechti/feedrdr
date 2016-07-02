package feedreader.oauth;

import com.restfb.json.JsonObject;

public class WindowsLiveOAuthProvider extends BaseOauthProvider
{    
    public WindowsLiveOAuthProvider()
    {
        super("https://apis.live.net/v5.0/me?access_token=");
    }
    
    @Override
    public boolean onResponse(String response)
    {
        JsonObject o = new JsonObject(response);
    
        name = "Live-Account";
        email = "";
        locale = "en_US";
    
        try
        {
            email = o.getJsonObject("emails").getString("preferred");
            name = o.getString("name");
            locale = o.getString("locale");
        } 
        catch (Exception e) {
            return false;
        }
        
        return true;
    }

    @Override
    protected String getDefaultName()
    {
        return "Live-Account";
    }

    @Override
    public OAuthType getAuthType()
    {
        return OAuthType.LIVE;
    }

}
