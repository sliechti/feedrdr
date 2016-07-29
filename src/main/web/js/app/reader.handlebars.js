Handlebars.registerHelper("favico", function favico(url) {
	return baseFavicoDomain + url;
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
