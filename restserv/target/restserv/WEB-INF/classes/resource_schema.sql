DROP DATABASE resource_db;
CREATE DATABASE resource_db WITH OWNER spring;

CREATE TABLE settings (
  uin  integer,
  hex varchar(8) not null,
  primary key (uin)
);
