package com.tsang.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 用户角色关联实体，对应数据库 user_role 表
 */
@TableName("user_role")
public class UserRole {

    // 主键ID
    private Integer id;

    // 用户ID
    private Integer userId;

    // 角色ID
    private Integer roleId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
}
