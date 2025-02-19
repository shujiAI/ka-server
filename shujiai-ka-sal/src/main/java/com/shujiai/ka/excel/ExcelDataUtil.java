package com.shujiai.ka.excel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Slf4j
public class ExcelDataUtil {

    public static final String CHILDREN = "children";

    public static final Map<Integer, String> NUMBER_MAP = new HashMap<>();


    /**
     * 最多十级
     */
    static {
        NUMBER_MAP.put(1, "一级指标");
        NUMBER_MAP.put(2, "二级指标");
        NUMBER_MAP.put(3, "三级指标");
        NUMBER_MAP.put(4, "四级指标");
        NUMBER_MAP.put(5, "五级指标");
        NUMBER_MAP.put(6, "六级指标");
        NUMBER_MAP.put(7, "七级指标");
        NUMBER_MAP.put(8, "八级指标");
        NUMBER_MAP.put(9, "九级指标");
        NUMBER_MAP.put(10, "十级指标");
    }


    /**
     * 获取values的某一列数据
     *
     * @param index
     * @param values
     * @return
     */
    public static List<Object> getValueListIndexValueList(Integer index, List<List<Object>> values) {
        try {
            List<Object> indexValueList = new ArrayList<>();
            if (CollectionUtils.isEmpty(values)) {
                return new ArrayList<>();
            }
            if (index == null) {
                return new ArrayList<>();
            }
            for (List<Object> list : values) {
                Object object = list.get(index);
                indexValueList.add(object);
            }
            return indexValueList;
        } catch (Exception e) {
            log.error("转换合并列异常e{}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }


    /**
     * 递归获取树的层级
     *
     * @param root
     * @return
     */
    public static Integer getTreeDeepByTree(JSONObject root) {
        if (root == null) {
            return 0;
        } else if (root.get(CHILDREN) == null) {
            return 1;
        }
        Object newChildren = root.get(CHILDREN);
        JSONArray newChildrenJsonArray = JSONArray.parseArray(JSON.toJSONString(newChildren));
        //定义一个【保存深度的数组】
        List<Integer> heights = new ArrayList<>();
        //遍历当前节点下的所有子节点
        for (Object item : newChildrenJsonArray) {
            //对每一个节点求最大深度
            int depth = getTreeDeepByTree(JSONObject.parseObject(JSON.toJSONString(item)));
            //把每个节点对应的深度保存在数组中
            heights.add(depth);
        }
        //返回数组中最大的元素
        return Collections.max(heights) + 1;
    }

}
