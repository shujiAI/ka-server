package com.shujiai.ka.manager;

import com.google.common.collect.Lists;
import com.shujiai.ka.api.ExamApplyPlanApi;
import com.shujiai.ka.constant.KAConstant;
import com.shujiai.ka.dubbo.manager.ApiInvokeManager;
import com.shujiai.ka.util.EntityUtil;
import com.shujiai.base.context.Context;
import com.shujiai.metadata.facade.dto.sql.QueryResult;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ExamApplyPlanManagerImpl
 *
 * @author hxh
 * @date 2023/11/8
 * @time 13:29:23
 */
@Component
public class ExamApplyPlanManager {

    @Autowired
    @Qualifier("sysApiInvokeManagerImpl")
    private ApiInvokeManager apiInvokeManager;

    public QueryResult customGetExamApplyPlanList(Context context, String appId, String version, Map<String, Object> params) {
        QueryResult queryResult = apiInvokeManager.queryByCustomerApi(
                context, appId, version, ExamApplyPlanApi.customGetExamApplyPlanList,
                params
        );

        return queryResult;
    }

    public List<Map<String, Object>> listALl(Context context, String appId, String version, Map<String, Object> params) {
        Long pageNo = 1L;
        Long pageSize = 200L;

        params.put(KAConstant.PAGE_NO_KEY, pageNo);
        params.put(KAConstant.PAGE_SIZE_KEY, pageSize);
        List<Map<String, Object>> rs = Lists.newArrayList();
        List<Map<String, Object>> list = apiInvokeManager.listRecordsV2(
                context, appId, version, ExamApplyPlanApi.SYS_LIST_RECORDS, pageNo,  pageSize,  null, params
        ).getData();
        while (CollectionUtils.isNotEmpty(list)) {
            EntityUtil.deleteCustomFields(list, EntityUtil.SYS_FIELDS_EXCLUDE_TIME);
            rs.addAll(list);
            pageNo++;
            params.put(KAConstant.PAGE_NO_KEY, pageNo);
            list = apiInvokeManager.listRecordsV2(
                    context, appId, version, ExamApplyPlanApi.SYS_LIST_RECORDS, pageNo,  pageSize,  null, params
            ).getData();
        }

        return rs;
    }

    public Map<String, Object> createExamApplyPlan(Context context, String appId, String version, Map<String, Object> params) {
        String id = apiInvokeManager.createIfDuplicateThrow(context, appId, version,
                ExamApplyPlanApi.SYS_CREATE,
                params, "已存在业务水平测试任务");

        Map<String, Object> result = new HashMap<>(2);
        result.put(KAConstant.ID, id);

        return result;
    }

    public Boolean updateExamApplyPlan(Context context, String appId, String version, Map<String, Object> params) {
        Boolean flag = apiInvokeManager.updateById(context, appId, version, ExamApplyPlanApi.SYS_UPDATE, params);

        return flag;
    }

    public QueryResult customGetExamApplyPlanStatus(Context context, String appId, String version, Map<String, Object> params) {
        QueryResult queryResult = apiInvokeManager.queryByCustomerApi(
                context, appId, version, ExamApplyPlanApi.customGetExamApplyPlanStatus,
                params
        );

        return queryResult;
    }
}
