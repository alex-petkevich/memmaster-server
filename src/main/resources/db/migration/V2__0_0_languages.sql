CREATE TABLE IF NOT EXISTS `directory`
(
	`id`   INT PRIMARY KEY AUTO_INCREMENT,
	`type` VARCHAR(20),
	`key` VARCHAR(255),
	`value` VARCHAR(255)
);

create index `key`
    on directory (`key`);

insert into directory (`key`, value) values ('AR','Arabic');
insert into directory (`key`, value) values ('BG','Bulgarian');
insert into directory (`key`, value) values ('CS','Czech');
insert into directory (`key`, value) values ('DA','Danish');
insert into directory (`key`, value) values ('DE','German');
insert into directory (`key`, value) values ('EL','Greek');
insert into directory (`key`, value) values ('EN','English (all English variants)');
insert into directory (`key`, value) values ('ES','Spanish');
insert into directory (`key`, value) values ('ET','Estonian');
insert into directory (`key`, value) values ('FI','Finnish');
insert into directory (`key`, value) values ('FR','French');
insert into directory (`key`, value) values ('HE','Hebrew');
insert into directory (`key`, value) values ('HU','Hungarian');
insert into directory (`key`, value) values ('ID','Indonesian');
insert into directory (`key`, value) values ('IT','Italian');
insert into directory (`key`, value) values ('JA','Japanese');
insert into directory (`key`, value) values ('KO','Korean');
insert into directory (`key`, value) values ('LT','Lithuanian');
insert into directory (`key`, value) values ('LV','Latvian');
insert into directory (`key`, value) values ('NB','Norwegian Bokmål');
insert into directory (`key`, value) values ('NL','Dutch');
insert into directory (`key`, value) values ('PL','Polish');
insert into directory (`key`, value) values ('PT','Portuguese (all Portuguese variants)');
insert into directory (`key`, value) values ('RO','Romanian');
insert into directory (`key`, value) values ('RU','Russian');
insert into directory (`key`, value) values ('SK','Slovak');
insert into directory (`key`, value) values ('SL','Slovenian');
insert into directory (`key`, value) values ('SV','Swedish');
insert into directory (`key`, value) values ('TH','Thai');
insert into directory (`key`, value) values ('TR','Turkish');
insert into directory (`key`, value) values ('UK','Ukrainian');
insert into directory (`key`, value) values ('VI','Vietvaluese');
insert into directory (`key`, value) values ('ZH','Chinese (all Chinese variants)');

update directory set type = 'language';