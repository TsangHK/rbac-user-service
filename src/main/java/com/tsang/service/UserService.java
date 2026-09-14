package com.tsang.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tsang.entity.User;

/**
 * 用户业务接口
 */
public interface UserService {

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户，不存在时为null
     */
    User findById(Integer id);

    /**
     * 新增用户
     * <p>
     * 密码会先做BCrypt加密再入库
     *
     * @param user 用户信息（密码为明文）
     * @return true：新增成功
     */
    boolean add(User user);

    /**
     * 修改用户
     * <p>
     * 不允许通过此接口修改密码
     *
     * @param user 要修改的字段，id必填
     * @return true：修改成功
     */
    boolean update(User user);

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return true：删除成功
     */
    boolean delete(Integer id);

    /**
     * 分页 + 条件查询
     *
     * @param page 当前页码（从1开始）
     * @param size 每页条数
     * @param name 姓名模糊查询，null时不过滤
     * @param age  年龄精确匹配，null时不过滤
     * @return 分页结果（记录列表 + 总条数 + 页码信息）
     */
    Page<User> page(Integer page,
                    Integer size,
                    String name,
                    Integer age);

    /**
     * 登录校验
     *
     * @param username 登录账号
     * @param password 用户输入的明文密码
     * @return 校验通过返回用户；
     * 账号不存在或密码错误返回null
     */
    User login(String username, String password);
}
