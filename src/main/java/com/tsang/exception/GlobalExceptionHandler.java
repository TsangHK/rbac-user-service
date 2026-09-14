package com.tsang.exception;


import com.tsang.common.Result;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.Objects;


/**
 * 全局异常处理器
 *
 * 统一处理项目异常返回
 */
@RestControllerAdvice
public class GlobalExceptionHandler {


    // 日志
    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);



    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result businessException(
            BusinessException e){


        return Result.error(e.getMessage());

    }



    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result validateException(
            MethodArgumentNotValidException e){


        FieldError fieldError =
                e.getBindingResult()
                        .getFieldError();

        String message =
                fieldError != null
                        ? fieldError.getDefaultMessage()
                        : "参数不合法";


        return Result.error(message);

    }



    /**
     * 处理数据冲突异常（如新增时登录账号已存在）
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Result dataIntegrityException(
            DataIntegrityViolationException e){


        log.warn("数据完整性冲突：{}", e.getMessage());


        return Result.error("数据冲突：登录账号可能已存在");

    }



    /**
     * 处理查询参数（@RequestParam）上的校验失败
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public Result requestParamException(
            HandlerMethodValidationException e){


        String message =
                e.getAllValidationResults()
                        .stream()
                        .flatMap(result ->
                                result.getResolvableErrors().stream())
                        .map(MessageSourceResolvable::getDefaultMessage)
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse("参数不合法");


        return Result.error(message);

    }



    /**
     * 处理账号重复导致的数据异常
     */
    @ExceptionHandler(TooManyResultsException.class)
    public Result tooManyResultsException(
            TooManyResultsException e){


        log.error("账号存在重复数据", e);


        return Result.error("账号数据异常：存在重复的登录账号，请联系管理员处理");

    }



    /**
     * 处理未知异常
     */
    @ExceptionHandler(Exception.class)
    public Result exception(Exception e){


        // 记录日志
        log.error("系统异常", e);


        return Result.error("系统异常");

    }



}
