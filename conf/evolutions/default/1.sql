# noinspection SqlNoDataSourceInspectionForFile

# --- !Ups

create table users (
  "userID" varchar(120) not null primary key,
  "username" varchar(120) not null,
  "hash" varchar (80) not null
);

# --- !Downs

drop table users;
