<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div id="account">

	<form method="POST" class="form" ction="">

		<c:choose>
			<c:when test="${user.oauthUser}">
				<div> E-mail ${user.email}</div>
			</c:when>
			<c:otherwise>

		<div class="group">Change password:</div>

		<label for="-1">New password
			<input type="password" tabindex="2"  name="-1">
		</label>

		<label for="-confirm">Confirm new password
			<input type="password" tabindex="3"  name="-confirm">
		</label>

		<div class="group">Change email:</div>

		<label for="email">Email
			<span class="text-info">Changing your email triggers a verification email.</span>
			<input type="text" tabindex="4"  name="email" value="${user.email}">
		</label>


		<label for="verify-email">Verify email:
			<input type="text" tabindex="5" name="verify-email" value="${emailChanged}">
		</label>

		<div class="group">Current password:</div>

		<label for="user-name">Confirm changes with current password
			<input type="password" tabindex="6"  name="user-name">
		</label>

			</c:otherwise>
		</c:choose>

		<input type="submit" tabindex="7" name="submit" value="Save settings">

	</form>

</div>


