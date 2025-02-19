package com.shujiai.ka.constant;

import com.shujiai.base.result.ResultCode;
import com.shujiai.base.result.ResultCodes;

/**
 * ka 公共错误码  170000-171999
 *
 * @author xiaokang
 * @date 2023/11/5 14:17
 */
public class CommonCodes extends ResultCodes {

    public static ResultCode USER_ID_INVALID = new ResultCode(170001, "用户ID无效", "user id invalid");
    public static ResultCode CONTEXT_INVALID = new ResultCode(170002, "Context无效", "context invalid");
    public static ResultCode APP_ID_INVALID = new ResultCode(170003, "appId无效", "appId invalid");

    public static ResultCode BIZ_ERROR = new ResultCode(170004, "${message}", "${message}");
}
