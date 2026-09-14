package com.tsang.utils;

import com.tsang.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * JWT工具
 */
@Component
public class JwtUtils {

    // 签名密钥
    private final SecretKey key;

    // Token有效期（毫秒）
    private final long expirationMs;

    public JwtUtils(JwtProperties properties) {

        this.key = Keys.hmacShaKeyFor(
                properties.secret().getBytes(StandardCharsets.UTF_8)
        );

        this.expirationMs = properties.expirationMs();
    }

    /**
     * 生成Token，权限编码列表随Token下发
     */
    public String createToken(
            Integer id,
            String username,
            List<String> permissions) {

        return Jwts.builder()
                .claim("id", id)
                .claim("username", username)
                .claim("permissions", permissions)
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    /**
     * 解析Token，签名错误或已过期会抛异常
     */
    public Claims parse(String token) {

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
