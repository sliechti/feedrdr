package feedreader.entities;

/**
 * OPMLEntry:
 *
 * <code>
 *         &lt;outline
 *                  type="rss"
 *                  title="Slashdot"
 *                  text="Slashdot"
 *                  version="RSS"
 *                  fz:quickMode="false"
 *                  xmlUrl="http://rss.slashdot.org/Slashdot/slashdot"
 *                  htmlUrl="http://slashdot.org/" />
 * </code>
 */
public class OPMLEntry {

    String title;
    String xmlUrl;

    public OPMLEntry(String title, String xmlUrl) {
        this.title = title;
        this.xmlUrl = xmlUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getXmlUrl() {
        return xmlUrl.toLowerCase();
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(this.getClass().getSimpleName()).append(": ").append("title=[").append(title).append("] ")
                .append("xmlUrl=[").append(xmlUrl).append("]");

        return sb.toString();
    }

}
