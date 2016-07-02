var MAX_INPUT_SIZE = 50;
var CONTENT_MAX_LEN = 500;
var NOCACHE = 0;

var VIEW_LIST = 1;
var VIEW_MAGAZINE = 2;
var VIEW_STREAM = 3;

var TEMPLATE_ID_STREAM = "stream";
var TEMPLATE_ID_ALL = "all";
var TEMPLATE_ID_RECENTLY_READ = "rr";
var TEMPLATE_ID_SAVED = "saved";
var TEMPLATE_ID_SOURCE = "source";

var FILTER_SHOW_ALL = 0;
var FILTER_BY_UNREAD = 1;

var SORT_NEW_FIRST = 0;
var SORT_OLD_FIRST = 1;

var ROUTE_FEED = "f";
var ROUTE_FEED_SOURCE = "s";
var ROUTE_VIEW = "v";
var ROUTE_ALL = "a";
var ROUTE_SAVED = "s";
var ROUTE_RECENTLY_READ = "r";

var apiCurrentStreamsFeed = ';'
var apiUrlStreamsFeedAll = '/api/v1/user/feeds/all';
var apiUrlStreamsFeedRecent = '/api/v1/entries/recently_read';
var apiUrlStreamsFeedSaved = '/api/v1/entries/saved';
var apiUrlStreamsFeed = '/api/v1/user/streams/feed';
var apiUrlMarkRead = '/api/v1/entries/read';
var apiUrlStreamUpdate = '/api/v1/user/streams/update';
var apiUrlStreamDelete = '/api/v1/user/streams/delete';
var apiUrlStreamsList = '/api/v1/user/streams/list';
var apiUrlStreamsMarkAllRead = '/api/v1/user/streams/allread';
var apuUrlStreamsUnreadCount = '/api/v1/user/streams/unreadcount';
var apiUrlSaveReaderSettings = '/api/v1/user/settings/reader';
var apiUrlSaveModulesSettings = '/api/v1/user/settings/modules';
var apiUrlEntriesData = '/api/v1/user/feeds/data';
var apiUrlSourcesGet = '/api/v1/sources/get';
var apiUrlSubscriptionsAdd = '/api/v1/user/subscriptions/add';
var apiUrlRecentlyReadClear = '/api/v1/entries/clear_recently_read';
var apiUrlEntriesEntry = '/api/v1/entries/entry';
var apiUrlStreamsRename = '/api/v1/user/streams/rename';
var apiUrlEntriesUpdate = '/api/v1/entries/update';
var apiUrlSubscriptionsGet = '/api/v1/user/subscriptions/get';
var apiUrlSourceId = '/api/v1/user/feeds/sourceid';
var apiUrlStreamsAdd = '/api/v1/user/streams/add';
var baseFavicoDomain = 'http://www.google.com/s2/favicons?domain=';

var streamId = 0;
var streamDir = 0;
var entriesApi = '';
var readerHome = '';
var profileId = 0;

var tmplOptions = {
	"id" : "",
	"showIco" : true,
	"showSource" : true,
	"sourceLen" : 12
};

var streamGroups = {};
var filteredStreamGroups = streamGroups;

var selectedStream = {};
var streamEntries = {};
var moreAvailable = true;

var currentView = 1;

var streamGroupsTmpl;
var streamGroupsSmallTmpl;
var streamGroupHeaderTmpl;
var sourceHeaderTmpl;
var recentlyReadHeaderTmpl;
var simpleViewHeaderTmpl;
var contentAllRead = {};

var entriesTmpl;
var viewLineTmpl;
var viewMagTmpl;
var viewNewsTmpl;
var pageUnknownTmpl;

var streamPage = 0;
var entriesPerPage = 0;
var pendingRequest = false;
var topUnread = undefined;

var lastRead = undefined;

var queryGroups = {
	showUnreadOnly : false,
	sortByAlphabet : 0,
	sortByUnread : 0
}

var renderTime = new Date().getTime();

var onEntriesLoadedListeners = [];
var onViewChangedListeners = [];

var router = {};

function initReader() {
	readerHome = baseUrl + "/pages/reader.jsp";
	setupTemplates();
	setupRoutes();
	tmplOptions.id = TEMPLATE_ID_STREAM;
	initReaderSubscriptions();
	setupEvents();
}

function setupEvents() {
	window.onscroll = function(obj) {
		if ($("#stream_more").visible()) {
			loadMore();
		}

		if (selectedStream.h_filter_by != FILTER_BY_UNREAD) {
			return;
		}

		if (!topUnread) {
			return;
		}

		var o = topUnread.get(0);
		if (typeof o.getBoundingClientRect === 'function') {
			// Use this native browser method, if available.
			var box = o.getBoundingClientRect();
			if (box.top < 70) {
				if (lastRead != topUnread) {
					markRead(topUnread.get(0));
					lastRead = topUnread;
				}

				var next = topUnread.next(".news");
				if (next.length == 1) {
					lastTopY = box.top;
					topUnread = next;
				}
			}
		}
	};

	registerOnEntriesLoadedListeners(function(data) {
		if (!data || !data.entries) {
			return;
		}
		updateNewsListView();
	});
}

function setupTemplates() {

	Handlebars.registerPartial("header_right_tools", $("#header_right_tools").html());

	// TODO Compile only when used for the first time.
	streamGroupsTmpl = Handlebars.compile($('#stream_groups_tmpl').html());
	streamGroupsSmallTmpl = Handlebars.compile($('#stream_groups_small_tmpl').html());

	viewLineTmpl = Handlebars.compile($("#news_line_tmpl").html());
	viewMagTmpl = Handlebars.compile($("#news_mag_tmpl").html());
	viewNewsTmpl = Handlebars.compile($("#news_stream_tmpl").html());
	entriesTmpl = viewLineTmpl;

	sourceHeaderTmpl = Handlebars.compile($("#source_header_tmpl").html());
	recentlyReadHeaderTmpl = Handlebars.compile($("#recently_read_header_tmpl").html());
	simpleViewHeaderTmpl = Handlebars.compile($("#simple_view_header_tmpl").html());

	contentAllRead = Handlebars.compile($("#content_all_read").html());
	contentEmptySource = Handlebars.compile($("#content_empty_source").html());
	
	pageUnknownTmpl = Handlebars.compile($("#content_unknown").html());
}

function setupRoutes() {
	router = new Router().init();
	router.on('/f/:feedid', loadStream);
	router.on('/r/:feedid', reloadStream);
	router.on('/v/a', loadAll);
	router.on('/v/r', loadRecentlyRead);
	router.on('/v/s', loadSaved);
	router.on('/s/:sourceid', loadSource);
}

function apiMarkRead(entries, callback) {
	var queryData = {};
	queryData.e = entries;
	queryData.sid = selectedStream.l_stream_id;
	queryData.c = selectedStream.l_gr_unread;

	$.get(baseUrl + apiUrlMarkRead, queryData, function(data, status) {
		if (callback) {
			callback(data, status);
		}
	});
}

function setProfile(obj) {
	this.profileId = obj.profileId;
	queryGroups.showUnreadOnly = obj.settings.showUnreadOnly;
	if (obj.settings.sortAz == 1 || obj.settings.sortAz == 2) {
		queryGroups.sortByAlphabet = obj.settings.sortAz;
	}
	if (obj.settings.sortUnread == 1 || obj.settings.sortUnread == 2) {
		queryGroups.sortByUnread = obj.settings.sortUnread;
	}
}

function setEntriesPerPage(len) {
	entriesPerPage = len;
}

/**
 * Register callback to be called when #triggerOnEntriesLoadedListeners is called.
 * 
 * @param callback
 */
function registerOnEntriesLoadedListeners(callback) {
	onEntriesLoadedListeners.push(callback);
}

/** 
 * Called by the different load* methods after the API call requesting entries finishes.
 * 
 * @see #loadAll
 * @see #loadEntries
 */
function triggerOnEntriesLoadedListeners(data) {
	for (var i = 0; i < onEntriesLoadedListeners.length; i++) {
		onEntriesLoadedListeners[i](data);
	}
}

/**
 * Register callback to be triggered when triggerChangeViewListeners is called.
 */
function registerChangeViewListener(listener) {
	onViewChangedListeners.push(listener);
}

/**
 * Triggered whenever the news list view is updated.
 */
function triggerChangeViewListeners(view) {
	for (var i = 0; i < onViewChangedListeners.length; i++) {
		onViewChangedListeners[i](view);
	}
}


function displayStreamHeader() {
	if (!streamGroupHeaderTmpl) {
		streamGroupHeaderTmpl = Handlebars.compile($("#stream_group_header_tmpl").html());
	}

	$("#stream_group_header").html(streamGroupHeaderTmpl(selectedStream));
	$("#txt_stream_rename").keyup(function(e) {
		if ((e.keyCode || e.which) == 13) {
			renameStreamGroup(); // enter
		}
		if ((e.keyCode || e.which) == 27) {
			$("#div_stream_rename").hide();
			$("#span_stream_rename").show();
		}
	});
}

function showOnlyWithUnread(yesNo) {
	queryGroups.showUnreadOnly = yesNo;
	runQuery();
	saveReaderSettings();
}

function sortByAlphabet(val) {
	queryGroups.sortByAlphabet = val;
	queryGroups.sortByUnread = 0;
	runQuery();
	saveReaderSettings();
}

function sortByUnread(val) {
	queryGroups.sortByUnread = val;
	queryGroups.sortByAlphabet = 0;
	runQuery();
	saveReaderSettings();
}

function runQuery() {
	var j = jlinq.from(streamGroups);

	if (queryGroups.showUnreadOnly) {
		j.greater("l_gr_unread", 0);
	}

	if (queryGroups.sortByAlphabet != 0) {
		j.sort(((queryGroups.sortByAlphabet == 1) ? "-" : "") + "s_stream_name");
	}

	if (queryGroups.sortByUnread != 0) {
		j.sort(((queryGroups.sortByUnread == 1) ? "-" : "") + "l_gr_unread");
	}

	filteredStreamGroups = j.select();
	renderStreamGroups(filteredStreamGroups);
}

function renderStreamGroups(data) {
	if ($("#menusubs").is(":hidden")) {
		$("#small_menusubs").html(streamGroupsSmallTmpl({"groups" : data}));
	} else {
		$("#menusubs").html(streamGroupsTmpl({"groups" : data}));
	}
	selectDom("e_" + selectedStream.l_stream_id);
}

/** 
 * @param streamGroupList list of stream groups to get the unread count.
 * @param nocache 1 = calculate again, 0 = get from from cache.
 * @param callback callback to call when query finishes.
 */
function getUnreadCount(streamGroupList, nocache, callback) {
	var ids = '';
	for (var i = 0; i < streamGroupList.length; i++) {
		ids += streamGroupList[i].l_stream_id + ",";
	}

	if (ids.length == 0) {
		if (callback) {
			callback()
		} else {
			return;
		}
	}

	if (nocache == undefined) {
		nocache = 0;
	}
	
	var queryData = {};
	queryData.sid = ids.slice(0, -1);
	queryData.nc = nocache;

	$.getJSON(baseUrl + apuUrlStreamsUnreadCount, queryData, function(data, status) {
		for (var x = 0; x < streamGroups.length; x++) {
			for (var i = 0; i < data.entries.length; i++) {
				if (streamGroups[x].l_stream_id == data.entries[i].id) {
					streamGroups[x].l_gr_unread = data.entries[i].count;
					break;
				}
			}
		}
		runQuery();
		
		if (callback != undefined) {
			callback();
		}
	});
}

function refreshUnread() {
	getUnreadCount(streamGroups, 1);
}

function getStreamGroups(callback) {
	var queryData = {};
	queryData.views = true;
	
	$.getJSON(baseUrl + apiUrlStreamsList, queryData, function(data) {
		streamGroups = data;
		getUnreadCount(streamGroups, 0, function() {
			if (callback) {
				callback(streamGroups, filteredStreamGroups);
			}
		});
	});
}

function closeAllHeaderTools() {
	$("#header_subscriptions").css("display", "none");
	return false;
}

// Stream rename and subscriptions rename work same. Candidates to create a little rename tool.
function showRename(partialSelector) {
	$("#span_" + partialSelector).hide();
	$("#div_" + partialSelector).show();
	input = $("#txt_" + partialSelector);
	txt = input.val();
	input.val('');
	input.focus();
	input.val(txt);
}

function toggleHeaderTool(name) {
	switch (name) {
	case "subscriptions":
		$("#header_subscriptions").toggle();
		renderSubscriptions();
		break;
	}
}

function showImport() {
	showModal("Add new feed to '" + selectedStream.s_stream_name + "'", "#div_add_subscription", 
			function closeImportModal() { /* noop */ }
	);
}

function importSingleFeed() {
	var queryData = {};
	queryData.n = $("input[name=sName]:visible").val();
	queryData.u = $("input[name=sUrl]:visible").val();

	$.getJSON(baseUrl + apiUrlSubscriptionsAdd, queryData, function(data) {
		if (data.success) {
			hideModal();
			loadStream(selectedStream.l_stream_id);
		} else if (data.error) {
			modalError(data.error);
		} else {
			console.error("error importing single feed, data: ");
			console.error(data);
		}
	});
}

function showDeleteStream() {
	$("#modalBox").attr("data-stream_id", selectedStream.l_stream_id);
	showModal("Delete Stream", "#div_delete_stream");
}

function confirmClearRecentlyRead() {
	if (confirm('This will clear all recently read articles for this profile. Are you sure?')) {
		$.getJSON(baseUrl + apiUrlRecentlyReadClear, {}, function(data) {
			if (data.count >= 0) {
				loadRecentlyRead();
			} else {
				console.error("error deleting entreis");
				console.error(data);
			}
		});
	}
}

function deleteStream() {
	var queryData = {};
	queryData.sid = $("#modalBox").attr("data-stream_id");
	queryData.dp = $("input[name=delete_profiles]:visible").is(':checked');

	$.getJSON(baseUrl + apiUrlStreamDelete, queryData, function(data) {
		if (data.count > 0) {
			location.assign(readerHome);
		}
	});

	return false;
}

function updateStreamGroupsList(sid, name) {
	for (var i = 0; i < streamGroups.length; i++) {
		if (streamGroups[i].i == sid) {
			streamGroups[i].n = name;
			break;
		}
	}

	$("#menusubs").html(streamGroupsTmpl({"groups" : streamGroups}));
}

function sendActionEntry(entryId, action, callback) {
	var queryData = {};
	queryData.id = entryId;
	queryData.act = action;

	$.getJSON(baseUrl + apiUrlEntriesEntry, queryData, function(data) {
		callback(data);
	});
}

function removeEntry(entryId) {
	sendActionEntry(entryId, 'r', function(data) {
		// TODO: Implement pop-up.
		if (data.count == 1) {
			$("#news_" + entryId).remove();
		} else if (data.count == 0) {
			console.error("wasnt not saved.")
		} else {
			console.error("save error")
		}
	});
}

function saveEntry(entryId) {
	sendActionEntry(entryId, 's', function(data) {
		// TODO: Implement pop-up.
		if (data.count >= 0) {
			$("#news_" + entryId).addClass("saved");
			setTimeout(function() {
				$("#news_" + entryId).removeClass("saved");
			}, 1500);
		} else {
			console.error("save error")
		}
	});
}

function renameSubscription() {
	var val = $("#txt_subscription_rename").val();
	var subsId = $("#txt_subscription_rename").attr("data-value");

	saveSubscription(subsId, val, function(data) {
		if (data.count > 0) {
			$("#div_subscription_rename").hide();
			$("#span_subscription_rename").text(val);
			$("#span_subscription_rename").show();
		} else {
			console.error("error renaming subscription, data ");
			console.error(data);
		}
	});
}

function renameStreamGroup() {
	var queryData = {};
	queryData.sid = selectedStream.l_stream_id;
	queryData.sn = $("#txt_stream_rename").val();

	$.getJSON(baseUrl + apiUrlStreamsRename, queryData, function(data) {
		if (data.count > 0) {
			selectedStream.s_stream_name = queryData.sn;
			displayStreamHeader();
			updateStreamGroupsList(queryData.sid, queryData.sn);
		}
	});

}

function fetchFavIcos() {
	uniqueSources = [];

	for (var i = 0; i < streamEntries.length; i++) {
		var sourceId = streamEntries[i].l_xml_id;
		if (uniqueSources.indexOf(sourceId) >= 0) {
			continue;
		}
		uniqueSources.push(sourceId);
	}

	var queryData = {};
	queryData.ids = uniqueSources.toString();
	queryData.qtype = 1; // query type.

	$.getJSON(baseUrl + apiUrlSourcesGet, queryData, function(data) {
		if (data && data.entries) {
			data.entries.forEach(function(e, i, a) {
				if (e.s_link != '') {
					$("img[name=favico_" + e.l_xml_id + "]").attr('src',
							baseFavicoDomain + e.s_link);
				} else if (e.s_xml_url) {
					$("img[name=favico_" + e.l_xml_id + "]").attr('src',
							baseFavicoDomain + e.s_xml_url);
				}
			});
		}
	});
}

/**
 * @param {type} images if image is null, only content is queried.
 * @param {type} content if content is null, only images are queried.
 * @returns {undefined}
 */
function fetchNewsData(images, content) {
	var queryData = {};
	queryData.entries = "";
	queryData.img = images;
	queryData.cnt = content;
	queryData.ml = CONTENT_MAX_LEN;

	for (var i = 0; i < streamEntries.length; i++) {
		queryData.entries += streamEntries[i].l_entry_id;
		if (i + 1 != streamEntries.length) {
			queryData.entries += ",";
		}
	}

	$.getJSON(baseUrl + apiUrlEntriesData, queryData, function(data) {
		if (data.entries) {
			for (var i = 0; i < data.entries.length; i++) {
				if (images && data.entries[i]) {
					if (data.entries[i].s_thumb_url == '') {
						$("#img_" + data.entries[i].l_entry_id).remove();
					} else if (currentView == VIEW_STREAM) {
						var img = document.createElement("img");
						img.src = data.entries[i].s_thumb_url;
						$("#img_" + data.entries[i].l_entry_id).html(img);
					} else if (currentView == VIEW_MAGAZINE) {
						$("#img_" + data.entries[i].l_entry_id).css('background',
								'url(' + data.entries[i].s_thumb_url + ") no-repeat center").css('background-size',
								'220px');
					}
				}

				if (content && data.entries[i].content) {
					$("#cnt_" + data.entries[i].l_entry_id).html(data.entries[i].content);
				}
			}
		}
	});
};

function markRead(obj) {
	obj.className = obj.className + " read";

	apiMarkRead($(obj).attr("data-id"), function(data, status) {
		if (data.count) {
			selectedStream.l_gr_unread--;
			updateUnread();
			runQuery();
		}
	});
}

function apiStreamRead(streamId, callback) {
	var queryData = {};
	queryData.sid = streamId;
	queryData.t = renderTime;

	$.getJSON(baseUrl + apiUrlStreamsMarkAllRead, queryData, function httpRet(data, status) {
		if (callback) {
			callback(data);
		}
	}, "json");

}

function markAllRead() {
	$(".news").not(".read").each(function(idx, o) {
		$(o).addClass("read");
	});

	apiStreamRead(selectedStream.l_stream_id, function(data) {
		selectedStream.l_gr_unread = 0;
		clearViewAndData();
		updateUnread();
		updateNewsListView();
		runQuery();
	});

	$(".mark_all_read").hide();
	moveTop();
}

function moveTop() {
	$(window).scrollTop(0);
}

function setLineView() {
	currentView = VIEW_LIST;
	clearView();
	loadEntries(0);
}

function setMagazineView() {
	currentView = VIEW_MAGAZINE;
	clearView();
	loadEntries(0);
}

function setStreamView() {
	currentView = VIEW_STREAM;
	clearView();
	loadEntries(0);
}

/**
 * Called when the news entries view needs to be updated.
 */
function updateNewsListView() {

	setViewOptions();
	updateNewsContentPane(streamEntries.length);
	updateHeaderRightTools();

	if (tmplOptions.id == TEMPLATE_ID_STREAM) {
		var queryData = {};
		queryData.sid = selectedStream.l_stream_id;
		queryData.filter = selectedStream.h_filter_by;
		queryData.sort = selectedStream.h_sort_by;
		queryData.v = currentView;
		queryData.c = selectedStream.l_gr_unread;

		$.getJSON(baseUrl + apiUrlStreamUpdate, queryData, function(data, success) { /**/ }, "json");
	} else {
		var queryData = {};
		queryData.tmpl = tmplOptions.id;
		queryData.v = currentView;

		switch (tmplOptions.id) {
			case TEMPLATE_ID_SAVED:
				selectedProfile.saved_settings.view = currentView;
				break;
			case TEMPLATE_ID_ALL:
				selectedProfile.all_settings.view = currentView;
				break;
			case TEMPLATE_ID_RECENTLY_READ:
				selectedProfile.rr_settings.view = currentView;
				break;
		}
		
		$.getJSON(baseUrl + apiUrlSaveModulesSettings, queryData, function(data, success) { /**/ }, "json");
	}

	if (topUnread == undefined) {
		topUnread = $(".news").first();
	}

	fetchFavIcos();

	if (currentView == VIEW_MAGAZINE || currentView == VIEW_STREAM) {
		fetchNewsData(true, true);
	}

	triggerChangeViewListeners(currentView);
}

function updateHeaderRightTools() {
	$(".mark_all_read").hide();
	
	if (tmplOptions.id == TEMPLATE_ID_SOURCE) {
		$("#right_tools").remove();
	}
	
	if (selectedStream.h_filter_by == FILTER_SHOW_ALL) {
		$("#show_unread").css("text-decoration", "none");
		$("#show_all").css("text-decoration", "underline");
	} else if (selectedStream.h_filter_by == FILTER_BY_UNREAD) {
		$("#show_unread").css("text-decoration", "underline");
		$("#show_all").css("text-decoration", "none");
		if (streamEntries.length > 0) {
			$(".mark_all_read").show();
		}
	}

	if (selectedStream.h_sort_by == SORT_NEW_FIRST) {
		$("#oldest_first").css("text-decoration", "none");
		$("#newset_first").css("text-decoration", "underline");
	} else if (selectedStream.h_filter_by == SORT_OLD_FIRST) {
		$("#oldest_first").css("text-decoration", "underline");
		$("#newset_first").css("text-decoration", "none");
	}
}

function setViewOptions(){
	$("#icon_view_line").removeClass("icon-view-bgcolor");
	$("#icon_view_mag").removeClass("icon-view-bgcolor");
	$("#icon_view_stream").removeClass("icon-view-bgcolor");

	switch (currentView) {
		case VIEW_LIST:
			tmplOptions.sourceLen = 11;
			$("#icon_view_line").addClass("icon-view-bgcolor");
			entriesTmpl = viewLineTmpl;
			break;
			
		case VIEW_MAGAZINE:
			$("#icon_view_mag").addClass("icon-view-bgcolor");
			tmplOptions.sourceLen = 50;
			entriesTmpl = viewMagTmpl;
			break;
	
		default:
		case VIEW_STREAM:
			$("#icon_view_stream").addClass("icon-view-bgcolor");
			tmplOptions.sourceLen = 50;
			entriesTmpl = viewNewsTmpl;
			currentView = VIEW_STREAM;
			break;
	}
	
	if (screen.width < 1200) {
		tmplOptions.sourceLen = 10;
	}
	
	if (screen.width < 1000) {
		tmplOptions.sourceLen = 8;
		tmplOptions.showSource = false;
	}
	
	if (currentView == VIEW_LIST && screen.width < 450) {
		tmplOptions.showIco = false;
		tmplOptions.titleLen = 35;
	}
	
	tmplOptions.leftSize = 2;
	tmplOptions.midSize = 9;
	
	if (!tmplOptions.showSource) {
		tmplOptions.leftSize--;
		tmplOptions.midSize++;
	}
	
	if (!tmplOptions.showIco) {
		tmplOptions.leftSize--;
		tmplOptions.midSize++;
	}
	
	selectedStream.h_view_mode = currentView;
}

function updateNewsContentPane(entriesLen) {
	
	if (entriesLen == 0 || entriesLen == undefined) {
		if (tmplOptions.id == TEMPLATE_ID_SOURCE) {
			if (streamPage == 0) {
				$("#stream_entries").html(contentEmptySource({"id" : selectedStream.l_stream_id}));
			}
			return;
		}
		
		if (tmplOptions.id == TEMPLATE_ID_ALL) {
			renderContentStart();
		} else if (tmplOptions.id == TEMPLATE_ID_SAVED) {
			$("#stream_entries").html($("#content_start_saved").html());
		} else if (tmplOptions.id == TEMPLATE_ID_RECENTLY_READ) {
			$("#stream_entries").html($("#content_start_recently_read").html());
		} else if (streamPage == 0 && streamSubscriptions.length != 0) {
			$("#stream_entries").html(contentAllRead({"name" : selectedStream.s_stream_name}));
		} else { // empty profile but user has subscriptions
			$("#stream_entries").html($("#content_start_source").html());
		}
	} else {
		$("#stream_more").hide();
		if (streamEntries.length == entriesPerPage) {
			$("#stream_more").show();
			moreAvailable = true;
		}

		$("#stream_entries").append(entriesTmpl({
			"tmplOptions" : tmplOptions,
			"midSize" : tmplOptions.midSize,
			"entries" : streamEntries
		}));
	}
}

function resetTemplate(type, showIco, showSource, showHeader) {
	renderTime = new Date().getTime();
	closeAllHeaderTools();
	tmplOptions.id = type;
	tmplOptions.showIco = showIco;
	tmplOptions.showSource = showSource;
	selectedStream = {};
	$("#simple_view_header").hide();
	$("#stream_group_header").hide();
	$("#" + showHeader).show();
	clearView();
	moveTop();
}


/**
 * Used when changing between profiles.
 * If you are changing views, use clearView().
 * If you are changing stream groups user clearViewAndData()
 */
function clearContent() {
	selectedStream = {};
	streamGroups = {};
	filteredStreamGroups = {};
	position = 0;
	
	$("#right_tools").remove(); // needs to be removed since it uses a partial template.
	$("#menusubs").html("");
	$("#stream_header").html("");
	$("#stream_config").hide();
	clearViewAndData();
}

/** 
 * @see #clearContent()
 */
function clearView() {
	topUnread = null;
	pendingRequest = false;
	$("#stream_entries").html("");
	$("#stream_more").hide();
	$(".mark_all_read").hide();
}

/** 
 * @see #clearContent()
 */
function clearViewAndData() {
	clearView();
	streamPage = 0;
	streamEntries = {};
	updateNewsListView();	
}

function loadStream(streamId) {
	this.streamId = streamId;
	apiCurrentStreamsFeed = apiUrlStreamsFeed;

	resetTemplate(TEMPLATE_ID_STREAM, true, true, 'stream_group_header');

	for (var i = 0; i < streamGroups.length; i++) {
		if (streamGroups[i].l_stream_id == streamId) {
			selectedStream = streamGroups[i];
		}
	}
	
	currentView = selectedStream.h_view_mode;
	fetchAllStreamSubscriptions(streamId);
	displayStreamHeader();
	loadEntries(0);
}

function loadAll() {
	currentView = selectedProfile.all_settings.view;
	apiCurrentStreamsFeed = apiUrlStreamsFeedAll;

	resetTemplate(TEMPLATE_ID_ALL, true, true, 'simple_view_header');
	selectDom("mAll");
	
	$.getJSON(baseUrl + apiCurrentStreamsFeed, {}, function(data) {
		streamEntries = data.entries;
		triggerOnEntriesLoadedListeners(streamEntries);
	});

	$("#simple_view_header").html(simpleViewHeaderTmpl({"title" : "All"}));
}

function selectDom(domId) {
	$(".gselected").removeClass("gselected");
	$("#" + domId).addClass("gselected");
}

function loadRecentlyRead() {
	currentView = selectedProfile.rr_settings.view;
	apiCurrentStreamsFeed = apiUrlStreamsFeedRecent;

	resetTemplate(TEMPLATE_ID_RECENTLY_READ, true, true, 'simple_view_header');
	selectDom("mRr");
	
	$.getJSON(baseUrl + apiCurrentStreamsFeed, {}, function(data) {
		streamEntries = data.entries;
		triggerOnEntriesLoadedListeners(streamEntries);
	});

	$("#simple_view_header").html(recentlyReadHeaderTmpl({
		"title" : "Recently read"
	}));
}

function loadSaved() {
	currentView = selectedProfile.saved_settings.view;
	apiCurrentStreamsFeed = apiUrlStreamsFeedSaved;

	resetTemplate(TEMPLATE_ID_SAVED, true, true, 'simple_view_header');
	selectDom("mSaved");
	
	$.getJSON(baseUrl + apiCurrentStreamsFeed, {}, function(data) {
		streamEntries = data.entries;
		triggerOnEntriesLoadedListeners(streamEntries);
	});

	$("#simple_view_header").html(simpleViewHeaderTmpl({"title" : "Saved"}));
}

function loadSource(sourceId) {
	resetTemplate(TEMPLATE_ID_SOURCE, false, false, 'simple_view_header');
	apiCurrentStreamsFeed = apiUrlSourceId;

	var queryData = {};
	queryData.id = sourceId;

	$.getJSON(baseUrl + apiUrlSourcesGet, queryData, function(data) {
		$("#simple_view_header").html(sourceHeaderTmpl(data));
	}, "json");

	var queryData = {};
	queryData.sid = sourceId;

	$.getJSON(baseUrl + apiUrlSubscriptionsGet, queryData, function(data) {
		// TODO: Can all this be done better?
		if (data && data.l_subs_id) {
			$("#user_subscription").show();

			var inputSize = (data.s_subs_name.length * 2 > MAX_INPUT_SIZE) ? MAX_INPUT_SIZE
					: data.s_subs_name.length * 2;
			$("#txt_subscription_rename").attr("size", inputSize);
			$("#txt_subscription_rename").attr("data-value", data.l_subs_id);
			$("#txt_subscription_rename").val(data.s_subs_name);

			$("#span_subscription_rename").html(data.s_subs_name);
			$("#span_subscription_rename").attr("href", baseUrl + "/pages/subscriptions.jsp#/v/" + data.l_subs_id);

			$("#txt_subscription_rename").keyup(function(e) {
				if ((e.keyCode || e.which) == 13)
					renameSubscription(); // enter
				if ((e.keyCode || e.which) == 27) {
					$("#div_subscription_rename").hide();
					$("#span_subscription_rename").show();
				}
			});
		}
	});

	var queryData = {};
	queryData.id = sourceId;

	selectedStream.l_stream_id = sourceId; // so loadMore keeps working
	
	$.getJSON(baseUrl + apiUrlSourceId, queryData, function(data) {
		streamEntries = data.entries;
		updateNewsListView();
	});
}

function loadMore() {
	$("#stream_more").hide();
	if (!moreAvailable) {
		return;
	}
	loadEntries(++streamPage);
}

function updateUnread() {	
	var c = selectedStream.l_gr_unread;
	if (c < 0) {
		selectedStream.l_gr_unread = 0;
	}

	$("#unread").text(c);
	$("#footer_unread").text(c);

	if (c == 0 && queryGroups.showUnreadOnly) {
		$("#e_" + selectedStream.l_stream_id).hide();
	}
}

function loadEntries(page) {
	if (pendingRequest) {
		return;
	}

	streamPage = page;
	pendingRequest = true;

	var queryData = {};
	queryData.id = selectedStream.l_stream_id;
	queryData.page = page;
	queryData.filter = selectedStream.h_filter_by;
	queryData.sort = selectedStream.h_sort_by;

	if (queryData.filter == FILTER_BY_UNREAD) {
		var len = $(".news").not(".read").length;
		queryData.offset = len;
	}

	$.getJSON(baseUrl + apiCurrentStreamsFeed, queryData, function(data, success) {
		streamEntries = data.entries;
		if (data.unread != undefined) {
			selectedStream.l_gr_unread = data.unread;
			updateUnread();
		} else {
			updateUnread();
		}
		runQuery();
		triggerOnEntriesLoadedListeners(data.entries);
		pendingRequest = false;
	}, "json");
}

function loadUnknown() {
	$("#stream_entries").html(pageUnknownTmpl());
}

function reloadStream() {
	clearView();
	loadEntries(0);
}

function saveStreamGroup() {
	var input = $("#menusubs input:first-child");

	if (input.length == 0) {
		console.error("error, can't create new stream group.");
	}

	var queryData = {};
	queryData.sn = input.val();

	$.getJSON(baseUrl + apiUrlStreamsAdd, queryData, function(data) {
		if (data.success) {
			getStreamGroups(function() {
				loadStream(data.id);
			});
		}
	});
}

function cancelStreamGroup() {
	$("#newGroup").remove();
}

function newStreamGroup() {
	var input = $("#menusubs input:first-child");

	if (input.length == 0) {
		$("#menusubs").prepend("<div id='newGroup'><input size='8' type='text' value=''>\
                <button onclick='saveStreamGroup()'>S</button><button onclick='cancelStreamGroup()'>C</button></div>");
		input = $("#menusubs input:first-child");
	}

	input.focus();

	input.on('keyup', function(e) {
		if ((e.keyCode || e.which) == 13) { // ENTER
			saveStreamGroup();
		}
		if ((e.keyCode || e.which) == 27) { // ESC
			cancelStreamGroup();
		}
	})
}

function showAll() {
	selectedStream.h_filter_by = FILTER_SHOW_ALL;
	clearView();	
	loadEntries(0);
}

function showUnreadOnly() {
	selectedStream.h_filter_by = FILTER_BY_UNREAD;
	clearView();
	loadEntries(0);
}

function streamSort(sortDir) {
	selectedStream.h_sort_by = sortDir;
	clearView();
	loadEntries(0);
}

function saveReaderSettings() {
	$.get(baseUrl + apiUrlSaveReaderSettings, queryGroups, function(data, status) { /* noop */ });
}

function closeMenu() {
	$("#leftbar").hide();
	$("#stream_content").removeClass("col-sm-offset-3").removeClass(
			"col-md-offset-2").removeClass("col-sm-9").removeClass("col-md-10");
	$(".show_menu").show();
}

function showMenu() {
	$("#leftbar").show();
	$("#stream_content").addClass("col-sm-offset-3")
			.addClass("col-md-offset-2").addClass("col-sm-9").addClass(
					"col-md-10");
	$(".show_menu").hide();
}

function reloadSelected() {
	location.reload();
}

function renderContentStart() {
	$("#stream_entries").html($("#content_start_all").html());
}
