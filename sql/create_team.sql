-- auto-generated definition
create table team
(
    id          bigint auto_increment comment 'id'
        primary key,
    user_id     bigint                             not null comment '创建人id',
    name        varchar(256)                       null comment '队伍名',
    description varchar(1024)                      null comment '队伍描述',
    password    varchar(256)                       null comment '队伍密码',
    max_num     int                                null comment '最大人数',
    status      int      default 0                 null comment '状态 0-公开 1-私有 2-加密',
    expire_time datetime                           null comment '过期时间',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint  default 0                 null comment '逻辑删除'
)
    comment '队伍表';

