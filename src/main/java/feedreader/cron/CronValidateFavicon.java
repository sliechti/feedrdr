package feedreader.cron;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feedreader.config.FeedAppConfig;
import feedreader.store.Database;

public class CronValidateFavicon implements Runnable {

	private static final Logger logger = LoggerFactory
			.getLogger(CronValidateFavicon.class);

	public CronValidateFavicon() {
		logger.info("starting, running every: {} minutes",
				FeedAppConfig.DELAY_VALIDATE_FAVICON_IN_MINUTES);
	}

	@Override
	public void run() {
		try (Connection conn = Database.getConnection()) {
			StringBuffer validEntry = new StringBuffer();
			StringBuffer inValidEntry = new StringBuffer();

			// favicon can be removed from the s_link where b_is_favicon_exist =
			// true,
			// checking only invalid url will not be right, so removed condition
			// WHERE b_is_favicon_exist = false.
			String query = "SELECT l_xml_id,s_xml_url FROM feedreader.feedsources";
			ResultSet rs = Database.rawQuery(conn, query);

			while (rs.next()) {
				String link = rs.getString("s_xml_url");
				StringBuffer url = new StringBuffer(link);
				if (link.charAt(link.length() - 1) == '/')
					url.append("favicon.ico");
				else
					url.append("/favicon.ico");
				try {
					HttpURLConnection connection = (HttpURLConnection) new URL(
							url.toString()).openConnection();
					connection.setRequestMethod("GET");
					connection.connect();
					if (connection.getResponseCode() != 200) {
						inValidEntry.append(rs.getLong("l_xml_id") + ",");
					} else {
						validEntry.append(rs.getLong("l_xml_id") + ",");
					}
				} catch (IOException ex) {
					logger.error("error occur for url: {}", url);
				}catch (Exception ex) {
					logger.error("error occur for url: {}", url);
				}
			}
			if (validEntry.length() > 0) {
				logger.info("Favicon exists for entry id's {}",
						validEntry.toString());
				String updateLink = "UPDATE feedreader.feedsources SET b_is_favicon_exist=true where l_xml_id IN("
						+ validEntry.substring(0, validEntry.length() - 1)
								.toString() + ")";
				conn.createStatement().executeUpdate(updateLink);
			}
			if (inValidEntry.length() > 0) {
				logger.info("Favicon is absent for entry id's {}",
						inValidEntry.toString());
				String updateLink = "UPDATE feedreader.feedsources SET b_is_favicon_exist=false where l_xml_id IN("
						+ inValidEntry.substring(0, inValidEntry.length() - 1)
								.toString() + ")";
				conn.createStatement().executeUpdate(updateLink);
			}
		} catch (SQLException e) {
			logger.error("sql error: {}", e, e.getMessage());
		}
	}

}
