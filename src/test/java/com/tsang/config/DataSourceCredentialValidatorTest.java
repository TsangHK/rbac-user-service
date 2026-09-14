package com.tsang.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 数据库密码启动校验测试：配置缺失时启动失败并给出可读提示
 */
class DataSourceCredentialValidatorTest {

    @Test
    void 未配置数据库密码时启动失败() {

        MockEnvironment environment = new MockEnvironment();

        assertThatThrownBy(() ->
                new DataSourceCredentialValidator(environment))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("DB_PASSWORD");
    }

    @Test
    void 密码为空串时启动失败() {

        MockEnvironment environment = new MockEnvironment()
                .withProperty("spring.datasource.password", "");

        assertThatThrownBy(() ->
                new DataSourceCredentialValidator(environment))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("DB_PASSWORD");
    }

    @Test
    void 配置了密码时正常启动() {

        MockEnvironment environment = new MockEnvironment()
                .withProperty("spring.datasource.password", "some-password");

        assertThatCode(() ->
                new DataSourceCredentialValidator(environment))
                .doesNotThrowAnyException();
    }
}
