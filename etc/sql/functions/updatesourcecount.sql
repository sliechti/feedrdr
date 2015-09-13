CREATE OR REPLACE FUNCTION feedreader.updateSourceCount(xmlid bigint, t0 int8, t1 int8, t2 int8) RETURNS text AS '
    DECLARE
        count integer;
        count0 integer;
        count1 integer;
        count2 integer;
    BEGIN
        count := (select count(l_entry_id) from feedreader.feedentries where l_xml_id = xmlId);
        count0 := (select count(l_entry_id) from feedreader.feedentries where l_xml_id = xmlId and t_pub_date > t0);
        count1 := (select count(l_entry_id) from feedreader.feedentries where l_xml_id = xmlId and t_pub_date > t1);
        count2 := (select count(l_entry_id) from feedreader.feedentries where l_xml_id = xmlId and t_pub_date > t2);

        update feedreader.feedsources set i_total_entries = count, i_count_0 = count0, i_count_1 = count1, i_count_2 = count2 where l_xml_id = xmlId;

        return count0 || '' '' || count1 || '' ''  || count2 || '', all '' || count;
    END;
' language plpgsql;

