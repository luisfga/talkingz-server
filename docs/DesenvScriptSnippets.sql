use orchestra;

select * from user;
select * from message;
select * from shared_file;
 
delete from message;


insert into message (sender_id, dest_id, id_on_client, sent_time, content, mime_type, media, status_msg) 
values (4, 1, 0, 1589086300000, 'Testando o header da data', 100, null, 0);

delete from message where sender_id = 1;

delete from user where id > 0;

GRANT ALL ON *.* TO javauser@localhost;

update user set token='@test5' where id=5;
