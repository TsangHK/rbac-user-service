package com.tsang.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Plus配置
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 注册MyBatis Plus插件拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        // 创建插件拦截器链
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 加入分页插件，声明数据库类型是MySQL
        PaginationInnerInterceptor pagination = new PaginationInnerInterceptor(DbType.MYSQL);

        // 单页最多100条
        pagination.setMaxLimit(100L);

        interceptor.addInnerInterceptor(pagination);

        return interceptor;
    }
}
