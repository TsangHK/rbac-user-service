package com.tsang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tsang.entity.RolePermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色权限关联表数据库操作
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {
}
