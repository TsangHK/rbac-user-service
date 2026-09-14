package com.tsang.entity;


import com.baomidou.mybatisplus.annotation.TableName;


/**
 * 角色实体，对应数据库 role 表
 */
@TableName("role")
public class Role {


    // 角色ID
    private Integer id;


    // 角色名称
    private String name;


    // 角色编码，例如 ADMIN
    private String code;


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


}
