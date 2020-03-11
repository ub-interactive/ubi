# --- !Ups
create table ubi_ccat.course
(
    course_id           varchar(255)  not null,
    title               varchar(255)  not null,
    subtitle            varchar(255)  null,
    thumbnail_url       text          null,
    cover_url           text          not null,
    price               int           not null,
    promotion_price     int           null,
    sale_type           varchar(255)  not null,
    tags                varchar(1024) null,
    course_intro        text          null,
    course_menu         text          null,
    course_info         text          null,
    flash_sale_start_at int           null,
    flash_sale_end_at   int           null,
    sale_stock          int           null
);

create unique index course_course_id_uindex
    on course (course_id);

# --- !Downs
drop table if exists ubi_ccat.course;