# --- !Ups
create table ubi_qdog.subject
(
    subject_id binary(16) not null primary key,
    title      text       not null
);

# --- !Downs
drop table if exists ubi_qdog.subject;