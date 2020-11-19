DROP KEYSPACE IF EXISTS orchestra;

CREATE KEYSPACE orchestra
    WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};

USE orchestra;

DROP TABLE IF EXISTS user;
CREATE TABLE user (
    id uuid,
    search_token text,
    join_time bigint,
    name text,
    email text,
    thumbnail blob,
    password text,
    PRIMARY KEY(id)
);

DROP TABLE IF EXISTS message;
CREATE TABLE message (
    uid uuid PRIMARY KEY,
    sender_id uuid,
    dest_id uuid,
    sent_time bigint,
    content text,
    mime_type tinyint,
    media_thumbnail blob,
    media_download_token text,
    status_msg tinyint
);

DROP TABLE IF EXISTS shared_file;
CREATE TABLE shared_file (
    uid uuid PRIMARY KEY,
    download_token text,
    file_bytes blob,
    mime_type tinyint,
    size bigint,
    date_time bigint
);
