package com.shujiai.ka.service;

import com.shujiai.base.context.Context;
import com.shujiai.base.result.Result;
import com.shujiai.ka.entity.TestDO;

public interface TestService {
    Result<String> testTransactional(Context context, String appId, String version, TestDO testDO);
}
