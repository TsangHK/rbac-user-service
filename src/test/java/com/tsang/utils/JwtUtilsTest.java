package com.tsang.utils;

import com.tsang.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * JWT工具测试
 */
class JwtUtilsTest {

    // 测试密钥（仅测试用）
    private static final String SECRET =
            "unit-test-secret-key-1234567890abcdef";

    private JwtUtils newJwtUtils(long expirationMs) {
        return new JwtUtils(new JwtProperties(SECRET, expirationMs));
    }

    @Test
    void 创建并解析Token() {

        JwtUtils jwtUtils = newJwtUtils(24 * 60 * 60 * 1000);

        String token = jwtUtils.createToken(
                1,
                "admin",
                List.of("user:list", "user:delete")
        );

        Claims claims = jwtUtils.parse(token);

        assertThat(claims.get("id")).isEqualTo(1);
        assertThat(claims.get("username")).isEqualTo("admin");
        assertThat(claims.get("permissions"))
                .isEqualTo(List.of("user:list", "user:delete"));
    }

    @Test
    void 篡改的Token解析失败() {

        JwtUtils jwtUtils = newJwtUtils(24 * 60 * 60 * 1000);

        String token = jwtUtils.createToken(1, "admin", List.of());

        assertThatThrownBy(() -> jwtUtils.parse(token + "x"))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void 过期的Token解析失败() throws Exception {

        JwtUtils jwtUtils = newJwtUtils(200);

        String token = jwtUtils.createToken(1, "admin", List.of());

        Thread.sleep(500);

        assertThatThrownBy(() -> jwtUtils.parse(token))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void 密钥太短时直接报错() {

        assertThatThrownBy(() -> new JwtProperties("short-secret", 1000))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("JWT_SECRET");
    }
}
