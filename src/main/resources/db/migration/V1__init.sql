create table member
(
    id                 bigint auto_increment,
    created_date       datetime(6) not null,
    last_modified_date datetime(6) not null,
    nickname           varchar(10)  not null,
    profile_key        varchar(255) not null,
    CONSTRAINT pk_member_id PRIMARY KEY (id)
) engine=InnoDB default charset=utf8mb4;

alter table member
    add unique uk_member_nickname (nickname);

create table users
(
    id                 bigint auto_increment,
    created_date       datetime(6) not null,
    last_modified_date datetime(6) not null,
    signed_date        datetime(6) not null,
    social_type        varchar(255) not null,
    username           varchar(255) not null,
    withdraw           bit          not null,
    user_role        varchar(30) not null,
    member_id          bigint not null,
    CONSTRAINT pk_users_id PRIMARY KEY (id)
) engine=InnoDB default charset=utf8mb4;

alter table users
    add constraint fk_users_member_id_ref_member_id
        foreign key (member_id)
            references member (id),
    add unique uk_user_username (username);

create table family_map
(
    id                 bigint auto_increment,
    created_date       datetime(6) not null,
    last_modified_date datetime(6) not null,
    guardian_id        bigint not null,
    kid_id             bigint not null,
    CONSTRAINT pk_family_map_id PRIMARY KEY (id)
) engine=InnoDB default charset=utf8mb4;

create table family_request
(
    id                 bigint auto_increment,
    created_date       datetime(6) not null,
    last_modified_date datetime(6) not null,
    receiver_id        bigint not null,
    sender_id          bigint not null,
    CONSTRAINT pk_family_request_id PRIMARY KEY (id)
) engine=InnoDB default charset=utf8mb4;

create table stamp_board
(
    id                  bigint auto_increment,
    guardian_id         bigint      not null,
    kid_id              bigint      not null,
    name                varchar(20) not null,
    status              varchar(20) not null,
    current_stamp_count int         not null,
    goal_stamp_count    int         not null,
    reward              varchar(50) not null,
    completed_date      timestamp null,
    reward_date         timestamp null,
    last_modified_date  timestamp default CURRENT_TIMESTAMP null,
    created_date        timestamp default CURRENT_TIMESTAMP null,
    CONSTRAINT pk_stamp_board_id PRIMARY KEY (id)
) engine=InnoDB default charset=utf8mb4;

create table mission
(
    id                 bigint auto_increment,
    stamp_board_id     bigint      not null,
    content            varchar(30) not null,
    is_active          boolean     not null,
    last_modified_date timestamp default CURRENT_TIMESTAMP null,
    created_date       timestamp default CURRENT_TIMESTAMP null,
    CONSTRAINT pk_mission_id PRIMARY KEY (id)
) engine=InnoDB default charset=utf8mb4;

alter table mission
    add constraint fk_mission_stamp_board_id_ref_stamp_board_id
        foreign key (stamp_board_id)
            references stamp_board (id);

create table stamp
(
    id              bigint auto_increment,
    stamp_board_id  bigint not null,
    stamp_design_id int    not null,
    mission_id      bigint not null,
    created_date    timestamp default CURRENT_TIMESTAMP null,
    CONSTRAINT pk_stamp_id PRIMARY KEY (id)
) engine=InnoDB default charset=utf8mb4;

alter table stamp
    add constraint fk_stamp_stamp_board_id_ref_stamp_board_id foreign key (stamp_board_id) references stamp_board (id),
    add constraint fk_stamp_mission_id_ref_mission_id foreign key (mission_id) references mission (id);

create table mission_complete
(
    id             bigint auto_increment,
    stamp_board_id bigint not null,
    mission_id     bigint not null,
    guardian_id    bigint not null,
    kid_id         bigint not null,
    created_date   timestamp default CURRENT_TIMESTAMP null,
    CONSTRAINT pk_mission_complete_id PRIMARY KEY (id)
) engine=InnoDB default charset=utf8mb4;

alter table mission_complete
    add constraint fk_mission_complete_guardian_id_ref_guardian_id foreign key (guardian_id) references member (id),
    add constraint fk_mission_complete_kid_id_ref_kid_id foreign key (kid_id) references member (id),
    add constraint fk_mission_complete_mission_id_ref_mission_id foreign key (mission_id) references mission (id),
    add constraint fk_mission_complete_stamp_board_id_ref_stamp_board_id foreign key (stamp_board_id) references stamp_board (id)
