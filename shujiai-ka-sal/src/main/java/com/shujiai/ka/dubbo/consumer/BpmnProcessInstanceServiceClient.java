package com.shujiai.ka.dubbo.consumer;

import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.shujiai.apaas.bpmn.facade.dto.process.*;
import com.shujiai.apaas.bpmn.facade.dto.task.ResendMessageReq;
import com.shujiai.apaas.bpmn.facade.runtime.modelfront.FrontModelDubboService;
import com.shujiai.apaas.bpmn.facade.runtime.process.BpmnProcessActionService;
import com.shujiai.apaas.bpmn.facade.runtime.process.BpmnProcessInstanceService;
import com.shujiai.apaas.bpmn.facade.runtime.task.TaskCollectionResourceService;
import com.shujiai.apaas.bpmn.facade.utils.modeler.EditorProcess;
import com.shujiai.apaas.bpmn.facade.utils.modeler.FrontModelVO;
import com.shujiai.base.context.Context;
import com.shujiai.base.exception.BizException;
import com.shujiai.base.result.Result;
import com.shujiai.base.result.ResultCode;
import com.shujiai.metadata.facade.codes.MetadataResultCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author: hanliang
 * @date: 2021/6/22 下午1:25
 */
@Component
@Slf4j
public class BpmnProcessInstanceServiceClient {

    @Reference(check = false, group = "${dubbo.bpmn.group.name}", version = "${dubbo.bpmn.version}", timeout = 15000, retries = 0)
    private BpmnProcessInstanceService bpmnProcessInstanceService;

    @Reference(check = false, group = "${dubbo.bpmn.group.name}", version = "${dubbo.bpmn.version}", timeout = 10000, retries = 0)
    private BpmnProcessActionService bpmnProcessActionService;

    @Reference(check = false, group = "${dubbo.bpmn.group.name}", version = "${dubbo.bpmn.version}", timeout = 10000, retries = 0)
    private TaskCollectionResourceService taskCollectionResourceService;

    @Reference(check = false, group = "${dubbo.bpmn.group.name}", version = "${dubbo.bpmn.version}", timeout = 10000, retries = 0)
    private FrontModelDubboService frontModelDubboService;


    public EditorProcess releaseTableFlow(String tenantId, String appId, String version, String modelCode) {
        Result<EditorProcess> result = frontModelDubboService.releaseModelDrive(tenantId, appId, version, modelCode);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    public EditorProcess bindTableFlow(String tenantId, String appId, String version, String modelCode,
                                       String tableName ,String tableDesc) {
        Result<EditorProcess> result = frontModelDubboService.bindModelDerive(tenantId, appId, version,
                modelCode,tableName,tableDesc);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }
    public EditorProcess getEditorProcessByModelCode(String tenantId, String appId, String version, String modelCode) {
        Result<EditorProcess> result = frontModelDubboService.getEditorProcessByModelCode(tenantId, appId, version, modelCode);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    public FrontModelVO getFrontModelVOByModelCode(String tenantId,
                                                   String appId, String version,
                                                   String modelCode) {
        Result<FrontModelVO> result = frontModelDubboService.getFrontModelVOByModelCode(tenantId, appId, version, modelCode);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    /**
     * 启动流程
     *
     * @param context
     * @param processCreateRequest
     * @return
     */
    public ProcessBaseResponse createProcessInstanceDTO(Context context, ProcessCreateRequest processCreateRequest) {
        Result<ProcessBaseResponse> result = bpmnProcessInstanceService.createProcessInstance(context, processCreateRequest);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    /**
     * 撤销流程
     *
     * @param context
     * @param processCancelRequest
     * @return
     */
    public ProcessCancelResponse cancelProcess(Context context, ProcessCancelRequest processCancelRequest) {
        Result<ProcessCancelResponse> result = bpmnProcessActionService.cancelProcess(context, processCancelRequest);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    public ProcessCancelResponse cancelProcessNoCheck(Context context, ProcessCancelRequest processCancelRequest) {
        Result<ProcessCancelResponse> result =
                bpmnProcessActionService.cancelProcessNoCheck(context, processCancelRequest);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    /**
     * 撤销审批流
     *
     * @param id 审批流id
     * @return 是否撤销成功
     */
    public boolean cancelProcessById(Context context, String id, boolean throwIfFail) {
        ProcessCancelRequest processCancelRequest = new ProcessCancelRequest();
        processCancelRequest.setProcessInstanceId(id);
        processCancelRequest.setCancelReason("人工撤销");
        boolean result = true;
        try {
            result = Optional.ofNullable(cancelProcessNoCheck(context, processCancelRequest)).map(ProcessCancelResponse::getResult).orElse(false);
        } catch (BizException e) {
            log.error("BpmnProcessInstanceServiceClient[cancelProcessById] error, exception: ", e);
            log.error("BpmnProcessInstanceServiceClient[cancelProcessById] error, result:", JSONUtil.toJsonStr(e.getResultCode()));
            if (throwIfFail) {
                // 上级抛出异常这边捕获
                throw new BizException(MetadataResultCodes.CORE_HR_JOB_CHANGE_CANCEL_PROCESS_ERROR);
            }
        }
        return result;
    }

    /**
     * 撤销审批流
     *
     * @param context 上下文
     * @param id      审批流id
     * @param reason  撤销原因
     * @return 是否撤销成功
     */
    public boolean cancelProcessById(Context context, String id, String reason) {
        ProcessCancelRequest processCancelRequest = new ProcessCancelRequest();
        processCancelRequest.setProcessInstanceId(id);
        processCancelRequest.setCancelReason(reason);
        return cancelProcessNoCheck(context, processCancelRequest).getResult();
    }

    /**
     * 删除流程
     *
     * @param context
     * @param processDeleteRequest
     * @return
     */
    public void deleteProcessInstance(Context context, ProcessDeleteRequest processDeleteRequest) {
        bpmnProcessActionService.deleteProcess(context, processDeleteRequest);
    }

    /**
     * 查询当前人是否有权限（当前审批流关联的发起人、审批人、抄送人等有权限）
     *
     * @param context
     * @param processBaseRequest
     * @return
     */
    public Boolean permissionProcess(Context context, ProcessBaseRequest processBaseRequest) {
        Result<Boolean> result = bpmnProcessActionService.permissionProcess(context, processBaseRequest);
        if (!result.isSuccess()) {
            log.error("校验是否审批相关人员出错:{}", result.getMessage());
            return Boolean.FALSE;
        }
        return result.getData();
    }

    /**
     * 催办审批流
     *
     * @param context           context
     * @param processInstanceId 审批流程id
     */
    public void remindProcess(Context context, String processInstanceId) {
        ResendMessageReq req = new ResendMessageReq();
        req.setProcessInstanceId(processInstanceId);
        req.setIsFlowLimit(true);
        Result<Boolean> result = taskCollectionResourceService.resendMessage(context, req);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
    }

    /**
     * 查询审批流当前是否还能继续催办
     *
     * @param context            context
     * @param processInstanceIds 审批流程id列表
     */
    public List<UrgeStatus> getProcessUrgeStatus(Context context, List<String> processInstanceIds) {
        ProcessUrgeStatusRequest req = new ProcessUrgeStatusRequest();
        req.setProcessInstanceIdList(processInstanceIds);
        Result<ProcessUrgeStatusResponse> result = taskCollectionResourceService.getProcessUrgeStatus(context, req);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }

        if (Objects.isNull(result.getData()) || CollectionUtils.isEmpty(result.getData().getUrgeStatusList())) {
            return Lists.newArrayList();
        }
        return result.getData().getUrgeStatusList();
    }
}
