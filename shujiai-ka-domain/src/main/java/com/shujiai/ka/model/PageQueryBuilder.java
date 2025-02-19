package com.shujiai.ka.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 查询
 *
 * @author jiuyi
 * @date 2023/11/4 10:28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageQueryBuilder {
    private Integer pageNo;
    private Integer pageSize;
    private Map<String, Object> param;
}
