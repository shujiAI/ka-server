package com.shujiai.ka.dao;

import com.shujiai.base.context.Context;
import com.shujiai.ka.entity.TestDO;

public interface TestDAO {
    String testTransactional(Context context, String appId, String version, TestDO testDO);
}
