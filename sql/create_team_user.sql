-- auto-generated definition
-- auto-generated definition
create table team_user
(
    id          bigint auto_increment comment 'id'
        primary key,
    user_id     bigint                             not null comment '用户id',
    team_id     bigint                             not null comment '队伍id',
    join_time   datetime default CURRENT_TIMESTAMP null comment '加入时间',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint  default 0                 null comment '逻辑删除'
)
    comment '队伍用户关联表';



