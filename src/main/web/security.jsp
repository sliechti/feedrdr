<%
	long userId = UserSession.getUserId(request);
	if (userId == 0) {
	    PageUtils.gotoStart(response);
	    return;
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