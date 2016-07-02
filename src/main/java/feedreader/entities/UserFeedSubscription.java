package feedreader.entities;

public class UserFeedSubscription
{
    long subsId;
    long userId;
    long xmlId;
    long fodlerId;
    String feedName;

    public UserFeedSubscription(long subsId, long userId, long xmlId, long fodlerId, String feedName)
    {
        this.subsId = subsId;
        this.userId = userId;
        this.xmlId = xmlId;
        this.fodlerId = fodlerId;
        this.feedName = feedName;
    }

    public long getSubsId()
    {
        return subsId;
    }
    
    public long getUserId()
    {
        return userId;
    }

    public long getXmlId()
    {
        return xmlId;
    }

    public long getFodlerId()
    {
        return fodlerId;
    }

    public String getFeedName()
    {
        return feedName;
    }

    @Override
    public String toString()
    {
        return "UserFeedSubscription{" + "subsId=" + subsId + ", userId=" + userId + ", xmlId=" + xmlId + ", fodlerId=" + fodlerId + ", feedName=" + feedName + '}';
    }


    
    

}
