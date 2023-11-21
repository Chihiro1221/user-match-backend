create table user
(
    id           bigint auto_increment comment 'id'
        primary key,
    nickname     varchar(256)                       null comment '昵称',
    avatar_url   varchar(1024)                      null comment '用户头像',
    username     varchar(256)                       null comment '用户名',
    password     varchar(256)                       not null comment '密码',
    gender       tinyint                            null comment '性别 0 男 1 女',
    phone        varchar(128)                       null comment '手机号',
    email        varchar(128)                       null comment '邮箱',
    status       int      default 0                 null comment '状态',
    create_time  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete    tinyint  default 0                 null comment '逻辑删除',
    role         int      default 0                 not null comment '角色 0 - 普通用户 1 - 管理员',
    planet_code  varchar(256)                       not null comment '星球编号',
    tags         varchar(1024)                      null comment '用户标签 json数组',
    introduction varchar(512)                       null comment '简介'
)
    comment '用户表';