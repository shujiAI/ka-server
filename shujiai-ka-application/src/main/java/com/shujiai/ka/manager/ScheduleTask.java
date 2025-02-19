package com.shujiai.ka.manager;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.shujiai.ka.dubbo.consumer.HrEmployeeServiceClient;
import com.shujiai.ka.dubbo.consumer.RbacServiceClient;
import com.shujiai.base.context.Context;
import com.shujiai.usercenter.facade.dto.req.query.DeptHrEmployeeQueryReq;
import com.shujiai.usercenter.facade.dto.rsp.HrEmployeeSimpleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author guowei
 */
@Component
public class ScheduleTask {

    @Autowired
    private HrEmployeeServiceClient hrEmployeeServiceClient;
    @Autowired
    private RbacServiceClient rbacServiceClient;

    /**
     * 中台角色编码，不同环境保持一致
     * 根据角色编码获取各基地对应审批人
     */
    public static final String NATION_ROLE_CODE = "role-EC2T9eijZTKWF0ntumcAEmjFVZDCgPHS";
    public static final String PROVINCE_ROLE_CODE = "role-PaWV73WytBjFBGhEVk8G4kEBKpl509EO";
    public static final String TRAIN_ROLE_CODE = "role-H8DPT1QHheGRjOhkOfkjNg4Hp5khaXNM";
    public static final String SPECIALITY_ROLE_CODE = "role-vAIUbemrmkBxrRHSh0Qnwz8BzumzN3Bh";

    public List<Map<String, Object>> createSpecialtyBaseTask(Context context, List<Map<String, Object>> taskList, List<Map<String, Object>> specialtyBaseList) {

        if (taskList == null || taskList.isEmpty() || specialtyBaseList == null || specialtyBaseList.isEmpty()) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> baseTaskList = new ArrayList<>();
        String BASE_TYPE = "1";

        //专业基地的任务
        List<Map<String, Object>> specialtyTaskList = taskList.stream().filter(t -> BASE_TYPE.equals(t.get("base_type"))).collect(Collectors.toList());

        //先按任务ID 分组，再按业务表单基地专业 分组
        Map<String, Map<String, List<Map<String, Object>>>> taskMap = specialtyTaskList.stream().collect(Collectors.groupingBy(task -> (String) task.get("task_id"), Collectors.groupingBy(task -> (String) task.get("training_specialty"))));

        Map<String, List<Map<String, Object>>> specialtyBaseMap = specialtyBaseList.stream()
                .filter(base -> base.get("specialty") != null)
                .collect(Collectors.groupingBy(base -> (String) base.get("specialty")));


        taskMap.forEach((taskId, bizFormBaseMap) -> {

            bizFormBaseMap.forEach((trainingSpecialty, bizFormBaseFormList) -> {
                //专业 对应的 专业基地
                List<Map<String, Object>> baseList = specialtyBaseMap.get(trainingSpecialty);
                if (baseList == null) {
                    return;
                }

                for (Map<String, Object> base : baseList) {

                    Map<String, Object> dept = JSON.parseObject(JSON.toJSONString(base.get("department")));

                    List<String> specialtyApprover = getApprovalUserIds(context, (String) dept.get("id"), SPECIALITY_ROLE_CODE);
                    List<String> trainApprover = getApprovalUserIds(context, getParentDeptId(dept), TRAIN_ROLE_CODE);

                    Map<String, Object> baseTask = new HashMap<>();
                    baseTask.put("id", IdUtil.fastSimpleUUID());
                    baseTask.put("task_id", taskId);
                    baseTask.put("training_specialty", trainingSpecialty);
                    baseTask.put("base_type", BASE_TYPE);
                    baseTask.put("dept_id", dept.get("id"));
                    baseTask.put("base_id", base.get("id"));
                    baseTask.put("base_name", base.get("name"));
                    baseTask.put("trian_base_id", base.get("train_base_id"));
                    baseTask.put("trian_base_name", base.get("train_base_name"));
                    baseTask.put("base_province", base.get("area"));
                    baseTask.put("sub_form_num", bizFormBaseFormList.size() - 1);
                    baseTask.put("specialty_approver", specialtyApprover);
                    baseTask.put("train_approver", trainApprover);
                    baseTask.put("status", "0");

                    String focusType = base.get("focus_type") == null ? "1" : (String) base.get("focus_type");

                    List<Map<String, Object>> baseTaskDetailList = bizFormBaseFormList.stream().filter(taskForm -> {
                        //适用基地类别（1全部 2常规 3重点）
                        String applyFocusType = (String) taskForm.get("apply_base_type");
                        return focusType.equals(applyFocusType) || "1".equals(applyFocusType);
                    }).map(this::convertTaskFormToBaseTaskDetail).collect(Collectors.toList());

                    baseTask.put("core_index_num", coreIndexNumSum(baseTaskDetailList));
                    baseTask.put("bse_task_record_detail", baseTaskDetailList);

                    baseTaskList.add(baseTask);
                }
            });
        });

        return baseTaskList;
    }

    public List<Map<String, Object>> createTrainBaseTask(Context context, List<Map<String, Object>> taskList, List<Map<String, Object>> trainBaseList) {

        if (taskList == null || taskList.isEmpty() || trainBaseList == null || trainBaseList.isEmpty()) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> baseTaskList = new ArrayList<>();
        String BASE_TYPE = "2";

        //培训基地的任务
        List<Map<String, Object>> trainTaskList = taskList.stream().filter(t -> BASE_TYPE.equals(t.get("base_type"))).collect(Collectors.toList());

        //先按任务ID 分组，再按业务表单基地ID 分组
        Map<String, Map<String, List<Map<String, Object>>>> taskMap = trainTaskList.stream().collect(Collectors.groupingBy(task -> (String) task.get("task_id"), Collectors.groupingBy(task -> (String) task.get("biz_form_base_id"))));


        taskMap.forEach((taskId, bizFormBaseMap) -> {
            bizFormBaseMap.forEach((bizFormBaseId, bizFormBaseFormList) -> {

                for (Map<String, Object> base : trainBaseList) {

                    Map<String, Object> dept = JSON.parseObject(JSON.toJSONString(base.get("department")));

                    List<String> trainApprover = getApprovalUserIds(context, (String) dept.get("id"), TRAIN_ROLE_CODE);
                    List<String> provinceApprover = getApprovalUserIds(context, getParentDeptId(dept), PROVINCE_ROLE_CODE);

                    Map<String, Object> baseTask = new HashMap<>();
                    baseTask.put("id", IdUtil.fastSimpleUUID());
                    baseTask.put("task_id", taskId);
                    baseTask.put("biz_form_base_id", bizFormBaseId);
                    baseTask.put("base_type", BASE_TYPE);
                    baseTask.put("dept_id", dept.get("id"));
                    baseTask.put("base_id", base.get("id"));
                    baseTask.put("base_name", base.get("name"));
                    baseTask.put("trian_base_id", base.get("id"));
                    baseTask.put("trian_base_name", base.get("name"));
                    baseTask.put("base_province", base.get("area"));
                    baseTask.put("sub_form_num", bizFormBaseFormList.size() - 1);
                    baseTask.put("train_approver", trainApprover);
                    baseTask.put("province_approver", provinceApprover);
                    baseTask.put("status", "0");

                    List<Map<String, Object>> baseTaskDetailList = bizFormBaseFormList.stream().map(this::convertTaskFormToBaseTaskDetail).collect(Collectors.toList());

                    baseTask.put("core_index_num", coreIndexNumSum(baseTaskDetailList));
                    baseTask.put("bse_task_record_detail", baseTaskDetailList);

                    baseTaskList.add(baseTask);
                }
            });
        });

        return baseTaskList;
    }

    private int coreIndexNumSum(List<Map<String, Object>> baseTaskDetailList) {
        return baseTaskDetailList.stream().filter(baseTaskDetail -> "1".equals(baseTaskDetail.get("form_attr"))).mapToInt(f -> f.get("core_index_num") == null ? 0 : (Integer) f.get("core_index_num")).reduce(Integer::sum).orElse(0);
    }

    private Map<String, Object> convertTaskFormToBaseTaskDetail(Map<String, Object> taskForm) {

        Map<String, Object> baseTaskDetail = new HashMap<>();
        baseTaskDetail.put("biz_form_base_form_id", taskForm.get("biz_form_base_form_id"));
        baseTaskDetail.put("form_id", taskForm.get("form_id"));
        baseTaskDetail.put("form_name", taskForm.get("form_name"));
        baseTaskDetail.put("form_type", taskForm.get("form_type"));
        baseTaskDetail.put("form_attr", taskForm.get("form_attr"));
        baseTaskDetail.put("form_content", taskForm.get("form_json"));
        baseTaskDetail.put("status", "0");
        baseTaskDetail.put("core_index_num", taskForm.get("core_index_num"));
        baseTaskDetail.put("write_times", taskForm.get("write_times"));
        baseTaskDetail.put("write_control", taskForm.get("write_control"));
        baseTaskDetail.put("page_path", taskForm.get("page_path"));

        Object writeTimes = taskForm.get("write_times");
        if (writeTimes instanceof Integer && (Integer) writeTimes > 0) {
            List<Map<String, Object>> baseTaskDetailParts = new ArrayList<>();
            for (int i = 0; i < (Integer) writeTimes; i++) {
                Map<String, Object> baseTaskDetailPart = new HashMap<>();
                baseTaskDetailPart.put("form_type", taskForm.get("form_type"));
                baseTaskDetailPart.put("form_attr", taskForm.get("form_attr"));
                baseTaskDetailPart.put("form_content", taskForm.get("form_json"));
                baseTaskDetailPart.put("status", "0");
                baseTaskDetailPart.put("core_index_num", taskForm.get("core_index_num"));
                baseTaskDetailParts.add(baseTaskDetailPart);
            }
            baseTaskDetail.put("bse_task_record_part", baseTaskDetailParts);
        }

        return baseTaskDetail;
    }

    private String getParentDeptId(Map<String, Object> dept) {
        String deptId = (String) dept.get("id");
        String idPath = (String) dept.get("idPath");
        String pDeptId = deptId;
        String[] idPaths = idPath.split("/");
        if (idPaths.length > 1) {
            pDeptId = idPaths[idPaths.length - 2];
        }
        return pDeptId;
    }

    public List<String> getApprovalUserIds(Context context, String deptId, String roleCode) {
        DeptHrEmployeeQueryReq req = new DeptHrEmployeeQueryReq();
        req.setDeptIds(Collections.singletonList(deptId));
        req.setLeave(false);
        List<HrEmployeeSimpleDTO> deptUsers = hrEmployeeServiceClient.getOnlyEmployeeSimpleList(context, req);
        List<String> userIds = deptUsers.stream().map(HrEmployeeSimpleDTO::getIdentityId).collect(Collectors.toList());
        userIds = rbacServiceClient.getAllUserIdsOfRole(context, roleCode, userIds);
        return userIds;
    }


}
