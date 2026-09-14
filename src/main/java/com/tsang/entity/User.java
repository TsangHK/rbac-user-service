package com.tsang.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * 用户实体类
 * 对应数据库 user 表
 */
@TableName("user")
public class User {


    // 用户ID（数据库自增）
    @TableId(type = IdType.AUTO)
    private Integer id;


    // 用户姓名
    private String name;


    // 用户年龄
    private Integer age;


    // 登录账号
    private String username;


    // 登录密码，只写不读：入参能收到，响应不输出
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;


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


    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
