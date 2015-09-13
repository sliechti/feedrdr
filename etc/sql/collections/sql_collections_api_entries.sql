SELECT t1.l_collection_id, t1.s_feed_name, t2.l_xml_id, t2.s_xml_url, t3.s_title, t3.s_link 
	FROM feedreader.sourcecollectionslist AS t1 
	LEFT JOIN feedreader.feedsources as t2 
		ON t1.l_xml_id = t2.l_xml_id
	LEFT JOIN feedreader.feedsourcechanneldata as t3
		ON t2.l_xml_id = t3.l_xml_id WHERE t1.l_collection_id IN (41);
		