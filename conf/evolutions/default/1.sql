# --- !Ups

create table "users" (
  "userID" varchar not null primary key,
  "username" varchar not null,
  "hash" varchar (80) not null
);

# --- !Downs

drop table "users" if exists;
