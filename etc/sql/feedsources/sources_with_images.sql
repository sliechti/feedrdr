SELECT l_xml_id FROM feedreader.feedentries 
    WHERE s_thumb_url != ''
    GROUP BY l_xml_id

