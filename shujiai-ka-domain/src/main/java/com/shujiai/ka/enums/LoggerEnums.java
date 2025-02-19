package com.shujiai.ka.enums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum LoggerEnums {
    /**
     * 本系统作为dubbo服务提供者日志
     */
    SERVICE_LOG("SERVICE_LOG"),
    /**
     * 本系统作为dubbo服务消费者日志
     */
    SAL_LOG("SAL_LOG"),
    /**
     * 数据库访问相关日志
     */
    DAL_LOG("SAL_LOG"),
    /**
     * 错误日志
     */
    ERROR_LOG("ERROR_LOG");


    private final Logger logger;

    LoggerEnums(String name) {
        this.logger = LoggerFactory.getLogger(name);
    }

    public Logger getLogger() {
        return logger;
    }
}
