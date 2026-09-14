package com.tsang.exception;


import com.tsang.common.Result;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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
     * 处理认证失败异常，返回真实的 HTTP 401
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Result> unauthorizedException(
            UnauthorizedException e){


        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Result.error(
                        HttpStatus.UNAUTHORIZED.value(),
                        e.getMessage()
                ));

    }



    /**
     * 处理请求体无法解析（JSON格式错误、编码不对等）
     *
     * 不加这个处理器会落到下面的兜底分支，被报成"系统异常"+HTTP 200，
     * 把客户端的错误伪装成服务端故障
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Result> messageNotReadableException(
            HttpMessageNotReadableException e){


        log.warn("请求体解析失败：{}", e.getMessage());


        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Result.error(
                        HttpStatus.BAD_REQUEST.value(),
                        "请求体格式不正确"
                ));

    }



    /**
     * 处理访问不存在的路径，返回真实的 HTTP 404
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Result> noResourceFoundException(
            NoResourceFoundException e){


        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Result.error(
                        HttpStatus.NOT_FOUND.value(),
                        "接口不存在"
                ));

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
