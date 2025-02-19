package com.shujiai.ka.logger;

import org.slf4j.Logger;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * 日志打印
 *
 * @author xiaokang
 * @date 2023/11/5 9:07
 */
public class LoggerFactory {
    public static final String EMPTY = "";
    public static final String DELIMITER_VERTICAL = "|";
    public static final String CLASS_METHOD = "%s.%s|";
    public static final String EXCLUDE_CLASS_KEYWORD = "Dubbo";
    private final Class<?> currentClass;
    private final Logger logger;

    public LoggerFactory(Class<?> tClass, LoggerEnums loggerEnums) {
        this.currentClass = tClass;
        logger = loggerEnums.getLogger();
    }

    public void error(String msg) {
        logger.error(appendClassAndMethod() + msg);
    }

    public void error(String format, Object... args) {
        logger.error(appendClassAndMethod() + format, args);
    }

    public void error(String format, Throwable e) {
        logger.error(format, e);
    }

    public void warn(String format, Object... args) {
        logger.warn(appendClassAndMethod() + format, args);
    }

    public void warn(String msg) {
        logger.warn(appendClassAndMethod() + msg);
    }

    public void info(String format, Object... args) {
        logger.info(appendClassAndMethod() + format, args);
    }

    public void info(String msg) {
        logger.info(appendClassAndMethod() + msg);
    }

    private String appendClassAndMethod() {
        //有人在Dubbo中用，AOP已经切过了，不需要
        if (Objects.nonNull(currentClass) && !currentClass.getName().contains(EXCLUDE_CLASS_KEYWORD)) {
            Optional<StackTraceElement> runClass = Arrays.stream(Thread.currentThread().getStackTrace()).filter(s -> s.getClassName().equals(currentClass.getName())).findAny();
            //这里也可以打印方法入参，目前不需要
            if (runClass.isPresent()) {
                return String.format(CLASS_METHOD, runClass.get().getClassName(), runClass.get().getMethodName());
            } else {
                return currentClass.getName() + DELIMITER_VERTICAL;
            }
        }
        return EMPTY;
    }
}
