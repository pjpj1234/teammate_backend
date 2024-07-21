
-- auto-generated definition
create table user
(
    id           bigint auto_increment comment 'id'
        primary key,
    userAccount  varchar(256)                       null comment '账户',
    userName     varchar(256)                       null comment '用户名',
    gender       tinyint                            null comment '性别',
    avatarUrl    varchar(1024)                      null comment '头像',
    userPassword varchar(512)                       not null comment '密码',
    phone        varchar(128)                       null comment '电话',
    email        varchar(512)                       null comment '邮箱',
    userStatus   tinyint  default 0                 null comment '状态 0 -正常',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '逻辑删除',
    userRole     int      default 0                 not null comment '0- 普通用户 1-管理员',
    validCode    varchar(512)                       not null comment '校验码',
    tags         varchar(1024)                      null comment '标签列表'
);

create table team
(
    id           bigint auto_increment comment 'id'
        primary key,
    name         varchar(256)                       not null comment '队伍名称',
    description  varchar(1024)                      null comment '队伍描述',
    maxNum       tinyint  default 5                 not null comment '最大人数',
    expireTime   datetime                           null comment '过期时间',
    userId       bigint                             null comment '创始人id',
    status       tinyint  default 0                 not null comment '状态 0 -公开、1 - 私有、2 - 加密',
    password     varchar(512)                       null comment '密码',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '逻辑删除',
);

create table user_team
(
    id           bigint auto_increment comment 'id'
        primary key,
    userId      bigint                             null comment '用户id',
    teamId      bigint                             null comment '队伍id',
    joinTime     datetime default CURRENT_TIMESTAMP null comment '加入时间',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '逻辑删除'
);