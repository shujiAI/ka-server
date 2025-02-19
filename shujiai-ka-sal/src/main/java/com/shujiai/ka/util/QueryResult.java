package com.shujiai.ka.util;

import com.shujiai.base.result.ResultCode;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 统一数据平台返回
 *
 * @author jiuyi
 * @date 2021/12/1 3:58 下午
 */
@Data
public class QueryResult<T> implements Serializable {

    private List<T> list;

    private T data;

    private Long count;

    private Integer pageNo;

    private Integer pageSize;

    private boolean success;
    private ResultCode resultCode;
}
