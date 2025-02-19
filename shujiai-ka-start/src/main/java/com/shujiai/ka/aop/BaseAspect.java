package com.shujiai.ka.aop;

import com.google.common.collect.Maps;
import com.shujiai.ka.constant.CommonCodes;
import com.shujiai.ka.logger.LoggerConst;
import com.shujiai.ka.util.ContextHolder;
import com.shujiai.base.context.Context;
import com.shujiai.base.exception.BizException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * BaseAspect
 *
 * @author xiaokang
 * @date 2023/11/5 14:17
 */
public class BaseAspect {

    protected static final String CONTEXT_KEY = "context";
    protected static final String APP_ID_KEY = "appId";
    protected static final String APP_VERSION_KEY = "version";
    protected static final String RULE_SYS_APPID = "sysruleappid";
    protected static final int LOG_MAX_CONTENT_LENGTH = 4096;

    protected String restoreContent(String content) {
        if (StringUtils.isEmpty(content)) {
            return content;
        }
        if (content.length() > LOG_MAX_CONTENT_LENGTH) {
            return content.substring(0, LOG_MAX_CONTENT_LENGTH) + "...";
        }
        return content;
    }
    /**
     * 设置context
     *
     * @param joinPoint
     */
    protected void setContext(JoinPoint joinPoint) {
        Map<String, Object> params = this.argsToMap(joinPoint);
        Context context = (Context) params.get(CONTEXT_KEY);
        if (null == context || StringUtils.isEmpty(context.getTenantId())) {
            throw new BizException(CommonCodes.CONTEXT_INVALID);
        }
        ContextHolder.setContext(context);

        boolean containAppIdParam = params.containsKey(APP_ID_KEY) || params.containsKey(RULE_SYS_APPID);
        if (containAppIdParam) {
            //目前appId必须，后边有特殊再处理
            String appId = (String) params.get(APP_ID_KEY);
            if (StringUtils.isEmpty(appId)) {

                //兼容规则引擎的appId
                appId = (String) params.get(RULE_SYS_APPID);
                if (StringUtils.isEmpty(appId)) {
                    throw new BizException(CommonCodes.APP_ID_INVALID);
                }
            }
            ContextHolder.setAppId(appId);
        }

        String version = (String) params.get(APP_VERSION_KEY);

        ContextHolder.setAppVersion(version);
    }




    /**
     * 打印内容
     *
     * @param content
     * @return
     */
    protected String content(String content) {
        if (StringUtils.isEmpty(content)) {
            return "-";
        }
        content = content.replace("|", "!@@!");
        if (content.length() > LoggerConst.LOG_MAX_CONTENT_SIZE) {
            return content.substring(0, LoggerConst.LOG_MAX_CONTENT_SIZE) + "...";
        }
        return content;
    }

    protected Map<String, Object> argsToMap(JoinPoint joinPoint) {
        List<String> paramNames = handleParamName(joinPoint);
        if (CollectionUtils.isEmpty(paramNames)) {
            return null;
        }
        List<Object> paramValues = handleParamValue(joinPoint);
        Map<String, Object> map = Maps.newLinkedHashMap();
        for (int i = 0; i < paramNames.size(); i++) {
            map.put(paramNames.get(i).toLowerCase(), paramValues.get(i));
        }
        return map;
    }

    private List<String> handleParamName(JoinPoint joinPoint) {
        Signature s = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) s;
        Method m = methodSignature.getMethod();
        Parameter[] params = m.getParameters();
        return Arrays.stream(params).map(Parameter::getName).collect(Collectors.toList());
    }

    private List<Object> handleParamValue(JoinPoint joinPoint) {
        return Arrays.stream(joinPoint.getArgs())
                .map(e -> {
                    if (e instanceof HttpServletRequest) {
                        return HttpServletRequest.class.getSimpleName();
                    } else if (e instanceof HttpServletResponse) {
                        return HttpServletResponse.class.getSimpleName();
                    }
                    return e;
                })
                .collect(Collectors.toList());
    }


}
