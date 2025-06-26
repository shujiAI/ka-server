package com.shujiai.ka.dao.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shujiai.base.context.Context;
import com.shujiai.base.exception.BizException;
import com.shujiai.ka.dao.TestDAO;
import com.shujiai.ka.entity.TestDO;
import com.shujiai.ka.mapper.TestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TestDAOImpl extends ServiceImpl<TestMapper, TestDO> implements TestDAO {
    @Autowired
    private TestMapper testMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String testTransactional(Context context, String appId, String version, TestDO testDO) {
        testMapper.deleteById("1");
        final int insert = testMapper.insert(testDO);

        final List<TestDO> testDOS = testMapper.selectByList(testDO);
        System.out.println("testDOS = " + JSON.toJSONString(testDOS));

        if("1".equals(testDO.getId())){
            throw new BizException("测试事务");
        }

        return testDO.getId();
    }
}
