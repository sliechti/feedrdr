package feedreader.entities;

import java.sql.ResultSet;
import java.sql.SQLException;

import feedreader.store.DBFields;

public class StreamGroup {
    private final long userId;
    private final long id;
    private final String name;

    public StreamGroup(long userId, long id, String name) {
        this.userId = userId;
        this.id = id;
        this.name = name;
    }

    public long getUserId() {
        return userId;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "UserFolder{" + "userId=" + userId + ", folderId=" + id + ", name=" + name + '}';
    }

    public StringBuilder toJSON(StringBuilder sb) {
        sb.append("{\"n\" : \"").append(name).append("\",")
                .append("\"i\": ").append(id).append("}");
        return sb;
    }

    public static StreamGroup fromRs(long userId, ResultSet rs) throws SQLException {
        return new StreamGroup(userId, rs.getLong(DBFields.LONG_STREAM_ID), rs.getString(DBFields.STR_STREAM_NAME));
    }

}
