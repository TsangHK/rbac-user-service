package com.tsang.common;

import lombok.Data;

/**
 * 统一响应结果
 * <p>
 * code取值约定：200成功、500业务或系统异常、401未登录/Token无效、403权限不足
 */
@Data
public class Result {

    // 状态码
    private Integer code;

    // 提示信息
    private String message;

    // 返回数据，没有数据时为null
    private Object data;

    /**
     * 成功响应
     *
     * @param data 返回给前端的数据，没有则传null
     */
    public static Result success(Object data) {
        Result result = new Result();

        result.code = 200;
        result.message = "操作成功";
        result.data = data;

        return result;
    }

    /**
     * 失败响应
     *
     * @param message 错误提示信息
     */
    public static Result error(String message) {
        return error(500, message);
    }

    /**
     * 失败响应（自定义状态码）
     *
     * @param code    状态码，如401、403
     * @param message 错误提示信息
     */
    public static Result error(int code, String message) {
        Result result = new Result();

        result.code = code;
        result.message = message;

        return result;
    }
}
