package com.tsang.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tsang.entity.User;
import com.tsang.exception.BusinessException;
import com.tsang.mapper.UserMapper;
import com.tsang.utils.PasswordUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


import java.util.List;


/**
 * 用户业务实现
 */
@Service
public class UserServiceImpl implements UserService {

    // 用户数据库操作
    @Resource
    private UserMapper userMapper;


    /**
     * 查询全部用户
     */
    @Override
    public List<User> findAll() {

        // 传null表示不加任何WHERE条件
        return userMapper.selectList(null);

    }


    /**
     * 根据ID查询用户
     */
    @Override
    public User findById(Integer id) {

        return userMapper.selectById(id);

    }


    /**
     * 新增用户
     */
    @Override
    public boolean add(User user) {

        // 账号已存在直接拒绝
        Long exists = userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, user.getUsername())
        );

        if (exists != null && exists > 0) {

            throw new BusinessException("登录账号已存在");

        }

        // 入库前先做BCrypt加密
        user.setPassword(
                PasswordUtils.encode(
                        user.getPassword()
                )
        );

        // insert返回受影响行数，大于0即成功
        return userMapper.insert(user) > 0;

    }


    /**
     * 修改用户，不允许修改密码
     */
    @Override
    public boolean update(User user) {

        user.setPassword(null);

        return userMapper.updateById(user) > 0;

    }


    /**
     * 删除用户
     */
    @Override
    public boolean delete(Integer id) {

        return userMapper.deleteById(id) > 0;

    }


    /**
     * 分页 + 条件查询
     */
    @Override
    public Page<User> page(
            Integer page,
            Integer size,
            String name,
            Integer age
    ) {

        // 分页对象：当前页码 + 每页条数
        Page<User> pageInfo =
                new Page<>(page, size);

        // 条件构造器：用来动态拼接WHERE
        LambdaQueryWrapper<User> wrapper =
                new LambdaQueryWrapper<>();

        // 姓名模糊查询：name有值才拼接
        wrapper.like(
                name != null && !name.trim().isEmpty(),
                User::getName,
                name
        );

        // 年龄精确查询：age不为null才拼接
        wrapper.eq(
                age != null,
                User::getAge,
                age
        );

        // 分页查询：分页插件改写成LIMIT并执行COUNT
        userMapper.selectPage(
                pageInfo,
                wrapper
        );

        return pageInfo;

    }


    /**
     * 登录校验
     */
    @Override
    public User login(
            String username,
            String password
    ) {

        // 第一步：根据用户名查询用户
        LambdaQueryWrapper<User> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.eq(
                User::getUsername,
                username
        );


        User user =
                userMapper.selectOne(wrapper);

        // 用户不存在
        if (user == null) {

            return null;

        }

        // 第二步：BCrypt校验密码
        boolean result =
                PasswordUtils.matches(
                        password,
                        user.getPassword()
                );

        // 密码错误
        if (!result) {

            return null;

        }

        // 账号密码都正确
        return user;

    }


}
