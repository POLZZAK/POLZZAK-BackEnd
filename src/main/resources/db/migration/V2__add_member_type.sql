create table member_type_detail
(
    id           bigint auto_increment,
    type         varchar(30) not null,
    detail         varchar(30) not null,
    created_date timestamp default CURRENT_TIMESTAMP null,
    CONSTRAINT pk_member_type_detail_id PRIMARY KEY (id)
) engine = InnoDB default charset = utf8mb4;

alter table member_type_detail
    add unique uk_member_type_detail_detail (detail);

alter table member
    add column member_type_detail_id bigint not null,
    add constraint fk_member_member_type_detail_ref_member_type_detail_id
        foreign key (member_type_detail_id)
            references member_type_detail (id);
