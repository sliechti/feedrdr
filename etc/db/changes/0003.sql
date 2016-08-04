alter table feedreader.users add COLUMN verify_attempt smallint DEFAULT 0;
alter table feedreader.users add COLUMN b_acct_disabled BOOLEAN DEFAULT false;
