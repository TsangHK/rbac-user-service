-- 初始数据，初始账号密码均为：123456
-- 种子数据固定ID从100开始，避免与已有数据的主键冲突

-- 用户
INSERT IGNORE INTO `user` (`id`, `name`, `age`, `username`, `password`) VALUES
(100, '管理员', 30, 'admin', '$2a$10$tqBchRT9ndO1kY3nQJvLdOID8B2AV6.MZjQUcxlQNNB9AbOVUPRpG'),
(101, '演示用户', 20, 'user',  '$2a$10$tqBchRT9ndO1kY3nQJvLdOID8B2AV6.MZjQUcxlQNNB9AbOVUPRpG');

-- 角色
INSERT IGNORE INTO `role` (`id`, `name`, `code`) VALUES
(100, '管理员', 'ADMIN'),
(101, '普通用户', 'USER');

-- 权限
INSERT IGNORE INTO `permission` (`id`, `name`, `code`, `type`, `parent_id`, `path`) VALUES
(100, '查询用户', 'user:list',   2, 0, '/page'),
(101, '新增用户', 'user:add',    2, 0, '/add'),
(102, '修改用户', 'user:update', 2, 0, '/update'),
(103, '删除用户', 'user:delete', 2, 0, '/delete');

-- 用户-角色
INSERT IGNORE INTO `user_role` (`id`, `user_id`, `role_id`) VALUES
(100, 100, 100),
(101, 101, 101);

-- 角色-权限：管理员拥有全部权限，普通用户只有查询
INSERT IGNORE INTO `role_permission` (`id`, `role_id`, `permission_id`) VALUES
(100, 100, 100),
(101, 100, 101),
(102, 100, 102),
(103, 100, 103),
(104, 101, 100);
