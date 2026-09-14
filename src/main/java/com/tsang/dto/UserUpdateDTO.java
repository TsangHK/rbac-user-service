package com.tsang.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


/**
 * 修改用户入参：id必填，不含密码字段
 */
public class UserUpdateDTO {


    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Integer id;


    /**
     * 用户姓名
     */
    @NotBlank(message = "姓名不能为空")
    private String name;


    /**
     * 用户年龄
     */
    @Positive(message = "年龄必须大于0")
    private Integer age;


    /**
     * 登录账号
     */
    @NotBlank(message = "登录账号不能为空")
    private String username;


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


}
