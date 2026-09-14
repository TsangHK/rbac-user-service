package com.tsang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tsang.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联表数据库操作
 */
@Mapper
public interface UserRoleMapper
        extends BaseMapper<UserRole> {
}
