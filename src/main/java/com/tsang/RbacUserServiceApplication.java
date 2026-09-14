package com.tsang;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;


/**
 * Spring Boot 项目启动入口
 */
@SpringBootApplication
@MapperScan("com.tsang.mapper")
@ConfigurationPropertiesScan
public class RbacUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RbacUserServiceApplication.class, args);
    }

}
