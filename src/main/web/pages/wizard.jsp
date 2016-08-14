<%
// how to add feeds step: show images of locations.
// would be cool if /blog could be added as a news source.
 %>
 <jsp:include page="header.jsp">
	<jsp:param value="title" name="Welcome Wizard"/>
 </jsp:include>
<!-- Facebook Pixel Code -->
<script>
!function(f,b,e,v,n,t,s){if(f.fbq)return;n=f.fbq=function(){n.callMethod?
n.callMethod.apply(n,arguments):n.queue.push(arguments)};if(!f._fbq)f._fbq=n;
n.push=n;n.loaded=!0;n.version='2.0';n.queue=[];t=b.createElement(e);t.async=!0;
t.src=v;s=b.getElementsByTagName(e)[0];s.parentNode.insertBefore(t,s)}(window,
document,'script','https://connect.facebook.net/en_US/fbevents.js');

fbq('init', '504829576373713');
fbq('track', "PageView");</script>
<noscript><img height="1" width="1" style="display:none"
src="https://www.facebook.com/tr?id=504829576373713&ev=PageView&noscript=1"
/></noscript>
<!-- End Facebook Pixel Code -->
</head>
<div class="center content-width">
<div id="wizard-content" class="form form-wide">

	<div class="password-setup">
		<div>
			<h3>Welcome to feedrdr!</h3>
			<p>Let's get you started. You only need to set-up a password and
			add some feeds.</p>
		</div>
		<form id="password-form" class="form" method="post" onsubmit="return false;">
			<div class="hide msg msg-info" id="password-msg"></div>
			<div>
				<input tabindex="1" type="password" name="pwd1" placeholder="password">
			</div>
			<div>
				<input tabindex="2" type="password" name="pwd2" placeholder="confirm your password">
			</div>
			<div>
				<button tabindex="3" id="btn-pwd-change" class="submit-btn min250 max300"
					onclick="changePassword(this, $('#password-form'))">Set-up password</button>
			</div>
		</form>
	</div>

	<div class="thanks hide">
		<h3>You are all set</h3>
		<p>
			We really hope you enjoy feedrdr as much as we loved creating it.
		</p>
		<p>
			We are adding something new every week so make sure to follow us on
			<a href="https://www.facebook.com/feedrdr">Facebook</a>,
			<a href="https://twitter.com/feedrdrco">Twitter</a> and our
			<a href="https://blog.feedrdr.co/">Blog</a>
		</p>
		<div>
			<button tabindex="3" id="btn-pwd-change"
				class="submit-btn min250 max300"
				onclick="startReading()">
				Start reading</button>
		</div>
	</div>

	<div>&nbsp;</div>

</div>
</div>

<script>
	if (fbq) {
	fbq('track', 'Lead');
		}
	function startReading() {
		if (fbq) {
			fbq('track', 'CompleteRegistration');
		}
		location.href='${baseUrl}/reader';
	}
</script>

</body>
</html>
