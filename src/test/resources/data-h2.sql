-- 集成测试用种子数据（H2），与 src/main/resources/data.sql 保持一致
-- 不叫 data.sql 的原因见 schema-h2.sql 顶部说明
-- 同样不使用反引号
-- H2每次全新启动，不需要 INSERT IGNORE，也不存在主键冲突
-- 初始账号密码均为：123456

INSERT INTO user (id, name, age, username, password) VALUES
(100, '管理员', 30, 'admin', '$2a$10$tqBchRT9ndO1kY3nQJvLdOID8B2AV6.MZjQUcxlQNNB9AbOVUPRpG'),
(101, '演示用户', 20, 'user',  '$2a$10$tqBchRT9ndO1kY3nQJvLdOID8B2AV6.MZjQUcxlQNNB9AbOVUPRpG');

INSERT INTO role (id, name, code) VALUES
(100, '管理员', 'ADMIN'),
(101, '普通用户', 'USER');

INSERT INTO permission (id, name, code, type, parent_id, path) VALUES
(100, '查询用户', 'user:list',   2, 0, '/page'),
(101, '新增用户', 'user:add',    2, 0, '/add'),
(102, '修改用户', 'user:update', 2, 0, '/update'),
(103, '删除用户', 'user:delete', 2, 0, '/delete');

INSERT INTO user_role (id, user_id, role_id) VALUES
(100, 100, 100),
(101, 101, 101);

INSERT INTO role_permission (id, role_id, permission_id) VALUES
(100, 100, 100),
(101, 100, 101),
(102, 100, 102),
(103, 100, 103),
(104, 101, 100);
