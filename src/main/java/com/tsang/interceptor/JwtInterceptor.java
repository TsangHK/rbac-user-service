package com.tsang.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsang.annotation.RequiresPermission;
import com.tsang.common.Result;
import com.tsang.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

/**
 * JWT登录拦截器：校验Token与接口权限
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    // 写错误响应体用
    private static final ObjectMapper MAPPER = new ObjectMapper();

    // JWT工具
    private final JwtUtils jwtUtils;

    public JwtInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {
        // 获取Token
        String token = request.getHeader("Authorization");

        // 没有Token
        if (token == null || token.isEmpty()) {
            return error(response, 401, "请先登录");
        }

        // 解析Token
        Claims claims;

        try {
            // 去除Bearer
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // 解析JWT
            claims = jwtUtils.parse(token);
        } catch (Exception e) {
            return error(response, 401, "Token无效，请重新登录");
        }

        // 保存用户ID
        request.setAttribute("userId", claims.get("id"));

        // 保存用户名
        request.setAttribute("username", claims.get("username"));

        // 保存权限编码列表
        List<String> permissions = toPermissions(claims);

        request.setAttribute("permissions", permissions);

        // 接口权限校验
        if (handler instanceof HandlerMethod handlerMethod) {
            RequiresPermission requires = handlerMethod.getMethodAnnotation(RequiresPermission.class);

            if (requires != null && !permissions.contains(requires.value())) {
                return error(response, 403, "没有操作权限");
            }
        }

        return true;
    }

    /**
     * 从Token中取出权限编码列表
     */
    private List<String> toPermissions(Claims claims) {
        Object value = claims.get("permissions");

        if (!(value instanceof List<?> list)) {
            return List.of();
        }

        return list.stream().map(String::valueOf).toList();
    }

    /**
     * 返回JSON错误
     */
    private boolean error(HttpServletResponse response, int status, String message) throws Exception {
        response.setContentType("application/json;charset=UTF-8");

        response.setStatus(status);

        // 交给Jackson生成JSON
        response.getWriter().write(MAPPER.writeValueAsString(Result.error(status, message)));

        return false;
    }
}
