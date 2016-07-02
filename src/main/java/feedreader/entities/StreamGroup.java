package feedreader.entities;

public class StreamGroup
{
    long userId;
    long folderId;
    String name;

    public StreamGroup(long userId, long folderId, String name)
    {
        this.userId = userId;
        this.folderId = folderId;
        this.name = name;
    }
    
    public long getUserId()
    {
        return userId;
    }

    public long getFolderId()
    {
        return folderId;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return "UserFolder{" + "userId=" + userId + ", folderId=" + folderId + ", name=" + name + '}';
    }

    public StringBuilder toJSON(StringBuilder sb)
    {
        sb.append("{\"n\" : \"").append(name).append("\",")
                .append("\"i\": ").append(folderId).append("}");
        return sb;
    }
    
}
