package com.shujiai.ka.exceptions;

import com.shujiai.base.exception.BizException;
import com.shujiai.base.result.ResultCode;

/**
 * @author hxh
 * @date 2023/11/4
 * @time 8:31 下午
 * @description
 */

public class MetadataBizException extends BizException {

    private static final long serialVersionUID = -7640930517128270694L;
    private final ResultCode resultCode;

    private Class<?> objectClass;

    public MetadataBizException(ResultCode resultCode) {
        super(resultCode);
        this.resultCode = resultCode;
    }

    public MetadataBizException(ResultCode resultCode, String message) {
        super(new ResultCode(resultCode.getCode(), message, resultCode.getLocalMessage()));
        this.resultCode = new ResultCode(resultCode.getCode(), message, resultCode.getLocalMessage());
    }

    public MetadataBizException(ResultCode resultCode, Throwable cause) {
        super(resultCode.getMessage(), cause, resultCode);
        this.resultCode = resultCode;
    }

    public MetadataBizException(ResultCode resultCode, String message, Throwable cause) {
        super(message, cause, resultCode);
        this.resultCode = new ResultCode(resultCode.getCode(), message, resultCode.getLocalMessage());
    }

    public MetadataBizException(ResultCode resultCode, String message, Class<?> objectClass, Throwable cause) {
        super(message, cause, resultCode);
        this.resultCode = new ResultCode(resultCode.getCode(), message, resultCode.getLocalMessage());
        this.objectClass = objectClass;
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
