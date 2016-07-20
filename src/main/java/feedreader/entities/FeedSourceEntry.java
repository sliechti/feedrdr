package feedreader.entities;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class FeedSourceEntry {

    private long addedAt = 0;
    private long checkedAt = 0;
    private long id = 0;
    private String xmlUrl = "";

    public FeedSourceEntry(long id, String xmlUrl) {
        this.id = id;
        this.xmlUrl = xmlUrl;
    }

    public long getAddedAt() {
        return addedAt;
    }

    public long getCheckedAt() {
        return checkedAt;
    }

    public long getId() {
        return id;
    }

    public String getXmlUrl() {
        return xmlUrl.toLowerCase();
    }

    public void setAddedAt(long addedAt) {
        this.addedAt = addedAt;
    }

    public void setCheckedAt(long l) {
        this.checkedAt = l;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
    }

}
