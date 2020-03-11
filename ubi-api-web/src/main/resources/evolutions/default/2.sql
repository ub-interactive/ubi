# --- !Ups
create table ubi_ccat.subject
(
    subject_id binary(16) not null primary key,
    title      text       not null
);

# --- !Downs
drop table if exists ubi_ccat.subject;