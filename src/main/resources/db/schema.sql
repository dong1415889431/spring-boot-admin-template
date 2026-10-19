-- ============================================================
-- 建表脚本（第 3 周：认证模块）
-- 数据库：admin_template（与 application.yml 的 datasource 一致）
-- 执行方式：mysql -uroot -p < schema.sql  （或在客户端手动执行）
--
-- 说明：第 3 周只建 user 表；role/permission 相关表第 4 周（RBAC）补充。
-- ============================================================

CREATE DATABASE IF NOT EXISTS `admin_template`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE `admin_template`;

CREATE TABLE IF NOT EXISTS `user` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`   VARCHAR(64)  NOT NULL                COMMENT '用户名',
    `password`   VARCHAR(100) NOT NULL                COMMENT '密码（BCrypt 加密）',
    `nickname`   VARCHAR(64)  DEFAULT NULL            COMMENT '昵称',
    `email`      VARCHAR(128) DEFAULT NULL            COMMENT '邮箱',
    `status`     TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：1 启用，0 禁用',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`    TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除：0 未删除，1 已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '用户表';
