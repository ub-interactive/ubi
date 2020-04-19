# --- !Ups
create table ubi_qdog.course_subject
(
    course_id  binary(16) not null,
    subject_id binary(16) not null
);


# --- !Downs
drop table if exists ubi_qdog.course_subject;