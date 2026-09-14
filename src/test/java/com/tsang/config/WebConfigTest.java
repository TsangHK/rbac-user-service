package com.tsang.config;

import com.tsang.interceptor.JwtInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 拦截器注册配置测试：登录接口、健康检查被放行
 */
class WebConfigTest {

    @Test
    void 登录接口与健康检查被放行() {
        InterceptorRegistry registry =
                mock(InterceptorRegistry.class);

        InterceptorRegistration registration =
                mock(InterceptorRegistration.class);

        when(registry.addInterceptor(any()))
                .thenReturn(registration);

        when(registration.addPathPatterns(any(String[].class)))
                .thenReturn(registration);

        WebConfig webConfig =
                new WebConfig(mock(JwtInterceptor.class));

        webConfig.addInterceptors(registry);

        // 拦截所有接口
        verify(registration).addPathPatterns("/**");

        // 放行登录接口、健康检查
        verify(registration).excludePathPatterns(
                "/login",
                "/actuator/health",
                "/actuator/health/**"
        );
    }
}
