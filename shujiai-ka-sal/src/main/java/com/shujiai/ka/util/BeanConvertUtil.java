package com.shujiai.ka.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.NamingCase;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.shujiai.ka.constant.CommonConstant;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.cglib.beans.BeanCopier;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * bean类型转换
 *
 * @author xiaokang
 * @date 2023/11/6
 */
public class BeanConvertUtil {
    public static <FROM, TO> TO convert(FROM dto, Class<FROM> fromClass, Class<TO> toClass) {
        try {

            if (dto == null) {
                return toClass.newInstance();
            }
            TO newDto;
            newDto = toClass.newInstance();

            BeanCopier copier = BeanCopier.create(fromClass, toClass, false);
            copier.copy(dto, newDto, null);
            return newDto;

        } catch (Exception e) {
            throw new RuntimeException("can not convert", e);
        }
    }

    public static <FROM, TO> List<TO> convert(Collection<FROM> list, Class<FROM> fromClass, Class<TO> toClass) {
        try {
            if (CollectionUtils.isEmpty(list)) {
                return new ArrayList<>(0);
            }
            List<TO> newList = new ArrayList<>();
            for (FROM dto : list) {
                newList.add(BeanConvertUtil.convert(dto, fromClass, toClass));
            }
            return newList;
        } catch (Exception e) {
            throw new RuntimeException("can not convert", e);
        }
    }

    public static <TO> TO deepConvert(Object source, Class<TO> toClass) {
        try {
            if (source == null) {
                return toClass.newInstance();
            }
            return JSON.parseObject(JSON.toJSONString(source), toClass);
        } catch (Exception e) {
            throw new RuntimeException("can not deep convert", e);
        }
    }

    public static <TO> List<TO> deepConvert(List<?> sourceList, Class<TO> toClass) {
        try {
            if (CollectionUtils.isEmpty(sourceList)) {
                return Lists.newArrayList();
            }
            return JSON.parseArray(JSON.toJSONString(sourceList), toClass);
        } catch (Exception e) {
            throw new RuntimeException("can not deep convert", e);
        }
    }

    /**
     * 将map转成beanList
     *
     * @param mapList
     * @param toClass
     * @param <T>
     * @return
     */
    public static <T> List<T> mapsToList(List<Map<String, Object>> mapList, Class<T> toClass) {
        if (CollectionUtils.isEmpty(mapList)) {
            return new ArrayList<>(0);
        }
        try {
            List<T> beanList = new ArrayList<>(mapList.size());
            for (Map<String, Object> map : mapList) {
                beanList.add(mapToBean(map, toClass));
            }
            return beanList;
        } catch (Exception e) {
            throw new RuntimeException("can not convert mapsToList", e);
        }
    }

    public static List<Map<String, Object>> listToMaps(List<?> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Lists.newArrayList();
        }
        try {
            List<Map<String, Object>> mapList = new ArrayList<>(list.size());
            for (Object bean : list) {
                mapList.add(beanToMap(bean));
            }
            return mapList;
        } catch (Exception e) {
            throw new RuntimeException("can not convert mapsToList", e);
        }
    }

    public static Map<String, Object> beanToMap(Object obj) {
        return BeanUtil.beanToMap(obj, false, true);
    }

    public static Map<String, Object> beanToUnderLineMap(Object obj) {
        return BeanUtil.beanToMap(obj, true, true);
    }

    public static <T> T mapToBean(Map<String, Object> obj, Class<T> tClass) {
        return BeanUtil.toBeanIgnoreError(obj, tClass);
    }

    public static String listToMd5(Collection<String> list) {
        CollUtil.removeBlank(list);
        CollUtil.sortByPinyin(list);
        String identityIdListStr = CollUtil.join(list, CommonConstant.DELIMITER_SLASH);
        return DigestUtil.md5Hex(identityIdListStr.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * list转换
     * @param dtoList
     * @param toClass
     * @return
     * @param <FROM>
     * @param <TO>
     */
    public static <FROM, TO> List<TO> convert(Collection<FROM> dtoList, Class<TO> toClass) {
        return convert(dtoList, toClass, null);
    }
    /**
     * list属性定义
     * @param dtoList
     * @param toClass
     * @param nameCovert
     * @return
     * @param <FROM>
     * @param <TO>
     */
    public static <FROM, TO> List<TO> convert(Collection<FROM> dtoList, Class<TO> toClass, NameCovert nameCovert) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return new ArrayList<>(0);
        }
        List<TO> toDtoList = new ArrayList<>(dtoList.size());
        for (FROM dto : dtoList) {
            toDtoList.add(BeanConvertUtil.convert(dto, toClass, nameCovert));
        }
        return toDtoList;
    }

    /**
     * bean转换
     *
     * @param dto
     * @param nameCovert 下划线和驼峰可以对应转
     * @param <FROM>
     * @param <TO>
     * @return
     */
    public static <FROM, TO> TO convert(FROM dto, Class<TO> toClass, NameCovert nameCovert) {
        if (Objects.isNull(dto)) {
            return null;
        }
        CopyOptions copyOption = CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true).setIgnoreCase(true);
        if (NameCovert.camelMatchUnderline.equals(nameCovert)) {
            copyOption.setFieldNameEditor(NamingCase::toCamelCase);
        }
        if (NameCovert.underlineMatchCamel.equals(nameCovert)) {
            copyOption.setFieldNameEditor(NamingCase::toUnderlineCase);
        }

        return BeanConvertUtil.convertBase(dto, toClass, copyOption);
    }

    private static <FROM, TO> TO convertBase(FROM dto, Class<TO> toClass, CopyOptions copyOption) {
        try {
            if (Objects.isNull(dto)) {
                return null;
            }

            return BeanUtil.toBean(dto, toClass, copyOption);

        } catch (Exception e) {
            throw new RuntimeException("can not convert", e);
        }
    }

    public enum NameCovert {
        /**
         * 源是驼峰对应目标下划线
         */
        camelMatchUnderline,

        /**
         * 源是下划线对应目标驼峰
         */
        underlineMatchCamel,
        ;
    }

}
