--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = feedreader, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

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
    s_authtoken text DEFAULT ''::text NOT NULL,
    l_selected_profile_id bigint DEFAULT 0 NOT NULL,
    s_locale text DEFAULT ''::text NOT NULL,
    s_email text,
    s_screen_name character varying(12) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE feedreader.users OWNER TO sliechti;

--
-- Name: COLUMN users.e_oauth; Type: COMMENT; Schema: feedreader; Owner: sliechti
--

COMMENT ON COLUMN users.e_oauth IS '0 = NONE, 1 = Facebook, 2 = Google+. See class OAuthType.';


--
-- Name: COLUMN users.s_authtoken; Type: COMMENT; Schema: feedreader; Owner: sliechti
--

COMMENT ON COLUMN users.s_authtoken IS 'oAuth Token';


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
-- Name: userstreamgroups_pkey; Type: CONSTRAINT; Schema: feedreader; Owner: sliechti; Tablespace: 
--

ALTER TABLE ONLY userstreamgroups
    ADD CONSTRAINT userstreamgroups_pkey PRIMARY KEY (l_stream_id);


--
-- Name: fkuserauthto821453; Type: FK CONSTRAINT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY userauthtokens
    ADD CONSTRAINT fkuserauthto821453 FOREIGN KEY (l_user_id) REFERENCES users(l_user_id);


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
-- Name: saved_entries_feed_entries_fkey; Type: FK CONSTRAINT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY usersavedentries
    ADD CONSTRAINT saved_entries_feed_entries_fkey FOREIGN KEY (l_entry_id) REFERENCES feedentries(l_entry_id);


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
-- Name: viewoptions_folders_fkey; Type: FK CONSTRAINT; Schema: feedreader; Owner: sliechti
--

ALTER TABLE ONLY userstreamgroups
    ADD CONSTRAINT viewoptions_folders_fkey FOREIGN KEY (l_view_id) REFERENCES streamgroupviewoptions(l_view_id);


--
-- PostgreSQL database dump complete
--

