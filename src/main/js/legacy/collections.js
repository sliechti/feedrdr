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

	var queryData = {};
	collectionsIds = [];
	$.get(baseUrl + apiCollectionsList, queryData, function(data, status) {
		collections = data.entries;
		if (data.entries) {
			for (var x = 0; x < data.entries.length; x++) {
				var c = data.entries[x];
				collectionsIds[collectionsIds.length] = c.l_collection_id;
				collectionsById[c.l_collection_id] = c;
				$("#collections-content .wrapper").append(collectionTmpl({"c" : c}));
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
