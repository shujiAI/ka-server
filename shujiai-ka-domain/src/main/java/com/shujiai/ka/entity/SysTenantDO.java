package com.shujiai.ka.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author shujiai
 * @since 2023-06-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_tenant")
public class SysTenantDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private Date beginDate;

    private Date endDate;

    private Integer status;

    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    private String updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

    @TableLogic(value = "0",delval = "now()")
    private Long isDeleted;

    private String bpmInstanceStatus;

    private String bpmInstanceId;

}
