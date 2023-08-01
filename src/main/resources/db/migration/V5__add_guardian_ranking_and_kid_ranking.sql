create table guardian_ranking_summary
(
    id                 bigint auto_increment,
    ranking            int         not null,
    ranking_status     varchar(5)  not null,
    nickname           varchar(10) not null,
    point              int         not null,
    level              int         not null,
    member_type_detail varchar(30) not null,
    created_date       datetime(6) not null,
    CONSTRAINT pk_guardian_ranking_summary PRIMARY KEY (id)
) engine = InnoDB
  default charset = utf8mb4;

create table kid_ranking_summary
(
    id             bigint auto_increment,
    ranking        int         not null,
    ranking_status varchar(5)  not null,
    nickname       varchar(10) not null,
    point          int         not null,
    level          int         not null,
    created_date   datetime(6) not null,
    CONSTRAINT pk_kid_ranking_summary PRIMARY KEY (id)
) engine = InnoDB
  default charset = utf8mb4;
