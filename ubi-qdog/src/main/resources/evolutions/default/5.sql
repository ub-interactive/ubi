# --- !Ups
alter table course modify title varchar(255) not null;

alter table course modify subtitle varchar(255) null;

# --- !Downs