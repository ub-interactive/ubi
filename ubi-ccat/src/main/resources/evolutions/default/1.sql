# --- !Ups
create table ubi_ccat.course
(
    course_id           binary(16) not null primary key,
    title               text       not null,
    subtitle            text       null,
    thumbnail_url       text       null,
    cover_url           text       not null,
    price               int        not null,
    promotion_price     int        null,
    sale_type           text       not null,
    tags                text       not null,
    course_intro        text       null,
    course_menu         text       null,
    course_info         text       null,
    flash_sale_start_at text       null,
    flash_sale_end_at   text       null,
    sale_stock          int        null
);

# --- !Downs
drop table if exists ubi_ccat.course;