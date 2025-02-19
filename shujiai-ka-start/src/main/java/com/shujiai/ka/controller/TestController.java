package com.shujiai.ka.controller;

import com.shujiai.ka.dubbo.consumer.BpmnProcessInstanceServiceClient;
import com.shujiai.ka.manager.ScheduleTask;
import com.shujiai.ka.test.TestOss;
import com.shujiai.apaas.bpmn.facade.dto.process.ProcessBaseResponse;
import com.shujiai.apaas.bpmn.facade.dto.process.ProcessCreateRequest;
import com.shujiai.base.context.Context;
import com.shujiai.base.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private TestOss testOss;
    @Autowired
    private BpmnProcessInstanceServiceClient bpmnProcessInstanceServiceClient;
    @Autowired
    private ScheduleTask scheduleTask;

    @RequestMapping(value = "/uploadOssObject", produces = "application/json")
    public Object uploadOssObject(@RequestParam(value = "file") MultipartFile file) {

        Context context = new Context("60511DDBA5FE493E9F1973F577504CF2", "8b8e158e7a6f469e9d3667bbead8e874");
        String appId = "3e02a807e03529a3eae9c778d4d38318";
        String version = "__sys_draft_version__";

        String fileUrl = null;
        try {
            fileUrl = testOss.uploadOssFile(context, appId, version, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result<>(fileUrl);
    }

    @RequestMapping(value = "/getOssObject", produces = "application/json")
    public Object getOssObject(@RequestBody Map<String, Object> params) {

        try {
            testOss.getOssObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "ok";
    }

    @RequestMapping(value = "/createProcessInstance", produces = "application/json")
    public Object createProcessInstanceDTO(Context context, @RequestBody  Map<String, Object> params) {
//        ProcessCreateRequest processCreateRequest = new ProcessCreateRequest();
//        processCreateRequest.setAppId(table.getAppId());
//        processCreateRequest.setAppVersion(table.getVersion());
//        processCreateRequest.setProcessDefinitionKey(action.getFlowKey());//流程定义id:  流程列表返回的modelKey
//        processCreateRequest.setBusinessKey("123");//业务id，可以是表记录row的id
//        processCreateRequest.setName(action.getFlowName());//流程名称： 流程列表返回的name
//        processCreateRequest.setVariables(params);//流程变量，一搬就是表的一行
//        processCreateRequest.setFrontModelVersion(action.getFlowVersion());//流程版本：流程列表返回的version
//        processCreateRequest.setFrontModelId(action.getFlowId());//流程模型id：流程列表返回的id
//        processCreateRequest.setFormKey(table.getId());//表定义id
//        processCreateRequest.setRecordId(id);//表记录row的id
//        processCreateRequest.setFrontModelType(action.getFrontModelType());//流程模型类型：流程列表返回的modelType
//        processCreateRequest.setTableName(table.getTableName());//表名
//        ProcessBaseResponse processInstanceDTO = bpmnProcessInstanceServiceClient.createProcessInstanceDTO(context, processCreateRequest);
//
//        return processInstanceDTO.getProcessInstanceId();//返回流程实例id

        return null;
    }

    @RequestMapping(value = "/createProcessInstance2", produces = "application/json")
    public Object createProcessInstanceDTO2(Context context, @RequestBody  Map<String, Object> params) {
         context = new Context("71AF130EB02A4F589C4603575B061894", "f3d063b413e24b99b55428a3520f3091");
        String appId = "27fc10f76e3142247d0d2aa183c1e675";
        String version = "__sys_draft_version__";

        params = new HashMap<String, Object>(){{
            put("name","张三");
            put("age", 23);
        }};

        ProcessCreateRequest processCreateRequest = new ProcessCreateRequest();
        processCreateRequest.setAppId("27fc10f76e3142247d0d2aa183c1e675");
        processCreateRequest.setAppVersion("1.0.0");
        processCreateRequest.setProcessDefinitionKey("bpmn_63f169ed-86cc-11ee-8421-1a2ab4a03d01");//流程定义id:  流程列表返回的modelKey
        processCreateRequest.setBusinessKey("124");//业务id，可以是表记录row的id
        processCreateRequest.setName("测试业务自己发起任务");//流程名称： 流程列表返回的name
        processCreateRequest.setVariables(params);//流程变量，一搬就是表的一行
        processCreateRequest.setFrontModelVersion("2");//流程版本：流程列表返回的version
        processCreateRequest.setFrontModelId("68df4849-b080-11ee-836a-569e577c4a6b");//流程模型id：流程列表返回的id
//        processCreateRequest.setFormKey("hu_parallel");//表定义id
        processCreateRequest.setRecordId("1");//表记录row的id
        processCreateRequest.setFrontModelType(0);//流程模型类型：流程列表返回的modelType
        processCreateRequest.setTableName("hu_parallel");//表名
        ProcessBaseResponse processInstanceDTO = bpmnProcessInstanceServiceClient.createProcessInstanceDTO(context, processCreateRequest);

        return processInstanceDTO.getProcessInstanceId();//返回流程实例id

//        return null;
    }

    @RequestMapping(value = "/getApprovalUserIds", produces = "application/json")
    public Object getApprovalUserIds(Context context, @RequestBody  Map<String, String> params) {
        context = new Context("60511DDBA5FE493E9F1973F577504CF2", "6ab74e7a885142579c9e680aaa3daa76");
        final List<String> deptId = scheduleTask.getApprovalUserIds(context, params.get("deptId"), ScheduleTask.TRAIN_ROLE_CODE);

        return deptId;
    }

}
