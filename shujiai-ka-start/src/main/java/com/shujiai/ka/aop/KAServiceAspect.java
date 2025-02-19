package com.shujiai.ka.aop;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.shujiai.ka.constant.CommonCodes;
import com.shujiai.ka.constant.KAResultCodes;
import com.shujiai.ka.logger.LogUtil;
import com.shujiai.ka.logger.LoggerConst;
import com.shujiai.ka.logger.LoggerEnums;
import com.shujiai.ka.util.ContextHolder;
import com.shujiai.base.context.Context;
import com.shujiai.base.exception.BizException;
import com.shujiai.base.result.Result;
import com.shujiai.base.result.ResultCode;
import com.shujiai.base.result.ResultCodes;
import com.shujiai.tools.utils.GsonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * service AOP
 *
 * @author hxh
 * @date 2023/11/9 21:57
 */
@Aspect
@Component
public class KAServiceAspect extends BaseAspect {
    private static final Logger EXAM_SERVICE_LOG = LoggerEnums.EXAM_SERVICE_LOG.getLogger();
    private static final Logger EXAM_ERROR_LOG = LoggerEnums.EXAM_ERROR_LOG.getLogger();
    private static final String CONTEXT_KEY = "context";
    private static final String APP_ID_KEY = "appId";
    private static final String DELIMITER = ":";
    private static final String COMMA = ",";


    @Pointcut("execution(public * com.shujiai.ka.service..*.*(..))")
    public void service() {
    }

    @Around("service()")
    public Object logAndHandleException(ProceedingJoinPoint jp) {
        long start = System.currentTimeMillis();
        Object result = null;
        //获取方法参数值数组
        Object[] args = jp.getArgs();
        MethodSignature methodSignature = (MethodSignature) jp.getSignature();
        Method method = methodSignature.getMethod();
        int status = 1;
        try {

            //这两个参数是必须的，直接加了判断
            Map<String, Object> params = this.argsToMap(jp);
            Context context = (Context) params.get(CONTEXT_KEY);
            if (null == context || StringUtils.isEmpty(context.getIdentityId())) {
                throw new BizException(CommonCodes.CONTEXT_INVALID);
            }
            this.setContextHolder(jp);
            if (params.containsKey(APP_ID_KEY)) {
                if (StringUtils.isEmpty((String)params.get(APP_ID_KEY))) {
                    throw new BizException(CommonCodes.APP_ID_INVALID);
                }
            }

            result = jp.proceed();
            return result;
        } catch (ValidationException | IllegalArgumentException e) {
            status = 0;
            EXAM_ERROR_LOG.error("", e);
            String message = e.getMessage();
            if (StringUtils.contains(message, COMMA)) {
                message = message.split(COMMA)[0];
            }
            if (StringUtils.contains(message, DELIMITER)) {
                message = message.split(DELIMITER)[1];
            }
            Map<String, String> errorArgs = new HashMap<>();
            errorArgs.put("message", message);
            result = new Result<ResultCodes>(KAResultCodes.PARAMETER_INVALID.getCode(), message, null, false, errorArgs);
            return result;
        } catch (BizException e) {
            status = 0;
            // 业务异常
            ResultCode resultCode = e.getResultCode();
            result = new Result<>(resultCode);
            return result;
        } catch (Throwable t) {
            status = 0;
            // 未知异常
            EXAM_ERROR_LOG.error(String.format("args:%s", JSON.toJSONString(args)), t);
            result = new Result<>(ResultCodes.SERVER_ERROR);
            return result;
        } finally {
            ContextHolder.clear();
            long costMs = System.currentTimeMillis() - start;
            resultLog(method, costMs, status, args, result);
        }
    }

    private void resultLog(Method method, long cost, int status, Object[] args, Object result) {
        String argStr = GsonUtils.toJson(args);
        String resultStr = GsonUtils.toJson(result);
        EXAM_SERVICE_LOG.info("{}|{}|{}|{}|{}",
                method.getDeclaringClass().getName() + "." + method.getName(),
                cost + "ms",
                status,
                argStr.length() > LoggerConst.LOG_MAX_CONTENT_SIZE ? argStr.substring(0, LoggerConst.LOG_MAX_CONTENT_SIZE) : argStr,
                resultStr.length() > LoggerConst.LOG_MAX_CONTENT_SIZE ? resultStr.substring(0, LoggerConst.LOG_MAX_CONTENT_SIZE) : resultStr
        );
    }

    private Map<String, Object> argsToMap(ProceedingJoinPoint joinPoint) {
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

    private List<String> handleParamName(ProceedingJoinPoint joinPoint) {
        Signature s = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) s;
        Method m = methodSignature.getMethod();
        Parameter[] params = m.getParameters();
        return Arrays.stream(params).map(Parameter::getName).collect(Collectors.toList());
    }

    private List<Object> handleParamValue(ProceedingJoinPoint joinPoint) {
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

    private void setContextHolder(JoinPoint joinPoint) {
        super.setContext(joinPoint);

        String userTraceId = ContextHolder.getContext().getTraceId();
        userTraceId = LogUtil.generateTraceId(userTraceId);
        ContextHolder.setUserTraceId(userTraceId);
        ContextHolder.setArmsTraceId(LogUtil.getArmsTraceId());
    }

}
