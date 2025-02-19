package com.shujiai.ka.constant;

/**
 * CommonDbFieldConstants
 * 共有的数据表字段
 *
 * @author hxh
 * @date 2023/11/4
 * @time 12:57:51
 */
public class CommonDbFieldConstants {

    /**
     * 唯一主键
     **/
    public static final String ID = "id";

    /**
     * 创建时间
     **/
    public static final String GMT_CREATE = "gmt_create";

    /**
     * 修改时间
     **/
    public static final String GMT_MODIFIED = "gmt_modified";

    /**
     * 创建人
     **/
    public static final String CREATE_BY = "create_by";

    /**
     * 修改人
     **/
    public static final String UPDATE_BY = "update_by";

    /**
     * 租户ID
     **/
    public static final String TENANT_ID = "tenant_id";

    /**
     * 是否删除
     **/
    public static final String IS_DELETED = "is_deleted";

    /**
     * 流程实例状态 AGREE:已同意 REFUSE:已拒绝 PENDING:审批中 CANCEL:已撤销
     **/
    public static final String BPM_INSTANCE_STATUS = "bpm_instance_status";

    /**
     * 流程实例id
     **/
    public static final String BPM_INSTANCE_ID = "bpm_instance_id";
}
