package com.tsang.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tsang.entity.Role;
import org.apache.ibatis.annotations.Mapper;


/**
 * 角色表数据库操作
 */
@Mapper
public interface RoleMapper
        extends BaseMapper<Role> {


}
