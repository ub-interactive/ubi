# --- !Ups
create table ubi_qdog.home_subject
(
    subject_id            binary(16)   not null primary key,
    subject_display_style varchar(255) not null,
    display_order         int          not null
);

# --- !Downs
drop table if exists ubi_qdog.home_subject;