package com.shujiai.ka.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.shujiai.base.exception.BizException;
import com.shujiai.base.result.ResultCodes;
import com.shujiai.ka.enums.LoggerEnums;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;

@Aspect
@Component
public class SalServiceAspect extends BaseAspect {

    private static final Logger salLog = LoggerEnums.SAL_LOG.getLogger();

    @Pointcut("execution(public * com.shujiai.ka.dubbo.consumer.*.*(..))")
    public void salService() {
    }

    @Around("salService()")
    public Object logAndHandleException(ProceedingJoinPoint jp) {
        long start = System.currentTimeMillis();
        Object result = null;
        Throwable throwable = null;
        //获取方法参数值数组
        Object[] args = jp.getArgs();
        Method method = ((MethodSignature) jp.getSignature()).getMethod();
        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();

        try {
            result = jp.proceed();
        } catch (BizException e) {
            throwable = e;
            throw e;
        } catch (Throwable t) {
            // 未知异常
            throwable = t;
            throw new BizException(ResultCodes.SERVER_ERROR.getCode(), "三方服务异常");
        } finally {
            long costMs = System.currentTimeMillis() - start;

            salLog.error("{}|{}|{}|{}|{}|{}",
                    className, methodName, costMs,
                    restoreContent(JSON.toJSONString(args, SerializerFeature.WriteMapNullValue)),
                    restoreContent(JSON.toJSONString(result, SerializerFeature.WriteMapNullValue)),
                    Objects.isNull(throwable) ? "" : throwable);
        }
        return result;
    }


}
