package com.shujiai.ka.service.impl;

import com.shujiai.base.context.Context;
import com.shujiai.base.result.Result;
import com.shujiai.gateway.register.annotation.YdcGateway;
import com.shujiai.ka.dao.TestDAO;
import com.shujiai.ka.entity.TestDO;
import com.shujiai.ka.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@org.springframework.stereotype.Service
@Service(version = "${dubbo.provider.version}", group = "${dubbo.provider.group}")
@Slf4j
public class TestServiceImpl implements TestService {
    @Autowired
    private TestDAO testDAO;

    @Override
    @YdcGateway(url = "kaserver/test/testTransactional.json", name = "test：测试事务")
    public Result<String> testTransactional(Context context, String appId, String version, TestDO testDO) {
        final String id = testDAO.testTransactional(context, appId, version, testDO);

        return new Result<>(id);
    }
}
