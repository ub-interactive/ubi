# --- !Ups
create table project.project_extra_info
(
    id                  bigint identity primary key,
    is_system_record    bit           not null,
    occ_version_number  int           not null,
    created_by          nvarchar(36),
    created_at          datetimeoffset(6),
    updated_by          nvarchar(36),
    updated_at          datetimeoffset(6),
    project_id          bigint        not null,
    highlight_space_ids nvarchar(max) not null
);

create table project.space_extra_info
(
    id                 bigint identity primary key,
    is_system_record   bit           not null,
    occ_version_number int           not null,
    created_by         nvarchar(36),
    created_at         datetimeoffset(6),
    updated_by         nvarchar(36),
    updated_at         datetimeoffset(6),
    space_id           bigint        not null,
    project_id         bigint        not null,
    status_c           nvarchar(255) not null,
    picture_i          nvarchar(max) not null,
    location_t         nvarchar(max) not null,
    description_t      nvarchar(max) not null,
    extended_info_j    nvarchar(max) not null,
    facilities_j       nvarchar(max) not null,
    display_order      int           not null
);

create table social.checkin
(
    id                 bigint identity primary key,
    is_system_record   bit default 0     not null,
    occ_version_number int default 0     not null,
    created_by         nvarchar(36),
    created_at         datetimeoffset(6),
    updated_by         nvarchar(36),
    updated_at         datetimeoffset(6),
    user_id            bigint            not null,
    user_number        nvarchar(255)     not null,
    project_id         bigint            not null,
    project_code       nvarchar(255)     not null,
    space_id           bigint            not null,
    space_code         nvarchar(255)     not null,
    checkin_at         datetimeoffset(6) not null,
    mention_user_ids   nvarchar(max)     not null,
    message            nvarchar(255)
);

# --- !Downs
drop table project.project_extra_info;

drop table project.space_extra_info;

drop table social.checkin;