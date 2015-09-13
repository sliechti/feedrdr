var subscriptionsHome = '';

var subscriptions = [];
var subscriptionsIdx = [];

var filteredSubscriptions = [ {
	"l_subs_id" : 0,
	"s_subs_name" : 0
} ];
var selectedSubscriptionId = 0;

var allSubscriptionsTmpl = {};
var subscriptionDetailsTmpl = {};
var profileStreamGrpTmpl = {};

var jlinqQuery = {};

var query = {
	"sortBy" : "s_subs_name",
	"showOnlyInactive" : false,
	"showZeroSources" : false,
	"keyword" : ""
};

var onAllSubscriptionsAvailableListeners = [];

var sr = {};

function initSubscriptions() {
	setupTemplates();

	var router = new Router().init();
	router.on('/view/:id', showSubscriptionDetails);
	sr = router.getRoute();
}

function setupTemplates(){
	susbcriptionsHome = baseUrl + "/pages/susbcriptions.jsp";
	allSubscriptionsTmpl = Handlebars.compile($("#all_subscriptions_tmpl").html());
	subscriptionDetailsTmpl = Handlebars.compile($("#subscription_details_tmpl").html());
	profileStreamGrpTmpl = Handlebars.compile($("#profile_stream_groups_tmpl").html());
}

function initialRenderAll() {
	query.keyword = $("#searchQuery").val();
	query.showOnlyInactive = $("input[name=onlyinactive]").prop('checked');
	runQuery(true);
}

function registerOnAllSubscriptionsAvailable(listener) {
	onAllSubscriptionsAvailableListeners.push(listener);
}

function triggerOnAllSubscriptionsAvailable() {
	for (var i = 0; i < onAllSubscriptionsAvailableListeners.length; i++) {
		onAllSubscriptionsAvailableListeners[i](subscriptions);
	}
}

function checkFilter() {
	query.showOnlyInactive = $("input[name=onlyinactive]").prop('checked');
	query.showZeroSources = $("input[name=withoutentries]").prop('checked');
	runQuery(true);
}

function sortBy(column, direction) {
	if (direction == "desc")
		column = "-" + column;
	query.sortBy = column;
	runQuery(true);
}

function filterByKeyword() {
	var i = $("#searchQuery");
	query.keyword = i.val();
	runQuery(true);
}

function runQuery(renderTmpl) {
	jlinqQuery = jlinq.from(subscriptions).sort(query.sortBy);
	if (query.showOnlyInactive) {
		jlinqQuery.equals("b_gaveup", true);
	}
	if (query.showZeroSources) {
		jlinqQuery.equals("i_total_entries", 0);
	}

	if (query.keyword.length > 0) {
		jlinqQuery.contains("s_subs_name", query.keyword);
	}

	var filtered = jlinqQuery.select();

	if (renderTmpl)
		renderAllSubscriptions(filtered);

	if (filtered.length > 0) {
		showSubscriptionDetails(filtered[0].l_subs_id);
	} else {
		showSubscriptionDetails(0);
	}

}

function renameSubscription(id, name) {
	for (var i = 0; i < subscriptions.length; i++) {
		if (subscriptions[i].l_subs_id == id) {
			subscriptions[i].s_subs_name = name;
			break;
		}
	}
	runQuery(true);
}

function saveSubscription(id, name, callback) {
	var queryData = {};
	queryData.id = id;
	queryData.n = name;

	$.getJSON(baseUrl + '/api/v1/user/subscriptions/set', queryData, function(data) {
		if (callback) {
			return callback(data);
		}

		if (data && data.count > 0) {
			//location.reload();
			renameSubscription(id, name);
		} else {
			console.log("error ");
			console.log(data);
		}
	});
}

function loadDefaultView() {
	if (subscriptions && subscriptions.length > 0) {
		showSubscriptionDetails(subscriptions[0].l_subs_id);
	}
}

function loadSelectedView() {
	showSubscriptionDetails(selectedSubscriptionId);
}

function renderAllSubscriptions(data) {
	$("#all_subscriptions_list").html(allSubscriptionsTmpl({
		"subscriptions" : data
	}));
	$("#count").text(data.length);
}

function showSubscriptionDetails(subsId) {
	$("#profile_stream_groups").html('');

	queryData = {};
	queryData.id = subsId;

	$.getJSON(baseUrl + '/api/v1/user/subscriptions/get', queryData, function(data) {
		$("#subscription_details").html(subscriptionDetailsTmpl(data));
	});

	getSubscriptionStreamProfiles(subsId);
}

function apiGetAllSubscriptions(callback) {
	if (callback) {
		registerOnAllSubscriptionsAvailable(callback);
	}

	$.getJSON(baseUrl + '/api/v1/user/subscriptions/list', {}, function(data) {
		subscriptions = data.entries;
		for (var i = 0; i < subscriptions.length; i++) {
			subscriptionsIdx.push(subscriptions[i].l_xml_id);
		}
		triggerOnAllSubscriptionsAvailable();
	});
}

function getSubscriptionStreamProfiles(subsId) {
	if (!subsId) {
		return;
	}

	var queryData = {};
	queryData.id = subsId;

	$.getJSON(baseUrl + '/api/v1/user/subscriptions/withprofile', queryData, function(data) {
		$("#profile_stream_groups").html("");

		var streamWithProfiles = {};

		data.forEach(function(e, i, a) {
			if (!streamWithProfiles[e.s_stream_name]) {
				streamWithProfiles[e.s_stream_name] = {};
				streamWithProfiles[e.s_stream_name].l_stream_id = e.l_stream_id;
				streamWithProfiles[e.s_stream_name].profiles = new Array();
			}

			streamWithProfiles[e.s_stream_name].profiles.push({
				"l_profile_id" : e.l_profile_id,
				"s_profile_name" : e.s_profile_name
			});
		});

		for (k in streamWithProfiles) {
			$("#profile_stream_groups").append(profileStreamGrpTmpl({
				"name" : k,
				"streamid" : streamWithProfiles[k].l_stream_id,
				"subid" : subsId,
				"profiles" : streamWithProfiles[k].profiles
			}));
		}
	});
}

function getAllStreamSubscriptions(streamId, callback) {
	var queryData = {};
	queryData.sid = streamId;

	$.getJSON(baseUrl + '/api/v1/user/subscriptions/list', queryData, function(data) {
		callback(data);
	});
}

function addSubscriptionToNewStream(profileId) {
}

function removeFromStreamGroup(subsId, streamId) {

	var queryData = {};
	queryData.sui = subsId;
	queryData.sid = streamId;

	$.getJSON(baseUrl + '/api/v1/user/subscriptions/removefromstream', queryData, function(data) {
		if (data.count > 0) {
			showSubscriptionDetails(subsId);
		} else {
			console.error("error ");
			console.error(data);
		}
	});

}

function removeStreamFromProfile(profileId, streamId) {
	var queryData = {};
	queryData.pid = profileId;
	queryData.sid = streamId;

	//    $.getJSON(baseUrl + '/api/v1/user/streams/delete', queryData, function(data) {
	//        console.log(data);
	//        
	//        if (data.count > 0) {
	//            $("#span_" + profileId + "_" + streamId).remove();
	//        } else {
	//            console.error("error ");
	//            console.error(data);
	//        }
	//    });    
}

function removeFromSubscriptionsArray(name, val) {
	for (var i = 0; i < subscriptions.length; i++) {
		if (subscriptions[i][name] == val) {
			console.log("removed from subscriptions " + name + "=" + val + "@idx " + i);
			subscriptions.splice(i, 1);
			return;
		}
	}
}

function removeSubscription(subsId) {
	console.log("remove subscription " + selectedSubscriptionId);

	var queryData = {};
	queryData.sid = subsId;

	$.getJSON(baseUrl + '/api/v1/user/subscriptions/remove', queryData, function(data) {
		if (data.count >= 0) {
			removeFromSubscriptionsArray("l_subs_id", subsId);
			runQuery(true, true);
		} else {
			console.error("error removing subscription id " + subsId);
			console.error(data);
		}
	});
}
