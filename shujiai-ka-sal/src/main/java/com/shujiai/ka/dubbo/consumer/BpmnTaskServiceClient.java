package com.shujiai.ka.dubbo.consumer;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.shujiai.apaas.bpmn.facade.dto.DataResult;
import com.shujiai.apaas.bpmn.facade.dto.IdRequest;
import com.shujiai.apaas.bpmn.facade.dto.UserInfoDTO;
import com.shujiai.apaas.bpmn.facade.dto.process.ProcessCancelRequest;
import com.shujiai.apaas.bpmn.facade.dto.process.ProcessCancelResponse;
import com.shujiai.apaas.bpmn.facade.dto.task.*;
import com.shujiai.apaas.bpmn.facade.history.comment.HistoryCommentService;
import com.shujiai.apaas.bpmn.facade.runtime.process.BpmnProcessActionService;
import com.shujiai.apaas.bpmn.facade.runtime.task.TaskCollectionResourceService;
import com.shujiai.apaas.bpmn.facade.runtime.task.TaskResourceService;
import com.shujiai.apaas.bpmn.facade.utils.ApproveCodeEnum;
import com.shujiai.apaas.bpmn.facade.utils.BpmnResultCodes;
import com.shujiai.base.context.Context;
import com.shujiai.base.exception.BizException;
import com.shujiai.base.result.Result;
import com.shujiai.base.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author duanjie
 * @version 1.0.0
 * @date 2021/10/10
 */
@Component
@Slf4j
public class BpmnTaskServiceClient {

    @Reference(check = false, group = "${dubbo.bpmn.group.name}", version = "${dubbo.bpmn.version}", timeout = 10000, retries = 0)
    private TaskCollectionResourceService taskCollectionResourceService;

    @Reference(check = false, group = "${dubbo.bpmn.group.name}", version = "${dubbo.bpmn.version}", timeout = 10000, retries = 0)
    private TaskResourceService taskResourceService;

    @Reference(check = false, group = "${dubbo.bpmn.group.name}", version = "${dubbo.bpmn.version}", timeout = 10000, retries = 0)
    private BpmnProcessActionService bpmnProcessActionService;

    @Reference(check = false, group = "${dubbo.bpmn.group.name}", version = "${dubbo.bpmn.version}", timeout = 10000, retries = 0)
    private HistoryCommentService historyCommentService;
    /**
     * 发起待办任务
     *
     * @param context           context
     * @param taskCreateRequest 创建任务id
     * @return 任务id列表
     */
    public List<String> createTasks(Context context, TaskCreateRequest taskCreateRequest) {
        Result<DataResult<TaskBaseResponse>> tasks = taskCollectionResourceService.createTasks(context, taskCreateRequest);
        if (!tasks.isSuccess()) {
            throw new BizException(new ResultCode(tasks.getCode(), tasks.getMessage(), tasks.getArgs()));
        }
        if (Objects.nonNull(tasks.getData()) && Objects.nonNull(tasks.getData().getData())) {
            return tasks.getData().getData().stream().map(TaskBaseResponse::getId).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * 批量撤销任务
     *
     * @param context          context
     * @param taskBatchRequest 批量撤销任务请求
     */
    public void batchCancelTask(Context context, TaskBatchRequest taskBatchRequest) {
        Result result = taskResourceService.batchCancelTask(context, taskBatchRequest);
        if (BpmnResultCodes.BPMN_TASK_INSTANCE_NOT_FOUND.getCode().equals(result.getCode())) {
            return;
        }
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
    }


    /**
     * 批量撤销发出的任务
     *
     * @param context     上下文
     * @param ids         任务id
     * @param throwIfFail 是否错误的时候抛出异常
     */
    public void batchCancelTaskByIds(Context context, Collection<String> ids, boolean throwIfFail) {
        if (CollectionUtil.isEmpty(ids)) {
            log.info("batchCancelTaskByIds ids is empty");
            return;
        }
        TaskBatchRequest taskBatchRequest = new TaskBatchRequest();
        taskBatchRequest.setTaskIds(new ArrayList<>(ids));
        Result<List<String>> result = taskResourceService.batchCancelTask(context, taskBatchRequest);
        // 找不到任务实例 不抛错误处理
        if (BpmnResultCodes.BPMN_TASK_INSTANCE_NOT_FOUND.getCode().equals(result.getCode())) {
            return;
        }
        if (throwIfFail && !result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
    }


    public void remindTask(Context context, String taskId) {
        ResendMessageReq req = new ResendMessageReq();
        req.setTaskId(taskId);
        Result<Boolean> result = taskCollectionResourceService.resendMessage(context, req);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
    }

    /**
     * 任务操作
     *
     * @param context       context
     * @param taskActionReq 任务操作请求
     */
    public void executeTaskAction(Context context, TaskActionReq taskActionReq) {
        Result result = taskResourceService.executeTaskActionNoCheck(context, taskActionReq);
        if (BpmnResultCodes.BPMN_TASK_INSTANCE_NOT_FOUND.getCode().equals(result.getCode())) {
            return;
        }
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
    }

    /**
     * 更新任务状态
     *
     * @param context     上下文
     * @param taskId      任务ID
     * @param approveCode 状态
     */
    public void updateTaskApproveCode(Context context, String taskId, ApproveCodeEnum approveCode) {
        if (StrUtil.isBlank(taskId)) {
            return;
        }
        TaskActionReq taskActionReq = new TaskActionReq();
        taskActionReq.setTaskId(taskId);
        taskActionReq.setApproveCode(approveCode);
        taskActionReq.setAction(approveCode.name());
        executeTaskAction(context, taskActionReq);
    }

    /**
     * 批量任务操作
     *
     * @param context       context
     * @param taskActionReq 任务操作请求
     */
    public void batchExecuteTaskAction(Context context, TaskActionReq taskActionReq) {
        Result result = taskResourceService.batchExecuteTaskAction(context, taskActionReq);
        if (BpmnResultCodes.BPMN_TASK_INSTANCE_NOT_FOUND.getCode().equals(result.getCode())) {
            return;
        }

        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
    }

    /**
     * 任务转交
     *
     * @param context
     * @param request
     */
    public void executeTransferAssignee(Context context, TaskTransferRequest request) {
        Result result = taskResourceService.transferAssigneeNoCheck(context, request);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
    }

    /**
     * 撤销流程
     *
     * @param context
     * @param request
     */
    public void cancelProcess(Context context, ProcessCancelRequest request) {
        Result<ProcessCancelResponse> result = bpmnProcessActionService.cancelProcessNoCheck(context, request);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
    }

    /**
     * 修改代办任务信息
     *
     * @param context               context
     * @param taskCardDescUpdateReq 修改请求
     */
    public void updateTasks(Context context, TaskCardDescUpdateReq taskCardDescUpdateReq) {
        Result<TaskCardDescUpdateResp> result = taskResourceService.updateTaskCardDesc(context, taskCardDescUpdateReq);
        if (BpmnResultCodes.BPMN_TASK_INSTANCE_NOT_FOUND.getCode().equals(result.getCode())) {
            return;
        }
        if (BpmnResultCodes.BPMN_TASK_UPDATE_CARD_DESC_ERROR.getCode().equals(result.getCode())) {
            return;
        }

        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
    }

    /**
     * 修改已完成任务的卡片数据
     *
     * @param context                    context
     * @param thirdTaskCardDescUpdateReq 修改请求
     */
    public void updateThirdTaskCardDesc(Context context, ThirdTaskCardDescUpdateReq thirdTaskCardDescUpdateReq) {
        Result<TaskCardDescUpdateResp> result = taskResourceService.updateThirdTaskCardDesc(context, thirdTaskCardDescUpdateReq);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
    }

    /**
     * 获取运行中的taskId列表
     *
     * @param context           context
     * @param request           request
     * @return taskId列表
     */
    public List<String> getRunTaskByProcessInstanceId(Context context, IdRequest request) {
        final Result<List<RuntimeTaskResponse>> result = taskResourceService.getRunTaskByProcessInstanceId(context, request);
        if (result == null || result.getData() == null || result.getData().isEmpty()) {
            throw new BizException(new ResultCode(105005, "审批流程没有任务ID", "审批流程没有任务ID"));
        }
        final List<String> taskIdList = result.getData().stream().filter(dto -> context.getIdentityId().equals(dto.getAssignee()))
                .map(RuntimeTaskResponse::getTaskId).collect(Collectors.toList());

        if (taskIdList == null || taskIdList.size() == 0) {
            throw new BizException(new ResultCode(105005, "未找到您的审批流程任务", "未找到您的审批流程任务"));
        }

        return taskIdList;
    }

    /**
     * 创建历史评论
     */
    public Result<Boolean> createActHisComment(Context context,
                                               String processInstanceId,
                                               String taskId,
                                               String approveCode,
                                               String approveCodeName,
                                               String actionMessage,
                                               List<String> actionFile,
                                               List<UserInfoDTO> userList,
                                               String userId) {
        return historyCommentService.createActHisComment(context, processInstanceId, taskId, approveCode, approveCodeName, actionMessage, actionFile, userList, userId);
    }
}
