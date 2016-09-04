// Source: src/main/js/legacy/collections.js
var COLUMNS = 3;
var MODAL_COLLECTION_ADD = "modal_collection_add";

var currentColumn = 0;

var apiCollectionsList = "/api/v1/collections/list";
var apiCollectionsEntries = "/api/v1/collections/entries";
var apiCollectionAdd = "/api/v1/collections/add";
var baseFavicoDomain = 'http://www.google.com/s2/favicons?domain=';

var collections = {};
var collectionsIds = [];
var collectionsById = {};
var collectionTmpl = {};
var feedEntriesTmpl = {};


Handlebars.registerHelper('favico', function(data) {
    return baseFavicoDomain;
  });

function initCollections() {

	collectionTmpl = Handlebars.compile($('#collections_tmpl').html());
	feedEntriesTmpl = Handlebars.compile($('#feeds_tmpl').html());
	createdTmpl = Handlebars.compile($("#collection_created_tmpl").html());

	var queryData = {};
	collectionsIds = [];
	$.get(baseUrl + apiCollectionsList, queryData, function(data, status) {
		collections = data.entries;
		if (data.entries) {
			for (var x = 0; x < data.entries.length; x++) {
				var c = data.entries[x];
				collectionsIds[collectionsIds.length] = c.l_collection_id;
				collectionsById[c.l_collection_id] = c;
				console.log(c);
				console.log("currentColumn " + currentColumn);
				c.column = currentColumn;
				$("#col" + currentColumn++).append(collectionTmpl({"c" : c}));
				if (currentColumn >= COLUMNS) {
					currentColumn = 0;
				}
			}
		} else {
			console.error("no entries found " + data);
		}
	});

}

function toggleFeedsCollection(caller, collectionId) {
	var domFeeds = $("#feeds_" + collectionId);
	if (domFeeds.is(":visible")) {
		domFeeds.hide();
	} else {
		var queryData = {};
		queryData.ids = collectionId;
		$.get(baseUrl + apiCollectionsEntries, queryData, function(data, status) {
			domFeeds.html(feedEntriesTmpl({"entries" : data.entries}));
			domFeeds.show();
		});
	}
}

function showAddModal(caller, collectionId) {
	$("#collectionCreated").hide();
	var selected = collectionsById[collectionId];
	showModal("Add '" + selected.s_name + "' collection", "#" + MODAL_COLLECTION_ADD,
			function onClose() {},
			function onInit() {
				$("#collectionName").val(selected.s_name);
				$("#collectionId").val(collectionId);
			});
}

function importCollection() {

	var formId = $("#collectionId").val();
	var formName = $("#collectionName").val();
	var formProfiles = $("#selectedProfiles").val() || [];

	var queryData = {};
	queryData.id =formId;
	queryData.name = formName;
	queryData.profiles = formProfiles.toString();

	$.get(baseUrl + apiCollectionAdd, queryData, function(data, status) {
		if (data.error) {
			modalError(data.error);
		} else if (data.success) {
			console.log(data);
			$("#collectionCreated").html(createdTmpl(data));
			$("#collectionCreated").show();
		} else {
			console.error(data);
			console.error(status);
		}
	});

}


// Source: src/main/js/legacy/global.js

var baseUrl = "/";
var closeModalCallback = {};
var MODALBOX_SELECTOR = "#modalBox";

function setBaseUrl(url) {
	baseUrl = url;
}

Handlebars.registerHelper('formatUnixTs', function(str) {
	return new Date(str).toLocaleString();
});

Handlebars.registerHelper('toLowerCase', function(str) {
	return str.toLowerCase();
});

Handlebars.registerHelper('toUpperCase', function(str) {
	return str.toUpperCase();
});

function cut(str, len) {
	if (str && str.length > len) {
		return str.substring(0, len) + "...";
	}
	return str;
}

Handlebars.registerHelper('cut', function(str, len) {
	return cut(str, len);
});

function toggleCheckbox(name) {
	$(name).prop('checked', !$(name).prop('checked'));
}

function hideModal() {
	$(MODALBOX_SELECTOR).keydown(null);
	$(MODALBOX_SELECTOR).hide();
	if (closeModalCallback) {
		closeModalCallback();
	}
}

function modalError(error) {
	$("#modalTitle").css("background-color", "red");
	$("#modalTitle").text(error);
}

function showModal(title, contentSelector, closeCallback, postInitCallback) {
	$("#modalTitle").css("background-color", "");
	$(MODALBOX_SELECTOR).show();
	$("#modalTitle").text(title);
	var content = $(contentSelector);
	content.remove(); // so we don't have duplicates.
	$("#modalContent").html(content.html());
	var $exampleModal = $(MODALBOX_SELECTOR), $exampleModalClose = $(".modal-header button");

	// added this line to set focus on the modal to close on ESC key
	$(".modal-header button").focus();
	this.closeModalCallback = closeCallback;

	$(MODALBOX_SELECTOR).keydown(function(e) {
		if (e.keyCode == 27) {
			hideModal();
		}
		// on enter key press, check if modal is visible and contain
		// btn-primary. If yes then call click() method.
		if ($(MODALBOX_SELECTOR).is(':visible') && e.keyCode == 13) {
			if ($(".modal-footer .btn-primary").is(':visible')) {
				$(".modal-footer .btn-primary").click();
			}
		}
	});

	if (postInitCallback) {
		postInitCallback($(MODALBOX_SELECTOR));
	}
}

function toogleSmallMenu() {
	$("#nav").toggle();
	$("#content").css("margin-top", "50px");
}

function closeLeftBar() {
	$('#leftbar').hide();
	$(document).off("keyup.leftbar");
	$(document).off("mousedown.leftbar");
}

function openLeftBar() {
	$('#leftbar').toggle();

	if ($("#leftbar").is(":visible")) {
		$(document).on("keyup.leftbar", function(e) {
			if (e.keyCode == 27) {
				closeLeftBar();
			}
		});
		$(document).on("mousedown.leftbar", function(e) {
			var container = $("#leftbar");
			if (!container.is(e.target)
					&& e.target.id != 'logo'
					&& container.has(e.target).length === 0) {
				closeLeftBar();
			}
		});
	}
}
// Source: src/main/js/legacy/profiles.js
var profilesTmpl;
var apiUrlProfileList = '/api/v1/user/profiles/list';

var selectedProfile = {
	'l_profile_id' : 0,
	's_profile_name' : '',
	's_color' : ''
};

var profiles = [ selectedProfile ];

var onProfileSelectedListeners = [];
var onProfilesAvailableListeners = [];
var onProfileDataChangedListeners = [];

function initProfiles() {
	console.debug('init profiles');
	registerOnProfileSelected(function() {
		renderProfiles();
	});

	registerOnProfileDataChange(function() {
		renderProfiles();
	});

	$.getJSON(baseUrl + apiUrlProfileList, '', function(data) {
		profiles = {};

		if (data.entries) {
			profiles = data.entries;
			triggerOnProfilesAvailable();
		} else {
			console.error("error fetching profiles ");
			console.error(data);
		}
	});
}

function renderProfiles() {
	if (!profilesTmpl) {
		profilesTmpl = Handlebars.compile($("#left-menu-profiles-tmpl").html());
	}

	$("#profiles .content").html(profilesTmpl({
		"profiles" : profiles
	}));
}

function registerOnProfileSelected(listener) {
	onProfileSelectedListeners.push(listener);
}

function registerOnProfilesAvailable(listener) {
	onProfilesAvailableListeners.push(listener);
}

function registerOnProfileDataChange(listener) {
	onProfileDataChangedListeners.push(listener);
}

function triggerOnProfilesAvailable() {
	console.debug('trigger profiles avail.', profiles);
	for (var i = 0; i < onProfilesAvailableListeners.length; i++) {
		onProfilesAvailableListeners[i](profiles);
	}
}

function triggerOnProfileSelected(data) {
	for (var i = 0; i < onProfileSelectedListeners.length; i++)
		onProfileSelectedListeners[i](data);
}

function triggerOnProfileDataChanged() {
	for (var i = 0; i < onProfileDataChangedListeners.length; i++)
		onProfileDataChangedListeners[i](profiles);
}

function setSessionProfile(profileId, callback) {
	$.get(baseUrl + "/pages/set_profile.jsp?id=" + profileId, function(data) {
		if (callback) {
			callback(data);
		}
	});
}

function selectProfile(profileId, resetRoute) {
	console.debug('select profile', profileId);
	if (resetRoute) {
		window.location.hash = '';
	}

	if (profileId == 0) {
		return;
	}

	for (var i = 0; i < profiles.length; i++) {
		if (profiles[i].l_profile_id == profileId) {
			selectedProfile = profiles[i];
		}
	}

	if (selectedProfile.l_profile_id == 0) {
		console.error("unknown profile id " + profileId);
	}

	$(".profileColor").css("background-color", "#" + selectedProfile.s_color);
	$(".profile-color").css("color", "#" + selectedProfile.s_color);

	$("#profile").text(selectedProfile.s_profile_name);
	$("#profiles").hide();
	// needed to set the session value in the background.
	setSessionProfile(profileId, function() {
		triggerOnProfileSelected(selectedProfile);
	});
}

function renameProfile(id, name, color) {
	if (id == $("#profile").attr("data-id")) {
		$("#profile").attr("data-id", id);
		$("#profile").text(name);
	}

	for (var i = 0; i < profiles.length; i++) {
		if (profiles[i].l_profile_id == id) {
			profiles[i].s_profile_name = name;
			if (color)
				profiles[i].s_color = color;
			triggerOnProfileDataChanged();
			return;
		}
	}
}

function showCreateNewProfile() {
	showModal("Create new Profile", "#div_new_profile", function() {
		$("#loader").css("background-color", "#" + selectedProfile.s_color);
	});

	$("input[name=profile_name]").focus();

	var pObj = $("input[name=picker]");
	pObj.change(changeProfileColor);
	var picker = new jscolor.color(pObj.get(0), {
		slider : false,
		pickerFaceColor : 'transparent',
		pickerFace : 3,
		pickerBorder : 0,
		pickerInsetColor : 'black'
	});
	picker.fromString('99FF33');
}

function changeProfileColor(color) {
	$("#loader").css("background-color", "#" + color.target.value);
}

function closeNewProfile() {
	hideModal();
}

function createNewProfile(inputName, inputPicker) {
	var name = $("input[name=" + inputName + "]");
	var color = $("input[name=" + inputPicker + "]");

	var queryData = {};
	queryData.n = name.val();
	queryData.c = color.val();

	$.getJSON(baseUrl + "/api/v1/user/profiles/new", queryData, function(data) {
		if (data.success) {
			setSessionProfile(data.profileid, function() {
				location.reload();
			});
			hideModal();
		} else {
			$("#modal_error_text").text("Couldn't add profile. " + data.error);
			$("#modal_error").show();
			return;
		}
	});
}

// Source: src/main/js/legacy/reader.admin.js

var apiShareCollection = "api/v1/collections/share"; 

function showShareCollection() {
	console.log(selectedStream);
	showModal("Share " + selectedStream.s_stream_name, "#div_share_collection", 
		function closeModal() {
		},
		function postInit(modal) {
			$("#collectionName").val(selectedStream.s_stream_name);
		}
	);
}

function shareCollection() {
	var queryData = {};
	queryData.id = selectedStream.l_stream_id;
	queryData.name = $("#collectionName").val();
	queryData.description = $("#collectionDesc").val();
	
	$.post(baseUrl + "/" + apiShareCollection, queryData, function http(data, status) {
		if (data.error) {
			modalError(data.error);
		} else if (data.success) {
			hideModal();
		} else {
			console.error(data);
			console.error(status);
		}
	});
}

// Source: src/main/js/legacy/reader.handlebars.js
Handlebars.registerHelper("favico", function favico(url) {
	return baseFavicoDomain + url;
});

Handlebars.registerHelper("log", function(a, b, c) {
	console.log(a, b, c);
});

Handlebars.registerHelper("timediff", function timediff(time) {
	var s = (renderTime - time) / 1000;
	var m = Math.round(s / 60);
	if (m < 60)
		return m + "m";
	var h = Math.round(m / 60);
	if (h < 24)
		return h + "h";
	var d = Math.round(h / 24);
	return d + "d";
});

Handlebars.registerHelper("tools", function tools(tmpl, entryid, options) {
	if (tmpl == TEMPLATE_ID_SAVED) {
		return '<a href="" onclick="removeEntry(' + entryid + ');return false;">R</a>';
	} else {
		return '<a href="" onclick="saveEntry(' + entryid
				+ ');return false;"><span class="glyphicon glyphicon-floppy-disk"></span></a>'
	}
});

Handlebars.registerHelper("position", function position() {
	return ++position;
});

Handlebars.registerHelper("showGroupCount", function showGroupCount(a, show) {
	if (queryGroups.showUnreadOnly || show)
		return a.l_gr_unread;
	return "";
});

Handlebars.registerHelper("showNextOptions", function showNextOptions(obj) {
	var hasUnread = false;
	var afterCurrent = false;
	var sgLeft = undefined;
	var sgCurrent = undefined;
	var sgRight = undefined;

	for (var x = 0; x < filteredStreamGroups.length; x++) {
		sg = filteredStreamGroups[x];
		if (sg.l_stream_id == selectedStream.l_stream_id) {
			sgCurrent = sg;
			sgCurrent.x = x;
			afterCurrent = true;
			continue;
		}

		if (sg.l_gr_unread == 0) {
			continue;
		}

		hasUnread = true;

		if (!afterCurrent) {
			sgLeft = sg;
			continue;
		}

		if (afterCurrent && sgRight == undefined) {
			sgRight = sg;
		}
	}

	var ret = "";

	if (!hasUnread) {
		return "... and there are no more streams with unread stories.";
	}

	if (sgLeft != undefined) {
		ret += " Move up to <a href='#/f/" + sgLeft.l_stream_id + "'>" + sgLeft.s_stream_name + "</a>";
	}

	if (sgRight != undefined) {
		if (sgLeft != undefined) {
			ret += " <br>or<br> ";
		}
		ret += " Move down to <a href='#/f/" + sgRight.l_stream_id + "'>" + sgRight.s_stream_name + "</a>";
	}

	return ret;
});


Handlebars.registerHelper('sourceName', function sourceName(id) {
	idx = subscriptionsIdx.indexOf(id);
	if (idx != -1) {
		return subscriptions[idx].s_subs_name;
	} else {
		return 'n.a.';
	}
});

Handlebars.registerHelper("showSourceData", function showSourceData(id, tmplOptions, showDiv, options) {
	if (tmplOptions.showIco || tmplOptions.showSource) {
		var ret = '';
		if (showDiv) {
			ret += '<div class="col-xs-' + tmplOptions.leftSize + ' text-left">';
		}

		if (tmplOptions.showIco) {
			ret += '<img name="favico_' + id + '" class="favico" src="' + baseUrl + '/img/16x16t.png">';
		}

		if (tmplOptions.showSource) {
			idx = subscriptionsIdx.indexOf(id);
			if (idx != -1) {
				ret += '&nbsp;<a name="source_' + id + '" href="#/s/' + id + '">'
						+ cut(subscriptions[idx].s_subs_name, tmplOptions.sourceLen) + '</a>';
			} else {
				ret += '&nbsp;<a name="source_' + id + '" href="#/s/' + id + '">source</a>';
			}
		}

		if (showDiv) {
			ret += "</div>";
		}

		return ret;
	}
	return "";
});

// Source: src/main/js/legacy/reader.js
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
var baseFavicoDomain = 'https://www.google.com/s2/favicons?domain=';

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
	readerHome = baseUrl + "/reader";
	setupTemplates();
	setupRoutes();
	tmplOptions.id = TEMPLATE_ID_STREAM;
	initReaderSubscriptions();
	setupEvents();
}

function setupEvents() {
	window.onscroll = function(obj) {
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

	Handlebars.registerPartial("stream-options", $("#stream-options").html());

	// TODO Compile only when used for the first time.
	streamGroupsTmpl = Handlebars.compile($('#stream_groups_tmpl').html());

	viewLineTmpl = Handlebars.compile($("#news_line_tmpl").html());
	viewMagTmpl = Handlebars.compile($("#news_mag_tmpl").html());
	viewNewsTmpl = Handlebars.compile($("#news_stream_tmpl").html());
	entriesTmpl = viewLineTmpl;

	sourceHeaderTmpl = Handlebars.compile($("#source-header-tmpl").html());
	recentlyReadHeaderTmpl = Handlebars.compile($("#recently_read_header_tmpl")
			.html());
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
 * Register callback to be called when #triggerOnEntriesLoadedListeners is
 * called.
 *
 * @param callback
 */
function registerOnEntriesLoadedListeners(callback) {
	onEntriesLoadedListeners.push(callback);
}

/**
 * Called by the different load* methods after the API call requesting entries
 * finishes.
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

function displayStreamHeader(options) {
	if (!streamGroupHeaderTmpl) {
		streamGroupHeaderTmpl = Handlebars.compile($("#stream-header-tmpl").html());
	}
	$("#stream-header .content").html(streamGroupHeaderTmpl({
			'options' : options,
			'stream' : selectedStream
	}));
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
	console.debug('run query function', streamGroups);
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
	console.debug('render stream groups', data);
	$("#menusubs").html(streamGroupsTmpl({
		"groups" : data
	}));
	selectDom("e_" + selectedStream.l_stream_id);
}

/**
 * @param streamGroupList
 *            list of stream groups to get the unread count.
 * @param nocache
 *            1 = calculate again, 0 = get from from cache.
 * @param callback
 *            callback to call when query finishes.
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

	$.getJSON(baseUrl + apuUrlStreamsUnreadCount, queryData, function(data,
			status) {
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
		console.debug('get stream groups data', data);
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

// Stream rename and subscriptions rename work same. Candidates to create a
// little rename tool.
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
	showModal("Add new feed to '" + selectedStream.s_stream_name + "'",
			"#div_add_subscription", function closeImportModal() { /* noop */
			});
}

function importSingleFeed() {
	var queryData = {};
	queryData.n = $("input[name=sName]:visible").val();
	queryData.u = $("input[name=sUrl]:visible").val();
	queryData.sid = selectedStream.l_stream_id;


	$.getJSON(baseUrl + apiUrlSubscriptionsAdd, queryData, function(data) {
		if (data.success) {
			hideModal();
			loadStream(selectedStream.l_stream_id);
		} else if (data.error) {
			modalError(data.error);
		} else {
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
			}
		});
	}
}

function deleteStream(streamId) {
	if (confirm("Delete stream from all profiles?")) {
		var queryData = {};
		queryData.sid = streamId;
		queryData.dp = true;

		$.getJSON(baseUrl + apiUrlStreamDelete, queryData, function(data) {
			if (data.count > 0) {
				location.assign(readerHome);
			}
		});
	}
	return false;
}

function updateStreamGroupsList(sid, name) {
	for (var i = 0; i < streamGroups.length; i++) {
		if (streamGroups[i].i == sid) {
			streamGroups[i].n = name;
			break;
		}
	}

	$("#menusubs").html(streamGroupsTmpl({
		"groups" : streamGroups
	}));
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
		console.debug('remove entry', data);
	});
}

function saveEntry(caller, entryId) {
	var i = $(caller).find('i');
	if (i && i.hasClass('fa-bookmark-o')) {
		$(i).removeClass('fa-bookmark-o').addClass('fa-bookmark');
		sendActionEntry(entryId, 's', function(data) {
			console.debug('save entry result', data);
		});
	} else if (i && i.hasClass('fa-bookmark')) {
		$(i).removeClass('fa-bookmark').addClass('fa-bookmark-o');
		sendActionEntry(entryId, 'r', function(data) {
			console.debug('delete entry result', data);
		});
	}
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
 * @param {type}
 *            images if image is null, only content is queried.
 * @param {type}
 *            content if content is null, only images are queried.
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
						$("#img_" + data.entries[i].l_entry_id).css(
								'background', 'url(' + data.entries[i].s_thumb_url + ") no-repeat top center");
					}
				}

				if (content && data.entries[i].content) {
					$("#cnt_" + data.entries[i].l_entry_id).html(
							data.entries[i].content);
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

	$.getJSON(baseUrl + apiUrlStreamsMarkAllRead, queryData, function httpRet(
			data, status) {
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
	});

	// assume call works, otherwise it lags.
	selectedStream.l_gr_unread = 0;
	clearViewAndData();
	updateUnread();
	updateNewsListView();
	runQuery();

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

		$.getJSON(baseUrl + apiUrlStreamUpdate, queryData, function(data, success) { /**/
		}, "json");
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

		$.getJSON(baseUrl + apiUrlSaveModulesSettings, queryData, function(data, success) { /**/
		}, "json");
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

function setViewOptions() {
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

/**
 * function that renders the news entries
 */
function updateNewsContentPane(entriesLen) {
	console.log('update news content pane', entriesLen, selectedStream, tmplOptions);
	$("#load-more").hide();
	if (selectedStream.h_filter_by == FILTER_BY_UNREAD) {
		$('#mark-all-read').show();
	} else {
		$('#mark-all-read').hide();
	}

	if (entriesLen == 0 || entriesLen == undefined) {
		$('#mark-all-read').hide();
		$('#to-top').hide();
		if (tmplOptions.id == TEMPLATE_ID_SOURCE) {
			if (streamPage == 0) {
				$("#stream-entries").html(contentEmptySource({
					"id" : selectedStream.l_stream_id
				}));
			}
			return;
		}

		if (tmplOptions.id == TEMPLATE_ID_ALL) {
			renderContentStart();
		} else if (tmplOptions.id == TEMPLATE_ID_SAVED) {
			$("#stream-entries").html($("#content_start_saved").html());
		} else if (tmplOptions.id == TEMPLATE_ID_RECENTLY_READ) {
			$("#stream-entries").html($("#content_start_recently_read").html());
		} else if (streamPage == 0 && streamSubscriptions.length != 0) {
			$("#stream-entries").html(contentAllRead({
				"name" : selectedStream.s_stream_name
			}));
		} else { // empty profile but user has subscriptions
			$("#stream-entries").html($("#content_start_source").html());
		}
	} else {
		$('#to-top').show();
		if (streamEntries.length == entriesPerPage) {
			$("#load-more").show();
			moreAvailable = true;
		}

		$("#stream-entries").append(entriesTmpl({
			"tmplOptions" : tmplOptions,
			"midSize" : tmplOptions.midSize,
			"entries" : streamEntries
		}));
	}
}

function resetTemplate(type, showIco, showSource) {
	renderTime = new Date().getTime();
	closeAllHeaderTools();
	tmplOptions.id = type;
	tmplOptions.showIco = showIco;
	tmplOptions.showSource = showSource;
	selectedStream = {};
	clearView();
	moveTop();
}

/**
 * Used when changing between profiles. If you are changing views, use
 * clearView(). If you are changing stream groups user clearViewAndData()
 */
function clearContent() {
	selectedStream = {};
	streamGroups = {};
	filteredStreamGroups = {};
	position = 0;

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
	$("#stream-entries").html("");
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

	resetTemplate(TEMPLATE_ID_STREAM, true, true);

	for (var i = 0; i < streamGroups.length; i++) {
		if (streamGroups[i].l_stream_id == streamId) {
			selectedStream = streamGroups[i];
		}
	}

	currentView = selectedStream.h_view_mode;
	fetchAllStreamSubscriptions(streamId);
	displayStreamHeader({
		'showFilter': true,
		'showRanking': true,
		'showDeleteStream': true
	});
	loadEntries(0);
}

function loadAll() {
	currentView = selectedProfile.all_settings.view;
	apiCurrentStreamsFeed = apiUrlStreamsFeedAll;

	resetTemplate(TEMPLATE_ID_ALL, true, true);
	selectDom("mAll");

	$.getJSON(baseUrl + apiCurrentStreamsFeed, {}, function(data) {
		streamEntries = data.entries;
		triggerOnEntriesLoadedListeners(streamEntries);
	});

	selectedStream.s_stream_name = 'All';
	selectedStream.l_stream_id = 0;
	selectedStream.l_view = TEMPLATE_ID_ALL;

	displayStreamHeader();
}

function selectDom(domId) {
	$(".gselected").removeClass("gselected");
	$("#" + domId).addClass("gselected");
}

function loadRecentlyRead() {
	currentView = selectedProfile.rr_settings.view;
	apiCurrentStreamsFeed = apiUrlStreamsFeedRecent;

	resetTemplate(TEMPLATE_ID_RECENTLY_READ, true, true);
	selectDom("mRr");

	$.getJSON(baseUrl + apiCurrentStreamsFeed, {}, function(data) {
		streamEntries = data.entries;
		triggerOnEntriesLoadedListeners(streamEntries);
	});

	selectedStream.s_stream_name = 'Recently read';
	selectedStream.l_stream_id = 0;
	selectedStream.l_view = TEMPLATE_ID_RECENTLY_READ;

	displayStreamHeader({'showRecentlyRead' : true});
}

function loadSaved() {
	currentView = selectedProfile.saved_settings.view;
	apiCurrentStreamsFeed = apiUrlStreamsFeedSaved;

	resetTemplate(TEMPLATE_ID_SAVED, true, true);
	selectDom("mSaved");

	$.getJSON(baseUrl + apiCurrentStreamsFeed, {}, function(data) {
		streamEntries = data.entries;
		triggerOnEntriesLoadedListeners(streamEntries);
	});

	$("#simple_view_header").html(simpleViewHeaderTmpl({
		"title" : "Saved"
	}));
}

function loadSource(sourceId) {
	console.debug('load source', sourceId);
	resetTemplate(TEMPLATE_ID_SOURCE, false, false);
	apiCurrentStreamsFeed = apiUrlSourceId;

	var queryData = {};
	queryData.id = sourceId;

	$.getJSON(baseUrl + apiUrlSourcesGet, queryData, function(data) {
		$("#stream-header .content").html(sourceHeaderTmpl(data));
	}, "json");

	var queryData = {};
	queryData.sid = sourceId;

	$.getJSON(baseUrl + apiUrlSubscriptionsGet, queryData, function(data) {
		console.debug('source user subscription info', data);
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
	$("#stram-footer").hide();
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
	$("#stream-entries").html(pageUnknownTmpl());
}

function reloadStream() {
	clearView();
	loadEntries(0);
}

function cancelStreamGroup() {
	$("#newGroup").remove();
}

function newStreamGroup() {
	var input = $("#menusubs input:first-child");

	if (input.length == 0) {
		$("#menusubs")
				.prepend(
						"<div id='newGroup'><input size='8' type='text' value=''>\
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
	$.get(baseUrl + apiUrlSaveReaderSettings, queryGroups, function(data,
			status) { /* noop */
	});
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
	$("#stream-entries").html($("#content_start_all").html());
}


function toggleEditTools(caller, title) {
	$('#edit_tools').toggle();
	if ($('#edit_tools').is(':visible')) {
		$(caller).html('&laquo;&laquo;');
	} else {
		$(caller).html(title + '&nbsp;&raquo;&raquo;');
	}
}
// Source: src/main/js/legacy/reader.subscriptions.js
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

// Source: src/main/js/legacy/settings.js
var r = {};

var profilesSettingsTmpl = {};

function initSettings() {
	profilesSettingsTmpl = Handlebars.compile($("#all_profiles_settings_tmpl").html());

	var router = new Router().init();

	router.on('/v/:panel', loadSettingsView);
	router.on('/v/:panel/:val', loadSettingsView);

	r = router.getRoute();
}

function setupProfileView(data) {
	$("#profile_settings_list").html(profilesSettingsTmpl({
		"profiles" : data
	}));
	loadColorPickers();
}

function loadSettingsView(str, val) {
	console.log("show panel " + str + ", val " + val);
	$("#content_panel").children().each(function(i, o) {
		if (o.id == "error" || o.id == "info")
			return;
		$(o).hide();
	});
	$("#" + str).show();
}

function showMessage(selector, msg) {
	if (!msg || msg == '') {
		$("#" + selector).hide();
		return;
	}

	$("#" + selector + "_text").text(msg);
	$("#" + selector).show();
}

function showError(msg) {
	showMessage('error', msg);
}

function showInfo(msg) {
	showMessage('info', msg);
}

function loadColorPickers() {
	$("#profile_settings_list").children("div").each(function(i, o) {
		var pObj = $("#picker_" + o.id);
		pObj.change(changeProfileColor);
		var picker = new jscolor.color(pObj.get(0), {
			slider : true,
			pickerFaceColor : 'transparent',
			pickerMode : "HSV",
			pickerFace : 1,
			pickerBorder : 0,
			pickerInsetColor : 'black'
		});
		picker.fromString(pObj.attr("data-color"));
	});
}

function deleteProfile(id) {
	if (confirm("Are you sure? Deleting a profile means:\n"
			+ "* All recently read and saved information for this profile is deleted. \n"
			+ "* Any stream groups not used by other profiles are deleted.")) {
		showError('');// clears

		var queryData = {};
		queryData.pid = id;

		console.log("deleting profile  ", id);

		$.getJSON(baseUrl + '/api/v1/user/profiles/delete', queryData, function(data) {
			console.log(data);
			if (data.count > 0) {
				location.reload();
			} else if (data.error) {
				showError(data.error);
			}
		});
	}
}
// Source: src/main/js/legacy/subscriptions.js
var subscriptionsHome = '';

var subscriptions = [];
var subscriptionsIdx = [];

var filteredSubscriptions = [ {
	"l_subs_id" : 0,
	"s_subs_name" : 0
} ];
var selectedSubscriptionId = 0;

var allSubscriptionsTmpl = {};
//var subscriptionDetailsTmpl = {};
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

function setupSubsTemplates(){
	susbcriptionsHome = baseUrl + "/pages/susbcriptions.jsp";
	allSubscriptionsTmpl = Handlebars.compile($("#all_subscriptions_tmpl").html());
//	subscriptionDetailsTmpl = Handlebars.compile($("#subscription_details_tmpl").html());
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

function runSubsQuery(renderTmpl) {
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
//		$("#subscription_details").html(subscriptionDetailsTmpl(data));
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
//		$("#profile_stream_groups").html("");


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
//			$("#profile_stream_groups").append(profileStreamGrpTmpl({
//				"name" : k,
//				"streamid" : streamWithProfiles[k].l_stream_id,
//				"subid" : subsId,
//				"profiles" : streamWithProfiles[k].profiles
//			}));
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

//# sourceMappingURL=app.legacy.js.map