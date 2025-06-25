package com.shujiai.ka.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shujiai.ka.entity.TestDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TestMapper extends BaseMapper<TestDO> {
    List<TestDO> selectByList(TestDO testDO);
}
