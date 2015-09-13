-- DROP FUNCTION feedreader.gettotalentriescount(text, bigint);
-- sample select feedreader.getreadentries(40, 1417435200089, 200, '348,349,350');

create or replace function feedreader.gettotalentriescount(p1 text) returns 
    table(f1 bigint, f2 bigint, f3 bigint, f4 bigint)  as '
declare 
    sql text;
begin
    sql := ''select sum(i_total_entries), sum(i_count_0), sum(i_count_1), sum(i_count_2)
            from feedreader.feedsources where l_xml_id in ('' || p1 || '');'';

    return query execute sql;
end;
' language plpgsql;

create or replace function feedreader.gettotalentriescountsince(p1 text, t0 bigint) returns 
    table(f1 bigint)  as '
declare 
    sql text;
begin
    sql := ''select count(l_entry_id) from feedreader.feedentries where l_xml_id in ('' || p1 || '') 
                and t_pub_date > '' || t0 || '';'';

    return query execute sql;
end;
' language plpgsql;
