package com.shujiai.ka.constant;

import com.shujiai.base.result.ResultCode;

public class ExamApplyResultCodes {

    /**
     * 172000~172200 公共
     */
    public static ResultCode PARAMETER_INVALID = new ResultCode(172000, "参数无效", "Parameter invalid.");
    public static ResultCode EXAM_APPLY_ERROR = new ResultCode(172001, "${message}", "${message}");

}
