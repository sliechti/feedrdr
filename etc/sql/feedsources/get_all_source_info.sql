select *, t1.s_description as s_data_description from feedreader.feedsources as t0
    left join feedreader.feedsourcechanneldata as t1
        on t0.l_xml_id = t1.l_xml_id
    left join feedreader.feedsourcechannelimage as t2
        on t0.l_xml_id = t2.l_xml_id
    where t0.l_xml_id = 385