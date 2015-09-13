var streamSubscriptions = [];
// keyed by subscription id
var namedStreamSubscriptions = {};

var apiSubscriptionAddToStream = "/api/v1/user/subscriptions/addtostream";
var apiSubscriptionRemoveFromStream = "/api/v1/user/subscriptions/removefromstream";

var queryAll = {
	"sortBy" : "s_subs_name",
	"keyword" : ""
};

var querySubs = {
	"sortBy" : "s_subs_name",
	"keyword" : ""
};

var idSearchAll = "searchQueryAll";
var idSearchSubs = "searchQuerySubs";

var allSubsTmpl;
var subsTmpl;

function initReaderSubscriptions() {
	allSubsTmpl = Handlebars.compile($("#all_subscriptions_tmpl").html());
	subsTmpl = Handlebars.compile($("#subscribed_tmpl").html());

	$("#" + idSearchAll).on('keyup', function(e) {
		if ((e.keyCode || e.which) == 27) { // ESC
			// cancelStreamGroup();
		}
		filterByKeywordAll(e.target.value);
		runQuery()
	})

	$("#" + idSearchSubs).on('keyup', function(e) {
		if ((e.keyCode || e.which) == 27) { // ESC
			// cancelStreamGroup();
		}
		filterByKeywordSubs(e.target.value);
		runQuery()
	});

	fetchAllSubscriptions();
}

function renderAllReaderSubscriptions(data) {
	$("#all_count").text(data.length);
	$("#all_subscriptions").html(allSubsTmpl({
		"subscriptions" : data
	}));
}

function clearStreamSubscriptions() {
	while (streamSubscriptions.length > 0) {
		streamSubscriptions.pop();
	}
	while (namedStreamSubscriptions.length > 0) {
		namedStreamSubscriptions.pop();
	}
}

function fetchAllStreamSubscriptions(streamId, callback) {

	getAllStreamSubscriptions(streamId, function(data) {
		clearStreamSubscriptions();
		$("#stream_count").text(data.entries.length);

		if (data && data.entries) {
			streamSubscriptions = data.entries;

			data.entries.forEach(function(e, i, a) {
				namedStreamSubscriptions['s' + e.l_subs_id] = {};
				namedStreamSubscriptions['s' + e.l_subs_id].name = e.s_subs_name;
				namedStreamSubscriptions['s' + e.l_subs_id].xml_id = e.l_xml_id;
			});
		}

		if (callback)
			callback(data);
	});
}

function renderReaderSubscriptions(data) {
	$("#stream_count").text(data.length);
	$("#subscribed").html("");
	$("#subscribed").html(subsTmpl({
		"subscriptions" : data
	}));
}

function fetchAllSubscriptions(callback) {
	apiGetAllSubscriptions(function(data) {
		$("#all_count").text(subscriptions.length);
		// data = subscriptions, also loaded in the subscriptions.js file.
		for (var i = subscriptions.length - 1; i >= 0; i--) {
			if (namedStreamSubscriptions['s' + subscriptions[i].l_subs_id]) {
				subscriptions.splice(i, 1);
			}
		}

		if (callback)
			callback(data);
	});
}

function renderSubscriptions() {
	filterByKeywordAll();
	filterByKeywordSubs();
}

function removeSubscription(id) {
	var queryData = {};
	queryData.sid = selectedStream.l_stream_id;
	queryData.sui = id;

	$.getJSON(baseUrl + apiSubscriptionRemoveFromStream, queryData, function(data) {
		if (data.count > 0) {
			$("#subscription_r_" + id).remove();
			$("#subscription_r_" + id).empty();
			$("#reload").show();
		} else {
			console.error(data);
		}
	});
}

function addSubscription(id) {
	var queryData = {};
	queryData.sid = selectedStream.l_stream_id;
	queryData.sui = id;

	$.getJSON(baseUrl + apiSubscriptionAddToStream, queryData, function(data) {
		if (data.count > 0) {
			$("#subscription_l_" + id).remove();
			$("#subscription_l_" + id).empty();
			$("#reload").show();
		} else {
			console.error(data);
		}
	});
}

function filterByKeywordAll(key) {
	queryAll.keyword = (key) ? key : $("#" + idSearchAll).val();
	runReaderQuery(subscriptions, queryAll, true);
}

function filterByKeywordSubs(key) {
	querySubs.keyword = (key) ? key : $("#" + idSearchSubs).val();
	runReaderQuery(streamSubscriptions, querySubs, false);
}

function runReaderQuery(data, query, allTmpl) {
	var jlinqQuery = jlinq.from(data).sort(query.sortBy);

	if (query.keyword && query.keyword.length > 0) {
		jlinqQuery.contains("s_subs_name", query.keyword);
	}

	if (allTmpl) {
		renderAllReaderSubscriptions(jlinqQuery.select());
	} else {
		renderReaderSubscriptions(jlinqQuery.select());
	}
}
