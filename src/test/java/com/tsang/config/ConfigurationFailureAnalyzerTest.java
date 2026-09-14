package com.tsang.config;

import com.tsang.exception.ConfigurationException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.diagnostics.FailureAnalysis;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 配置缺失的启动失败提示测试：配置异常输出成说明块
 */
class ConfigurationFailureAnalyzerTest {

    private final ConfigurationFailureAnalyzer analyzer =
            new ConfigurationFailureAnalyzer();

    @Test
    void 数据库密码缺失输出说明块() {
        FailureAnalysis analysis = analyzer.analyze(
                new ConfigurationException(
                        "数据库密码未配置，请通过环境变量 DB_PASSWORD 设置"));

        assertThat(analysis).isNotNull();

        // 具体原因
        assertThat(analysis.getDescription())
                .contains("DB_PASSWORD");

        // 告诉用户该怎么做
        assertThat(analysis.getAction())
                .contains("DB_PASSWORD", "JWT_SECRET");
    }

    @Test
    void JWT密钥缺失输出说明块() {
        FailureAnalysis analysis = analyzer.analyze(
                new ConfigurationException(
                        "JWT密钥未配置或长度不足，请通过环境变量 JWT_SECRET 设置至少32个字符的密钥"));

        assertThat(analysis).isNotNull();
        assertThat(analysis.getDescription()).contains("JWT_SECRET");
    }

    /**
     * 其它异常不由该分析器接管
     */
    @Test
    void 非配置异常不处理() {
        assertThat(analyzer.analyze(new IllegalStateException("其它异常")))
                .isNull();
    }
}
