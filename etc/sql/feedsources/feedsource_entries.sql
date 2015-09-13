SELECT * FROM feedreader.feedsources as t0 
LEFT JOIN feedreader.feedentries as t1 ON
t0.l_xml_id = t1.l_xml_id WHERE t0.l_xml_id = 13867;

-- FeedEntriesTable.save(long, XmlFeedEntry, boolean)
-- DELETE FROM feedreader.feedentries WHERE 
-- s_link = 'http://feedproxy.google.com/~r/imgurgallery/~3/rto8c8ymibq/bsp7fqnhttp://imgur.com/gallery/bsp7fqn' OR 
-- s_title = 'The Tickle Monster'
