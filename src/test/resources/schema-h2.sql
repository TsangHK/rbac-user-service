-- 集成测试用建表脚本（H2），结构与 src/main/resources/schema.sql 一致
--
-- 刻意不叫 schema.sql：Spring默认按 classpath*:schema.sql 发现脚本，
-- 那样会同时匹配到 target/classes 和 target/test-classes 下的两份，建表和种子数据各执行两次
-- （种子数据第二次会主键冲突）。这里由 application-test.properties 显式指向本文件，
-- 执行哪一份是确定的，不依赖classpath顺序。
--
-- 两处刻意的差异：
-- 1. 不使用反引号。H2里被引号包起来的标识符大小写敏感，而MyBatis-Plus生成的SQL
--    不带引号（H2会转成大写），混用会导致"列不存在"。表名 user 是H2关键字，
--    靠连接串里的 NON_KEYWORDS=USER 让它当普通标识符处理。
-- 2. 去掉 MySQL 的 ENGINE / CHARSET / COMMENT 子句（H2不支持）。
-- 字段类型、非空、唯一约束与生产脚本保持一致。

CREATE TABLE IF NOT EXISTS user (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(50)  NOT NULL,
    age      INT          NULL,
    username VARCHAR(50)  NOT NULL,
    password VARCHAR(100) NOT NULL,
    CONSTRAINT uk_user_username UNIQUE (username)
);

CREATE TABLE IF NOT EXISTS role (
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(50) NOT NULL,
    CONSTRAINT uk_role_code UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS permission (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    name      VARCHAR(50)  NOT NULL,
    code      VARCHAR(50)  NOT NULL,
    type      INT          NULL,
    parent_id INT          NULL,
    path      VARCHAR(200) NULL,
    CONSTRAINT uk_permission_code UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS user_role (
    id      INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    CONSTRAINT uk_user_role UNIQUE (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS role_permission (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    role_id       INT NOT NULL,
    permission_id INT NOT NULL,
    CONSTRAINT uk_role_permission UNIQUE (role_id, permission_id)
);
