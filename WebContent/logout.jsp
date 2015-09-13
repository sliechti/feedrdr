<%@page import="feedreader.security.CookieUtils"%>
<%@page import="feedreader.config.Constants"%>
<%@page import="feedreader.utils.PageUtils"%>
<%
    session.invalidate();
	CookieUtils.wipe(response, Constants.USER_COOKIE);
    PageUtils.gotoStart(response);
%>