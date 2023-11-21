create table tag
(
    id          int auto_increment comment 'id'
        primary key,
    name        varchar(256)                       not null comment '标签名',
    user_id     bigint                             null comment '创建人 id',
    parent_id   int                                null comment '父级标签 id',
    is_parent   tinyint  default 0                 null comment '是否为父级标签',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint  default 0                 null comment '逻辑删除'
)
    comment '标签表';

