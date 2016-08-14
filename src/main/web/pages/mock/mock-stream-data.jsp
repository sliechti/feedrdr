
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%!

public class StreamEntry {

    private String pubDate;
    private String title;
    private long id;

    public StreamEntry(long id, String pubDate, String title) {
        this.id = id;
        this.pubDate = pubDate;
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getTitle() {
        return title;
    }
}

%>

<%
	List<StreamEntry> entries = new ArrayList<StreamEntry>();
	entries.add(new StreamEntry(1, "28m", "Your Airbnb Wi-Fi Is Not Secure: How to Protect Yourself When Traveling"));
	entries.add(new StreamEntry(2, "1h", "The Gap Between Good and Great Facebook Content Is Getting Worse"));
	entries.add(new StreamEntry(3, "28m", "Your Airbnb Wi-Fi Is Not Secure: How to Protect Yourself When Traveling"));
	entries.add(new StreamEntry(4, "28m", "Your Airbnb Wi-Fi Is Not Secure: How to Protect Yourself When Traveling"));
	entries.add(new StreamEntry(5, "28m", "Your Airbnb Wi-Fi Is Not Secure: How to Protect Yourself When Traveling"));
	entries.add(new StreamEntry(6, "28m", "Your Airbnb Wi-Fi Is Not Secure: How to Protect Yourself When Traveling"));
	entries.add(new StreamEntry(7, "28m", "Your Airbnb Wi-Fi Is Not Secure: How to Protect Yourself When Traveling"));
	entries.add(new StreamEntry(8, "28m", "Your Airbnb Wi-Fi Is Not Secure: How to Protect Yourself When Traveling"));
	entries.add(new StreamEntry(9, "1h", "The Gap Between Good and Great Facebook Content Is Getting Worse"));
	entries.add(new StreamEntry(10, "1h", "The Gap Between Good and Great Facebook Content Is Getting Worse"));
	entries.add(new StreamEntry(11, "1h", "The Gap Between Good and Great Facebook Content Is Getting Worse"));
	entries.add(new StreamEntry(12, "1h", "The Gap Between Good and Great Facebook Content Is Getting Worse"));
	entries.add(new StreamEntry(13, "1h", "The Gap Between Good and Great Facebook Content Is Getting Worse"));
	entries.add(new StreamEntry(14, "1h", "The Gap Between Good and Great Facebook Content Is Getting Worse"));
	request.setAttribute("streamEntries", entries);
%>