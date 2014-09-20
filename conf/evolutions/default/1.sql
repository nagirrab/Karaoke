# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "session_songs" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"session_id" BIGINT NOT NULL,"submit_date" TIMESTAMP NOT NULL,"title" VARCHAR(254) NOT NULL,"artist" VARCHAR(254) NOT NULL);
create table "sessions" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"user_id" BIGINT NOT NULL,"password" VARCHAR(254),"auto_approve" BOOLEAN NOT NULL,"auto_order" BOOLEAN NOT NULL,"notes" VARCHAR(254) NOT NULL);
create table "singers" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"session_id" BIGINT NOT NULL,"user_id" BIGINT);
create table "users" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"email" VARCHAR(254) NOT NULL,"password_hash" VARCHAR(254) NOT NULL,"password_salt" VARCHAR(254) NOT NULL);

# --- !Downs

drop table "users";
drop table "singers";
drop table "sessions";
drop table "session_songs";

