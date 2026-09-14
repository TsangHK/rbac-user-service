package com.tsang.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 密码工具测试
 */
class PasswordUtilsTest {

    @Test
    void 加密后不保留明文() {

        String hash = PasswordUtils.encode("123456");

        assertThat(hash)
                .startsWith("$2")
                .isNotEqualTo("123456");
    }

    @Test
    void 正确密码校验通过() {

        String hash = PasswordUtils.encode("123456");

        assertThat(PasswordUtils.matches("123456", hash))
                .isTrue();
    }

    @Test
    void 错误密码校验失败() {

        String hash = PasswordUtils.encode("123456");

        assertThat(PasswordUtils.matches("wrong", hash))
                .isFalse();
    }
}
