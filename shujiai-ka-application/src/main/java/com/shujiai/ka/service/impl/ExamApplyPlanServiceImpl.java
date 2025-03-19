package com.shujiai.ka.service.impl;

import com.shujiai.ka.api.ExamApplyPlanApi;
import com.shujiai.ka.constant.KAConstant;
import com.shujiai.ka.dubbo.manager.ApiInvokeManager;
import com.shujiai.ka.exceptions.KABizException;
import com.shujiai.ka.constant.ExamApplyResultCodes;
import com.shujiai.ka.manager.ExamApplyPlanManager;
import com.shujiai.ka.service.ExamApplyPlanService;
import com.shujiai.ka.tablefield.ExamApplyPlanDbFieldConstants;
import com.shujiai.ka.util.ContextCheck;
import com.shujiai.base.context.Context;
import com.shujiai.base.result.Result;
import com.shujiai.gateway.register.annotation.YdcGateway;
import com.shujiai.metadata.facade.dto.sql.QueryResult;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ExamApplyPlanServiceImpl
 *
 * @author hxh
 * @date 2023/11/9
 * @time 13:28:10
 */
@org.springframework.stereotype.Service
@Service(version = "${dubbo.provider.version}", group = "${dubbo.provider.group}")
public class ExamApplyPlanServiceImpl implements ExamApplyPlanService {
    @Autowired
    private ExamApplyPlanManager examApplyPlanManager;
    @Autowired
    @Qualifier("sysApiInvokeManagerImpl")
    private ApiInvokeManager apiInvokeManager;
    /**
     * 获取list
     *
     * @param context 上下文,包含租户、登录用户等
     * @param appId 应用ID
     * @param version 版本
     * @param params 参数
     *
     * @return {@link Result}<{@link Map}<{@link String}, {@link Object}>>
     */
    @Override
    @YdcGateway(url = "kaserver/demoGetExamApplyPlanList.json", name = "demo：获取xxx水平测试list")
    public Result<QueryResult> getExamApplyPlanList(@Valid @NotNull Context context, @Valid @NotEmpty(message = "appId字段不能为空") String appId, @Valid @NotEmpty(message = "version字段不能为空") String version, Map<String, Object> params) {
        ContextCheck.valid(context);
        ContextCheck.validParam(appId, KAConstant.APP_ID_KEY);
//        //构造参数，查询plan下的人员数量
//        permissionManager.setParamsWithDeptProcessLevel(context, params);

        QueryResult queryResult = examApplyPlanManager.customGetExamApplyPlanList(context, appId, version, params);

        return new Result<>(queryResult);
    }

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
    @Override
    @YdcGateway(url = "kaserver/demoAddExamApplyPlan.json", name = "demo：创建xxx水平测试")
    public Result<Map<String, Object>> addExamApplyPlan(@Valid @NotNull Context context, @Valid @NotEmpty(message = "appId字段不能为空") String appId, @Valid @NotEmpty(message = "version字段不能为空") String version, Map<String, Object> params) {
        ContextCheck.valid(context);
        //参数校验
        ContextCheck.validParam(params.get(ExamApplyPlanDbFieldConstants.year), ExamApplyPlanDbFieldConstants.year);
        String applyBeginDate = (String) params.get(ExamApplyPlanDbFieldConstants.applyBeginDate);
        String applyEndDate = (String) params.get(ExamApplyPlanDbFieldConstants.applyEndDate);
        String approveBeginDate = (String) params.get(ExamApplyPlanDbFieldConstants.approveBeginDate);
        String approveEndDate = (String) params.get(ExamApplyPlanDbFieldConstants.approveEndDate);
        ContextCheck.validParam(applyBeginDate, ExamApplyPlanDbFieldConstants.applyBeginDate);
        ContextCheck.validParam(applyEndDate, ExamApplyPlanDbFieldConstants.applyEndDate);
        ContextCheck.validParam(approveBeginDate, ExamApplyPlanDbFieldConstants.approveBeginDate);
        ContextCheck.validParam(approveEndDate, ExamApplyPlanDbFieldConstants.approveEndDate);

        checkAllFieldTime( applyBeginDate,  approveBeginDate,  applyEndDate,  approveEndDate);
        //判断重复
        List<Map<String, Object>> sameYearList = examApplyPlanManager.listALl(context, appId, version,
                new HashMap<String, Object>() {{
                    put(ExamApplyPlanDbFieldConstants.year, params.get(ExamApplyPlanDbFieldConstants.year));
                    put(ExamApplyPlanDbFieldConstants.stopPlan, 0);
                }});
        if(sameYearList.size() > 0){
            throw new KABizException(ExamApplyResultCodes.EXAM_APPLY_ERROR, "已存在该xxx水平测试任务");
        }
        //参数处理
        params.remove(ExamApplyPlanDbFieldConstants.ID);
        params.put(ExamApplyPlanDbFieldConstants.stopPlan, 0);

        Map<String, Object> map = examApplyPlanManager.createExamApplyPlan(context, appId, version, params);
        return new Result<>(map);
    }

    private String changeLongToDateString(Object date){
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        if(date instanceof Long) {
            Date date1 = new Date((Long) date);
            String dateStr = sdfDate.format(date1);
            return dateStr;
        }
        if(date instanceof String) {
            return (String) date;
        }
        return null;
    }

    private void checkAllFieldTime(String applyBeginDate, String approveBeginDate, String applyEndDate, String approveEndDate){
        ContextCheck.validParam(applyBeginDate, ExamApplyPlanDbFieldConstants.applyBeginDate);
        ContextCheck.validParam(applyEndDate, ExamApplyPlanDbFieldConstants.applyEndDate);
        ContextCheck.validParam(approveBeginDate, ExamApplyPlanDbFieldConstants.approveBeginDate);
        ContextCheck.validParam(approveEndDate, ExamApplyPlanDbFieldConstants.approveEndDate);
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfBegin = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        SimpleDateFormat sdfEnd = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        try {
            Date today = sdfBegin.parse(sdfBegin.format(new Date()));
            Date applyBeginDate1 = sdfBegin.parse(sdfBegin.format(sdfDate.parse(applyBeginDate)));
            Date approveBeginDate1 = sdfBegin.parse(sdfBegin.format(sdfDate.parse(approveBeginDate)));
            Date applyEndDate1 = sdfEnd.parse(sdfEnd.format(sdfDate.parse(applyEndDate)));
            Date approveEndDate1 = sdfEnd.parse(sdfEnd.format(sdfDate.parse(approveEndDate)));
            if(applyBeginDate1.compareTo(today) < 0){
                throw new KABizException(ExamApplyResultCodes.EXAM_APPLY_ERROR, "报名开始日期不得早于操作日当天");
            }
            if(applyEndDate1.compareTo(applyBeginDate1) < 0){
                throw new KABizException(ExamApplyResultCodes.EXAM_APPLY_ERROR, "报名截止日期不得早于报名开始日期");
            }
            if(approveBeginDate1.compareTo(today) < 0){
                throw new KABizException(ExamApplyResultCodes.EXAM_APPLY_ERROR, "审批开始日期不得早于操作日当天");
            }
            if(approveEndDate1.compareTo(approveBeginDate1) < 0){
                throw new KABizException(ExamApplyResultCodes.EXAM_APPLY_ERROR, "审批截止日期不得早于审批开始日期");
            }
            if(approveEndDate1.compareTo(applyEndDate1) < 0){
                throw new KABizException(ExamApplyResultCodes.EXAM_APPLY_ERROR, "审批截止日期不得早于报名截止日期");
            }
        } catch (ParseException e) {
            throw new KABizException(ExamApplyResultCodes.EXAM_APPLY_ERROR, "日期格式错误");
        }
    }

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
    @Override
    @YdcGateway(url = "kaserver/demoUpdateExamApplyPlan.json", name = "demo：编辑xxx水平测试")
    public Result<Object> updateExamApplyPlan(@Valid @NotNull Context context, @Valid @NotEmpty(message = "appId字段不能为空") String appId, @Valid @NotEmpty(message = "version字段不能为空") String version, Map<String, Object> params) {
        ContextCheck.valid(context);
        ContextCheck.validParam(params.get(ExamApplyPlanDbFieldConstants.ID), ExamApplyPlanDbFieldConstants.ID);
        String applyBeginDate = (String) params.get(ExamApplyPlanDbFieldConstants.applyBeginDate);
        String applyEndDate = (String) params.get(ExamApplyPlanDbFieldConstants.applyEndDate);
        String approveBeginDate = (String) params.get(ExamApplyPlanDbFieldConstants.approveBeginDate);
        String approveEndDate = (String) params.get(ExamApplyPlanDbFieldConstants.approveEndDate);

        //判断重复
        QueryResult queryResult = examApplyPlanManager.customGetExamApplyPlanList(context, appId, version,
                new HashMap<String, Object>() {{
                    put(ExamApplyPlanDbFieldConstants.ID, params.get(ExamApplyPlanDbFieldConstants.ID));
                }});
        List<Map<String, Object>> list = queryResult.getData();
        if(list == null || list.size() == 0){
            throw new KABizException(ExamApplyResultCodes.EXAM_APPLY_ERROR, "未找到存在该xxx水平测试任务");
        }
        Map<String, Object> dto = list.get(0);
        if(Integer.parseInt(dto.get(ExamApplyPlanDbFieldConstants.stopPlan)+"") == 1){
            throw new KABizException(ExamApplyResultCodes.EXAM_APPLY_ERROR, "该xxx水平测试任务已终止");
        }
        String status = (String) dto.get(ExamApplyPlanDbFieldConstants.status);
        Map<String, Object> doParam = new HashMap<>(16);
        if("未开始".equals(status)){
            //参数校验:
            checkAllFieldTime( applyBeginDate,  approveBeginDate,  applyEndDate,  approveEndDate);
            doParam = params;
        }else if("进行中".equals(status)){
            checkAllFieldTime( changeLongToDateString(dto.get(ExamApplyPlanDbFieldConstants.applyBeginDate)),
                    approveBeginDate,  applyEndDate,  approveEndDate);
            doParam.put(ExamApplyPlanDbFieldConstants.ID, params.get(ExamApplyPlanDbFieldConstants.ID));
            doParam.put(ExamApplyPlanDbFieldConstants.applyEndDate, applyEndDate);
            doParam.put(ExamApplyPlanDbFieldConstants.approveBeginDate, approveBeginDate);
            doParam.put(ExamApplyPlanDbFieldConstants.approveEndDate, approveEndDate);
        } else if("已结束".equals(status)){
            checkAllFieldTime( changeLongToDateString(dto.get(ExamApplyPlanDbFieldConstants.applyBeginDate)),
                    changeLongToDateString(dto.get(ExamApplyPlanDbFieldConstants.approveBeginDate)),
                    applyEndDate,  approveEndDate);
            doParam.put(ExamApplyPlanDbFieldConstants.ID, params.get(ExamApplyPlanDbFieldConstants.ID));
            doParam.put(ExamApplyPlanDbFieldConstants.applyEndDate, applyEndDate);
            doParam.put(ExamApplyPlanDbFieldConstants.approveEndDate, approveEndDate);
        } else{
            return null;
        }

        Boolean flag = examApplyPlanManager.updateExamApplyPlan(context, appId, version, doParam);
        return new Result<>(flag);
    }

    @Override
    @YdcGateway(url = "kaserver/testA.json", name = "demo：编辑xxx水平测试")
    public Result<QueryResult> testA(Context context, String appId, String version, Map<String, Object> params) {
        QueryResult queryResult = apiInvokeManager.queryByCustomerApi(
                context, appId, version, ExamApplyPlanApi.TESTA,
                params
        );

        return new Result<>(queryResult);
    }
}
