<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div id="account">

	<form method="POST" class="form form-wide bottom10" action="">

		<div>
			<p class="important pad5">
				Please remember that your email is required to sign in.<br><br>
				We send a new verification code when the email address changes.<br>
			</p>
		</div>
		<div>
			<label for="email">Change email ${user.email} to: <br>
				<input type="text" tabindex="1" name="email" value="">
			</label>
		</div>

		<div>
			<input type="submit" class="min250 max300" tabindex="2" name="submit" value="Save settings">
		</div>

	</form>

	<hr class="hr-separator">

	<form method="POST" class="form form-wide" action="">

		<c:choose>
			<c:when test="${user.generated}">
				<c:set var="pwdButton" value="Set password" />
				<div> E-mail ${user.email}</div>
			</c:when>
			<c:otherwise>
				<div>
					<label for="confirm">Current password<br>
						<input type="password" tabindex="5"  name="current">
					</label>
				</div>
				<c:set var="pwdButton" value="Change password" />
				<div>
					<label for="-1">New password<br>
						<input type="password" tabindex="3"  name="pwd">
					</label>
				</div>
				<div>
					<label for="confirm">Confirm new password<br>
						<input type="password" tabindex="4"  name="confirm">
					</label>
				</div>
			</c:otherwise>
		</c:choose>
		<div>
			<input type="submit" class="min250 max300" tabindex="6" name="submit" value="${pwdButton}">
		</div>

	</form>

</div>


