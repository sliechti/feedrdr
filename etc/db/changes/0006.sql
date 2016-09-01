CREATE TABLE feedreader.collections (
l_user_id bigint NOT NULL,
l_collection_id bigint NOT NULL
);

ALTER TABLE ONLY feedreader.collections
    ADD CONSTRAINT collections_l_user_id FOREIGN KEY (l_user_id) REFERENCES feedreader.users(l_user_id);
    
ALTER TABLE ONLY feedreader.collections
    ADD CONSTRAINT collections_l_collection_id FOREIGN KEY (l_collection_id) REFERENCES feedreader.sourcecollections(l_collection_id);