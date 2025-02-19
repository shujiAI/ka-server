package com.shujiai.ka.service;

import com.shujiai.base.context.Context;
import com.shujiai.base.result.Result;
import com.shujiai.metadata.facade.dto.sql.QueryResult;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * ExamApplyPlanService
 * 年度水平测试任务
 *
 * @author hxh
 * @date 2023/11/9
 * @time 12:33:32
 */
public interface ExamApplyPlanService {

    /**
     * 获取列表
     *
     * @param context 上下文,包含租户、登录用户等
     * @param appId 应用ID
     * @param version 版本
     * @param params 参数
     *
     * @return {@link Result}<{@link Map}<{@link String}, {@link Object}>>
     */
    Result<QueryResult> getExamApplyPlanList(@Valid @NotNull Context context,
                                             @Valid @NotEmpty(message = "appId字段不能为空") String appId,
                                             @Valid @NotEmpty(message = "version字段不能为空") String version,
                                             Map<String, Object> params);

    Result<QueryResult> testA(@Valid @NotNull Context context,
                                             @Valid @NotEmpty(message = "appId字段不能为空") String appId,
                                             @Valid @NotEmpty(message = "version字段不能为空") String version,
                                             Map<String, Object> params);


    /**
     * 新增
     *
     * @param context 上下文,包含租户、登录用户等
     * @param appId 应用ID
     * @param version 版本
     * @param params 参数
     *
     * @return {@link Result}<{@link Map}<{@link String}, {@link Object}>>
     */
    Result<Map<String, Object>> addExamApplyPlan(@Valid @NotNull Context context,
                                                 @Valid @NotEmpty(message = "appId字段不能为空") String appId,
                                                 @Valid @NotEmpty(message = "version字段不能为空") String version,
                                                 Map<String, Object> params);

    /**
     * 编辑
     *
     * @param context 上下文,包含租户、登录用户等
     * @param appId 应用ID
     * @param version 版本
     * @param params 参数
     *
     * @return {@link Result}<{@link Map}<{@link String}, {@link Object}>>
     */
    Result<Object> updateExamApplyPlan(@Valid @NotNull Context context,
                                       @Valid @NotEmpty(message = "appId字段不能为空") String appId,
                                       @Valid @NotEmpty(message = "version字段不能为空") String version,
                                       Map<String, Object> params);
}
