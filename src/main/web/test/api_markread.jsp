<%@page contentType="application/json" %>
<%@page import="feedreader.store.UserFeedEntries"%>
<%@page import="feedreader.store.Database"%>
<%@page import="feedreader.security.Session"%>
<%@page import="feedreader.utils.JSONUtils"%>
<%@page import="feedreader.config.Constants"%>
<%@page import="feedreader.config.Constants"%>
<%@page import="feedreader.security.Parameter"%>
<%

    String entries = Parameter.asString(request, "e", "");
    long userId = Session.asLong(session, Constants.SESSION_USERID_FIELD, 0);
    long profileId = Session.asLong(session, Constants.SESSION_SELECTED_PROFILE_ID, 0);
    
    if (userId == 0 || profileId == 0) {
        out.write(JSONUtils.error(0, "empty profile or user id"));
        return;
    }
    
    if (entries.contains(",")) {
        String[] eall = entries.split(",");
        
        int c = 0;
        for (String e : eall)
        {
            long entryId = Long.parseLong(e);
            c += (UserFeedEntries.setRead(true, userId, profileId, entryId)) ? 1 : 0;
        }
        out.write(JSONUtils.count(c));
    }
    else
    {
        long entryId = Long.parseLong(entries);
        out.write(JSONUtils.count((UserFeedEntries.setRead(true, userId, profileId, entryId)) ? 1 : 0));
    }
%>