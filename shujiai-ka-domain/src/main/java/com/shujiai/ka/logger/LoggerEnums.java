package com.shujiai.ka.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 日志常量枚举
 *
 * @author xiaokang
 * @date 2023/11/5 9:07
 */
public enum LoggerEnums {

    /**
     * 业务日志
     */
    EXAM_SERVICE_LOG("EXAM_SERVICE_LOG"),

    /**
     * 业务日志
     */
    EXAM_ERROR_LOG("EXAM_ERROR_LOG"),

    /**
     * 错误日志
     */
    ERROR_LOG("ERROR_LOG"),

    SAL_LOG("SAL_LOG"),


    ;

    private final Logger logger;

    LoggerEnums(String name) {
        this.logger = LoggerFactory.getLogger(name);
    }

    public Logger getLogger() {
        return logger;
    }
}
