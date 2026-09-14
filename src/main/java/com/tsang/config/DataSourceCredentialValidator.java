package com.tsang.config;


import com.tsang.exception.ConfigurationException;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


/**
 * 数据库密码启动校验
 */
@Component
public class DataSourceCredentialValidator {


    public DataSourceCredentialValidator(Environment environment) {

        String password =
                environment.getProperty("spring.datasource.password");


        // 配置项缺失或为空
        if (password == null || password.isBlank()) {

            throw new ConfigurationException(
                    "数据库密码未配置，请通过环境变量 DB_PASSWORD 设置");

        }

    }


}
