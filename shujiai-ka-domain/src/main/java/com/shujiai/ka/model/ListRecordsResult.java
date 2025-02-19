package com.shujiai.ka.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * ListRecordsResult
 *
 * @author hxh
 * @date 2023/11/4
 * @time 09:56:43
 */
@Data
public class ListRecordsResult implements Serializable {

    private static final long serialVersionUID = 7265274147288625692L;

    private Long pageNo;

    private Long pageSize;

    private List<Map<String, Object>> data;

    private Long count;
}
