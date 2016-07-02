package feedreader.oauth;

import com.restfb.json.JsonObject;

public class GoogleOAuthProvider extends BaseOauthProvider 
{
    public GoogleOAuthProvider()
    {
        super("https://www.googleapis.com/userinfo/v2/me?access_token=");
    }
    
    @Override
    public boolean onResponse(String response)
    {
        JsonObject o = new JsonObject(response);
    
        try
        {
            email = o.getString("email");
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
        return "Google-Account";
    }

    @Override
    public OAuthType getAuthType()
    {
        return OAuthType.GOOGLE;
    }

}
