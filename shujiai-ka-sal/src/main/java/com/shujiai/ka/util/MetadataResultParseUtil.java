package com.shujiai.ka.util;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.shujiai.ka.constant.CommonConstant;
import com.shujiai.ka.model.QueryRes;
import com.shujiai.base.exception.BizException;
import com.shujiai.base.result.Result;
import com.shujiai.base.result.ResultCode;
import com.shujiai.metadata.facade.dto.sql.QueryResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MetadataResultParseUtil
 *
 * @author xiaokang
 * @date 2023/11/6
 */
public class MetadataResultParseUtil {

    /**
     * 原生方法的返回结果封装，调用metadata 所有api，返回结果都是对象结构，可以认为是json/map结构
     * Get方法返回一个Object(Map<String, Object>)
     * List方法返回一个List Object(List<Map<String, Object>)
     * Update/Delete方法返回一个Object（Map<String, Object>）, eg: {"success": true}
     * <p>
     * 所以该方法可以将invokeApi返回的Object反序列化为目标Class
     *
     * @param clazz clazz
     * @param result 结果
     *
     * @return {@link QueryRes}<{@link T}>
     */
    public static <T> QueryRes<T> parseMetadataResult(Result<Object> result, Class<T> clazz) {

        if (!result.isSuccess()) {
            ResultCode resultCode = new ResultCode(result.getCode(), result.getMessage(), "");
            throw new BizException(resultCode);
        }

        QueryRes<T> queryResult = new QueryRes<>();
        JSONObject resultDataObj = JSON.parseObject(JSON.toJSONString(result.getData()));
        if (!resultDataObj.containsKey(CommonConstant.DATA_KEY)) {
            queryResult.setData(BeanUtil.copyProperties(resultDataObj, clazz));
            return queryResult;
        }
        if (resultDataObj.get(CommonConstant.DATA_KEY) instanceof JSONObject) {
            queryResult.setData(JSON.parseObject(resultDataObj.getString(CommonConstant.DATA_KEY), clazz));
            return queryResult;
        }

        if (resultDataObj.get(CommonConstant.DATA_KEY) instanceof JSONArray) {
            queryResult.setCount(resultDataObj.getLongValue("count"));
            if (queryResult.getCount() > 0) {
                queryResult.setList(JSON.parseArray(resultDataObj.getString(CommonConstant.DATA_KEY), clazz));
            }
            queryResult.setCount(resultDataObj.getLongValue("count"));
            queryResult.setPageNo(resultDataObj.getIntValue("pageNo"));
            queryResult.setPageSize(resultDataObj.getIntValue("pageSize"));
            return queryResult;
        }

        return queryResult;
    }

    /**
     * 原生方法的返回结果封装，调用metadata 所有api，返回结果都是对象结构，可以认为是json/map结构
     * Get方法返回一个Object(Map<String, Object>)
     * List方法返回一个List Object(List<Map<String, Object>)
     * Update/Delete方法返回一个Object（Map<String, Object>）, eg: {"success": true}
     * <p>
     * 所以该方法可以将invokeApi返回的Object反序列化为通用的Map结构，可读性好于Object
     *
     * @param result 结果
     *
     * @return {@link QueryRes}<{@link Map}<{@link String}, {@link Object}>>
     */
    public static QueryRes<Map<String, Object>> parseMetadataResultV2(Result<Object> result) {

        if (!result.isSuccess()) {
            ResultCode resultCode = new ResultCode(result.getCode(), result.getMessage(), "");
            throw new BizException(resultCode);
        }

        QueryRes<Map<String, Object>> queryResult = new QueryRes<>();
        JSONObject resultDataObj = JSON.parseObject(JSON.toJSONString(result.getData()));
        if (!resultDataObj.containsKey(CommonConstant.DATA_KEY)) {
            //            queryResult.setData(BeanUtil.copyProperties(resultDataObj, Map.class));
            //            Map<String, Object> itemMap = JSONObject.toJavaObject(resultDataObj, Map.class);
            Map<String, Object> itemMap = resultDataObj.toJavaObject(new TypeReference<Map<String, Object>>() {

            });
            queryResult.setData(itemMap);
            return queryResult;
        }
        if (resultDataObj.get(CommonConstant.DATA_KEY) instanceof JSONObject) {
            queryResult.setData(JSON.parseObject(resultDataObj.getString(CommonConstant.DATA_KEY), new TypeReference<Map<String, Object>>() {

            }));
            return queryResult;
        }

        if (resultDataObj.get(CommonConstant.DATA_KEY) instanceof JSONArray) {
            queryResult.setCount(resultDataObj.getLongValue("count"));
            if (queryResult.getCount() > 0) {

                List<Map<String, Object>> listData = JSON.parseObject(resultDataObj.getString(CommonConstant.DATA_KEY),
                        new TypeReference<List<Map<String, Object>>>() {

                        });
                queryResult.setList(listData);
            }
            queryResult.setCount(resultDataObj.getLongValue("count"));
            queryResult.setPageNo(resultDataObj.getIntValue("pageNo"));
            queryResult.setPageSize(resultDataObj.getIntValue("pageSize"));
            return queryResult;
        }

        return queryResult;
    }

    /**
     * 原生方法的返回结果封装，调用metadata 所有api，返回结果都是对象结构，可以认为是json/map结构
     * Get方法返回一个Object(Map<String, Object>)
     * List方法返回一个List Object(List<Map<String, Object>)
     * Update/Delete方法返回一个Object（Map<String, Object>）, eg: {"success": true}
     * <p>
     * 所以该方法可以将invokeApi返回的Object反序列后，填充到QueryResult结构体中
     * 该方法暂时不推荐 TODO
     *
     * @param result 结果
     *
     * @return {@link QueryResult}
     */
    public static QueryResult parseMetadataResultV3(Result<Object> result) {

        if (!result.isSuccess()) {
            ResultCode resultCode = new ResultCode(result.getCode(), result.getMessage(), "");
            throw new BizException(resultCode);
        }

        QueryResult queryResult = new QueryResult();
        JSONObject resultDataObj = JSON.parseObject(JSON.toJSONString(result.getData()));
        if (!resultDataObj.containsKey(CommonConstant.DATA_KEY)) {
            //            queryResult.setData(BeanUtil.copyProperties(resultDataObj, Map.class));
            //            Map<String, Object> itemMap = JSONObject.toJavaObject(resultDataObj, Map.class);
            Map<String, Object> itemMap = resultDataObj.toJavaObject(new TypeReference<Map<String, Object>>() {

            });

            List<Map<String, Object>> data = new ArrayList<>(8);
            data.add(itemMap);
            queryResult.setData(data);
            return queryResult;
        }
        if (resultDataObj.get(CommonConstant.DATA_KEY) instanceof JSONObject) {
            List<Map<String, Object>> data = new ArrayList<>(8);
            Map<String, Object> itemMap = JSON.parseObject(resultDataObj.getString(CommonConstant.DATA_KEY), new TypeReference<Map<String, Object>>() {

            });
            data.add(itemMap);
            queryResult.setData(data);
            return queryResult;
        }

        if (resultDataObj.get(CommonConstant.DATA_KEY) instanceof JSONArray) {
            queryResult.setCount(resultDataObj.getLongValue("count"));
            if (queryResult.getCount() > 0) {

                List<Map<String, Object>> listData = JSON.parseObject(resultDataObj.getString(CommonConstant.DATA_KEY),
                        new TypeReference<List<Map<String, Object>>>() {

                        });
                queryResult.setData(listData);
            }
            queryResult.setCount(resultDataObj.getLongValue("count"));
            queryResult.setPageNo(resultDataObj.getIntValue("pageNo"));
            queryResult.setPageSize(resultDataObj.getIntValue("pageSize"));
            return queryResult;
        }

        return queryResult;
    }

    public static <T> QueryRes<T> transMetadataResult(Result<QueryResult> result, Class<T> clazz) {
        if (!result.isSuccess()) {
            ResultCode resultCode = new ResultCode(result.getCode(), result.getMessage(), "");
            throw new BizException(resultCode);
        }

        QueryRes<T> queryResult = new QueryRes<>();
        QueryResult resultData = result.getData();
        queryResult.setList(JSON.parseArray(JSON.toJSONString(resultData.getData()), clazz));
        queryResult.setCount(resultData.getCount());
        queryResult.setPageNo(resultData.getPageNo());
        queryResult.setPageSize(resultData.getPageSize());

        return queryResult;

    }

    public static <T> T transMapObject(Object object, Class<T> clazz) {

        return transMapObject(object, true, clazz);
    }

    public static <T> T transMapObject(Object object, boolean writeNull, Class<T> clazz) {

        if (writeNull) {
            return JSON.parseObject(JSON.toJSONString(object, SerializerFeature.WriteMapNullValue), clazz);
        } else {
            return JSON.parseObject(JSON.toJSONString(object), clazz);

        }
    }

    public static <T> T transMapObject(Map<String, Object> object, Class<T> clazz) {

        return transMapObject(object, true, clazz);
    }

    public static <T> T transMapObject(Map<String, Object> object, boolean writeNull, Class<T> clazz) {

        if (writeNull) {
            return JSON.parseObject(JSON.toJSONString(object, SerializerFeature.WriteMapNullValue), clazz);
        } else {
            return JSON.parseObject(JSON.toJSONString(object), clazz);

        }
    }

    public static <T> List<T> transMapObject(List<Map<String, Object>> object, Class<T> clazz) {

        return transMapObject(object, true, clazz);
    }

    public static <T> List<T> transMapObject(List<Map<String, Object>> object, boolean writeNull, Class<T> clazz) {
        if (writeNull) {
            return JSON.parseArray(JSON.toJSONString(object, SerializerFeature.WriteMapNullValue), clazz);
        } else {
            return JSON.parseArray(JSON.toJSONString(object), clazz);
        }
    }

    public static <T> T transQueryResultObject(QueryResult result, Class<T> clazz) {

        return JSON.parseObject(JSON.toJSONString(result), clazz);
    }

    public static Map<String, Object> transQueryResultToMapObject(QueryResult result) {

        return JSON.parseObject(JSON.toJSONString(result), new TypeReference<Map<String, Object>>() {

        });
    }

    public static Map<String, Object> toMapObject(Object obj) {

        return toMapObject(obj, true);
    }

    public static Map<String, Object> toMapObject(Object obj, boolean writeNull) {

        if (writeNull) {
            return JSON.parseObject(JSON.toJSONString(obj, SerializerFeature.WriteMapNullValue), new TypeReference<Map<String, Object>>() {

            });
        } else {
            return JSON.parseObject(JSON.toJSONString(obj), new TypeReference<Map<String, Object>>() {

            });
        }
    }

    public static List<Map<String, Object>> toMapObject(List<Object> obj) {
        return toMapObject(obj, true);
    }

    public static List<Map<String, Object>> toMapObject(List<Object> obj, boolean writeNull) {
        if (writeNull) {
            List<Map<String, Object>> value = JSON.parseObject(JSON.toJSONString(obj, SerializerFeature.WriteMapNullValue),
                    new TypeReference<List<Map<String, Object>>>() {

                    });

            return value;
        } else {
            List<Map<String, Object>> value = JSON.parseObject(JSON.toJSONString(obj),
                    new TypeReference<List<Map<String, Object>>>() {

                    });

            return value;
        }
    }

}
