package com.tsang.config;

import com.tsang.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC配置，注册JWT拦截器
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // JWT拦截器
    private final JwtInterceptor jwtInterceptor;

    public WebConfig(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(
                        jwtInterceptor
                )

                // 拦截所有接口
                .addPathPatterns("/**")

                // 放行登录接口、健康检查
                .excludePathPatterns("/login", "/actuator/health", "/actuator/health/**");
    }
}
