package com.tsang.config;

import com.tsang.exception.ConfigurationException;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT配置项：签名密钥与Token有效期
 */
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(String secret, long expirationMs) {

    public JwtProperties {
        // 密钥至少32字符
        if (secret == null || secret.trim().length() < 32) {
            throw new ConfigurationException("JWT密钥未配置或长度不足，请通过环境变量 JWT_SECRET 设置至少32个字符的密钥");
        }

        if (expirationMs <= 0) {
            throw new ConfigurationException("jwt.expiration-ms 必须大于0");
        }
    }
}
