create database if not exists backend;

use backend;

create table if not exists user
(
    name     varchar(50) primary key,
    password varchar(50)
);

create table if not exists mifare1k
(
    uid      varbinary(10) primary key,
    data     varbinary(48),
    username varchar(50),
    constraint fk_mifare1k_username foreign key (username) references user (name)
);
