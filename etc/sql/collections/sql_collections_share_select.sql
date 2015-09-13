SELECT t2.s_subs_name, t3.l_xml_id FROM feedreader.userstreamgroups AS t0
	LEFT JOIN feedreader.userstreamgroupfeedsubscription AS t1
		ON t0.l_stream_id = t1.l_stream_id
	LEFT JOIN feedreader.userfeedsubscriptions AS t2
		ON t1.l_subs_id = t2.l_subs_id
	LEFT JOIN feedreader.feedsources AS t3
		ON t2.l_xml_id = t3.l_xml_id
WHERE t0.l_stream_id = 259

