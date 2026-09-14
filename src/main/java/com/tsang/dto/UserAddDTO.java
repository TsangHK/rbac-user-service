package com.tsang.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

/**
 * 新增用户入参：密码为明文，由Service加密后入库
 */
@Getter
@Setter
public class UserAddDTO {

    // 用户姓名
    @NotBlank(message = "姓名不能为空")
    private String name;

    // 用户年龄
    @Positive(message = "年龄必须大于0")
    private Integer age;

    // 登录账号
    @NotBlank(message = "登录账号不能为空")
    private String username;

    // 登录密码
    @NotBlank(message = "密码不能为空")
    private String password;
}
