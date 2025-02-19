package com.shujiai.ka.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author jackyx
 * @date 2023/11/4 16:56
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 7038615788834775409L;

    private Long count;

    private Integer pageNo;

    private Integer pageSize;

    private List<T> data;
}
