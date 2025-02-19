package com.shujiai.ka.facade.api;


import com.alibaba.fastjson.JSON;
import com.shujiai.base.context.Context;
import com.shujiai.base.result.Result;
import com.shujiai.metadata.facade.dto.request.InvokeApiRequest;
import com.shujiai.ka.dubbo.consumer.MetadataApiServiceClient;
import com.shujiai.ka.facade.dto.ExamApplyPlanDTO;
import com.shujiai.ka.facade.request.ExamApplyPlanQueryRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;


@SpringBootTest
class TenantServiceTest {

    @Autowired
    private ExamApplyPlanApiService examApplyPlanApiService;
    @Autowired
    private MetadataApiServiceClient metadataApiServiceClient;

    /**
     * web请求通过网关时，网关会校验token并生成Context对象送给下游系统
     */
    private static Context getContext(){
        Context context = new Context();
        // 租户id
        context.setTenantId("");
        // 身份id
        context.setIdentityId("");
        // 应用id
        context.setAppId("");
        return context;
    }

    @Test
    void invokeApi(){
        InvokeApiRequest request = new InvokeApiRequest();
        // api接口名称
        request.setName("");
        // 应用id
        request.setAppId("");
        //应用版本
        request.setVersion("");
        // api接口参数
        request.setParams(new HashMap<>());
        // 调用接口
        Object o = this.metadataApiServiceClient.invokeApi(getContext(), request);
    }

    @Test
    void getExamApplyPlanList(){
        ExamApplyPlanQueryRequest request = new ExamApplyPlanQueryRequest();
        String appId = "xxxxx";
        Result<List<ExamApplyPlanDTO>> result = examApplyPlanApiService.getExamApplyPlanList(getContext(), appId, null, request);
        System.out.println(JSON.toJSONString(result));
    }
}
