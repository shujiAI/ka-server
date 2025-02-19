package com.shujiai.ka.dubbo.consumer;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.shujiai.base.context.Context;
import com.shujiai.base.exception.BizException;
import com.shujiai.base.result.Result;
import com.shujiai.base.result.ResultCode;
import com.shujiai.metadata.facade.ApiService;
import com.shujiai.metadata.facade.dto.request.InvokeApiRequest;
import com.shujiai.ka.constants.UserCenterConstant;
import com.shujiai.ka.util.MapBuilder;
import com.shujiai.ka.util.QueryResult;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 消费Metadata api service
 *
 * @author songke
 * @date 2021/11/29 13:56
 **/
@Component
public class MetadataApiServiceClient {

    @Reference(check = false, group = "${dubbo.metadata.group.name}", version = "${dubbo.metadata.version}", timeout = 60000, retries = 0)
    private ApiService apiService;

    @Deprecated
    public Object invokeApi(Context context, InvokeApiRequest request) {
        Result<Object> result = apiService.invokeApi(context, request);
        if (!result.isSuccess()) {
            ResultCode resultCode = new ResultCode(result.getCode(), result.getMessage(), result.getArgs());
            throw new BizException(resultCode);
        }
        return result.getData();
    }

    /**
     * 调用meta的创建api
     */
    public String invokeCreateApi(Context context, InvokeApiRequest request) {
        Result<Object> result = apiService.invokeApi(context, request);
        if (!result.isSuccess()) {
            ResultCode resultCode = new ResultCode(result.getCode(), result.getMessage(), result.getArgs());
            throw new BizException(resultCode);
        }
        JSONObject resultDataObj = JSON.parseObject(JSON.toJSONString(result.getData()));
        return resultDataObj.getString("id");
    }

    /**
     * 系统API请用这个
     */
    public <T> QueryResult<T> invokeApiSysApi(Context context, String appId, String apiName, Map<String, Object> where, Class<T> clazz) {
        InvokeApiRequest request = new InvokeApiRequest();
        request.setAppId(appId);
        request.setName(apiName);

        request.setParams(MapBuilder.of(UserCenterConstant.REQUEST_PARAM_KEY, where));
        return invokeApiResult(context, request, clazz);
    }

    /**
     * 查询使用时请注意参数嵌套
     */
    public <T> QueryResult<T> invokeApiResult(Context context, InvokeApiRequest request, Class<T> clazz) {
        QueryResult<T> queryResult = new QueryResult<>();
        Result<Object> result = apiService.invokeApi(context, request);
        if (!result.isSuccess()) {
            ResultCode resultCode = new ResultCode(result.getCode(), result.getMessage(), result.getArgs());
            throw new BizException(resultCode);
        }

        queryResult.setSuccess(Boolean.TRUE);
        JSONObject resultDataObj = JSON.parseObject(JSON.toJSONString(result.getData(), SerializerFeature.DisableCircularReferenceDetect));
        if (!resultDataObj.containsKey(UserCenterConstant.DATA_KEY)) {
            queryResult.setData(BeanUtil.copyProperties(resultDataObj, clazz));
            return queryResult;
        }

        if (resultDataObj.get(UserCenterConstant.DATA_KEY) instanceof JSONArray) {
            Long totalCount = resultDataObj.containsKey(UserCenterConstant.DATA_COUNT_KEY) ? resultDataObj.getLongValue(UserCenterConstant.DATA_COUNT_KEY) : resultDataObj.getJSONArray(UserCenterConstant.DATA_KEY).size();
            queryResult.setCount(totalCount);
            if (queryResult.getCount() > 0) {
                queryResult.setList(JSON.parseArray(resultDataObj.getString(UserCenterConstant.DATA_KEY), clazz));
            }
            queryResult.setPageNo(resultDataObj.getIntValue(UserCenterConstant.PAGE_NO_KEY));
            queryResult.setPageSize(resultDataObj.getIntValue(UserCenterConstant.PAGE_SIZE_KEY));

            return queryResult;
        }

        if (resultDataObj.get(UserCenterConstant.DATA_KEY) instanceof JSONObject) {
            queryResult.setData(JSON.parseObject(resultDataObj.getString(UserCenterConstant.DATA_KEY), clazz));
            return queryResult;
        }
        return queryResult;
    }

}
