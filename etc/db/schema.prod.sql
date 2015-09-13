--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: feedreader; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA feedreader;


ALTER SCHEMA feedreader OWNER TO postgres;

SET search_path = feedreader, pg_catalog;

--
-- Name: getreadentries(integer, bigint, integer, text); Type: FUNCTION; Schema: feedreader; Owner: sliechti
--

CREATE FUNCTION getreadentries(p1 integer, p2 bigint, p3 integer, p4 text) RETURNS text
    LANGUAGE plpgsql
    AS $_$
declare 
    rec record;
    sql text;
    ret text;
begin
    sql := 'select t0.l_entry_id from feedreader.userfeedentriesinfo as t0 
        left join feedreader.feedentries as t1
            on t0.l_entry_id = t1.l_entry_id
    where  
        t0.l_profile_id = ' || $1 ||' and t0.b_read = true 
        and t1.t_pub_date > ' || $2 ||'
         and t1.l_xml_id in (' || $4 ||')
         limit ' || $3 || ';';

    ret := '';
    for rec in execute sql
    loop
        ret := ret || rec.l_entry_id || ',';
    end loop;

    ret := trim(trailing ',' from ret);

    return ret;
        
end;
$_$;


ALTER FUNCTION feedreader.getreadentries(p1 integer, p2 bigint, p3 integer, p4 text) OWNER TO sliechti;

--
-- Name: gettotalentriescount(text); Type: FUNCTION; Schema: feedreader; Owner: sliechti
--

CREATE FUNCTION gettotalentriescount(p1 text) RETURNS TABLE(f1 bigint, f2 bigint, f3 bigint, f4 bigint)
    LANGUAGE plpgsql
    AS $$
declare 
    sql text;
begin
    sql := 'select sum(i_total_entries), sum(i_count_0), sum(i_count_1), sum(i_count_2)
            from feedreader.feedsources where l_xml_id in (' || p1 || ');';

    return query execute sql;
end;
$$;


ALTER FUNCTION feedreader.gettotalentriescount(p1 text) OWNER TO sliechti;

--
-- Name: gettotalentriescountsince(text, bigint); Type: FUNCTION; Schema: feedreader; Owner: sliechti
--

CREATE FUNCTION gettotalentriescountsince(p1 text, t0 bigint) RETURNS TABLE(f1 bigint)
    LANGUAGE plpgsql
    AS $$
declare 
    sql text;
begin
    sql := 'select count(l_entry_id) from feedreader.feedentries where l_xml_id in (' || p1 || ') 
                and t_pub_date > ' || t0 || ';';

    return query execute sql;
end;
$$;


ALTER FUNCTION feedreader.gettotalentriescountsince(p1 text, t0 bigint) OWNER TO sliechti;

--
-- Name: updatesourcecount(bigint, bigint, bigint, bigint); Type: FUNCTION; Schema: feedreader; Owner: sliechti
--

CREATE FUNCTION updatesourcecount(xmlid bigint, t0 bigint, t1 bigint, t2 bigint) RETURNS text
    LANGUAGE plpgsql
    AS $$
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

        return count0 || ' ' || count1 || ' '  || count2 || ', all ' || count;
    END;
$$;


ALTER FUNCTION feedreader.updatesourcecount(xmlid bigint, t0 bigint, t1 bigint, t2 bigint) OWNER TO sliechti;

--
-- Name: updatestreamunreadcount(bigint, bigint, bigint, bigint); Type: FUNCTION; Schema: feedreader; Owner: sliechti
--

CREATE FUNCTION updatestreamunreadcount(userid bigint, streamid bigint, c bigint, t0 bigint) RETURNS void
    LANGUAGE plpgsql
    AS $$
    BEGIN
        execute 'update feedreader.userstreamgroups set l_gr_unread = ' || c || '
            , t_gr_unread = ' || t0 || ' where l_stream_id = ' || streamId || ' and l_user_id = ' || userId || ';';
    END;
$$;


ALTER FUNCTION feedreader.updatestreamunreadcount(userid bigint, streamid bigint, c bigint, t0 bigint) OWNER TO sliechti;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: admins; Type: TABLE; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE TABLE admins (
    l_user_id bigint NOT NULL,
    l_admin_id bigint NOT NULL
);


ALTER TABLE feedreader.admins OWNER TO sliechti;

--
-- Name: feedentries; Type: TABLE; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE TABLE feedentries (
    l_entry_id bigint NOT NULL,
    l_xml_id bigint NOT NULL,
    s_link text NOT NULL,
    s_title text NOT NULL,
    s_content text NOT NULL,
    t_pub_date bigint DEFAULT 0 NOT NULL,
    t_discovered_at bigint DEFAULT 0 NOT NULL,
    s_thumb_url text DEFAULT ''::text NOT NULL,
    s_description text DEFAULT ''::text NOT NULL,
    s_clean_content text DEFAULT ''::text NOT NULL
);


ALTER TABLE feedreader.feedentries OWNER TO sliechti;

--
-- Name: feedentries_l_entry_id_seq; Type: SEQUENCE; Schema: feedreader; Owner: sliechti
--

CREATE SEQUENCE feedentries_l_entry_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE feedreader.feedentries_l_entry_id_seq OWNER TO sliechti;

--
-- Name: feedentries_l_entry_id_seq; Type: SEQUENCE OWNED BY; Schema: feedreader; Owner: sliechti
--

ALTER SEQUENCE feedentries_l_entry_id_seq OWNED BY feedentries.l_entry_id;


--
-- Name: feedentriesdata; Type: TABLE; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE TABLE feedentriesdata (
    l_entry_id bigint NOT NULL,
    l_clicks bigint DEFAULT 0 NOT NULL,
    l_fb_likes bigint NOT NULL
);


ALTER TABLE feedreader.feedentriesdata OWNER TO sliechti;

--
-- Name: feedentriesimages; Type: TABLE; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE TABLE feedentriesimages (
    l_image_id bigint NOT NULL,
    l_entry_id bigint NOT NULL,
    s_image_src text NOT NULL,
    i_height integer NOT NULL,
    i_width integer NOT NULL
);


ALTER TABLE feedreader.feedentriesimages OWNER TO sliechti;

--
-- Name: feedentriesimages_l_image_id_seq; Type: SEQUENCE; Schema: feedreader; Owner: sliechti
--

CREATE SEQUENCE feedentriesimages_l_image_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE feedreader.feedentriesimages_l_image_id_seq OWNER TO sliechti;

--
-- Name: feedentriesimages_l_image_id_seq; Type: SEQUENCE OWNED BY; Schema: feedreader; Owner: sliechti
--

ALTER SEQUENCE feedentriesimages_l_image_id_seq OWNED BY feedentriesimages.l_image_id;


--
-- Name: feedentrieslinks; Type: TABLE; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE TABLE feedentrieslinks (
    l_link_id bigint NOT NULL,
    l_entry_id bigint NOT NULL,
    s_href text NOT NULL,
    s_link_text text NOT NULL
);


ALTER TABLE feedreader.feedentrieslinks OWNER TO sliechti;

--
-- Name: feedentrieslinks_l_link_id_seq; Type: SEQUENCE; Schema: feedreader; Owner: sliechti
--

CREATE SEQUENCE feedentrieslinks_l_link_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE feedreader.feedentrieslinks_l_link_id_seq OWNER TO sliechti;

--
-- Name: feedentrieslinks_l_link_id_seq; Type: SEQUENCE OWNED BY; Schema: feedreader; Owner: sliechti
--

ALTER SEQUENCE feedentrieslinks_l_link_id_seq OWNED BY feedentrieslinks.l_link_id;


--
-- Name: feedsourcechanneldata; Type: TABLE; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE TABLE feedsourcechanneldata (
    l_xml_id bigint NOT NULL,
    s_title text DEFAULT ''::character varying NOT NULL,
    s_link character varying(255) NOT NULL,
    s_lang character varying(10) DEFAULT ''::character varying NOT NULL,
    s_description text DEFAULT ''::text NOT NULL,
    b_hasico boolean DEFAULT false NOT NULL,
    s_ico_type text DEFAULT ''::text NOT NULL
);


ALTER TABLE feedreader.feedsourcechanneldata OWNER TO sliechti;

--
-- Name: feedsourcechannelimage; Type: TABLE; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE TABLE feedsourcechannelimage (
    l_xml_id bigint NOT NULL,
    s_title character varying(255) DEFAULT ''::character varying NOT NULL,
    s_img_url character varying(255) DEFAULT ''::character varying NOT NULL,
    s_link character varying(255) DEFAULT ''::character varying NOT NULL,
    i_width integer DEFAULT 0 NOT NULL,
    i_height integer DEFAULT 0 NOT NULL,
    s_description text DEFAULT ''::text NOT NULL
);


ALTER TABLE feedreader.feedsourcechannelimage OWNER TO sliechti;

--
-- Name: feedsourcenodes; Type: TABLE; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE TABLE feedsourcenodes (
    l_xml_id bigint NOT NULL
);


ALTER TABLE feedreader.feedsourcenodes OWNER TO sliechti;

--
-- Name: feedsources; Type: TABLE; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE TABLE feedsources (
    l_xml_id bigint NOT NULL,
    s_xml_url character varying(255) NOT NULL,
    t_added_at bigint DEFAULT 0 NOT NULL,
    t_validated_at bigint DEFAULT 0 NOT NULL,
    t_checked_at bigint DEFAULT 0 NOT NULL,
    t_forcechecked_at bigint DEFAULT 0 NOT NULL,
    h_error_code smallint DEFAULT 0 NOT NULL,
    h_error_count smallint DEFAULT 0 NOT NULL,
    s_last_error text DEFAULT ''::text NOT NULL,
    l_lastfetch_size bigint DEFAULT 0 NOT NULL,
    i_fetch_count integer DEFAULT 0 NOT NULL,
    b_gaveup boolean DEFAULT false NOT NULL,
    i_count_0 integer DEFAULT 0 NOT NULL,
    i_count_1 integer DEFAULT 0 NOT NULL,
    i_count_2 integer DEFAULT 0 NOT NULL,
    i_total_entries integer DEFAULT 0 NOT NULL
);


ALTER TABLE feedreader.feedsources OWNER TO sliechti;

--
-- Name: feedsources_l_xml_id_seq; Type: SEQUENCE; Schema: feedreader; Owner: sliechti
--

CREATE SEQUENCE feedsources_l_xml_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE feedreader.feedsources_l_xml_id_seq OWNER TO sliechti;

--
-- Name: feedsources_l_xml_id_seq; Type: SEQUENCE OWNED BY; Schema: feedreader; Owner: sliechti
--

ALTER SEQUENCE feedsources_l_xml_id_seq OWNED BY feedsources.l_xml_id;


--
-- Name: httperrors; Type: TABLE; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE TABLE httperrors (
    id integer NOT NULL,
    s_http_code integer NOT NULL,
    s_error_code text NOT NULL,
    b_sent boolean DEFAULT false NOT NULL
);


ALTER TABLE feedreader.httperrors OWNER TO sliechti;

--
-- Name: httperrors_id_seq; Type: SEQUENCE; Schema: feedreader; Owner: sliechti
--

CREATE SEQUENCE httperrors_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE feedreader.httperrors_id_seq OWNER TO sliechti;

--
-- Name: httperrors_id_seq; Type: SEQUENCE OWNED BY; Schema: feedreader; Owner: sliechti
--

ALTER SEQUENCE httperrors_id_seq OWNED BY httperrors.id;


--
-- Name: seq_feedsources; Type: SEQUENCE; Schema: feedreader; Owner: sliechti
--

CREATE SEQUENCE seq_feedsources
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE feedreader.seq_feedsources OWNER TO sliechti;

--
-- Name: sourcecollections; Type: TABLE; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE TABLE sourcecollections (
    l_collection_id bigint NOT NULL,
    s_name text NOT NULL,
    s_description text NOT NULL,
    l_created_by bigint NOT NULL
);


ALTER TABLE feedreader.sourcecollections OWNER TO sliechti;

--
-- Name: sourcecollections_l_collection_id_seq; Type: SEQUENCE; Schema: feedreader; Owner: sliechti
--

CREATE SEQUENCE sourcecollections_l_collection_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE feedreader.sourcecollections_l_collection_id_seq OWNER TO sliechti;

--
-- Name: sourcecollections_l_collection_id_seq; Type: SEQUENCE OWNED BY; Schema: feedreader; Owner: sliechti
--

ALTER SEQUENCE sourcecollections_l_collection_id_seq OWNED BY sourcecollections.l_collection_id;


--
-- Name: sourcecollectionslist; Type: TABLE; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE TABLE sourcecollectionslist (
    l_collection_id bigint NOT NULL,
    l_xml_id bigint NOT NULL,
    l_entry_id bigint NOT NULL,
    s_feed_name text NOT NULL
);


ALTER TABLE feedreader.sourcecollectionslist OWNER TO sliechti;

--
-- Name: sourcecollectionslist_l_entry_id_seq; Type: SEQUENCE; Schema: feedreader; Owner: sliechti
--

CREATE SEQUENCE sourcecollectionslist_l_entry_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE feedreader.sourcecollectionslist_l_entry_id_seq OWNER TO sliechti;

--
-- Name: sourcecollectionslist_l_entry_id_seq; Type: SEQUENCE OWNED BY; Schema: feedreader; Owner: sliechti
--

ALTER SEQUENCE sourcecollectionslist_l_entry_id_seq OWNED BY sourcecollectionslist.l_entry_id;


--
-- Name: userauthtokens; Type: TABLE; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE TABLE userauthtokens (
    l_user_id bigint NOT NULL,
    e_oauth smallint DEFAULT 0 NOT NULL,
    s_auth_token text DEFAULT ''::text NOT NULL
);


ALTER TABLE feedreader.userauthtokens OWNER TO sliechti;

--
-- Name: COLUMN userauthtokens.e_oauth; Type: COMMENT; Schema: feedreader; Owner: sliechti
--

COMMENT ON COLUMN userauthtokens.e_oauth IS '0 = NONE, 1 = Facebook, 2 = Google+. See class OAuthType.';


--
-- Name: COLUMN userauthtokens.s_auth_token; Type: COMMENT; Schema: feedreader; Owner: sliechti
--

COMMENT ON COLUMN userauthtokens.s_auth_token IS 'oAuth Token';


--
-- Name: userfeedentriesinfo; Type: TABLE; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE TABLE userfeedentriesinfo (
    l_entry_id bigint NOT NULL,
    l_user_id bigint NOT NULL,
    l_profile_id bigint NOT NULL,
    b_read boolean DEFAULT false NOT NULL,
    b_hide boolean DEFAULT false NOT NULL,
    t_read_on bigint DEFAULT 0 NOT NULL,
    i_clicked integer DEFAULT 0 NOT NULL
);


ALTER TABLE feedreader.userfeedentriesinfo OWNER TO sliechti;

--
-- Name: TABLE userfeedentriesinfo; Type: COMMENT; Schema: feedreader; Owner: sliechti
--

COMMENT ON TABLE userfeedentriesinfo IS '(User specific data for each feed entry.
Mostly used as a filter on top of queries to FeedEntries,
e.g to filter out read or hidden articles)';


--
-- Name: userfeedsubscriptions; Type: TABLE; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE TABLE userfeedsubscriptions (
    l_subs_id bigint NOT NULL,
    l_user_id bigint NOT NULL,
    l_xml_id bigint NOT NULL,
    s_subs_name text NOT NULL,
    t_unread bigint DEFAULT 0 NOT NULL,
    l_unread bigint DEFAULT 0 NOT NULL,
    t_read_marker bigint DEFAULT 0 NOT NULL
);


ALTER TABLE feedreader.userfeedsubscriptions OWNER TO sliechti;

--
-- Name: COLUMN userfeedsubscriptions.t_unread; Type: COMMENT; Schema: feedreader; Owner: sliechti
--

COMMENT ON COLUMN userfeedsubscriptions.t_unread IS 'Last time we fetched the unread count.';


--
-- Name: userfeedsubscriptions_l_subs_id_seq; Type: SEQUENCE; Schema: feedreader; Owner: sliechti
--

CREATE SEQUENCE userfeedsubscriptions_l_subs_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE feedreader.userfeedsubscriptions_l_subs_id_seq OWNER TO sliechti;

--
-- Name: userfeedsubscriptions_l_subs_id_seq; Type: SEQUENCE OWNED BY; Schema: feedreader; Owner: sliechti
--

ALTER SEQUENCE userfeedsubscriptions_l_subs_id_seq OWNED BY userfeedsubscriptions.l_subs_id;


--
-- Name: userkeyvalues; Type: TABLE; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE TABLE userkeyvalues (
    l_key_id bigint NOT NULL,
    i_key_name integer DEFAULT 0 NOT NULL,
    s_key_value text NOT NULL,
    l_profile_id bigint NOT NULL,
    l_user_id bigint NOT NULL
);


ALTER TABLE feedreader.userkeyvalues OWNER TO sliechti;

--
-- Name: userkeyvalues_l_key_id_seq; Type: SEQUENCE; Schema: feedreader; Owner: sliechti
--

CREATE SEQUENCE userkeyvalues_l_key_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE feedreader.userkeyvalues_l_key_id_seq OWNER TO sliechti;

--
-- Name: userkeyvalues_l_key_id_seq; Type: SEQUENCE OWNED BY; Schema: feedreader; Owner: sliechti
--

ALTER SEQUENCE userkeyvalues_l_key_id_seq OWNED BY userkeyvalues.l_key_id;


--
-- Name: userprofiles; Type: TABLE; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE TABLE userprofiles (
    l_user_id bigint NOT NULL,
    l_profile_id bigint NOT NULL,
    s_profile_name text NOT NULL,
    s_color character varying(6) NOT NULL,
    b_default boolean DEFAULT false NOT NULL
);


ALTER TABLE feedreader.userprofiles OWNER TO sliechti;

--
-- Name: userprofiles_l_profile_id_seq; Type: SEQUENCE; Schema: feedreader; Owner: sliechti
--

CREATE SEQUENCE userprofiles_l_profile_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE feedreader.userprofiles_l_profile_id_seq OWNER TO sliechti;

--
-- Name: userprofiles_l_profile_id_seq; Type: SEQUENCE OWNED BY; Schema: feedreader; Owner: sliechti
--

ALTER SEQUENCE userprofiles_l_profile_id_seq OWNED BY userprofiles.l_profile_id;


--
-- Name: userprofilestreamgroup; Type: TABLE; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE TABLE userprofilestreamgroup (
    l_profile_id bigint NOT NULL,
    l_stream_id bigint NOT NULL
);


ALTER TABLE feedreader.userprofilestreamgroup OWNER TO sliechti;

--
-- Name: users; Type: TABLE; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE TABLE users (
    l_user_id bigint NOT NULL,
    s_pwd character varying(50) NOT NULL,
    l_selected_profile_id bigint DEFAULT 0 NOT NULL,
    s_locale text DEFAULT ''::text NOT NULL,
    s_email text NOT NULL,
    t_subscribed_at bigint DEFAULT 0 NOT NULL,
    b_verified boolean DEFAULT false NOT NULL,
    b_forgot_pwd boolean DEFAULT false NOT NULL,
    b_reg_sent boolean DEFAULT false NOT NULL,
    s_reg_code text DEFAULT ''::text NOT NULL,
    s_screen_name text DEFAULT ''::text NOT NULL,
    s_forgot_code text DEFAULT ''::text NOT NULL,
    s_reg_error text DEFAULT ''::text NOT NULL,
    e_main_oauth smallint DEFAULT 0 NOT NULL,
    e_user_type smallint DEFAULT 0 NOT NULL,
    s_cookie text DEFAULT ''::text NOT NULL,
    b_is_admin boolean DEFAULT false NOT NULL
);


ALTER TABLE feedreader.users OWNER TO sliechti;

--
-- Name: COLUMN users.e_main_oauth; Type: COMMENT; Schema: feedreader; Owner: sliechti
--

COMMENT ON COLUMN users.e_main_oauth IS 'Main oAuth Type. 0  if none, standard login. For other values see the OAuthType class.';


--
-- Name: COLUMN users.e_user_type; Type: COMMENT; Schema: feedreader; Owner: sliechti
--

COMMENT ON COLUMN users.e_user_type IS 'See userData.userType enum.';


--
-- Name: users_l_user_id_seq; Type: SEQUENCE; Schema: feedreader; Owner: sliechti
--

CREATE SEQUENCE users_l_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE feedreader.users_l_user_id_seq OWNER TO sliechti;

--
-- Name: users_l_user_id_seq; Type: SEQUENCE OWNED BY; Schema: feedreader; Owner: sliechti
--

ALTER SEQUENCE users_l_user_id_seq OWNED BY users.l_user_id;


--
-- Name: usersavedentries; Type: TABLE; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE TABLE usersavedentries (
    l_user_id bigint NOT NULL,
    l_profile_id bigint NOT NULL,
    l_entry_id bigint NOT NULL,
    t_saved_at bigint DEFAULT 0 NOT NULL,
    s_comments text DEFAULT ''::text NOT NULL
);


ALTER TABLE feedreader.usersavedentries OWNER TO sliechti;

--
-- Name: userstreamgroupfeedsubscription; Type: TABLE; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE TABLE userstreamgroupfeedsubscription (
    l_stream_id bigint NOT NULL,
    l_subs_id bigint NOT NULL
);


ALTER TABLE feedreader.userstreamgroupfeedsubscription OWNER TO sliechti;

--
-- Name: userstreamgroups; Type: TABLE; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE TABLE userstreamgroups (
    l_stream_id bigint NOT NULL,
    s_stream_name text NOT NULL,
    l_user_id bigint NOT NULL,
    t_gr_unread bigint DEFAULT 0 NOT NULL,
    l_gr_unread bigint DEFAULT 0 NOT NULL,
    t_gr_max_time bigint DEFAULT 0 NOT NULL
);


ALTER TABLE feedreader.userstreamgroups OWNER TO sliechti;

--
-- Name: COLUMN userstreamgroups.t_gr_max_time; Type: COMMENT; Schema: feedreader; Owner: sliechti
--

COMMENT ON COLUMN userstreamgroups.t_gr_max_time IS 'point in time we are at reading articles. no need to go behind this line.';


--
-- Name: userstreamgroups_l_stream_id_seq; Type: SEQUENCE; Schema: feedreader; Owner: sliechti
--

CREATE SEQUENCE userstreamgroups_l_stream_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE feedreader.userstreamgroups_l_stream_id_seq OWNER TO sliechti;

--
-- Name: userstreamgroups_l_stream_id_seq; Type: SEQUENCE OWNED BY; Schema: feedreader; Owner: sliechti
--

ALTER SEQUENCE userstreamgroups_l_stream_id_seq OWNED BY userstreamgroups.l_stream_id;


--
-- Name: userstreamgroupviewoptions; Type: TABLE; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE TABLE userstreamgroupviewoptions (
    h_view_mode smallint DEFAULT 0 NOT NULL,
    b_show_featured boolean DEFAULT true NOT NULL,
    i_max_results integer DEFAULT 20 NOT NULL,
    h_sort_by smallint DEFAULT 0 NOT NULL,
    l_v_stream_id bigint NOT NULL,
    h_filter_by smallint DEFAULT 0 NOT NULL
);


ALTER TABLE feedreader.userstreamgroupviewoptions OWNER TO sliechti;

--
-- Name: COLUMN userstreamgroupviewoptions.h_sort_by; Type: COMMENT; Schema: feedreader; Owner: sliechti
--

COMMENT ON COLUMN userstreamgroupviewoptions.h_sort_by IS '0 = publication date, 1 = discovery date.';


--
-- Name: COLUMN userstreamgroupviewoptions.h_filter_by; Type: COMMENT; Schema: feedreader; Owner: sliechti
--

COMMENT ON COLUMN userstreamgroupviewoptions.h_filter_by IS '0 = all, 1 = unread only.';


--
-- Name: l_entry_id; Type: DEFAULT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY feedentries ALTER COLUMN l_entry_id SET DEFAULT nextval('feedentries_l_entry_id_seq'::regclass);


--
-- Name: l_image_id; Type: DEFAULT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY feedentriesimages ALTER COLUMN l_image_id SET DEFAULT nextval('feedentriesimages_l_image_id_seq'::regclass);


--
-- Name: l_link_id; Type: DEFAULT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY feedentrieslinks ALTER COLUMN l_link_id SET DEFAULT nextval('feedentrieslinks_l_link_id_seq'::regclass);


--
-- Name: l_xml_id; Type: DEFAULT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY feedsources ALTER COLUMN l_xml_id SET DEFAULT nextval('feedsources_l_xml_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY httperrors ALTER COLUMN id SET DEFAULT nextval('httperrors_id_seq'::regclass);


--
-- Name: l_collection_id; Type: DEFAULT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY sourcecollections ALTER COLUMN l_collection_id SET DEFAULT nextval('sourcecollections_l_collection_id_seq'::regclass);


--
-- Name: l_entry_id; Type: DEFAULT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY sourcecollectionslist ALTER COLUMN l_entry_id SET DEFAULT nextval('sourcecollectionslist_l_entry_id_seq'::regclass);


--
-- Name: l_subs_id; Type: DEFAULT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY userfeedsubscriptions ALTER COLUMN l_subs_id SET DEFAULT nextval('userfeedsubscriptions_l_subs_id_seq'::regclass);


--
-- Name: l_key_id; Type: DEFAULT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY userkeyvalues ALTER COLUMN l_key_id SET DEFAULT nextval('userkeyvalues_l_key_id_seq'::regclass);


--
-- Name: l_profile_id; Type: DEFAULT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY userprofiles ALTER COLUMN l_profile_id SET DEFAULT nextval('userprofiles_l_profile_id_seq'::regclass);


--
-- Name: l_user_id; Type: DEFAULT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY users ALTER COLUMN l_user_id SET DEFAULT nextval('users_l_user_id_seq'::regclass);


--
-- Name: l_stream_id; Type: DEFAULT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY userstreamgroups ALTER COLUMN l_stream_id SET DEFAULT nextval('userstreamgroups_l_stream_id_seq'::regclass);


--
-- Name: admins_l_user_id_key; Type: CONSTRAINT; Schema: feedreader; Owner: sliechti; Tablespace: 
--

ALTER TABLE ONLY admins
    ADD CONSTRAINT admins_l_user_id_key UNIQUE (l_user_id);


--
-- Name: admins_pkey; Type: CONSTRAINT; Schema: feedreader; Owner: sliechti; Tablespace: 
--

ALTER TABLE ONLY admins
    ADD CONSTRAINT admins_pkey PRIMARY KEY (l_admin_id);


--
-- Name: feedentries_l_entry_id_key; Type: CONSTRAINT; Schema: feedreader; Owner: sliechti; Tablespace: 
--

ALTER TABLE ONLY feedentries
    ADD CONSTRAINT feedentries_l_entry_id_key UNIQUE (l_entry_id);


--
-- Name: feedentries_pkey; Type: CONSTRAINT; Schema: feedreader; Owner: sliechti; Tablespace: 
--

ALTER TABLE ONLY feedentries
    ADD CONSTRAINT feedentries_pkey PRIMARY KEY (l_entry_id);


--
-- Name: feedentries_s_link_key; Type: CONSTRAINT; Schema: feedreader; Owner: sliechti; Tablespace: 
--

ALTER TABLE ONLY feedentries
    ADD CONSTRAINT feedentries_s_link_key UNIQUE (s_link);


--
-- Name: feedentriesdata_l_entry_id_key; Type: CONSTRAINT; Schema: feedreader; Owner: sliechti; Tablespace: 
--

ALTER TABLE ONLY feedentriesdata
    ADD CONSTRAINT feedentriesdata_l_entry_id_key UNIQUE (l_entry_id);


--
-- Name: feedentriesimages_pkey; Type: CONSTRAINT; Schema: feedreader; Owner: sliechti; Tablespace: 
--

ALTER TABLE ONLY feedentriesimages
    ADD CONSTRAINT feedentriesimages_pkey PRIMARY KEY (l_image_id);


--
-- Name: feedentrieslinks_pkey; Type: CONSTRAINT; Schema: feedreader; Owner: sliechti; Tablespace: 
--

ALTER TABLE ONLY feedentrieslinks
    ADD CONSTRAINT feedentrieslinks_pkey PRIMARY KEY (l_link_id);


--
-- Name: feedsourcechanneldata_pkey; Type: CONSTRAINT; Schema: feedreader; Owner: sliechti; Tablespace: 
--

ALTER TABLE ONLY feedsourcechanneldata
    ADD CONSTRAINT feedsourcechanneldata_pkey PRIMARY KEY (l_xml_id);


--
-- Name: feedsourcechannelimage_pkey; Type: CONSTRAINT; Schema: feedreader; Owner: sliechti; Tablespace: 
--

ALTER TABLE ONLY feedsourcechannelimage
    ADD CONSTRAINT feedsourcechannelimage_pkey PRIMARY KEY (l_xml_id);


--
-- Name: feedsources_pkey; Type: CONSTRAINT; Schema: feedreader; Owner: sliechti; Tablespace: 
--

ALTER TABLE ONLY feedsources
    ADD CONSTRAINT feedsources_pkey PRIMARY KEY (l_xml_id);


--
-- Name: feedsources_s_xmlurl; Type: CONSTRAINT; Schema: feedreader; Owner: sliechti; Tablespace: 
--

ALTER TABLE ONLY feedsources
    ADD CONSTRAINT feedsources_s_xmlurl UNIQUE (s_xml_url);


--
-- Name: httperrors_pkey; Type: CONSTRAINT; Schema: feedreader; Owner: sliechti; Tablespace: 
--

ALTER TABLE ONLY httperrors
    ADD CONSTRAINT httperrors_pkey PRIMARY KEY (id);


--
-- Name: sourcecollections_pkey; Type: CONSTRAINT; Schema: feedreader; Owner: sliechti; Tablespace: 
--

ALTER TABLE ONLY sourcecollections
    ADD CONSTRAINT sourcecollections_pkey PRIMARY KEY (l_collection_id);


--
-- Name: sourcecollectionslist_pkey; Type: CONSTRAINT; Schema: feedreader; Owner: sliechti; Tablespace: 
--

ALTER TABLE ONLY sourcecollectionslist
    ADD CONSTRAINT sourcecollectionslist_pkey PRIMARY KEY (l_entry_id);


--
-- Name: userfeedsubscriptions_pkey; Type: CONSTRAINT; Schema: feedreader; Owner: sliechti; Tablespace: 
--

ALTER TABLE ONLY userfeedsubscriptions
    ADD CONSTRAINT userfeedsubscriptions_pkey PRIMARY KEY (l_subs_id);


--
-- Name: userkeyvalues_pkey; Type: CONSTRAINT; Schema: feedreader; Owner: sliechti; Tablespace: 
--

ALTER TABLE ONLY userkeyvalues
    ADD CONSTRAINT userkeyvalues_pkey PRIMARY KEY (l_key_id);


--
-- Name: userprofiles_pkey; Type: CONSTRAINT; Schema: feedreader; Owner: sliechti; Tablespace: 
--

ALTER TABLE ONLY userprofiles
    ADD CONSTRAINT userprofiles_pkey PRIMARY KEY (l_profile_id);


--
-- Name: users_pkey; Type: CONSTRAINT; Schema: feedreader; Owner: sliechti; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (l_user_id);


--
-- Name: users_s_email_key; Type: CONSTRAINT; Schema: feedreader; Owner: sliechti; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_s_email_key UNIQUE (s_email);


--
-- Name: userstreamgroups_pkey; Type: CONSTRAINT; Schema: feedreader; Owner: sliechti; Tablespace: 
--

ALTER TABLE ONLY userstreamgroups
    ADD CONSTRAINT userstreamgroups_pkey PRIMARY KEY (l_stream_id);


--
-- Name: userstreamgroupviewoptions_pkey; Type: CONSTRAINT; Schema: feedreader; Owner: sliechti; Tablespace: 
--

ALTER TABLE ONLY userstreamgroupviewoptions
    ADD CONSTRAINT userstreamgroupviewoptions_pkey PRIMARY KEY (l_v_stream_id);


--
-- Name: feedentries_l_xml_id; Type: INDEX; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE INDEX feedentries_l_xml_id ON feedentries USING btree (l_xml_id);


--
-- Name: feedentries_t_pub_date; Type: INDEX; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE INDEX feedentries_t_pub_date ON feedentries USING btree (t_pub_date);


--
-- Name: httperrors_s_http_code; Type: INDEX; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE INDEX httperrors_s_http_code ON httperrors USING btree (s_http_code);


--
-- Name: userfeedentriesinfo_l_entry_id; Type: INDEX; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE INDEX userfeedentriesinfo_l_entry_id ON userfeedentriesinfo USING btree (l_entry_id);


--
-- Name: userfeedentriesinfo_l_user_id; Type: INDEX; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE INDEX userfeedentriesinfo_l_user_id ON userfeedentriesinfo USING btree (l_user_id);


--
-- Name: userfeedsubscriptions_l_xml_id; Type: INDEX; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE INDEX userfeedsubscriptions_l_xml_id ON userfeedsubscriptions USING btree (l_xml_id);


--
-- Name: users_s_reg_code; Type: INDEX; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE INDEX users_s_reg_code ON users USING btree (s_reg_code);


--
-- Name: userstreamgroupfeedsubscription_l_stream_id; Type: INDEX; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE INDEX userstreamgroupfeedsubscription_l_stream_id ON userstreamgroupfeedsubscription USING btree (l_stream_id);


--
-- Name: userstreamgroupfeedsubscription_l_subs_id; Type: INDEX; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE INDEX userstreamgroupfeedsubscription_l_subs_id ON userstreamgroupfeedsubscription USING btree (l_subs_id);


--
-- Name: feedentries_images_l_entry_id; Type: FK CONSTRAINT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY feedentriesimages
    ADD CONSTRAINT feedentries_images_l_entry_id FOREIGN KEY (l_entry_id) REFERENCES feedentries(l_entry_id);


--
-- Name: feedentries_links_l_entry_id; Type: FK CONSTRAINT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY feedentrieslinks
    ADD CONSTRAINT feedentries_links_l_entry_id FOREIGN KEY (l_entry_id) REFERENCES feedentries(l_entry_id);


--
-- Name: feedsource_channeldata_fkey; Type: FK CONSTRAINT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY feedsourcechanneldata
    ADD CONSTRAINT feedsource_channeldata_fkey FOREIGN KEY (l_xml_id) REFERENCES feedsources(l_xml_id);


--
-- Name: feedsource_channelimage_fkey; Type: FK CONSTRAINT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY feedsourcechannelimage
    ADD CONSTRAINT feedsource_channelimage_fkey FOREIGN KEY (l_xml_id) REFERENCES feedsources(l_xml_id);


--
-- Name: feedsources_feedentries_fkey; Type: FK CONSTRAINT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY feedentries
    ADD CONSTRAINT feedsources_feedentries_fkey FOREIGN KEY (l_xml_id) REFERENCES feedsources(l_xml_id);


--
-- Name: fkuserauthto821453; Type: FK CONSTRAINT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY userauthtokens
    ADD CONSTRAINT fkuserauthto821453 FOREIGN KEY (l_user_id) REFERENCES users(l_user_id);


--
-- Name: fkuserfeeden702485; Type: FK CONSTRAINT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY userfeedentriesinfo
    ADD CONSTRAINT fkuserfeeden702485 FOREIGN KEY (l_user_id) REFERENCES users(l_user_id);


--
-- Name: groups_groupsubs_fkey; Type: FK CONSTRAINT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY userstreamgroupfeedsubscription
    ADD CONSTRAINT groups_groupsubs_fkey FOREIGN KEY (l_stream_id) REFERENCES userstreamgroups(l_stream_id);


--
-- Name: profile_streamgroups_fkey; Type: FK CONSTRAINT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY userprofilestreamgroup
    ADD CONSTRAINT profile_streamgroups_fkey FOREIGN KEY (l_profile_id) REFERENCES userprofiles(l_profile_id);


--
-- Name: profiles_fkey; Type: FK CONSTRAINT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY userprofiles
    ADD CONSTRAINT profiles_fkey FOREIGN KEY (l_user_id) REFERENCES users(l_user_id);


--
-- Name: saved_entries_profile_id_fkey; Type: FK CONSTRAINT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY usersavedentries
    ADD CONSTRAINT saved_entries_profile_id_fkey FOREIGN KEY (l_profile_id) REFERENCES userprofiles(l_profile_id);


--
-- Name: saved_entries_user_id_fkey; Type: FK CONSTRAINT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY usersavedentries
    ADD CONSTRAINT saved_entries_user_id_fkey FOREIGN KEY (l_user_id) REFERENCES users(l_user_id);


--
-- Name: stream_profiles_fkey; Type: FK CONSTRAINT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY userprofilestreamgroup
    ADD CONSTRAINT stream_profiles_fkey FOREIGN KEY (l_stream_id) REFERENCES userstreamgroups(l_stream_id);


--
-- Name: streamgroup_userfeed_fkey; Type: FK CONSTRAINT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY userstreamgroupfeedsubscription
    ADD CONSTRAINT streamgroup_userfeed_fkey FOREIGN KEY (l_subs_id) REFERENCES userfeedsubscriptions(l_subs_id);


--
-- Name: subscription_feedsources_fkey; Type: FK CONSTRAINT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY userfeedsubscriptions
    ADD CONSTRAINT subscription_feedsources_fkey FOREIGN KEY (l_xml_id) REFERENCES feedsources(l_xml_id);


--
-- Name: subscriptions_users_fkey; Type: FK CONSTRAINT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY userfeedsubscriptions
    ADD CONSTRAINT subscriptions_users_fkey FOREIGN KEY (l_user_id) REFERENCES users(l_user_id);


--
-- Name: user_streamgroup_fkey; Type: FK CONSTRAINT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY userstreamgroups
    ADD CONSTRAINT user_streamgroup_fkey FOREIGN KEY (l_user_id) REFERENCES users(l_user_id);


--
-- Name: userfeedentriesinfo_profile_id_fkey; Type: FK CONSTRAINT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY userfeedentriesinfo
    ADD CONSTRAINT userfeedentriesinfo_profile_id_fkey FOREIGN KEY (l_profile_id) REFERENCES userprofiles(l_profile_id);


--
-- Name: userprofiles_userkeyvalues_fkey; Type: FK CONSTRAINT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY userkeyvalues
    ADD CONSTRAINT userprofiles_userkeyvalues_fkey FOREIGN KEY (l_profile_id) REFERENCES userprofiles(l_profile_id);


--
-- Name: users_admins; Type: FK CONSTRAINT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY admins
    ADD CONSTRAINT users_admins FOREIGN KEY (l_user_id) REFERENCES users(l_user_id);


--
-- Name: feedreader; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA feedreader FROM PUBLIC;
REVOKE ALL ON SCHEMA feedreader FROM postgres;
GRANT ALL ON SCHEMA feedreader TO postgres;
GRANT ALL ON SCHEMA feedreader TO sliechti;


--
-- PostgreSQL database dump complete
--

