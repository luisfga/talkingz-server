create database IF NOT EXISTS talkingzappDB;
use talkingzappDB;
drop table IF EXISTS message;
drop table IF EXISTS user;
drop table IF EXISTS shared_file;

create table app_user (
    id uuid primary key,
    user_name varchar(255),
    thumbnail bytea,
    email varchar(255),
    join_time bigint,
    password varchar(255),
    search_token varchar(255)
);
grant all on app_user to javauser;

create table message (
    uuid bigint not null primary key,
    sender_id int not null,
    dest_id int not null,
    sent_time bigint,
    content varchar(1000),
    mime_type smallint not null,
    media_thumbnail bytea,
    media_download_token varchar(50),
    status_msg int not null,
    foreign key (sender_id) references app_user(id),
    foreign key (dest_id) references app_user(id)
);
create table shared_file (
    id serial primary key,
    download_token varchar(50) not null,
    file_bytes bytea not null,
    mime_type smallint not null,
    size int,
    date_time bigint
);