package com.shujiai.ka.model;

import com.shujiai.base.result.ResultCode;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 统一数据平台返回
 *
 * @author xiaokang
 * @date 2023/11/5 15:36
 */
@Data
public class QueryRes<T> implements Serializable {

    private List<T> list;

    private T data;

    private Long count;

    private Integer pageNo;

    private Integer pageSize;

    private boolean success;

    private ResultCode resultCode;
}

