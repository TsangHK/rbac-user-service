package com.tsang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tsang.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表数据库操作
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
