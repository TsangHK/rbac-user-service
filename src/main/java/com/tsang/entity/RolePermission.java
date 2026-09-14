package com.tsang.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * 角色权限关联实体，对应数据库 role_permission 表
 */
@Getter
@Setter
@TableName("role_permission")
public class RolePermission {

    // 主键
    private Integer id;

    // 角色ID
    private Integer roleId;

    // 权限ID
    private Integer permissionId;
}
