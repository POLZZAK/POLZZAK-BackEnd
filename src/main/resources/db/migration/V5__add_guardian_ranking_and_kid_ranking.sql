create table guardian_ranking_summary
(
    id                 bigint       not null auto_increment,
    ranking            int          not null,
    ranking_status     varchar(5)   not null,
    nickname           varchar(10)  not null,
    point              int          not null,
    level              int          not null,
    member_type_detail varchar(30)  not null,
    profile_key        varchar(255) not null,
    created_date       datetime(6)  not null,
    member_point_id    bigint       not null,
    CONSTRAINT pk_guardian_ranking_summary PRIMARY KEY (id)
) engine = InnoDB
  default charset = utf8mb4;

create table kid_ranking_summary
(
    id              bigint       not null auto_increment,
    ranking         int          not null,
    ranking_status  varchar(5)   not null,
    nickname        varchar(10)  not null,
    point           int          not null,
    level           int          not null,
    profile_key     varchar(255) not null,
    created_date    datetime(6)  not null,
    member_point_id bigint       not null,
    CONSTRAINT pk_kid_ranking_summary PRIMARY KEY (id)
) engine = InnoDB
  default charset = utf8mb4;

alter table guardian_ranking_summary
    add constraint fk_guardian_ranking_summary_ref_member_point
        foreign key (member_point_id)
            references member_point (member_id);

alter table kid_ranking_summary
    add constraint fk_kid_ranking_summary_ref_member_point
        foreign key (member_point_id)
            references member_point (member_id);
