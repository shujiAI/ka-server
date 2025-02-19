package com.shujiai.ka.util;

import com.shujiai.ka.constant.KAConstant;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * PageUtil
 *
 * @author huxinhui
 * @date 2023/11/4
 * @time 17:04
 */
public class PageUtil {

    public static final boolean checkHasMoreRecord(Object pageNoObj, Object pageSizeObj, Integer listSize, Long count) {
        return checkHasMoreRecord(pageNoObj, pageSizeObj, Long.valueOf(listSize), count);
    }

    public static final boolean checkHasMoreRecord(Object pageNoObj, Object pageSizeObj, Long listSize, Long count) {
        Long pageNo = 0L;
        Long pageSize = 10L;
        try {
            if (pageNoObj == null) {
                return false;
            }
            if (pageSizeObj == null) {
                return false;
            }
            if (pageNoObj instanceof Long) {
                pageNo = (Long) pageNoObj;
            } else if (pageNoObj instanceof Integer) {
                Integer pageNoInt = (Integer) pageNoObj;
                pageNo = Long.valueOf(pageNoInt.intValue());
            }
            if (pageSizeObj instanceof Long) {
                pageSize = (Long) pageSizeObj;
            } else if (pageSizeObj instanceof Integer) {
                Integer pageSizeInt = (Integer) pageSizeObj;
                pageSize = Long.valueOf(pageSizeInt.intValue());
            }
        } catch (Exception e) {
            return false;
        }

        return pageSize.equals(Long.valueOf(listSize)) && (pageNo - 1) * pageSize < count;
    }

    public static int getPageNo(Map<String, Object> params) {
        if (CollectionUtils.isEmpty(params)) {
            return KAConstant.DEFAULT_PAGE_NO;
        }
        return NumUtil.parseInt(params.get(KAConstant.PAGE_NO_KEY), KAConstant.DEFAULT_PAGE_NO);
    }

    public static int getPageSize(Map<String, Object> params) {
        if (CollectionUtils.isEmpty(params)) {
            return KAConstant.DEFAULT_PAGE_SIZE;
        }
        return NumUtil.parseInt(params.get(KAConstant.PAGE_SIZE_KEY), KAConstant.DEFAULT_PAGE_SIZE);
    }

}
