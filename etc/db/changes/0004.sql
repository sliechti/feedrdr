alter table feedreader.users add COLUMN l_verify_email_count bigint DEFAULT 0 NOT NULL;
alter table feedreader.users add COLUMN l_verify_email_date bigint;