package com.tsang.vo;

import java.util.List;

/**
 * 登录返回对象：token、用户信息、权限列表
 */
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    /**
     * 用户信息内部类，不含密码
     */
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

        public Integer getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getUsername() {
            return username;
        }
    }
}
