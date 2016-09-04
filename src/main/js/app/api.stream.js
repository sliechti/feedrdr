var apiUrlSubscriptionsAdd = '/api/v1/user/subscriptions/add';


/**
 * n = Name of Subscription. (optional)
 * u = URL of feed
 * sid = Stream ID where the subscription for the new feed should be added.
 *
 * @param queryData
 * @param callback to pass JSON result
 * @returns
 */
function addSubscription(queryData, callback) {
	$.getJSON(baseUrl + apiUrlSubscriptionsAdd, queryData, function(data) {
		callback(data);
	});
}