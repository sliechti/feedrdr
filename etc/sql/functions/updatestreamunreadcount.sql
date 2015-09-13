
CREATE OR REPLACE FUNCTION feedreader.updatestreamunreadcount(userId bigint, streamId bigint, c bigint, t0 bigint) RETURNS void AS '
    BEGIN
        execute ''update feedreader.userstreamgroups set l_gr_unread = '' || c || ''
            , t_gr_unread = '' || t0 || '' where l_stream_id = '' || streamId || '' and l_user_id = '' || userId || '';'';
    END;
' language plpgsql;