# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "session_song_orders" ("session_id" BIGINT NOT NULL,"song_id" BIGINT NOT NULL,"order" INTEGER NOT NULL);
create table "session_songs" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"singer_id" BIGINT NOT NULL,"session_id" BIGINT NOT NULL,"song_id" BIGINT,"submit_date" TIMESTAMP NOT NULL,"title" VARCHAR(254) NOT NULL,"artist" VARCHAR(254) NOT NULL,"external_link" VARCHAR(254),"special_request" VARCHAR(254),"status" VARCHAR(254) NOT NULL,"priority" INTEGER NOT NULL,"notes" VARCHAR(254) NOT NULL);
create table "sessions" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"user_id" BIGINT NOT NULL,"password" VARCHAR(254),"start_date" TIMESTAMP NOT NULL,"end_date" TIMESTAMP,"auto_approve" BOOLEAN NOT NULL,"auto_order" BOOLEAN NOT NULL,"status" VARCHAR(254) NOT NULL,"notes" VARCHAR(254) NOT NULL);
create table "singers" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"session_id" BIGINT NOT NULL,"user_id" BIGINT);
create table "songs" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"artist" VARCHAR(254) NOT NULL,"title" VARCHAR(254) NOT NULL,"duo" BOOLEAN,"year" VARCHAR(254));
create table "users" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"email" VARCHAR(254) NOT NULL,"password_hash" VARCHAR(254) NOT NULL,"password_salt" VARCHAR(254) NOT NULL);

# --- !Downs

drop table "users";
drop table "songs";
drop table "singers";
drop table "sessions";
drop table "session_songs";
drop table "session_song_orders";

