package com.shujiai.ka.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

//CREATE TABLE `test` (
//        `id` varchar(32) NOT NULL COMMENT '唯一主键',
//        `gmt_create` datetime(3) DEFAULT NULL COMMENT '创建时间',
//        `gmt_modified` datetime(3) DEFAULT NULL COMMENT '修改时间',
//        `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
//        `update_by` varchar(32) DEFAULT NULL COMMENT '修改人',
//        `tenant_id` varchar(32) DEFAULT '1B960A77F71B41358B43838B1B9FA5C0' COMMENT '租户ID',
//        `is_deleted` bigint DEFAULT '0' COMMENT '是否删除',
//        `bpm_instance_status` varchar(32) DEFAULT NULL COMMENT '流程实例状态 AGREE:已同意 REFUSE:已拒绝 PENDING:审批中 CANCEL:已撤销',
//        `bpm_instance_id` varchar(64) DEFAULT NULL COMMENT '流程实例id',
//        `isValid` varchar(255) DEFAULT '有效' COMMENT '是否失效',
//        `assertClass` varchar(255) DEFAULT NULL COMMENT '资产大类',
//PRIMARY KEY (`id`)
//) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("test")
public class TestDO extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableField(value = "isValid")
    private String isValid;

    @TableField(value = "assertClass")
    private String assertClass;
}
