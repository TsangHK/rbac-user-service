package com.tsang.exception;


/**
 * 配置异常：启动阶段发现关键配置缺失或不合法时抛出
 */
public class ConfigurationException extends IllegalStateException {


    public ConfigurationException(String message) {

        super(message);

    }


}
