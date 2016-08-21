<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div id="notifications">
	<form class="form" method="POST" action="">
		<input type="hidden" name="section" value="notifications" />
		<div class="x-spacing">
			<div>
				<label>
				<input type="checkbox" value="1" name="newsletter"
					<c:if test="${user.subscribedForNewsletter}">
					checked
					</c:if>> Monthly newsletter
				</label>
			</div>
			<div>
				<label>
				<input type="checkbox" value="1" name="updates"
					<c:if test="${user.subscribedToUpdates}">
					checked
					</c:if>>
					Weekly product updates (<a href="https://blog.feedrdr.co">see our Blog</a>")</label>
			</div>
		</div>
		<div>
			<input type="submit" tabindex="8" name="submit" value="Save settings" class="btn btn-primary btn">
		</div>
	</form>
</div>