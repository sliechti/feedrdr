package feedreader.cron;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feedreader.config.FeedAppConfig;
import feedreader.store.Database;

public class CronValidateFavicon implements Runnable {

	private static final Logger logger = LoggerFactory
			.getLogger(CronValidateFavicon.class);
    private static final int IMAGEHEIGHT = 16;
	private static final String FAVICONURL = "https://www.google.com/s2/favicons?domain=";
	private static final String DEFAULTURL = "http://default";

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

			// get favicon default url to match if google return return valid
			// icon or default icon.
			BufferedImage defaultImg = getImage(FAVICONURL + DEFAULTURL);

			if (defaultImg == null) {
				// TODO Need to add user define exception
				throw new Exception(" error occur, Not a valid url:"
						+ FAVICONURL + DEFAULTURL);
			}
			while (rs.next()) {
				boolean validImage = false;
				String link = rs.getString("s_xml_url");
				BufferedImage linkImg = getImage(FAVICONURL + link);
				if (linkImg == null) {
					// TODO Need to add user define exception
					throw new Exception(" error occur, Not a valid url:"
							+ FAVICONURL + link);
				}
				for (int x = 0; validImage == false && x < IMAGEHEIGHT; x++) {
					for (int y = 0; validImage == false && y < IMAGEHEIGHT; y++) {
						if (linkImg.getRGB(x, y) != defaultImg.getRGB(x, y)) {
							logger.info("images are not equal for url:" + link);
							validImage = true;
						}
					}
				}
				// if image are equal then the url's is not valid
				if (validImage) {
					validEntry.append(rs.getLong("l_xml_id") + ",");
				} else {
					inValidEntry.append(rs.getLong("l_xml_id") + ",");
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
		} catch (Exception e) {
			logger.error("Exception occur: {}", e, e.getMessage());
		}
	}

	private BufferedImage getImage(String url) {
		try {
			return ImageIO.read(new URL(url));

		} catch (MalformedURLException e) {
			logger.error("MalformedURLException: error occur for url: {}", url);
		} catch (IOException e) {
			logger.error("IOException: error occur for url: {}", url);
		}
		return null;
	}

}
