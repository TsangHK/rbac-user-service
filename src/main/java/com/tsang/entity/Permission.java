package com.tsang.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * 权限实体，对应数据库 permission 表
 */
@Getter
@Setter
@TableName("permission")
public class Permission {

    // 权限ID
    private Integer id;

    // 权限名称
    private String name;

    // 权限编码，例如 user:list
    private String code;

    // 类型：1 菜单，2 按钮
    private Integer type;

    // 父菜单ID，用于组装树形菜单
    private Integer parentId;

    // 前端路由
    private String path;
}
