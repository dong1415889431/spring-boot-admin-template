-- ============================================================
-- Table schema script (DDL) (Week 3: authentication module + Week 4: RBAC permission module)
-- Database: admin_template (matches the datasource in application.yml)
-- How to run: mysql -uroot -p < schema.sql  (or run manually in a client)
-- ============================================================

CREATE DATABASE IF NOT EXISTS `admin_template`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE `admin_template`;

CREATE TABLE IF NOT EXISTS `user` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'primary key',
    `username`   VARCHAR(64)  NOT NULL                COMMENT 'username',
    `password`   VARCHAR(100) NOT NULL                COMMENT 'password (BCrypt-encrypted)',
    `nickname`   VARCHAR(64)  DEFAULT NULL            COMMENT 'nickname',
    `email`      VARCHAR(128) DEFAULT NULL            COMMENT 'email',
    `status`     TINYINT      NOT NULL DEFAULT 1      COMMENT 'status: 1 enabled, 0 disabled',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'updated time',
    `deleted`    TINYINT      NOT NULL DEFAULT 0      COMMENT 'logical delete: 0 not deleted, 1 deleted',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = 'user table';


-- ============================================================
-- Week 4: RBAC permission module (roles / permissions / association tables)
-- ============================================================

CREATE TABLE IF NOT EXISTS `role` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'primary key',
    `name`        VARCHAR(64)  NOT NULL                COMMENT 'role name',
    `code`        VARCHAR(64)  NOT NULL                COMMENT 'role code, e.g. ADMIN/USER',
    `description` VARCHAR(255) DEFAULT NULL            COMMENT 'role description',
    `status`      TINYINT      NOT NULL DEFAULT 1      COMMENT 'status: 1 enabled, 0 disabled',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'updated time',
    `deleted`     TINYINT      NOT NULL DEFAULT 0      COMMENT 'logical delete: 0 not deleted, 1 deleted',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = 'role table';

CREATE TABLE IF NOT EXISTS `permission` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'primary key',
    `name`       VARCHAR(64)  NOT NULL                COMMENT 'permission name',
    `code`       VARCHAR(128) NOT NULL                COMMENT 'permission code, e.g. user:list',
    `type`       VARCHAR(16)  NOT NULL DEFAULT 'API'  COMMENT 'type: MENU/API/BUTTON',
    `sort`       INT          NOT NULL DEFAULT 0      COMMENT 'sort order',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'updated time',
    `deleted`    TINYINT      NOT NULL DEFAULT 0      COMMENT 'logical delete: 0 not deleted, 1 deleted',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = 'permission table';

CREATE TABLE IF NOT EXISTS `user_role` (
    `id`      BIGINT NOT NULL AUTO_INCREMENT COMMENT 'primary key',
    `user_id` BIGINT NOT NULL COMMENT 'user ID',
    `role_id` BIGINT NOT NULL COMMENT 'role ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = 'user-role association table';

CREATE TABLE IF NOT EXISTS `role_permission` (
    `id`            BIGINT NOT NULL AUTO_INCREMENT COMMENT 'primary key',
    `role_id`       BIGINT NOT NULL COMMENT 'role ID',
    `permission_id` BIGINT NOT NULL COMMENT 'permission ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
    KEY `idx_permission_id` (`permission_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = 'role-permission association table';


-- ============================================================
-- Seed data (idempotent, safe to run repeatedly)
-- ============================================================

-- Roles
INSERT IGNORE INTO `role` (`name`, `code`, `description`, `status`) VALUES
('Administrator', 'ADMIN', 'System administrator with all permissions', 1),
('Regular User', 'USER', 'Regular user', 1);

-- Permissions
INSERT IGNORE INTO `permission` (`name`, `code`, `type`, `sort`) VALUES
('User List', 'user:list', 'API', 1),
('User Create', 'user:create', 'API', 2),
('User Update', 'user:update', 'API', 3),
('User Delete', 'user:delete', 'API', 4),
('User Status', 'user:status', 'API', 5),
('User Assign Role', 'user:assign-role', 'API', 6),
('Role List', 'role:list', 'API', 7),
('Role Create', 'role:create', 'API', 8),
('Role Update', 'role:update', 'API', 9),
('Role Delete', 'role:delete', 'API', 10),
('Role Assign Permission', 'role:assign-permission', 'API', 11),
('Permission List', 'permission:list', 'API', 12);

-- Administrator role -> all permissions
INSERT IGNORE INTO `role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id FROM `role` r JOIN `permission` p
WHERE r.code = 'ADMIN';

-- Regular user role -> user list permission only (example)
INSERT IGNORE INTO `role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id FROM `role` r JOIN `permission` p
WHERE r.code = 'USER' AND p.code = 'user:list';

-- Default admin account (password: admin123)
INSERT IGNORE INTO `user` (`username`, `password`, `nickname`, `email`, `status`) VALUES
('admin', '$2a$10$vtpaH6dL9C5ZixsJ5m6hCeVJKsb/4IjFj1so..UtykTpmbwORhPVq', 'Admin', 'admin@example.com', 1);

-- admin user -> administrator role
INSERT IGNORE INTO `user_role` (`user_id`, `role_id`)
SELECT u.id, r.id FROM `user` u JOIN `role` r
WHERE u.username = 'admin' AND r.code = 'ADMIN';


-- ============================================================
-- Week 5: generic CRUD example (article management) + permissions + seed data
-- ============================================================

CREATE TABLE IF NOT EXISTS `article` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'primary key',
    `title`      VARCHAR(128) NOT NULL                COMMENT 'title',
    `content`    TEXT                                  COMMENT 'content',
    `author`     VARCHAR(64)  DEFAULT NULL            COMMENT 'author',
    `status`     TINYINT      NOT NULL DEFAULT 1      COMMENT 'status: 1 published, 0 draft',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'updated time',
    `deleted`    TINYINT      NOT NULL DEFAULT 0      COMMENT 'logical delete: 0 not deleted, 1 deleted',
    PRIMARY KEY (`id`),
    KEY `idx_title` (`title`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = 'article table (generic CRUD example)';

-- Article permissions
INSERT IGNORE INTO `permission` (`name`, `code`, `type`, `sort`) VALUES
('Article List', 'article:list', 'API', 13),
('Article Create', 'article:create', 'API', 14),
('Article Update', 'article:update', 'API', 15),
('Article Delete', 'article:delete', 'API', 16);

-- Re-assign all permissions to ADMIN (including newly added article permissions, idempotent)
INSERT IGNORE INTO `role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id FROM `role` r JOIN `permission` p
WHERE r.code = 'ADMIN';

-- Sample articles
INSERT IGNORE INTO `article` (`id`, `title`, `content`, `author`, `status`) VALUES
(1, 'Getting Started with the Spring Boot Admin Template', 'A sample article demonstrating how the generic CRUD module works.', 'admin', 1),
(2, 'How to Extend Your Own Business Module', 'Copy the article module, adjust the table and fields, and you can quickly scaffold products, orders, and more.', 'admin', 1),
(3, 'Draft: RBAC Permission Design Notes', 'An unpublished draft.', 'admin', 0);
