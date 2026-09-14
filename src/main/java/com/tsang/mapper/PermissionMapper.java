package com.tsang.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tsang.entity.Permission;
import org.apache.ibatis.annotations.Mapper;


/**
 * 权限表数据库操作
 */
@Mapper
public interface PermissionMapper
        extends BaseMapper<Permission> {


}
