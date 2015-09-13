<%@page import="feedreader.config.Environment"%>
<%@page import="feedreader.config.FeedAppConfig"%>
<%@page import="feedreader.store.UserProfilesTable"%>
<%@page import="feedreader.entities.ProfileData"%>
<%@page import="feedreader.config.Constants"%>
<%@page import="feedreader.security.Session"%>
<%@page import="feedreader.store.UsersTable"%>
<%@page import="feedreader.entities.UserData"%>
<%@page import="feedreader.utils.PageUtils"%>
<%@page import="feedreader.security.UserSession"%>

<% 
	long userId = UserSession.getUserId(request);
	if (userId == 0) {
	    PageUtils.gotoStart(response);
	}
	
	UserData user = UsersTable.get(userId);
	
	long profileId = Session.asLong(session, Constants.SESSION_SELECTED_PROFILE_ID, 0);
	ProfileData profile = user.getProfileData(profileId);
	
	if (profile.isEmpty()) {
	    profile = UserProfilesTable.getFirstProfile(userId);
	}
	
	request.setAttribute("user", user);
	request.setAttribute("profile", profile);
%>