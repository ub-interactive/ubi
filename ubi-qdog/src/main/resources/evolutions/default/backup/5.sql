# --- !Ups
create table ubi_qdog.order
(
    order_id     binary(16)   not null primary key,
    course_id    binary(16)   not null,
    order_no     varchar(255) not null,
    order_status varchar(255) not null,
    created_at   text         not null,
    payment_at   text         not null,
    price        int          not null,
    quantity     int          not null,
    total_amount int          not null
);

# --- !Downs
drop table if exists ubi_qdog.order;