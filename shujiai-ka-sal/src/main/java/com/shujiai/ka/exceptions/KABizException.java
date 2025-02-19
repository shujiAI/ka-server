package com.shujiai.ka.exceptions;

import com.shujiai.base.exception.BizException;
import com.shujiai.base.result.ResultCode;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hxh
 * @date 2023/11/4
 * @time 8:31 下午
 * @description
 */

public class KABizException extends BizException {

    private static final long serialVersionUID = 6631575189813381538L;
    private final ResultCode resultCode;

    private Class<?> objectClass;

    public KABizException(ResultCode resultCode) {
        super(resultCode);
        Map<String, String> args = resultCode.getArgs();
        if (args == null) {
            args = new HashMap<>(4);
        }
        String message = resultCode.getMessage();
        args.put("message", message);
        resultCode.setArgs(args);

        this.resultCode = resultCode;
    }
    public KABizException(Integer resultCode, String message) {
        this.resultCode = new ResultCode(resultCode, message, message);
        Map<String, String> args = new HashMap<>(4);
        args.put("message", message);
        this.resultCode.setArgs(args);
    }
    public KABizException(ResultCode resultCode, String message) {
        super(new ResultCode(resultCode.getCode(), message, resultCode.getLocalMessage()));
        this.resultCode = new ResultCode(resultCode.getCode(), message, resultCode.getLocalMessage());
        Map<String, String> args = resultCode.getArgs();
        if (args == null) {
            args = new HashMap<>(4);
        }
        args.put("message", message);
        this.resultCode.setArgs(args);
    }

    public KABizException(ResultCode resultCode, Throwable cause) {
        super(resultCode.getMessage(), cause, resultCode);
        this.resultCode = resultCode;
    }

    public KABizException(ResultCode resultCode, String message, Throwable cause) {
        super(message, cause, resultCode);
        this.resultCode = new ResultCode(resultCode.getCode(), message, resultCode.getLocalMessage());
    }

    public KABizException(ResultCode resultCode, String message, Class<?> objectClass, Throwable cause) {
        super(message, cause, resultCode);
        this.resultCode = new ResultCode(resultCode.getCode(), message, resultCode.getLocalMessage());
        this.objectClass = objectClass;
    }

    public KABizException(ResultCode resultCode, String arg, String argVal) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
        Map<String, String> args = new HashMap<>(4);
        args.put("arg", arg);
        args.put("argVal", argVal);
        this.resultCode.setArgs(args);
    }

    public KABizException(ResultCode resultCode, String message, Map<String, String> args) {
        super(StringUtils.isNotEmpty(message) ? message : resultCode.getMessage());
        this.resultCode = resultCode;
        this.resultCode.setArgs(args);
    }

    public Class<?> getObjectClass() {
        return this.objectClass;
    }

    public void setObjectClass(Class<?> objectClass) {
        this.objectClass = objectClass;
    }

    @Override
    public ResultCode getResultCode() {
        return resultCode;
    }
}
