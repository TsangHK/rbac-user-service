package com.tsang.exception;


/**
 * 认证失败异常：登录账号或密码错误
 *
 * 与业务异常区分开，由全局异常处理器返回真实的 HTTP 401
 */
public class UnauthorizedException extends RuntimeException {


    public UnauthorizedException(String message){

        super(message);

    }


}
