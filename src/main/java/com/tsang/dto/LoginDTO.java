package com.tsang.dto;


import jakarta.validation.constraints.NotBlank;


/**
 * 登录入参
 */
public class LoginDTO {


    // 登录账号
    @NotBlank(message = "登录账号不能为空")
    private String username;


    // 登录密码
    @NotBlank(message = "密码不能为空")
    private String password;


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
