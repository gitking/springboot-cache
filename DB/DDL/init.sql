create database if not exists spring_cache default charset utf8mb4;

use spring_cache;

create table if not exists `department` (
    `id` bigint(20) unsigned not null AUTO_INCREMENT COMMENT '主键ID',
    `departmentName` varchar(35) not null comment '部门名称',
    PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

create table if not exists `employee` (
    `id` bigint(20) unsigned not null AUTO_INCREMENT comment '主键ID',
    `lastName` varchar(35) not null comment '姓名',
    `email` varchar(35) not null comment '邮箱',
    `gender` char(1) not null comment '性别,0男1女',
    `d_id` bigint(20) not null comment '部门ID',
    PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

insert into `employee`(`lastName`, `email`, `gender`, `d_id`) values ('张三', 'zhangsan@atguigu.com', '0', 1),('李四', 'lisis@atguigu.com', '1', 2),('王五', 'wangwu@atguigu.com', '1', 3);