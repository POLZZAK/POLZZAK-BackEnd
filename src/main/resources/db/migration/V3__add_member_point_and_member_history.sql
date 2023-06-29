create table member_point
(
    member_id          bigint,
    point              int          not null,
    level              int          not null,
    created_date       datetime(6)  not null,
    last_modified_date datetime(6)  not null,
    CONSTRAINT pk_member_point_id PRIMARY KEY (member_id)
) engine=InnoDB default charset=utf8mb4;

create table member_point_history
(
    id                 bigint   auto_increment,
    description        varchar(30)  not null,
    increased_point     int         not null,
    remaining_point     int         not null,
    member_id           bigint      not null,
    created_date       datetime(6)  not null,
    CONSTRAINT pk_member_point_history_id PRIMARY KEY (id)
) engine=InnoDB default charset=utf8mb4;

alter table member_point_history
    add constraint fk_member_point_history_member_id_ref_member_point_member_id
        foreign key (member_id)
            references member_point (member_id);
