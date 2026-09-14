package com.tsang.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 角色权限关联实体，对应数据库 role_permission 表
 */
@TableName("role_permission")
public class RolePermission {

    // 主键
    private Integer id;

    // 角色ID
    private Integer roleId;

    // 权限ID
    private Integer permissionId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Integer permissionId) {
        this.permissionId = permissionId;
    }
}
