package com.tsang.exception;

import org.springframework.http.HttpStatus;

/**
 * 业务异常：请求本身合法，但不满足业务规则
 *
 * 默认返回 HTTP 400。语义更明确的场景在构造时指定状态码，
 * 例如"用户不存在"用 404、"登录账号已存在"用 409
 */
public class BusinessException extends RuntimeException {

    // 随异常一起返回的HTTP状态码
    private final HttpStatus status;

    /**
     * @param message 给前端看的错误提示，状态码默认400
     */
    public BusinessException(String message) {
        this(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * @param status  要返回的HTTP状态码
     * @param message 给前端看的错误提示
     */
    public BusinessException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
