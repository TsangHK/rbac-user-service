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
 * 所有错误都使用真实的HTTP状态码，响应体仍是统一的Result格式，
 * 其中 code 与 HTTP 状态码保持一致：
 * <ul>
 *     <li>400 参数或请求体不合法、业务规则不满足</li>
 *     <li>401 未登录、Token无效、登录失败</li>
 *     <li>403 权限不足</li>
 *     <li>404 路径或资源不存在</li>
 *     <li>409 与已有数据冲突，如登录账号已存在</li>
 *     <li>500 服务端异常</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 日志
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    /**
     * 处理业务异常，状态码由异常自身携带
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result> businessException(BusinessException e) {
        return fail(e.getStatus(), e.getMessage());
    }


    /**
     * 处理认证失败异常
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Result> unauthorizedException(UnauthorizedException e) {
        return fail(HttpStatus.UNAUTHORIZED, e.getMessage());
    }


    /**
     * 处理参数校验异常（请求体上的@Valid）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result> validateException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();

        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数不合法";

        return fail(HttpStatus.BAD_REQUEST, message);
    }


    /**
     * 处理查询参数（@RequestParam）上的校验失败
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Result> requestParamException(HandlerMethodValidationException e) {
        String message = e.getAllValidationResults()
                .stream()
                .flatMap(result -> result.getResolvableErrors().stream())
                .map(MessageSourceResolvable::getDefaultMessage)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("参数不合法");

        return fail(HttpStatus.BAD_REQUEST, message);
    }


    /**
     * 处理请求体无法解析（JSON格式错误、编码不对等）
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Result> messageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("请求体解析失败：{}", e.getMessage());

        return fail(HttpStatus.BAD_REQUEST, "请求体格式不正确");
    }


    /**
     * 处理访问不存在的路径
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Result> noResourceFoundException(NoResourceFoundException e) {
        return fail(HttpStatus.NOT_FOUND, "接口不存在");
    }


    /**
     * 处理数据冲突异常（先查重再插入之间被人抢插，兜底到唯一索引）
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Result> dataIntegrityException(DataIntegrityViolationException e) {
        log.warn("数据完整性冲突：{}", e.getMessage());

        return fail(HttpStatus.CONFLICT, "数据冲突：登录账号可能已存在");
    }


    /**
     * 处理账号重复导致的数据异常
     *
     * 库里存在重复账号属于服务端数据问题，返回500
     */
    @ExceptionHandler(TooManyResultsException.class)
    public ResponseEntity<Result> tooManyResultsException(TooManyResultsException e) {
        log.error("账号存在重复数据", e);

        return fail(HttpStatus.INTERNAL_SERVER_ERROR, "账号数据异常：存在重复的登录账号，请联系管理员处理");
    }


    /**
     * 处理未知异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result> exception(Exception e) {
        // 记录日志
        log.error("系统异常", e);

        return fail(HttpStatus.INTERNAL_SERVER_ERROR, "系统异常");
    }


    /**
     * 统一构造错误响应：响应体格式不变，HTTP状态码用真实值
     */
    private ResponseEntity<Result> fail(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Result.error(status.value(), message));
    }
}
