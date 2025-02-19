package com.shujiai.ka.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetaSysApiBuilder implements Serializable {
    private static final long serialVersionUID = 1973903695665873730L;

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

    /**
     * 默认为null,使用线上最新版本
     */
    @Builder.Default
    private String version = null;
}
