package com.shujiai.ka.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Collection;
import java.util.Map;

/**
 * 搜索请求
 *
 * @author jiuyi
 * @date 2023/11/4 4:39 PM
 */
@Data
@Accessors(chain = true)
public class PageInvokeQueryBuilder {
    private Integer pageNo;
    private Integer pageSize;
    private String text;
    private Collection<String> fieldName;
    private Collection<MetaSysApiSort> sortItems;
    private Map<String, Object> params;

    /**
     * 不分页需所有
     */
    private boolean _NONE_PAGE;
}
