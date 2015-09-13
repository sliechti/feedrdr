-- DROP FUNCTION feedreader.getreadentries(integer,bigint,integer,text);
-- sample select feedreader.getreadentries(40, 1417435200089, 200, '348,349,350');

create or replace function feedreader.getreadentries(p1 integer, p2 int8, p3 integer, p4 text) returns text as '
declare 
    rec record;
    sql text;
    ret text;
begin
    sql := ''select t0.l_entry_id from feedreader.userfeedentriesinfo as t0 
        left join feedreader.feedentries as t1
            on t0.l_entry_id = t1.l_entry_id
    where  
        t0.l_profile_id = '' || $1 ||'' and t0.b_read = true 
        and t1.t_pub_date > '' || $2 ||''
         and t1.l_xml_id in ('' || $4 ||'')
         limit '' || $3 || '';'';

    ret := '''';
    for rec in execute sql
    loop
        ret := ret || rec.l_entry_id || '','';
    end loop;

    ret := trim(trailing '','' from ret);

    return ret;
        
end;
' language plpgsql;

