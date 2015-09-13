--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
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

SET default_tablespace = '';

SET default_with_oids = false;

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
    s_title character varying(255) DEFAULT ''::character varying NOT NULL,
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
-- Name: feedsourcenodeattrs; Type: TABLE; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE TABLE feedsourcenodeattrs (
    l_node_id integer NOT NULL,
    attr_name text NOT NULL,
    attr_type text,
    attr_value text
);


ALTER TABLE feedreader.feedsourcenodeattrs OWNER TO sliechti;

--
-- Name: feedsourcenodeattrs_l_node_id_seq; Type: SEQUENCE; Schema: feedreader; Owner: sliechti
--

CREATE SEQUENCE feedsourcenodeattrs_l_node_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE feedreader.feedsourcenodeattrs_l_node_id_seq OWNER TO sliechti;

--
-- Name: feedsourcenodeattrs_l_node_id_seq; Type: SEQUENCE OWNED BY; Schema: feedreader; Owner: sliechti
--

ALTER SEQUENCE feedsourcenodeattrs_l_node_id_seq OWNED BY feedsourcenodeattrs.l_node_id;


--
-- Name: feedsourcenodes; Type: TABLE; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE TABLE feedsourcenodes (
    l_xml_id bigint NOT NULL,
    l_node_id integer NOT NULL,
    node_name text NOT NULL,
    node_val text
);


ALTER TABLE feedreader.feedsourcenodes OWNER TO sliechti;

--
-- Name: feedsourcenodes_l_node_id_seq; Type: SEQUENCE; Schema: feedreader; Owner: sliechti
--

CREATE SEQUENCE feedsourcenodes_l_node_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE feedreader.feedsourcenodes_l_node_id_seq OWNER TO sliechti;

--
-- Name: feedsourcenodes_l_node_id_seq; Type: SEQUENCE OWNED BY; Schema: feedreader; Owner: sliechti
--

ALTER SEQUENCE feedsourcenodes_l_node_id_seq OWNED BY feedsourcenodes.l_node_id;


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
    b_gaveup boolean DEFAULT false NOT NULL
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
-- Name: streamgroupviewoptions; Type: TABLE; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE TABLE streamgroupviewoptions (
    l_view_id integer NOT NULL,
    h_view_mode smallint DEFAULT 0 NOT NULL,
    b_show_featured boolean DEFAULT true NOT NULL,
    i_max_results integer DEFAULT 20 NOT NULL,
    h_sort_by smallint DEFAULT 0 NOT NULL
);


ALTER TABLE feedreader.streamgroupviewoptions OWNER TO sliechti;

--
-- Name: COLUMN streamgroupviewoptions.h_sort_by; Type: COMMENT; Schema: feedreader; Owner: sliechti
--

COMMENT ON COLUMN streamgroupviewoptions.h_sort_by IS '0 = publication date, 1 = discovery date.';


--
-- Name: streamgroupviewoptions_l_view_id_seq; Type: SEQUENCE; Schema: feedreader; Owner: sliechti
--

CREATE SEQUENCE streamgroupviewoptions_l_view_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE feedreader.streamgroupviewoptions_l_view_id_seq OWNER TO sliechti;

--
-- Name: streamgroupviewoptions_l_view_id_seq; Type: SEQUENCE OWNED BY; Schema: feedreader; Owner: sliechti
--

ALTER SEQUENCE streamgroupviewoptions_l_view_id_seq OWNED BY streamgroupviewoptions.l_view_id;


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
    i_unread smallint,
    t_last_read bigint,
    t_last_published bigint,
    h_publishwithdate smallint,
    h_publishmaxdays smallint,
    h_publishlimit smallint,
    b_publishtowall boolean,
    i_keepmaxdays smallint,
    i_articlesperday smallint
);


ALTER TABLE feedreader.userfeedsubscriptions OWNER TO sliechti;

--
-- Name: COLUMN userfeedsubscriptions.t_last_published; Type: COMMENT; Schema: feedreader; Owner: sliechti
--

COMMENT ON COLUMN userfeedsubscriptions.t_last_published IS '(job publishing job queries against it.
- get next entry in the discovery list table.
- get t_lastTimePublished from user subscription''s (this) table and publish all entries newer than t_lastTimePublished.)';


--
-- Name: COLUMN userfeedsubscriptions.h_publishwithdate; Type: COMMENT; Schema: feedreader; Owner: sliechti
--

COMMENT ON COLUMN userfeedsubscriptions.h_publishwithdate IS '0 (now), 1 (discovery date), 2 (pub date)';


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
    e_oauth character(2) DEFAULT '0'::bpchar NOT NULL,
    s_auth_token text DEFAULT ''::text NOT NULL,
    l_selected_profile_id bigint DEFAULT 0 NOT NULL,
    s_locale text DEFAULT ''::text NOT NULL,
    s_email text NOT NULL,
    s_screen_name character varying(12) DEFAULT ''::character varying NOT NULL,
    t_subscribed_at bigint DEFAULT 0 NOT NULL,
    b_verified boolean DEFAULT false NOT NULL,
    b_forgot_pwd boolean DEFAULT false NOT NULL,
    b_reg_sent boolean DEFAULT false NOT NULL
);


ALTER TABLE feedreader.users OWNER TO sliechti;

--
-- Name: COLUMN users.e_oauth; Type: COMMENT; Schema: feedreader; Owner: sliechti
--

COMMENT ON COLUMN users.e_oauth IS '0 = NONE, 1 = Facebook, 2 = Google+. See class OAuthType.';


--
-- Name: COLUMN users.s_auth_token; Type: COMMENT; Schema: feedreader; Owner: sliechti
--

COMMENT ON COLUMN users.s_auth_token IS 'oAuth Token';


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
    l_view_id integer
);


ALTER TABLE feedreader.userstreamgroups OWNER TO sliechti;

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
-- Name: l_node_id; Type: DEFAULT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY feedsourcenodeattrs ALTER COLUMN l_node_id SET DEFAULT nextval('feedsourcenodeattrs_l_node_id_seq'::regclass);


--
-- Name: l_node_id; Type: DEFAULT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY feedsourcenodes ALTER COLUMN l_node_id SET DEFAULT nextval('feedsourcenodes_l_node_id_seq'::regclass);


--
-- Name: l_xml_id; Type: DEFAULT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY feedsources ALTER COLUMN l_xml_id SET DEFAULT nextval('feedsources_l_xml_id_seq'::regclass);


--
-- Name: l_view_id; Type: DEFAULT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY streamgroupviewoptions ALTER COLUMN l_view_id SET DEFAULT nextval('streamgroupviewoptions_l_view_id_seq'::regclass);


--
-- Name: l_subs_id; Type: DEFAULT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY userfeedsubscriptions ALTER COLUMN l_subs_id SET DEFAULT nextval('userfeedsubscriptions_l_subs_id_seq'::regclass);


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
-- Name: streamgroupviewoptions_pkey; Type: CONSTRAINT; Schema: feedreader; Owner: sliechti; Tablespace: 
--

ALTER TABLE ONLY streamgroupviewoptions
    ADD CONSTRAINT streamgroupviewoptions_pkey PRIMARY KEY (l_view_id);


--
-- Name: userfeedsubscriptions_pkey; Type: CONSTRAINT; Schema: feedreader; Owner: sliechti; Tablespace: 
--

ALTER TABLE ONLY userfeedsubscriptions
    ADD CONSTRAINT userfeedsubscriptions_pkey PRIMARY KEY (l_subs_id);


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
-- Name: userfeedentriesinfo_l_entry_id; Type: INDEX; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE INDEX userfeedentriesinfo_l_entry_id ON userfeedentriesinfo USING btree (l_entry_id);


--
-- Name: userfeedentriesinfo_l_user_id; Type: INDEX; Schema: feedreader; Owner: sliechti; Tablespace: 
--

CREATE INDEX userfeedentriesinfo_l_user_id ON userfeedentriesinfo USING btree (l_user_id);


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
-- Name: viewoptions_folders_fkey; Type: FK CONSTRAINT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY userstreamgroups
    ADD CONSTRAINT viewoptions_folders_fkey FOREIGN KEY (l_view_id) REFERENCES streamgroupviewoptions(l_view_id);


--
-- Name: feedreader; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA feedreader FROM PUBLIC;
REVOKE ALL ON SCHEMA feedreader FROM postgres;
GRANT ALL ON SCHEMA feedreader TO postgres;
GRANT ALL ON SCHEMA feedreader TO feedreader;
GRANT ALL ON SCHEMA feedreader TO sliechti;


--
-- PostgreSQL database dump complete
--

