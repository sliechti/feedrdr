package feedreader.utils;

public class EmailParamsBuilder {

    private String bodyText;
    private String from;
    private String subject;
    private String to;
    private String toName;

    public EmailParamsBuilder setBodyText(String bodyText) {
        this.bodyText = bodyText;
        return this;
    }

    public EmailParamsBuilder setFrom(String from) {
        this.from = from;
        return this;
    }

    public EmailParamsBuilder setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public EmailParamsBuilder setTo(String to) {
        this.to = to;
        return this;
    }

    public EmailParamsBuilder setToName(String toName) {
        this.toName = toName;
        return this;
    }

    public String getBodyText() {
        return bodyText;
    }

    public String getFrom() {
        return from;
    }

    public String getSubject() {
        return subject;
    }

    public String getTo() {
        return to;
    }

    public String getToName() {
        return toName;
    }

}