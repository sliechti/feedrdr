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
		caller.text = "... show";
		domFeeds.hide();
	} else {
		var queryData = {};
		queryData.ids = collectionId;
		caller.text = "... hide";
		
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

