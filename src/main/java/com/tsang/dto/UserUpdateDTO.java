package com.tsang.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

/**
 * 修改用户入参：id必填，不含密码字段
 */
@Getter
@Setter
public class UserUpdateDTO {

    // 用户ID
    @NotNull(message = "用户ID不能为空")
    private Integer id;

    // 用户姓名
    @NotBlank(message = "姓名不能为空")
    private String name;

    // 用户年龄
    @Positive(message = "年龄必须大于0")
    private Integer age;

    // 登录账号
    @NotBlank(message = "登录账号不能为空")
    private String username;
}
