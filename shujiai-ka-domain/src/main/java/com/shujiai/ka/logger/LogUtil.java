package com.shujiai.ka.logger;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.MDC;

import java.util.UUID;

/**
 * 日志
 *
 * @author jiuyi
 * @date 2022/3/17 20:16
 */
public class LogUtil {

    public static final String TRACE_ID = "trace_id";

    public static final String ARMS_TRACE_ID = "EagleEye-TraceID";
    public static final String DELIMITER = "-";

    public static String getArmsTraceId() {
        try {
            return MDC.get(ARMS_TRACE_ID);
        } catch (Exception e) {
            return null;
        }
    }

    public static String generateTraceId() {
        String traceId = UUID.randomUUID().toString().replace(DELIMITER, "");
        MDC.put(TRACE_ID, traceId);
        return traceId;
    }

    public static String generateTraceId(String traceId) {
        if (StringUtils.isBlank(traceId)) {
            traceId = UUID.randomUUID().toString().replace(DELIMITER, "");
        }
        MDC.put(TRACE_ID, traceId);
        return traceId;
    }

    public static void clearTraceId() {
        MDC.remove(TRACE_ID);
    }

    public static Logger getErrorLogger() {
        return LoggerEnums.ERROR_LOG.getLogger();
    }

    public static LoggerFactory getLogger(Class<?> c, LoggerEnums loggerEnums) {
        return new LoggerFactory(c, loggerEnums);
    }
}
