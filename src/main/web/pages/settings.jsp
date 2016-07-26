<%@page import="org.slf4j.LoggerFactory"%>
<%@page import="org.slf4j.Logger"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@page import="feedreader.utils.Validate"%>
<%@page import="feedreader.security.UserSession"%>
<%@page import="feedreader.security.Parameter"%>
<%@page import="feedreader.store.UsersTable"%>
<%@page import="feedreader.entities.UserData"%>
<%@page import="feedreader.config.Constants"%>

<%
    request.setAttribute("e", true);
%>

<%@include file="header.jsp"%>


<%!// All code should be moved to a /settings servlet.
	private static class SettingsJsp {
	}

	static final String SAVED_RELOAD = "<hr><a class='block' href='' onClick='location.assing();return false;'><b>reload page</b></a>";
	static final Logger logger = LoggerFactory.getLogger(SettingsJsp.class);

	public String checkedIfTrue(boolean b) {
		return (b) ? "checked" : "";
	}

	public void updateUsers(HttpServletRequest request, UserData data) {
		logger.debug("updating user: {}", data);
		String info = "";
		String err = "";
		if (UsersTable.update(data) > 0) {
			request.setAttribute("info", "User data updated<hr>"
					+ "<a class='block' href='' onClick='location.assing();return false;'><b>reload page</b></a>");
		} else {
			request.setAttribute("err", "There was a problem updating your user settings. "
					+ "Please keep using your old settings until we figure out what went wrong.<br>");
		}
	}

	public void processNotificationsUpdate(HttpServletRequest request) {
		UserData data = (UserData) request.getAttribute("user");
		boolean getNewsletter = Parameter.asBoolean(request, Constants.CHECKBOX_SUBSCRIPTION_NEWSLETTER, false);
		boolean getProductUpdates = Parameter.asBoolean(request, Constants.CHECKBOX_SUBSCRIPTION_PRODUCT_UPDATES,
				false);
		data.setSubscribedForNewsletter(getNewsletter);
		data.setSubscribedToProductUpdates(getProductUpdates);
		updateUsers(request, data);
	}

	public void processSettingsUpdate(HttpServletRequest request) {
		UserData data = (UserData) request.getAttribute("user");
		String err = "";
		String info = "";
		String emailChanged = "";
		String currentDataPwd = data.getPwd();
		String displayName = Parameter.asString(request, Constants.INPUT_SCREEN_NAME, "");
		if (!displayName.equals(data.getScreenName())) {
			if (!displayName.isEmpty() && Validate.isValidScreenName(displayName)) {
				info += "Display name changed<br>";
				data.setScreenName(displayName);
			} else if (!displayName.isEmpty()) {
				err += "Invalid display name. " + Validate.getScreenNameRules() + "<br>";
			}
		}

		String newPwd = Parameter.asString(request, Constants.INPUT_PWD_NAME + "_1", "");
		String newPwdConfirm = Parameter.asString(request, Constants.INPUT_PWD_NAME + "_2", "");

		if (!newPwd.isEmpty() && newPwd.equals(newPwdConfirm)) {
			if (Validate.isValidPassword(newPwd)) {
				info += "Password changed. <br>";
				data.setPwd(newPwd);
			} else {
				err += "New password is invalid: " + Validate.getPasswordRules() + "<br>";
			}
		} else if (!newPwd.isEmpty()) {
			err += "New password and password confirmation don't match. <br>";
		}
		if (data.isOauthUser() && data.isGenerated()) {
			if (UsersTable.setNewPassword(data, newPwd) > 0) {
				info += "<hr><a class='block' href='' onClick='location.assing();return false;'><b>reload page</b></a>";
			}
		} else {
			String newEmail = Parameter.asString(request, Constants.INPUT_EMAIL_NAME + "_1", "");
			if (!newEmail.equalsIgnoreCase(data.getEmail())) {
				String newEmailConfirm = Parameter.asString(request, Constants.INPUT_EMAIL_NAME + "_2", "");
				if (!newEmail.isEmpty() && newEmail.equals(newEmailConfirm)) {
					if (Validate.isValidEmailAddress(newEmail)) {
						UserData testData = UsersTable.get(newEmail);
						if (testData.getUserId() == 0) {
							info += "Email changed. <br>";
							emailChanged = newEmail;
							data.setEmail(emailChanged);
						} else {
							err += "Can't use that email. <br>";
						}
					} else {
						err += "New email is invalid: " + Validate.getEmailRules() + "<br>";
					}
				} else if (!newPwd.isEmpty()) {
					err += "New email and email confirmation don't match. <br>";
				}
			}

			String currentPwd = Parameter.asString(request, Constants.INPUT_PWD_NAME, "");
			if (!currentPwd.isEmpty() && currentPwd.equals(currentDataPwd)) {
				if (UsersTable.update(data) > 0) {
					info += "<hr><a class='block' href='' onClick='location.assing();return false;'><b>reload page</b></a>";
					if (!emailChanged.isEmpty()) {
						UsersTable.unverify(data);
					}
				} else {
					err += "There was a problem updating your user settings. "
							+ "Please keep using your old settings until we figure out what went wrong.<br>";
					info = "";
				}
			} else if (!currentPwd.isEmpty()) {
				info = "";
				err += "Current password didn't match.";
			} else {
				info = "";
				err += "Need your current password";
			}
		}

		request.setAttribute("info", info);
		request.setAttribute("error", err);
		request.setAttribute("emailChanged", emailChanged);
	}%>
<%
    UserData data = (UserData) request.getAttribute("user");
			if (request.getMethod() == "POST") {
				String section = Parameter.asString(request, "section", "");
				if ("notifications".equals(section)) {
					processNotificationsUpdate(request);
				} else if ("settings".equals(section)) {
					processSettingsUpdate(request);
				}
			}
%>
<div class="col-lg-10">
	<div class="col-lg-5">
		<h4>
			<a href="#/v/account">Account settings</a>
		</h4>
		<h4>
			<a href="#/v/pro">Profiles settings</a>
		</h4>
		<h4>
			<a href="#/v/notifications">Notifications</a>
		</h4>
	</div>

	<div class="col-lg-7" id="content_panel">

		<div id="error" class="<c:if test="${empty err}">noshow</c:if> alert alert-danger">
			<button type="button" class="close" onclick="$('#error').hide();">
				<span aria-hidden="true">x</span><span class="sr-only">Close</span>
			</button>
			<p id="error_text">${err}</p>
		</div>

		<div id="info" class="<c:if test="${empty info}">noshow</c:if> alert alert-info alert-dismissible">
			<button type="button" class="close" onclick="$('#info').hide();">
				<span aria-hidden="true">x</span><span class="sr-only">Close</span>
			</button>
			<p id="info_text">${info}</p>
		</div>

		<div id="account">

			<h4>Account settings</h4>

			<form method="POST" action="">
				<input type="hidden" name="section" value="settings" />
				<label for="<%=Constants.INPUT_SCREEN_NAME%>">Display name</label> <input type="text" tabindex="1" class="form-control"
					name="<%=Constants.INPUT_SCREEN_NAME%>" value="<%=data.getScreenName()%>">

				<%
				    if (data.isOauthUser() && data.isGenerated()) {
				%>
				<h4>No password set yet</h4>
				<p>Once you set a password, you can also change your email</p>
				<label for="<%=Constants.INPUT_PWD_NAME + "_1"%>">New password</label>
				<input type="password" tabindex="2" class="form-control" name="<%=Constants.INPUT_PWD_NAME + "_1"%>">
				<label for="<%=Constants.INPUT_PWD_NAME + "_2"%>">Confirm new password</label>
				<input type="password" tabindex="3" class="form-control" name="<%=Constants.INPUT_PWD_NAME + "_2"%>">
				<h4> E-mail <%=data.getEmail()%></h4>
				<%
				    } else {
				%>
				<h4>Change password:</h4>

				<label for="<%=Constants.INPUT_PWD_NAME + "_1"%>">New password</label> <input type="password" tabindex="2" class="form-control"
					name="<%=Constants.INPUT_PWD_NAME + "_1"%>"> <label for="<%=Constants.INPUT_PWD_NAME + "_2"%>">Confirm new password</label> <input
					type="password" tabindex="3" class="form-control" name="<%=Constants.INPUT_PWD_NAME + "_2"%>">

				<h4>Change email:</h4>

				<label for="<%=Constants.INPUT_EMAIL_NAME + "_1"%>">Email <span class="text-info">Changing your email triggers a
						verification email.</span></label> <input type="text" tabindex="4" class="form-control" name="<%=Constants.INPUT_EMAIL_NAME + "_1"%>"
					value="<%=data.getEmail()%>"> <label for="<%=Constants.INPUT_EMAIL_NAME + "_2"%>">Verify email:</label> <input type="text"
					tabindex="5" class="form-control" name="<%= Constants.INPUT_EMAIL_NAME + "_2"%>" value="${emailChanged}">

				<h4>Current password:</h4>

				<label for="<%=Constants.INPUT_PWD_NAME%>">Confirm changes with current password</label> <input type="password" tabindex="6"
					class="form-control" name="<%=Constants.INPUT_PWD_NAME%>">
				<%
				    }
				%>
				<br> <input type="submit" tabindex="7" name="submit" value="Save settings" class="btn btn-primary btn">
			</form>

		</div>
		<div id="pro">
			<h4>User profiles</h4>
			<div id="profile_settings_list"></div>
			<p>
				<a href="" onclick="showCreateNewProfile(); return false;">Add new <span class="glyphicon glyphicon-plus text-right"></span>
				</a>
			</p>
		</div>

		<div id="notifications">
			<h4>Notifications</h4>

			<form method="POST" action="">
				<input type="hidden" name="section" value="notifications" />
				<div class="checkbox">
					<div>
						<label> <input type="checkbox" value="1" <%=checkedIfTrue(data.isSubscribedForNewsletter())%>
							name="<%=Constants.CHECKBOX_SUBSCRIPTION_NEWSLETTER%>">Receive monthly newsletter on everything related to feedrdr
						</label>
					</div>
					<div>
						<label> <input type="checkbox" value="1" <%=checkedIfTrue(data.isSubscribedToUpdates())%>
							name="<%=Constants.CHECKBOX_SUBSCRIPTION_PRODUCT_UPDATES%>">Receive weekly updates on new product updates
						</label>

					</div>
				</div>
				<br> <input type="submit" tabindex="8" name="submit" value="Save settings" class="btn btn-primary btn">
			</form>
		</div>
	</div>
</div>

<%@include file="tmpl/settings.tmpl.jsp"%>

<script type="text/javascript">
	initSettings();

	registerOnProfilesAvailable(function(data) {
		setupProfileView(data);
	});

	console.log("settings route:");
	console.log(r);

	if (r.length == 1) {
		console.log("load default view");
		loadSettingsView("account");
	} else {
		if (r[0] != "v") {
			console.log("unknown route path " + r[0]);
		} else {
			if (r.length > 2) {
				loadSettingsView(r[1], r[2]);
			} else {
				loadSettingsView(r[1]);
			}
		}
	}
</script>

<jsp:include page="footer.jsp"></jsp:include>
