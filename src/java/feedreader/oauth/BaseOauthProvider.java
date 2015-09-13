package feedreader.oauth;

import feedreader.config.FeedAppConfig;
import feedreader.log.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class BaseOauthProvider {

    public abstract boolean onResponse(String response);

    protected abstract String getDefaultName();

    public abstract OAuthType getAuthType();

    String name = "";
    String email = "";
    String locale = "";

    String token;
    String meUrl;

    private StringBuilder sb = new StringBuilder();

    public BaseOauthProvider(String meUrl) {
        this.meUrl = meUrl;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUrl() {
        return meUrl + token;
    }

    public boolean authenticate() {
        if (token.isEmpty()) {
            throw new RuntimeException("Need to set a token before calling authenticate.");
        }

        HttpURLConnection conn;

        try {
            URL url = new URL(meUrl + token);
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("User-Agent", FeedAppConfig.FETCH_USER_AGENT);
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(FeedAppConfig.FETCH_CONNECTION_TIMEOUT);
            conn.setReadTimeout(FeedAppConfig.FETCH_READ_TIMEOUT);

            if (conn.getResponseCode() != 200) {
                Logger.error(this.getClass()).log("error getting ").log(meUrl).log(token).end();
                return false;
            }
        } catch (Exception e) {
            Logger.error(this.getClass()).log("error ").log(e.getMessage()).end();
            return false;
        }

        try {
            InputStream is = conn.getInputStream();
            sb.setLength(0);

            int c;
            while ((c = is.read()) != -1) {
                sb.append((char) c);
            }
        } catch (IOException ioe) {
            Logger.error(this.getClass()).log(ioe.getMessage()).end();
            return false;
        }

        return onResponse(sb.toString());
    }

    public OAuthUser getOauthUser() {
        if (name.isEmpty()) {
            name = getDefaultName();
        }
        if (locale.isEmpty()) {
            locale = FeedAppConfig.DEFAULT_LOCALE;
        }

        return new OAuthUser(name, email, locale);
    }

}
