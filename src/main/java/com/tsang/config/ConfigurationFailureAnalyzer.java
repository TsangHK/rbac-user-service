package com.tsang.config;

import com.tsang.exception.ConfigurationException;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/**
 * 配置缺失的启动失败提示
 */
public class ConfigurationFailureAnalyzer extends AbstractFailureAnalyzer<ConfigurationException> {
    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, ConfigurationException cause) {
        // cause为null时不处理，交给默认逻辑
        if (cause == null) {
            return null;
        }

        return new FailureAnalysis(
                cause.getMessage(),
                """
                        请检查配置后再启动：
                          DB_PASSWORD  数据库密码（必填）
                          JWT_SECRET   JWT签名密钥（必填，至少32个字符）
                        
                        IDEA：Run → Edit Configurations → Environment variables
                        命令行：export DB_PASSWORD=... JWT_SECRET=...（Windows 用 set）
                        也可以写进不进git的 application-local.properties，
                        详见README「配置环境变量」一节""",
                cause);
    }
}
