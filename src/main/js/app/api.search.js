var apiSourcesFind = '/api/v1/sources/find';

/**
 * Find sources by title.
 *
 * query.title = source title
 *
 * @param query
 * @param callback
 */
function apiFindSource(query, callback) {
	$.getJSON(baseUrl + apiSourcesFind, query, function(data) {
		callback(data);
	})
}
