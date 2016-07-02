package feedreader.oauth;

public class OAuthUser {

    String name;
    String email;
    String locale;

    public OAuthUser(String name, String email, String locale) {
        this.name = name;
        this.email = email;
        this.locale = locale;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getLocale() {
        return locale;
    }

    @Override public String toString() {
        return "OAuthUser{" + "name=" + name + ", email=" + email + ", locale=" + locale + '}';
    }

}
