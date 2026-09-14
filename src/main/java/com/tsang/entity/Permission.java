package com.tsang.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 权限实体，对应数据库 permission 表
 */
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
