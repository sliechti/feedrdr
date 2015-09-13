INSERT INTO feedreader.sourcecollections (l_created_by, s_description, s_name) 
VALUES (%d, '%s', '%s');

INSERT INTO feedreader.sourcecollectionslist (s_feed_name, l_xml_id, l_collection_id)
VALUES ('%s', %d, %d);
