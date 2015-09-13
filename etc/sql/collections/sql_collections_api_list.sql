SELECT t0.*, count(t1.l_xml_id) as i_feeds FROM feedreader.sourcecollections as t0 
	LEFT JOIN feedreader.sourcecollectionslist AS t1
		ON t0.l_collection_id = t1.l_collection_id
	GROUP BY t0.l_collection_id;
