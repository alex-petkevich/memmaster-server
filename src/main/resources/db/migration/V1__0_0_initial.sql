CREATE TABLE IF NOT EXISTS `roles`
(
	`id`   INT PRIMARY KEY AUTO_INCREMENT,
	`name` VARCHAR(255)
);

insert into roles (id, name) values (1, 'ROLE_ADMIN');
insert into roles (id, name) values (2, 'ROLE_MODERATOR');
insert into roles (id, name) values (3, 'ROLE_USER');

CREATE TABLE IF NOT EXISTS `users`
(
    `id`       INT PRIMARY KEY AUTO_INCREMENT,
    `username` VARCHAR(255),
    `firstname` VARCHAR(255),
    `lastname` VARCHAR(255),
    `email`    VARCHAR(255),
    `password` VARCHAR(150),
    `image` VARCHAR(255),
    `lang` VARCHAR(10),
    `activation_key` VARCHAR(255),
    `active`   INT default 0,
    `created_at`  TIMESTAMP,
    `last_modified_at`  TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_roles
(
	user_id INT NOT NULL,
	role_id INT NOT NULL
);

insert into users (id, username, email, password, active, created_at) values (1, 'admin', 'admin@local', '$2a$10$p9TRp2W3W4Hf4L6FrBXmAeMgJXKODbtCng97kG4GTuDuR6lTemsLy', 1, NOW()); -- adminadminadmin

insert into user_roles values (1, 1);

CREATE TABLE settings
(
    `id`        INT PRIMARY KEY AUTO_INCREMENT,
    `user_id`   INT NOT NULL,
    `name`      VARCHAR(255),
    `value`     TEXT,
    `created_at`  TIMESTAMP,
    `last_modified_at`  TIMESTAMP
);

create table dictionary
(
    `id`        BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id`   INT NOT NULL,
    `name`      VARCHAR(255),
    `name_img`      VARCHAR(255),
    `value`     VARCHAR(255),
    `value_img`     VARCHAR(255),
    `is_remembered` BOOLEAN,
    `is_archived` BOOLEAN,
    `created_at`  TIMESTAMP,
    `last_modified_at`  TIMESTAMP
);

CREATE TABLE folders
(
    `id`        INT PRIMARY KEY AUTO_INCREMENT,
    `parent_id`   INT NOT NULL,
    `user_id`   INT NOT NULL,
    `uuid`      VARCHAR(255),
    `name`      VARCHAR(255),
    `icon`      VARCHAR(255),
    `active`      BOOLEAN,
    `public`      BOOLEAN,
    `created_at`  TIMESTAMP,
    `last_modified_at`  TIMESTAMP
);

CREATE TABLE dictionary_roles
(
    folder_id INT NOT NULL,
    dictionary_id INT NOT NULL
);

