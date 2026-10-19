-- ============================================================
-- 建表脚本（第 3 周：认证模块 + 第 4 周：RBAC 权限模块）
-- 数据库：admin_template（与 application.yml 的 datasource 一致）
-- 执行方式：mysql -uroot -p < schema.sql  （或在客户端手动执行）
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


-- ============================================================
-- 第 4 周：RBAC 权限模块（角色 / 权限 / 关联表）
-- ============================================================

CREATE TABLE IF NOT EXISTS `role` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        VARCHAR(64)  NOT NULL                COMMENT '角色名',
    `code`        VARCHAR(64)  NOT NULL                COMMENT '角色编码，如 ADMIN/USER',
    `description` VARCHAR(255) DEFAULT NULL            COMMENT '角色描述',
    `status`      TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：1 启用，0 禁用',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除：0 未删除，1 已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '角色表';

CREATE TABLE IF NOT EXISTS `permission` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`       VARCHAR(64)  NOT NULL                COMMENT '权限名',
    `code`       VARCHAR(128) NOT NULL                COMMENT '权限编码，如 user:list',
    `type`       VARCHAR(16)  NOT NULL DEFAULT 'API'  COMMENT '类型：MENU/API/BUTTON',
    `sort`       INT          NOT NULL DEFAULT 0      COMMENT '排序',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`    TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除：0 未删除，1 已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '权限表';

CREATE TABLE IF NOT EXISTS `user_role` (
    `id`      BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` BIGINT NOT NULL COMMENT '用户 ID',
    `role_id` BIGINT NOT NULL COMMENT '角色 ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户角色关联表';

CREATE TABLE IF NOT EXISTS `role_permission` (
    `id`            BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `role_id`       BIGINT NOT NULL COMMENT '角色 ID',
    `permission_id` BIGINT NOT NULL COMMENT '权限 ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
    KEY `idx_permission_id` (`permission_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '角色权限关联表';


-- ============================================================
-- 种子数据（幂等，可重复执行）
-- ============================================================

-- 角色
INSERT IGNORE INTO `role` (`name`, `code`, `description`, `status`) VALUES
('管理员', 'ADMIN', '系统管理员，拥有所有权限', 1),
('普通用户', 'USER', '普通用户', 1);

-- 权限
INSERT IGNORE INTO `permission` (`name`, `code`, `type`, `sort`) VALUES
('用户列表', 'user:list', 'API', 1),
('用户新增', 'user:create', 'API', 2),
('用户编辑', 'user:update', 'API', 3),
('用户删除', 'user:delete', 'API', 4),
('用户启禁用', 'user:status', 'API', 5),
('分配角色', 'user:assign-role', 'API', 6),
('角色列表', 'role:list', 'API', 7),
('角色新增', 'role:create', 'API', 8),
('角色编辑', 'role:update', 'API', 9),
('角色删除', 'role:delete', 'API', 10),
('分配权限', 'role:assign-permission', 'API', 11),
('权限列表', 'permission:list', 'API', 12);

-- 管理员角色 → 所有权限
INSERT IGNORE INTO `role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id FROM `role` r JOIN `permission` p
WHERE r.code = 'ADMIN';

-- 普通用户角色 → 仅用户列表权限（示例）
INSERT IGNORE INTO `role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id FROM `role` r JOIN `permission` p
WHERE r.code = 'USER' AND p.code = 'user:list';

-- 默认 admin 账号（密码 admin123）
INSERT IGNORE INTO `user` (`username`, `password`, `nickname`, `email`, `status`) VALUES
('admin', '$2a$10$vtpaH6dL9C5ZixsJ5m6hCeVJKsb/4IjFj1so..UtykTpmbwORhPVq', 'Admin', 'admin@example.com', 1);

-- admin 用户 → 管理员角色
INSERT IGNORE INTO `user_role` (`user_id`, `role_id`)
SELECT u.id, r.id FROM `user` u JOIN `role` r
WHERE u.username = 'admin' AND r.code = 'ADMIN';
