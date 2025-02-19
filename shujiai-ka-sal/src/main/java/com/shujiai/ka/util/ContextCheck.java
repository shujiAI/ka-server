package com.shujiai.ka.util;

import com.shujiai.ka.constant.KAResultCodes;
import com.shujiai.ka.exceptions.KABizException;
import com.shujiai.base.context.Context;
import com.shujiai.base.exception.BizException;
import com.shujiai.base.result.ResultCode;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * ContextCheck
 *
 * @author xiaokang
 * @date 2023/11/6
 */
public class ContextCheck {

    public static final String MESSAGE_TEMPLATE = "字段[%s]的值无效";
    /**
     * 数字/字母/-/_/汉字,最长64，不能以-/_开头和结尾
     */
    public static final String CHECK_NAME_REGEX = "^(?![-_])(?!.*?[-_]$)[a-zA-Z0-9- _\u4e00-\u9fa5]{1,64}$";
    public static final String CHECK_NAME_NEW_REGEX = "^[\\s\\S]{1,32}$";
    public static final String CHECK_NAME_NEW_REGEX_MESSAGE_TEMPLATE = "字段[%s]的值不超过32个字符";

    /**
     * 数据模型name正则校验
     * 数字/字母/-/_,最长32，不能以-/_开头和结尾
     */
    public static final String CHECK_DATA_MODEL_NAME_REGEX = "^(?![_])(?!.*[_]$)[a-zA-Z_]{1,32}$";

    public static boolean valid(Context context) {
        if (context == null) {
            throw new KABizException(KAResultCodes.KA_ILLEGAL_ARGUMENT_CONTEXT);
        }
        if (!StringUtils.hasText(context.getTenantId())) {
            throw new KABizException(KAResultCodes.KA_ILLEGAL_ARGUMENT_CONTEXT_TENANT_ID);
        }
        if (!StringUtils.hasText(context.getIdentityId())) {
            throw new KABizException(KAResultCodes.KA_ILLEGAL_ARGUMENT_CONTEXT_IDENTITY_ID);
        }

        return true;
    }

    public static boolean validParam(Object paramValue, String paramName) {
        return validParam(paramValue, paramName, true);
    }

    public static boolean validParam(Object paramValue, String paramName, boolean isThrow) {
        boolean valid = true;
        // set/list
        if (paramValue instanceof Collection) {
            valid = !CollectionUtils.isEmpty((Collection) paramValue);
        } else if (paramValue instanceof Map) {
            valid = !CollectionUtils.isEmpty((Map) paramValue);
        } else if (paramValue instanceof String) {
            valid = StringUtils.hasText(paramValue.toString());
        } else if (paramValue instanceof Boolean) {
            valid = (Boolean) paramValue;
        } else if (paramValue instanceof String[]) {
            valid = ((String[]) paramValue).length > 0;
        } else {
            valid = !ObjectUtils.isEmpty(paramValue);
        }

        if (!valid && isThrow) {
            String message = String.format(MESSAGE_TEMPLATE, paramName);
            throw new BizException(new ResultCode(105005, message, "invalid param value"));
        }

        return valid;
    }

    public static boolean validNameRegex(String paramValue, String paramName) {
        if (!StringUtils.hasText(paramValue) || !paramValue.matches(ContextCheck.CHECK_NAME_NEW_REGEX)) {
            String message = String.format(CHECK_NAME_NEW_REGEX_MESSAGE_TEMPLATE, paramName);
            throw new BizException(new ResultCode(105005, message, "invalid param value"));

        }
        return true;
    }

    public static void main(String[] args) {
        List<String> l = new ArrayList<>();
        validParam(l, "l", false);
        Map<String, Objects> m = new HashMap<>();
        validParam(m, "m", false);

        String s = "null";
        validParam(s, "s", false);

        Boolean b = Boolean.FALSE;
        validParam(b, "b", false);

        String[] a = s.split(",");
        validParam(a, "b", false);

    }

}
