package feedreader.entities;

import java.sql.ResultSet;
import java.sql.SQLException;

import feedreader.store.DBFields;

public class StreamGroup {
    long userId;
    long folderId;
    String name;

    public StreamGroup(long userId, long folderId, String name) {
        this.userId = userId;
        this.folderId = folderId;
        this.name = name;
    }

    public long getUserId() {
        return userId;
    }

    public long getFolderId() {
        return folderId;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "UserFolder{" + "userId=" + userId + ", folderId=" + folderId + ", name=" + name + '}';
    }

    public StringBuilder toJSON(StringBuilder sb) {
        sb.append("{\"n\" : \"").append(name).append("\",")
                .append("\"i\": ").append(folderId).append("}");
        return sb;
    }

    public static StreamGroup fromRs(long userId, ResultSet rs) throws SQLException {
        return new StreamGroup(userId, rs.getLong(DBFields.LONG_STREAM_ID), rs.getString(DBFields.STR_STREAM_NAME));
    }

}
