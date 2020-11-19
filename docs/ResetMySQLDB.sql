create database IF NOT EXISTS orchestra;
use orchestra;
drop table IF EXISTS message;
drop table IF EXISTS app_user;
drop table IF EXISTS shared_file;
create table app_user (
    id int not null auto_increment primary key,
    name varchar(255),
    thumbnail longblob,
    email varchar(255),
    join_time bigint,
    password varchar(255),
    token varchar(255)
);
create table message (
    uuid bigint not null primary key,
    sender_id int not null,
    dest_id int not null,
    sent_time bigint,
    content varchar(1000),
    mime_type tinyint not null,
    media_thumbnail longblob,
    media_download_token varchar(50),
    status_msg int not null,
    foreign key (sender_id) references app_user(id),
    foreign key (dest_id) references app_user(id)
);
create table shared_file (
    id int not null primary key auto_increment,
    download_token varchar(50) not null,
    file_bytes longblob not null,
    mime_type tinyint not null,
    size int,
    date_time bigint
);