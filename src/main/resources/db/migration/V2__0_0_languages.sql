CREATE TABLE IF NOT EXISTS `directory`
(
	`id`   INT PRIMARY KEY AUTO_INCREMENT,
	`type` VARCHAR(20),
	`key` VARCHAR(255),
	`value` VARCHAR(255)
);

create index IF NOT EXISTS `key`
    on directory (`key`);

insert into directory (`key`, value) values ('be','Belarussian');
insert into directory (`key`, value) values ('bg','Bulgarian');
insert into directory (`key`, value) values ('cs','Czech');
insert into directory (`key`, value) values ('da','Danish');
insert into directory (`key`, value) values ('de','German');
insert into directory (`key`, value) values ('el','Greek');
insert into directory (`key`, value) values ('en','English');
insert into directory (`key`, value) values ('es','Spanish');
insert into directory (`key`, value) values ('et','Estonian');
insert into directory (`key`, value) values ('fi','Finnish');
insert into directory (`key`, value) values ('fr','French');
insert into directory (`key`, value) values ('hu','Hungarian');
insert into directory (`key`, value) values ('it','Italian');
insert into directory (`key`, value) values ('lt','Lithuanian');
insert into directory (`key`, value) values ('lt','Latvian');
insert into directory (`key`, value) values ('no','Norwegian');
insert into directory (`key`, value) values ('nl','Dutch');
insert into directory (`key`, value) values ('pl','Polish');
insert into directory (`key`, value) values ('pt','Portuguese');
insert into directory (`key`, value) values ('ru','Russian');
insert into directory (`key`, value) values ('sk','Slovak');
insert into directory (`key`, value) values ('sv','Swedish');
insert into directory (`key`, value) values ('tr','Turkish');
insert into directory (`key`, value) values ('uk','Ukrainian');
insert into directory (`key`, value) values ('zh','Chinese');

update directory set type = 'language';