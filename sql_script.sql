create database if not exists backend;

use backend;

create table if not exists user
(
    name     varchar(50) primary key,
    password varchar(50)
);

insert into user(name, password)
values ('admin', 'admin');