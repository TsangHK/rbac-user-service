package com.tsang.utils;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


/**
 * 密码工具类
 */
public class PasswordUtils {


    /**
     * BCrypt加密器
     */
    private static final BCryptPasswordEncoder encoder =
            new BCryptPasswordEncoder();


    /**
     * 密码加密
     */
    public static String encode(String password) {


        return encoder.encode(password);


    }


    /**
     * 密码校验
     */
    public static boolean matches(
            String rawPassword,
            String encodePassword
    ) {


        return encoder.matches(
                rawPassword,
                encodePassword
        );


    }


}
