package feedreader.entities;

public class FeedSourceEntry {

    long id = 0;
    String xmlUrl = "";
    long checkedAt = 0;
    long addedAt = 0;

    public FeedSourceEntry(long id, String xmlUrl) {
        this.id = id;
        this.xmlUrl = xmlUrl;
    }

    public long getId() {
        return id;
    }

    public String getXmlUrl() {
        return xmlUrl.toLowerCase();
    }

    public long getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(long addedAt) {
        this.addedAt = addedAt;
    }

    public long getCheckedAt() {
        return checkedAt;
    }

    public void setCheckedAt(long l) {
        this.checkedAt = l;
    }

    @Override public String toString() {
        return "FeedSourceEntry{" + "id=" + id + ", xmlUrl=" + xmlUrl + ", " + "checkedAt=" + checkedAt + ", addedAt="
                + addedAt + '}';
    }

}
