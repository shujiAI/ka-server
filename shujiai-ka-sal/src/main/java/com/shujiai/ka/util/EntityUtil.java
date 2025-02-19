package com.shujiai.ka.util;


import com.shujiai.ka.constant.CommonDbFieldConstants;

import java.util.*;

/**
 * EntityUtil
 *
 * @author hxh
 * @date 2023/11/4
 * @time 17:52:54
 */

public class EntityUtil {

    public static final Set<String> SYS_FIELDS = new HashSet<>(
            Arrays.asList(
                    CommonDbFieldConstants.GMT_CREATE,
                    CommonDbFieldConstants.GMT_MODIFIED,
                    CommonDbFieldConstants.UPDATE_BY,
                    CommonDbFieldConstants.CREATE_BY,
                    CommonDbFieldConstants.BPM_INSTANCE_ID,
                    CommonDbFieldConstants.BPM_INSTANCE_STATUS,
                    CommonDbFieldConstants.TENANT_ID
            ));
    public static final Set<String> SYS_FIELDS_EXCLUDE_TIME = new HashSet<>(
            Arrays.asList(
                    CommonDbFieldConstants.UPDATE_BY,
                    CommonDbFieldConstants.CREATE_BY,
                    CommonDbFieldConstants.BPM_INSTANCE_ID,
                    CommonDbFieldConstants.BPM_INSTANCE_STATUS,
                    CommonDbFieldConstants.TENANT_ID
            ));

    public static void reserveSysFields(Map<String, Object> entity) {
        for (Iterator<Map.Entry<String, Object>> it = entity.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, Object> item = it.next();
            String key = item.getKey();
            if (!SYS_FIELDS.contains(key)) {
                it.remove();
            }
        }
    }

    public static void deleteSysFields(List<Map<String, Object>> entityList) {
        // 以下字段不需要更新
        for (Map<String, Object> entity : entityList) {
            EntityUtil.deleteSysFields(entity);
        }
    }

    public static void deleteSysFields(Map<String, Object> entity) {
        // 以下字段不需要更新
        for (Iterator<Map.Entry<String, Object>> it = entity.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, Object> item = it.next();
            String key = item.getKey();
            if (SYS_FIELDS.contains(key)) {
                it.remove();
            }
        }
    }

    public static void reserveCustomFields(Map<String, Object> entity, Set<String> fields) {
        for (Iterator<Map.Entry<String, Object>> it = entity.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, Object> item = it.next();
            String key = item.getKey();
            if (!fields.contains(key)) {
                it.remove();
            }
        }
    }

    public static void deleteCustomFields(List<Map<String, Object>> entityList, Set<String> fields) {
        for (Map<String, Object> entity : entityList) {
            EntityUtil.deleteCustomFields(entity, fields);
        }
    }

    public static void deleteCustomFields(Map<String, Object> entity, Set<String> fields) {
        for (Iterator<Map.Entry<String, Object>> it = entity.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, Object> item = it.next();
            String key = item.getKey();
            if (fields.contains(key)) {
                it.remove();
            }
        }
    }

}
