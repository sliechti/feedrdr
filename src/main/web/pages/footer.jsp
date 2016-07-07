<%@page import="feedreader.config.Environment"%>
<%@page import="feedreader.entities.ProfileData"%>
<%@page import="feedreader.entities.UserData"%>
<%@page import="feedreader.oauth.OAuthType"%>
<%@page import="feedreader.utils.PageUtils"%>

<%
UserData user = (UserData)request.getAttribute("user");
ProfileData profile = (ProfileData)request.getAttribute("profile");
%>

</div>
<!--row-->
</div>
<!--content-->

<!-- Modal -->
<div class="modal" id="modalBox" tabindex="-1" role="dialog" aria-labelledby="modalTitle" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" onclick="hideModal()">
            <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
        </button>
        <h4 class="modal-title" id="modalTitle">Title</h4>
      </div>
        <div id="modalContent"></div>
    </div>
  </div>
</div>

<!-- Piwik -->
<script type="text/javascript">
    var _paq = _paq || [];
    _paq.push(['trackPageView']);
    _paq.push(['enableLinkTracking']);
    (function() {
    var u="//feedrdr.piwikpro.com/";
    _paq.push(['setTrackerUrl', u+'piwik.php']);
    _paq.push(['setSiteId', 1]);
    var d=document, g=d.createElement('script'), s=d.getElementsByTagName('script')[0];
    g.type='text/javascript'; g.async=true; g.defer=true; g.src=u+'piwik.js'; s.parentNode.insertBefore(g,s);
    })();
    </script>
<noscript><p><img src="//feedrdr.piwikpro.com/piwik.php?idsite=1" style="border:0;" alt="" /></p></noscript>
<!-- End Piwik Code -->

<script type="text/javascript">
    initProfiles();

    registerOnProfilesAvailable(function() {
        selectProfile(<%= profile.getProfileId() %>);
    });

    $(document).ready(function() {
        if (screen.width < 1200) {
            $(".container").removeClass("container").addClass("container-fluid");
        }
        if (screen.width < 768) {
            $("#content").css("margin-top", "220px");
        }
        $("#footer_spacing").css("height", screen.height - 100);
        $(document).scroll( function() {
        	$(window).scrollTop(0);
        	$(document).unbind("scroll");
        });
    });

    if ($("#smallMenu").visible()) {
    	$("#homeIcon").html("&nbsp;&nbsp;&nbsp;");
    	$("#homeIcon").removeClass();
    }
</script>
`
<script type="text/javascript">
// Include the UserVoice JavaScript SDK (only needed once on a page)
UserVoice=window.UserVoice||[];(function(){var uv=document.createElement('script');uv.type='text/javascript';uv.async=true;uv.src='//widget.uservoice.com/dlGOwxgwLgBpAm2HWZhVOA.js';var s=document.getElementsByTagName('script')[0];s.parentNode.insertBefore(uv,s)})();

UserVoice.push(['set', {
  accent_color: '#6aba2e',
  trigger_color: 'white',
  trigger_background_color: '#6aba2e'
}]);


UserVoice.push(['identify', {
  email:      '<%= user.getEmail() %>',
  name:       '<%= user.getScreenName() %>',
  created_at: <%= user.getSubscribedAt() %>
}]);

// Or, use your own custom trigger:
UserVoice.push(['addTrigger', '#feedback']);

UserVoice.push(['autoprompt', {}]);

UserVoice = window.UserVoice || [];
function showClassicWidget() {
  UserVoice.push(['showLightbox', 'classic_widget', {
    mode: 'feedback',
    primary_color: '#cc6d00',
    link_color: '#007dbf',
    forum_id: 304969
  }]);
}
</script>

</body>
</html>


