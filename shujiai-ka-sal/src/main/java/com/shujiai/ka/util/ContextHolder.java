package com.shujiai.ka.util;

import com.shujiai.ka.logger.LogUtil;
import com.shujiai.base.context.Context;
import com.shujiai.base.exception.BizException;
import com.shujiai.base.result.ResultCode;
import org.slf4j.MDC;

import java.util.Objects;

/**
 * 用于上下文
 * 从dobboProvider的上下文获取
 * 非dubbo入口获取不到
 *
 * @author xiaokang
 * @date 2023/11/5 9:07
 */
public class ContextHolder {

    private static final ThreadLocal<Context> currentContext = new ThreadLocal<>();

    private static final ThreadLocal<String> currentAppId = new ThreadLocal<>();
    private static final ThreadLocal<String> currentAppVersion = new ThreadLocal<>();

    private static final ThreadLocal<String> currentUserTraceId = new ThreadLocal<>();

    private static final ThreadLocal<String> currentArmsTraceId = new ThreadLocal<>();

    /**
     * 用于存储状态机结果
     * 使用方法：
     * <blockquote><pre>
     *     // 设置返回值
     *     HrContextHolder.setResult("hello");
     *     // 获取返回值
     *     String result = HrContextHolder.getResult();
     * </pre></blockquote>
     */
    private static final ThreadLocal<? super Object> result = new ThreadLocal<>();


    private static ResultCode GET_CONTEXT_FAIL = new ResultCode(101113, "context获取失败。", "get context fail");
    private static ResultCode GET_APP_ID_FAIL = new ResultCode(101114, "appId获取失败。", "get appId fail");


    /**
     * 获取APPId
     *
     * @return
     */
    public static String getAppId() {
        String appId = currentAppId.get();
        return appId;
    }

    public static void setAppId(String appId) {
        currentAppId.set(appId);
    }

    public static void removeAppId() {
        currentAppId.remove();
    }

    /**
     * 获取应用程序版本
     *
     * @return {@link String}
     */
    public static String getAppVersion() {
        String version = currentAppVersion.get();

        return version;
    }

    /**
     * 设置应用程序版本
     *
     * @param version 版本
     */
    public static void setAppVersion(String version) {
        currentAppVersion.set(version);
    }

    /**
     * 获取context
     *
     * @return
     */
    public static Context getContext() {
        Context context = currentContext.get();
        if (Objects.isNull(context) || Objects.isNull(context.getTenantId())) {
            throw new BizException(GET_CONTEXT_FAIL);
        }
        return context;
    }

    public static void setContext(Context context) {
        currentContext.set(context);
    }

    /**
     * 设置回调或者监听的context
     */
    public static void setCallBackOrSynContext(Context context) {
        currentContext.set(context);
    }

    /**
     * 获取context
     *
     * @return
     */
    public static Context getContextNoThrow() {
        Context context = currentContext.get();
        return context;
    }

    public static void setContextAndAppId(Context context, String appId) {
        currentContext.set(context);
        currentAppId.set(appId);
    }

    public static String getUserTraceId() {
        return currentUserTraceId.get();
    }

    public static void setUserTraceId(String traceId) {
        currentUserTraceId.set(traceId);
        MDC.put(LogUtil.TRACE_ID, traceId);
    }

    public static String getArmsTraceId() {
        return currentArmsTraceId.get();
    }

    public static void setArmsTraceId(String traceId) {
        currentArmsTraceId.set(traceId);
        MDC.put(LogUtil.ARMS_TRACE_ID, traceId);
    }

    public static void clear() {
        currentContext.remove();
        currentAppId.remove();
        currentAppVersion.remove();
        currentUserTraceId.remove();
        currentArmsTraceId.remove();
        result.remove();
        MDC.remove(LogUtil.TRACE_ID);
        MDC.remove(LogUtil.ARMS_TRACE_ID);
    }


    /**
     * 设置状态机返回结果
     *
     * @param t   泛型
     * @param <T> object
     */
    public static <T> void setResult(T t) {
        result.set(t);
    }
}
