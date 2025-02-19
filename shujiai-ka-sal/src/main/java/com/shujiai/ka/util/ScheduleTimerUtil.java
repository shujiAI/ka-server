package com.shujiai.ka.util;

import com.alibaba.fastjson.JSONObject;
import com.shujiai.apaas.rule.facade.xxljob.YundongRuleXxlJobService;
import com.shujiai.apaas.rule.facade.xxljob.dto.XxlJobInfoDTO;
import com.shujiai.apaas.rule.facade.xxljob.enums.ExecutorBlockStrategyEnum;
import com.shujiai.apaas.rule.facade.xxljob.enums.ExecutorRouteStrategyEnum;
import com.shujiai.apaas.rule.facade.xxljob.enums.ScheduleTypeEnum;
import com.shujiai.apaas.rule.facade.xxljob.request.QueryXxlJobInfoRequest;
import com.shujiai.apaas.rule.facade.xxljob.request.XxlJobInfoRequest;
import com.shujiai.base.exception.BizException;
import com.shujiai.base.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ScheduleTimerUtil
 */
@Slf4j
@Component
public class ScheduleTimerUtil {
    public static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(3,
            3, 1000L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    @Reference(check = false, group = "${dubbo.rule.group.name}", version = "${dubbo.rule.version}", timeout = 10000)
    private YundongRuleXxlJobService xxlJobService;
    @Value("${xxl.job.executor.appname}")
    private String jobGroup;

    /**
     * 创建定时任务
     * @param handlerName
     * @param conExpress
     * @param executorParam
     */
    public Integer createOrUpdateTask(String handlerName, String conExpress, String businessId, String executorParam) {
        final XxlJobInfoDTO dbJob = getJobInfoByBusinessId(businessId);

        XxlJobInfoRequest request = new XxlJobInfoRequest();
        request.setAuthor("system");
        request.setBusinessId(businessId);
        request.setExecutorHandler(handlerName);
        request.setExecutorBlockStrategy(ExecutorBlockStrategyEnum.DISCARD_LATER);
        request.setExecutorRouteStrategy(ExecutorRouteStrategyEnum.FIRST);
        request.setScheduleType(ScheduleTypeEnum.CRON);
        request.setScheduleConf(conExpress);
        request.setJobDesc(handlerName);
        request.setExecutorTimeout(600);
        request.setJobGroup(jobGroup);
        request.setExecutorParam(executorParam);

        Result<Integer> result = null;
        if (dbJob != null) {
            request.setId(dbJob.getId());
            final Result<Boolean> booleanResult = xxlJobService.updateJobInfo(request);
            if (!booleanResult.isSuccess()) {
                throw new BizException("更新任务失败:" + businessId + ",return:" + JSONObject.toJSONString(booleanResult));
            }
            return dbJob.getId();
        } else {
            final Result<Integer> createResult = xxlJobService.createAndStartJobInfo(request);
            if (!createResult.isSuccess()) {
                throw new BizException("更新任务失败:" + businessId + ",return:" + JSONObject.toJSONString(createResult));
            }
            return createResult.getData();
        }

    }

    /**
     * 删除定时任务
     * @param businessId
     */
    public void deleteTaskByBusinessId(String businessId) {
        final Result<Boolean> booleanResult = xxlJobService.deleteJobInfo(businessId);
    }

    public XxlJobInfoDTO getJobInfoByBusinessId(String businessId) {
        QueryXxlJobInfoRequest queryXxlJobInfoRequest = new QueryXxlJobInfoRequest();
        queryXxlJobInfoRequest.setBusinessId(businessId);
        queryXxlJobInfoRequest.setPageSize(100L);
        Result<XxlJobInfoDTO> result = xxlJobService.getJobInfoByBusinessId(businessId);
        if (result == null) {
            throw new BizException("查询任务失败");
        }

        return result.getData();
    }

    private boolean isExist(String businessId) {
        final XxlJobInfoDTO job = getJobInfoByBusinessId(businessId);
        return job != null;
    }

}
