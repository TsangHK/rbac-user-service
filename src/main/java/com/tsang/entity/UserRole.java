package com.tsang.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户角色关联实体，对应数据库 user_role 表
 */
@Getter
@Setter
@TableName("user_role")
public class UserRole {

    // 主键ID
    private Integer id;

    // 用户ID
    private Integer userId;

    // 角色ID
    private Integer roleId;
}
