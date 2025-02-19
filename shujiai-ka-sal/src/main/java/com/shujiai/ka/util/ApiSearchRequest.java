package com.shujiai.ka.util;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 搜索请求
 *
 * @author jiuyi
 * @date 2021/12/16 4:39 PM
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiSearchRequest {
    private Integer pageNo;
    private Integer pageSize;
    private String text;
    private List<String> fieldName;
    private List<SearchSortDTO> sortItems;
    private Map<String, Object> params;

    /**
     * 不分页需所有
     */
    private boolean onePage;
}
