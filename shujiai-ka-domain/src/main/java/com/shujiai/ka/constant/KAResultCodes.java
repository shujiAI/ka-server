package com.shujiai.ka.constant;

/**
 * @author hxh
 */

import com.shujiai.base.result.ResultCode;
import com.shujiai.base.result.ResultCodes;

import java.io.Serializable;

/**
 * 工作区结果代码
 *
 * @author hxh
 * @date 2023/11/4
 * @time 3:11 下午
 * result codes定义
 * 需要定义明确具体的一个错误code，唯一定位一个错误
 */
public class KAResultCodes extends ResultCodes implements Serializable {

    private static final long serialVersionUID = -2957865765982188055L;

    public static ResultCode KA_SERVER_ERROR = new ResultCode(110000, "服务器错误", "unknown, server error.");

    /**
     * 系统相关的异常, 110001 - 110100
     */
    public static ResultCode KA_OBJECT_NOT_FOUND = new ResultCode(110001, "对象没有找到", "object not found.");

    public static ResultCode KA_IDENTITY_NOT_FOUND = new ResultCode(110002, "身份id不存在", "identity not found.");

    public static ResultCode KA_RPC_EXCEPTION = new ResultCode(110003, "二方服务调用异常", "dubbo rpc error.");

    public static ResultCode KA_DUPLICATE_KEY_EXCEPTION = new ResultCode(110004, "重复记录错误", "DuplicateKeyException.");

    public static ResultCode KA_NOT_SUPPORT_EXCEPTION = new ResultCode(110005, "暂不支持", "not support.");

    public static ResultCode KA_IDENTITY_SERVICE_EXCEPTION = new ResultCode(110006, "请求用户中心服务异常", "identity service exception.");

    public static ResultCode KA_FORBIDDEN_EXCEPTION = new ResultCode(110007, "系统禁止", "forbidden exception.");
    public static ResultCode APP_ID_INVALID = new ResultCode(100603, "appId无效", "appId invalid");
    public static ResultCode API_INVALID = new ResultCode(100604, "API为空或无效", "api invalid");
    public static ResultCode META_RESPONSE_EXCEPTION = new ResultCode(100019, "metadata数据返回异常", "metadata response exception");

    /**
     * 校验code 110100 - 110200
     */
    public static ResultCode KA_ILLEGAL_ARGUMENT = new ResultCode(110100, "参数非法", "illegal argument");
    public static ResultCode KA_OBJECT_SIZE_NOT_EQUAL = new ResultCode(110101, "返回数据条数不对等", "object size not equals");
    public static ResultCode KA_NULL_OBJECT = new ResultCode(110102, "空对象错误", "null object");
    public static ResultCode KA_ARGUMENT_ILLEGAL_LENGTH = new ResultCode(110103, "参数[${param}]限制${limitLength}个字", "argument length illegal");
    public static ResultCode KA_ARGUMENT_NAME_REGEX_ILLEGAL = new ResultCode(110104, "参数命名不合法", "argument name illegal, only support digit/alpha/-/_");
    public static ResultCode KA_NULL_POINTER_EXCEPTION = new ResultCode(110105, "系统异常", "null pointer exception");
    public static ResultCode KA_ILLEGAL_ACCESS_EXCEPTION = new ResultCode(110106, "非法访问", "illegal access");
    public static ResultCode KA_ILLEGAL_ARGUMENT_CONTEXT_TENANT_ID = new ResultCode(110107, "租户ID为空", "tenant_id is null");
    public static ResultCode KA_ILLEGAL_ARGUMENT_CONTEXT_IDENTITY_ID = new ResultCode(110108, "访问身份为空", "identity_id is null");
    public static ResultCode KA_ILLEGAL_ARGUMENT_CONTEXT = new ResultCode(110109, "上下文context为空", "context is null");
    public static ResultCode KA_CREATE_DEFAULT_SPACE_ERROR = new ResultCode(110110, "创建默认的Space空间错误", "create default space error");
    public static ResultCode KA_PERMISSION_CHECK_FAIL = new ResultCode(110111, "权限验证失败", "permission check fail");
    public static ResultCode KA_TASK_TAG_CAL_DATA_ERROR = new ResultCode(110112, "计算任务tag信息失败", "task cal tag data fail");
    public static ResultCode KA_ILLEGAL_OBJECT = new ResultCode(110113, "非法对象", "illegal object");
    public static ResultCode KA_VALID_FAIL = new ResultCode(110114, "校验失败", "valid fail");
    public static ResultCode COMMENT_INVALID_TYPE = new ResultCode(110115, "评论类型字段不正确", "comment entity_type valid");
    public static ResultCode APP_ID_EMPTY = new ResultCode(110116, "AppId为空", "appId is null");
    public static ResultCode API_NAME_EMPTY = new ResultCode(110117, "调用metaApi时apiName为空", "apiName is null");
    public static ResultCode TOKEN_INVALID = new ResultCode(110118, "token过期，请刷新页面重试", "token invalid");
    public static ResultCode STATUS_INVALID = new ResultCode(110119, "当前状态不能操作", "status invalid");

    /**
     * xx code 110200 - 110300
     */
    public static ResultCode KA_NO_PERMISSION_EXCEPTION = new ResultCode(110200, "无权限操作", "has no permission");
    public static ResultCode DATA_EMPTY_OR_DENY = new ResultCode(110201, "数据不存在或无权限", "data is empty or no permission");

    /**
     * xx code 110300 - 110400
     */
    public static ResultCode KA_REPORT_RECEIVERS_EMPTY_EXCEPTION = new ResultCode(110300, "简报接收人为空", "empty report receivers");

    /**
     * 业务异常
     */
    public static ResultCode JOIN_TENANT_ERROR = new ResultCode(110400, "加入租户失败", "join tenant error");
    public static ResultCode INVITE_CODE_INVALID_ERROR = new ResultCode(110401, "邀请码失效, 请联系邀请人", "invite code error");
    public static ResultCode ACCOUNT_ABNORMAL = new ResultCode(110402, "账号状态异常,请联企业系管理员", "account status abnormal.");
    public static ResultCode CREATE_INVITE_CODE_ERROR = new ResultCode(110403, "创建邀请码失败, 请联系企业管理员", "create invite code error");
    public static ResultCode UPDATE_INVITE_CODE_EXPIRE_TIME_ERROR = new ResultCode(110404, "更新邀请码过期时间失败, 请联系企业管理员", "update invite code expire time error");

    public static ResultCode PARAMETER_INVALID = new ResultCode(172000, "参数无效", "Parameter invalid.");
    public static ResultCode EXAM_ERROR = new ResultCode(172001, "${message}", "${message}");

}
