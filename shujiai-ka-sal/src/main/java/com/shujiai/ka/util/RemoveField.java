package com.shujiai.ka.util;

import com.shujiai.ka.constant.CommonDbFieldConstants;

import java.util.List;
import java.util.Map;

/**
 * @author guowei
 */
public class RemoveField {

    public static void removeCommonDbField(Map<?, ?> param) {
        param.remove(CommonDbFieldConstants.GMT_CREATE);
        param.remove(CommonDbFieldConstants.GMT_MODIFIED);
        param.remove(CommonDbFieldConstants.CREATE_BY);
        param.remove(CommonDbFieldConstants.UPDATE_BY);
        param.remove(CommonDbFieldConstants.IS_DELETED);

        param.forEach((k, v) -> {
            if (v instanceof List) {
                List<?> c = (List<?>) v;
                if (!c.isEmpty()) {
                    for (Object o : c) {
                        if (o instanceof Map) {
                            removeCommonDbField((Map<?, ?>) o);
                        }
                    }
                }
            }
        });
    }
}
