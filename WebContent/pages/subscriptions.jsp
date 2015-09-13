<% request.setAttribute("j", true); %>
<% request.setAttribute("s", true); %>

<jsp:include page="header.jsp"></jsp:include>

<div class="col-xs-12">
    <div class="row">
    	<div class="col-xs-12">
    		<h2>Manage <label id="count"></label> Subscriptions</h2>
    	</div>
    </div>
    <div class="row">
	    <div id="leftpane" class="col-xs-6">
	        <div>
	            <input type="search" id="searchQuery" value="" placeholder="subscription's name">
	            <a href="" onclick="sortBy('s_subs_name', 'asc');return false;">A-Z</a> | 
	            <a href="" onclick="sortBy('s_subs_name', 'desc');return false;">Z-A</a>
	            <br>  
	            <input type="checkbox" onclick="checkFilter();" name="onlyinactive">
	            <span onclick="toggleCheckbox('input[name=onlyinactive]');checkSubsFilter();">Inactive</span>
	            <input type="checkbox" onclick="checkFilter();" name="withoutentries">
	            <span onclick="toggleCheckbox('input[name=withoutentries]');checkSubsFilter();">Without articles</span>
	            <br><br>
	        </div>
	        <div id="all_subscriptions_list"></div>
	        
	    </div>
	    <div id="rightpane" class="col-xs-6">
	        <div>
	            <div id="subscription_details"></div>
	            <div id="profile_stream_groups">
	            </div>
	        </div>
	    </div>
    </div>
</div>

    <jsp:include page="tmpl/subscriptions.tmpl.jsp"></jsp:include>

<script type="text/javascript">
    initSubscriptions();
    
    console.log("subscriptions route ");
    console.log(sr);

    registerOnAllSubscriptionsAvailable(initialRenderAll);

    if (sr.length == 1) 
    {
        console.log("loading default view.");
        registerOnAllSubscriptionsAvailable(loadDefaultView);
    } 
    else if (sr[0] == "v")
    {
        selectedSubscriptionId = sr[1];
        console.log("setting subscription id from view " + selectedSubscriptionId);
        registerOnAllSubscriptionsAvailable(loadSelectedView);
    }
    
    $(document).on('submit', '#subscriptionForm', function() {
        saveSubscription(selectedSubscriptionId, $("input[name='name']").val());
        return false;
    });  
    
    $("#searchQuery").on('keyup', function(e) 
    {    
        if ((e.keyCode || e.which) == 27)  { // ESC
        }
        filterByKeyword(e.target.value);
        runQuery()
    });
    
    apiGetAllSubscriptions();
    
</script>

<jsp:include page="footer.jsp"></jsp:include>
