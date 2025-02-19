package com.shujiai.ka.exceptions;

import com.shujiai.base.exception.BizException;
import com.shujiai.base.result.ResultCode;

/**
 * @author hxh
 * @date 2023/11/4
 * @time 8:31 下午
 * @description
 */

public class IgnoreBizException extends BizException {

    private static final long serialVersionUID = -7640930517128270694L;
    private final ResultCode resultCode;

    public IgnoreBizException(ResultCode resultCode) {
        super(resultCode);
        this.resultCode = resultCode;
    }

    @Override
    public ResultCode getResultCode() {
        return resultCode;
    }

}
