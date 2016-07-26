
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
