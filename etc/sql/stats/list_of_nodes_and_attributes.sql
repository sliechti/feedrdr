SELECT t0.l_xml_id, t0.s_xml_url, * FROM feedreader.feedsourcenodes AS t1
    LEFT JOIN feedreader.feedsourcenodeattrs AS t2
        ON t1.l_node_id = t2.l_node_id
    LEFT JOIN feedreader.feedsources AS t0
        ON t1.l_xml_id = t0.l_xml_id
        WHERE t1.node_name like '%thumbnail%'
    ORDER BY t0.l_xml_id ASC, node_name ASC
