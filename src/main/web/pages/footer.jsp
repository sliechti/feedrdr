</body>
</html>

<script type="text/javascript">
    initProfiles();

    registerOnProfilesAvailable(function() {
        selectProfile(${profile.profileId});
    });

    $(document).ready(function() {
        $("#footer_spacing").css("height", screen.height - 100);
        $(document).scroll( function() {
        	$(window).scrollTop(0);
        	$(document).unbind("scroll");
        });
    });
</script>