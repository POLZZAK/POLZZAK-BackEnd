create table guardian_ranking
(
    id              bigint auto_increment,
    ranking         int         not null,
    ranking_status  varchar(5)  not null,
    member_point_id bigint      not null,
    created_date    datetime(6) not null,
    CONSTRAINT pk_guardian_ranking PRIMARY KEY (id)
) engine = InnoDB
  default charset = utf8mb4;

create table kid_ranking
(
    id              bigint auto_increment,
    ranking         int         not null,
    ranking_status  varchar(5)  not null,
    member_point_id bigint      not null,
    created_date    datetime(6) not null,
    CONSTRAINT pk_kid_ranking PRIMARY KEY (id)
) engine = InnoDB
  default charset = utf8mb4;

alter table guardian_ranking
    add constraint foreign key (member_point_id) references member_type_detail (id);

alter table kid_ranking
    add constraint foreign key (member_point_id) references member_type_detail (id);
