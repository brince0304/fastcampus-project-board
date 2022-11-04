insert into user_account (created_at, created_by, modified_at, modified_by, email, memo, nickname, user_id, user_password) values ('2022-05-01 13:04:04', 'test', '2022-04-02 16:00:20', 'Adelaida', 'ablunt0@kickstarter.com', 'Vestibulum', 'test', 'test', '$2a$10$ESEkLtJ50G8ly/StrADSpOvqebjUwvCtY4/ZhtT2ClqO2jfGcSCCe');
insert into user_account (created_at, created_by, modified_at, modified_by, email, memo, nickname, user_id, user_password) values ('2022-05-01 13:04:04', 'test2', '2022-04-02 16:00:20', 'Adelaida2', 'ablunt02@kickstarter.com', 'Vestibulum', 'test2', 'test2', '$2a$10$ESEkLtJ50G8ly/StrADSpOvqebjUwvCtY4/ZhtT2ClqO2jfGcSCCe');

insert into user_account_roles(user_account_user_id, roles) values ('test', 'ROLE_USER');
insert into user_account_roles(user_account_user_id, roles) values ('test2', 'ROLE_ADMIN');
insert into user_account_roles(user_account_user_id, roles) values ('test2', 'ROLE_USER');

insert into article (id,created_at, created_by, modified_at, modified_by, title, content, user_id,deleted, view_count,like_count) values (1,'2022-05-01 13:04:04', 'test', '2022-04-02 16:00:20', 'Adelaida', 'testtest', 'testesttesttest', 'test','N',0,0);

insert into hashtag (id,hashtag) values (1,'text');

insert into article_hashtag (article_id, hashtag_id) values (1,1);