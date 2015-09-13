<%@page import="feedreader.store.UserProfilesTable"%>
<%@page import="feedreader.security.Session"%>
<%@page import="feedreader.entities.ProfileData"%>
<%@page import="feedreader.security.Parameter"%>
<%@page import="feedreader.store.UsersTable"%>
<%@page import="feedreader.config.Constants"%>
<%
    long userId = Session.asLong(session, Constants.SESSION_USERID_FIELD, 0);
    
    if (userId == 0) {
        return;
    }
    
    long id = Parameter.asLong(request, "id", 0);
    ProfileData profileData = UserProfilesTable.getProfile(userId, id);
    
    if (profileData.getName().isEmpty()) return;
    
    UsersTable.setLastProfile(userId, id);
    
    Session.set(session, Constants.SESSION_SELECTED_PROFILE_ID, id);
    Session.set(session, Constants.SESSION_SELECTED_PROFILE_NAME, profileData.getName());
    Session.set(session, Constants.SESSION_SELECTED_PROFILE_COLOR, profileData.getColor());
  
    out.write("ok");
%>