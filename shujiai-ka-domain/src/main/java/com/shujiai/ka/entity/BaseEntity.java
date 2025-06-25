package com.shujiai.ka.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 唯一主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    @ExcelIgnore
    private String id;

    /**
     * 创建时间
     */
    @TableField(value = "gmt_create", fill = FieldFill.INSERT)
    @ExcelIgnore
    private Date gmtCreate;

    /**
     * 修改时间
     */
    @TableField(value = "gmt_modified", fill = FieldFill.INSERT_UPDATE)
    @ExcelIgnore
    private Date gmtModified;

    /**
     * 创建人
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    @ExcelIgnore
    private String createBy;

    /**
     * 修改人
     */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    @ExcelIgnore
    private String updateBy;

    /**
     * 租户ID
     */
    @TableField(value = "tenant_id", fill = FieldFill.INSERT)
    @ExcelIgnore
    private String tenantId;

    /**
     * 是否删除
     */
    @TableField("is_deleted")
    @TableLogic(value = "0", delval = "now()")
    @ExcelIgnore
    private Long isDeleted;

    /**
     * 流程实例状态 AGREE:已同意 REFUSE:已拒绝 PENDING:审批中 CANCEL:已撤销
     */
    @TableField("bpm_instance_status")
    @ExcelIgnore
    private String bpmInstanceStatus;

    /**
     * 流程实例id
     */
    @TableField("bpm_instance_id")
    @ExcelIgnore
    private String bpmInstanceId;

}
