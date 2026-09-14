package com.tsang.exception;


/**
 * 业务异常：业务代码主动抛出的错误，由 GlobalExceptionHandler 统一处理
 */
public class BusinessException extends RuntimeException {

    /**
     * @param message 给前端看的错误提示
     */
    public BusinessException(String message) {

        super(message);

    }

}
