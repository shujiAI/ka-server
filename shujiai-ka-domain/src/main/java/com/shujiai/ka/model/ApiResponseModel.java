package com.shujiai.ka.model;

import lombok.Data;

/**
 * 调metadata api返回结果
 *
 * @author hxh
 * @date 2023/11/4 19:49
 * <p>
 * 用于Create/Update/Delete api的返回结果序列化
 **/
@Data
public class ApiResponseModel {

    public static final String DATA_KEY = "data";

    public static final String HEAD_KEY = "head";
    private String id;
    private Boolean success;
    private Integer effectedRows;
}
