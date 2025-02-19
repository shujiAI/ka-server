package com.shujiai.ka.util;

import cn.hutool.core.lang.Pair;
import com.shujiai.metadata.facade.dto.SortItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * mapBuilder
 *
 * @author jiuyi
 * @date 2021/12/22 9:51 PM
 */
public class MapBuilder {
    public static <K, V> HashMap<K, V> EMPTY() {
        return new HashMap<>(0);
    }

    public static <K, V> HashMap<K, V> of(K k, V v) {
        final HashMap<K, V> map = new HashMap<>(2);
        map.put(k, v);
        return map;
    }


    public static <K, V> HashMap<K, V> of(K k, V v, K k2, V v2) {
        final HashMap<K, V> map = new HashMap<>(4);
        map.put(k, v);
        map.put(k2, v2);
        return map;
    }


    public static <K, V> HashMap<K, V> of(Pair<K, V>... pairs) {
        final HashMap<K, V> map = new HashMap<>(pairs.length * 2);
        for (Pair<K, V> pair : pairs) {
            map.put(pair.getKey(), pair.getValue());
        }
        return map;
    }

    public static <K, V> HashMap<K, V> of(K k, V v, K k2, V v2, K k3, V v3) {
        final HashMap<K, V> map = new HashMap<>(6);
        map.put(k, v);
        map.put(k2, v2);
        map.put(k3, v3);
        return map;
    }

    public static List<SortItem> sortOf(String fieldName, String order) {
        final List<SortItem> list = new ArrayList<>(2);
        SortItem sort = new SortItem();
        sort.setFieldName(fieldName);
        sort.setOrder(order);
        list.add(sort);
        return list;
    }

    public static List<SortItem> sortOf(String fieldName1, String order1, String fieldName2, String order2) {
        final List<SortItem> list = new ArrayList<>(2);
        SortItem sort1 = new SortItem();
        sort1.setFieldName(fieldName1);
        sort1.setOrder(order1);
        SortItem sort2 = new SortItem();
        sort2.setFieldName(fieldName2);
        sort2.setOrder(order2);
        list.add(sort1);
        list.add(sort2);
        return list;
    }

    public static List<SortItem> sortOf(String fieldName1, String order1, String fieldName2, String order2, String fieldName3, String order3) {
        final List<SortItem> list = new ArrayList<>(3);
        SortItem sort1 = new SortItem();
        sort1.setFieldName(fieldName1);
        sort1.setOrder(order1);
        SortItem sort2 = new SortItem();
        sort2.setFieldName(fieldName2);
        sort2.setOrder(order2);
        SortItem sort3 = new SortItem();
        sort3.setFieldName(fieldName3);
        sort3.setOrder(order3);
        list.add(sort1);
        list.add(sort2);
        list.add(sort3);
        return list;
    }

}
