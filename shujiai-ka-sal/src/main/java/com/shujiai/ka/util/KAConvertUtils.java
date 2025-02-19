package com.shujiai.ka.util;

import com.alibaba.fastjson.JSON;
import com.shujiai.ka.exceptions.KABizException;
import com.shujiai.base.result.ResultCodes;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author jackyx
 * @date 2022/09/02 15:58
 **/
public class KAConvertUtils {

    /**
     * 复制属性
     */
    public static void copyProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target);
    }

    /**
     * 类拷贝
     */
    public static <T> T convert(Object source, Class<T> targetClass) {
        T targetInstance;
        try {
            targetInstance = targetClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new KABizException(ResultCodes.SERVER_ERROR, "进行类转换时构建目标类对象失败");
        }
        if (source == null) {
            return targetInstance;
        }

        BeanUtils.copyProperties(source, targetInstance);
        return targetInstance;
    }

    /**
     * 批量拷贝
     */
    public static <T> List<T> convertList(List<?> sourceList, Class<T> targetClass) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return new ArrayList<>();
        }
        List<T> targetList = new ArrayList<>(sourceList.size());
        for (Object source : sourceList) {
            targetList.add(convert(source, targetClass));
        }
        return targetList;
    }

    public static <T> T deepConvert(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(source), targetClass);
    }

    public static <T> List<T> deepConvertList(List<?> sourceList, Class<T> targetClass) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return new ArrayList<>();
        }
        return JSON.parseArray(JSON.toJSONString(sourceList), targetClass);
    }

    public static <T> T transMapObject(Map<String, Object> data, Class<T> clazz) {
        return deepConvert(data, clazz);
    }

    public static <T> List<T> transMapObjects(List<Map<String, Object>> dataList, Class<T> clazz) {
        return deepConvertList(dataList, clazz);
    }
}
