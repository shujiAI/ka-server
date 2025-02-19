package com.shujiai.ka.facade.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ExamApplyPlanDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 编码
     */
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 状态 1正常 0冻结
     */
    private Integer year;


    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date gmtModified;



}
