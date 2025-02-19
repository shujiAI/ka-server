package com.shujiai.ka.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 排序
 *
 * @author jiuyi
 * @date 2023/11/4 4:41 PM
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetaSysApiSort implements Serializable {
    private static final long serialVersionUID = -8358945736690629672L;

    private String fieldName;

    private String order;

    public enum OrderEnum {
        ASC,
        DESC;
    }
}


