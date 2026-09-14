package com.tsang.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 登录入参
 */
@Getter
@Setter
public class LoginDTO {

    // 登录账号
    @NotBlank(message = "登录账号不能为空")
    private String username;

    // 登录密码
    @NotBlank(message = "密码不能为空")
    private String password;
}
