-- 建表脚本，首次启动时由 Spring Boot 自动执行

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id`       INT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    `name`     VARCHAR(50)  NOT NULL COMMENT '姓名',
    `age`      INT          NULL COMMENT '年龄',
    `username` VARCHAR(50)  NOT NULL COMMENT '登录账号',
    `password` VARCHAR(100) NOT NULL COMMENT '登录密码（BCrypt密文）',
    UNIQUE KEY `uk_user_username` (`username`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS `role` (
    `id`   INT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    `name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `code` VARCHAR(50) NOT NULL COMMENT '角色编码，例如ADMIN',
    UNIQUE KEY `uk_role_code` (`code`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '角色表';

-- 权限表
CREATE TABLE IF NOT EXISTS `permission` (
    `id`        INT AUTO_INCREMENT PRIMARY KEY COMMENT '权限ID',
    `name`      VARCHAR(50)  NOT NULL COMMENT '权限名称',
    `code`      VARCHAR(50)  NOT NULL COMMENT '权限编码，例如user:delete',
    `type`      INT          NULL COMMENT '类型：1菜单 2按钮',
    `parent_id` INT          NULL COMMENT '父菜单ID',
    `path`      VARCHAR(200) NULL COMMENT '前端路由',
    UNIQUE KEY `uk_permission_code` (`code`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '权限表';

-- 用户角色关联表（一个用户可以拥有多个角色）
CREATE TABLE IF NOT EXISTS `user_role` (
    `id`      INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL COMMENT '用户ID',
    `role_id` INT NOT NULL COMMENT '角色ID',
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户角色关联表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS `role_permission` (
    `id`            INT AUTO_INCREMENT PRIMARY KEY,
    `role_id`       INT NOT NULL COMMENT '角色ID',
    `permission_id` INT NOT NULL COMMENT '权限ID',
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '角色权限关联表';
