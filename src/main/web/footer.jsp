	<div class="footer">
		<ul>
			<li><a href="http://github.com/sliechti/feedrdr">GitHub</a></li>
			<li><a href="https://twitter.com/feedrdrco">Twitter</a></li>
			<li><a href="https://www.facebook.com/feedrdr">Facebook</a></li>
			<li><a href="https://blog.feedrdr.co">Blog</a></li>
		</ul>
	</div>

	<div id="fb-root"></div>
	<script>
		(function(d, s, id) {
			var js, fjs = d.getElementsByTagName(s)[0];
			if (d.getElementById(id))
				return;
			js = d.createElement(s);
			js.id = id;
			js.src = "//connect.facebook.net/en_US/sdk.js#xfbml=1&version=v2.3&appId=1587611758138796";
			fjs.parentNode.insertBefore(js, fjs);
		}(document, 'script', 'facebook-jssdk'));
	</script>

</body>


</html>

<script>
	$(document).ready(function() {
		$('#email').focus();
		fbq('track', 'ViewContent');
	});
</script>
<script>
	!function(d, s, id) {
		var js, fjs = d.getElementsByTagName(s)[0], p = /^http:/.test(d.location) ? 'http' : 'https';
		if (!d.getElementById(id)) {
			js = d.createElement(s);
			js.id = id;
			js.src = p + '://platform.twitter.com/widgets.js';
			fjs.parentNode.insertBefore(js, fjs);
		}
	}(document, 'script', 'twitter-wjs');
</script>

<script type="text/javascript">
	initHello('${oauthFacebooKey}', '${oauthGoogleKey}', '${oauthWindowsKey}');
	setHelloCallbacks(function(data) {
		console.log("on session ");
		console.log(data);
	}, function(data) {
		var ext = '${oauthDebug}';
		location.href = 'login/oauth.jsp?network=' + data.network + "&token=" + data.authResponse.access_token + ext;
	}, function(data) {
	});
</script>

<c:if test="${not isLocal}">
<script type="text/javascript">
	var _paq = _paq || [];
	_paq.push([ 'trackPageView' ]);
	_paq.push([ 'enableLinkTracking' ]);
	(function() {
		var u = "//feedrdr.piwikpro.com/";
		_paq.push([ 'setTrackerUrl', u + 'piwik.php' ]);
		_paq.push([ 'setSiteId', 1 ]);
		var d = document, g = d.createElement('script'), s = d
				.getElementsByTagName('script')[0];
		g.type = 'text/javascript';
		g.async = true;
		g.defer = true;
		g.src = u + 'piwik.js';
		s.parentNode.insertBefore(g, s);
	})();
</script>
<noscript>
	<p>
		<img src="//feedrdr.piwikpro.com/piwik.php?idsite=1" style="border: 0;" alt="" />
	</p>
</noscript>
</c:if>
