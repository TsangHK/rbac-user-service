package com.tsang.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 登录返回对象：token、用户信息、权限列表
 */
@Getter
@Setter
public class LoginVO {

    // JWT Token
    private String token;

    // 用户信息
    private UserInfo userInfo;

    // 用户权限
    private List<String> permissions;

    public LoginVO(String token, UserInfo userInfo, List<String> permissions) {
        this.token = token;
        this.userInfo = userInfo;
        this.permissions = permissions;
    }

    /**
     * 用户信息内部类，不含密码
     */
    @Getter
    public static class UserInfo {

        // 用户ID
        private Integer id;

        // 姓名
        private String name;

        // 登录账号
        private String username;

        public UserInfo(Integer id, String name, String username) {
            this.id = id;
            this.name = name;
            this.username = username;
        }
    }
}
