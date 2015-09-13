package feedreader.oauth;

public enum OAuthType
{
    UNDEFINED("un", -1),
    NONE("no", 0),
    FACEBOOK("fb", 1),
    GOOGLE("go", 2),
    LIVE("li", 3);

    public static OAuthType fromInt(int oAuth)
    {
        switch(oAuth)
        {
            case 0: return NONE;
            case 1: return FACEBOOK;
            case 2: return GOOGLE;
            case 3: return LIVE;
            default: return UNDEFINED;
        }
    }

    private final String name;
    private final int val;

    OAuthType(String name, int val)
    {
        this.name = name;
        this.val = val;
    }

    public int getVal()
    {
        return val;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return "oAuthType{" + "name=" + name + ", val=" + val + '}';
    }

}
