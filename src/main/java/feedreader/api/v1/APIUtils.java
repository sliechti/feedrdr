package feedreader.api.v1;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;

import feedreader.log.Logger;
import feedreader.utils.JSONUtils;

public class APIUtils {

    public static boolean isText(int ct) {
        return (ct == java.sql.Types.VARCHAR);
    }

    public static boolean isTimestamp(int ct) {
        return (ct == java.sql.Types.TIMESTAMP);
    }

    public static boolean isBoolean(int ct) {
        return (ct == java.sql.Types.BIT || ct == java.sql.Types.BOOLEAN);
    }

    public static int wrapObject(StringBuilder sb, ResultSet rs) {
        return wrapObject(sb, rs, false, null);
    }

    // The issue is how to recognize which ones are strings which ones are numbers. The check would be expensive.
    public static int wrapObject(StringBuilder sb, ResultSet rs, boolean appendRowCount, HashMap<String, String> mapTo) {
        int rows = 0;

        try {
            ResultSetMetaData metadata = rs.getMetaData();
            int numColumns = metadata.getColumnCount();

            while (rs.next()) {
                sb.append("{");
                rows++;

                for (int i = 1; i < numColumns + 1; i++) {
                    String n = metadata.getColumnName(i);

                    int type = metadata.getColumnType(i);

                    if (appendRowCount) {
                        sb.append("\"row\" : ").append(rows).append(",");
                    }

                    if (mapTo != null) {
                        String to = mapTo.get(n);
                        sb.append("\"").append(((to == null) ? n : to)).append("\":");
                    } else {
                        sb.append("\"").append(n).append("\":");
                    }

                    if (isText(type)) {
                        sb.append("\"").append(JSONUtils.escapeQuotes(rs.getString(i))).append("\"");
                    } else if (isBoolean(type)) {
                        sb.append(rs.getBoolean(i));
                    } else {
                        sb.append(rs.getLong(i));
                    }

                    if (i != numColumns) {
                        sb.append(",");
                    }
                }

                sb.append("},");
            }
            if (appendRowCount) {
                sb.insert(1, "\"rows\" : " + rows + ", ");
            }

            if (rows > 0 && sb.length() > 1) {
                sb.setLength(sb.length() - 1);
            }
        } catch (SQLException ex) {
            Logger.notice(APIUtils.class).log("wrapObject error, ").log(ex.getMessage()).end();
        }

        return rows;
    }
    
    public static StringBuilder updateInvalidSLinks(StringBuilder data){
		String path = data.substring(data.lastIndexOf("\":") + 3,
				data.lastIndexOf("}") - 1);
		StringBuffer url = new StringBuffer(path);
		if (path.charAt(path.length() - 1) == '/')
			url.append("favicon.ico");
		else
			url.append("/favicon.ico");
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(
					url.toString()).openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			if (connection.getResponseCode() != 200) {
				data.replace(data.lastIndexOf("\":") + 3, data.lastIndexOf("}") - 1,
						"https://feedrdr.co");
			}

		} catch (IOException ex) {
			Logger.notice(APIUtils.class).log("updateInvalidSLinks error, ").log(ex.getMessage()).end();
			ex.printStackTrace();
		}
		return data;
    }

}
