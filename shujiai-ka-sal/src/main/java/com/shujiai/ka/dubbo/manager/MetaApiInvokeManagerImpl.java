package com.shujiai.ka.dubbo.manager;

import com.google.common.collect.Maps;
import com.shujiai.ka.constant.KAResultCodes;
import com.shujiai.ka.dubbo.consumer.MetadataApiServiceClient;
import com.shujiai.ka.exceptions.KABizException;
import com.shujiai.ka.model.ApiResponseModel;
import com.shujiai.ka.model.ListRecordsResult;
import com.shujiai.ka.util.PageResult;
import com.shujiai.base.context.Context;
import com.shujiai.metadata.facade.dto.SortItem;
import com.shujiai.metadata.facade.dto.request.InvokeApiRequest;
import com.shujiai.metadata.facade.dto.sql.QueryResult;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * ApiInvokeManagerImpl
 *
 * @author hxh
 * @date 2023/11/4
 * @time 09:57:28
 */
@Component
public class MetaApiInvokeManagerImpl implements ApiInvokeManager {

//    private static final Logger serviceLogger = LoggerUtil.SERVICE_LOG.getLogger();

    @Autowired
    private MetadataApiServiceClient metadataApiServiceClient;

    /**
     * 批量插入
     *
     * @param context 上下文,包含租户、登录用户等
     * @param appId 应用ID
     * @param version 版本
     * @param tableName 表名
     * @param params 参数
     *
     * @return {@link Boolean}
     */
    @Override
    public List<String> batchInsert(Context context, String appId, String version, String tableName, List<Map<String, Object>> params) {
        return null;
    }

    /**
     * 批量更新
     *
     * @param context 上下文,包含租户、登录用户等
     * @param appId 应用ID
     * @param version 版本
     * @param apiName api名称
     * @param params 参数
     *
     * @return {@link Boolean}
     */
    @Override
    public Boolean batchUpdateBySysApi(Context context, String appId, String version, String apiName, List<Map<String, Object>> params) {

        return null;
    }

    /**
     * 批量更新
     *
     * @param context 上下文,包含租户、登录用户等
     * @param appId 应用ID
     * @param version 版本
     * @param apiName api名称
     * @param params 参数
     *
     * @return {@link Boolean}
     */
    @Override
    public Boolean batchCreateOrUpdateBySysApi(Context context, String appId, String version, String apiName, List<Map<String, Object>> params) {

        return null;
    }

    /**
     * 通过id查询记录
     *
     * @param context 上下文,包含租户、登录用户等
     * @param appId 应用程序id
     * @param version 版本
     * @param id id
     * @param apiName api名称
     *
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getById(Context context, String appId, String version, String id, String apiName) {
        Map<String, Object> params = Maps.newHashMapWithExpectedSize(2);
        params.put("id", id);
        InvokeApiRequest getByIdRequest = new InvokeApiRequest();
        getByIdRequest.setAppId(appId);
        getByIdRequest.setVersion(version);
        getByIdRequest.setName(apiName);
        getByIdRequest.setParams(params);

        Map<String, Object> result = (Map<String, Object>) metadataApiServiceClient.invokeApi(context, getByIdRequest);
        final int keySize = 2;
        // get的系统api只会返回head/data两个 key对应的对象响应
        if (result.size() == keySize && result.containsKey(ApiResponseModel.HEAD_KEY) && result.containsKey(ApiResponseModel.DATA_KEY)) {
            return (Map<String, Object>) result.get(ApiResponseModel.DATA_KEY);
        }

        return result;
    }

    /**
     * 通过id更新
     *
     * @param context 上下文,包含租户、登录用户等
     * @param appId 应用程序id
     * @param version 版本
     * @param apiName api名称
     * @param params 参数Map
     */
    @Override
    public Boolean updateById(Context context, String appId, String version, String apiName, Map<String, Object> params) {
        InvokeApiRequest updateRequest = new InvokeApiRequest();
        updateRequest.setAppId(appId);
        updateRequest.setVersion(version);
        updateRequest.setName(apiName);
        updateRequest.setParams(params);

        Object obj = metadataApiServiceClient.invokeApi(context, updateRequest);
        if (obj instanceof Boolean) {
            return (Boolean) obj;
        }

        return Boolean.TRUE;
    }

    /**
     * 更新记录，如果触发重复unique index则 抛异常
     *
     * @param context 上下文,包含租户、登录用户等
     * @param appId 应用ID
     * @param version 版本
     * @param apiName api名称
     * @param params 参数
     * @param throwMsg 异常消息
     *
     * @return {@link Boolean}
     */
    @Override
    public Boolean updateByIdIfDuplicateThrow(Context context, String appId, String version, String apiName, Map<String, Object> params, String throwMsg) {
        return this.updateById(context, appId, version, apiName, params);
    }

    /**
     * 创建
     *
     * @param context 上下文,包含租户、登录用户等
     * @param appId 应用程序id
     * @param version 版本
     * @param apiName api名称
     * @param params 参数map
     *
     * @return {@link String}
     */
    @Override
    @SuppressWarnings("unchecked")
    public String create(Context context, String appId, String version, String apiName, Map<String, Object> params) {
        InvokeApiRequest createProcessStageRequest = new InvokeApiRequest();
        createProcessStageRequest.setAppId(appId);
        createProcessStageRequest.setVersion(version);
        createProcessStageRequest.setName(apiName);
        createProcessStageRequest.setParams(params);

        Map<String, Object> result = (Map<String, Object>) metadataApiServiceClient.invokeApi(context, createProcessStageRequest);
        return String.valueOf(result.get("id"));
    }

    /**
     * 创建api, 如果metadata的table设置了uniq index，会触发duplicate exception
     *
     * @param context context
     * @param appId 应用id
     * @param version 版本
     * @param apiName api
     * @param params 参数
     * @param throwMsg 异常消息
     *
     * @return 主键id
     */
    @Override
    public String createIfDuplicateThrow(Context context, String appId, String version, String apiName, Map<String, Object> params, String throwMsg) {
        return this.create(context, appId, version, apiName, params);
    }

    /**
     * 创建或更新
     *
     * @param context 上下文,包含租户、登录用户等
     * @param appId 应用程序id
     * @param version 版本
     * @param apiName api名称
     * @param params 参数map
     *
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> createOrUpdate(Context context, String appId, String version, String apiName, Map<String, Object> params) {
        InvokeApiRequest createProcessStageRequest = new InvokeApiRequest();
        createProcessStageRequest.setAppId(appId);
        createProcessStageRequest.setVersion(version);
        createProcessStageRequest.setName(apiName);
        createProcessStageRequest.setParams(params);

        return (Map<String, Object>) metadataApiServiceClient.invokeApi(context, createProcessStageRequest);
    }

    /**
     * 执行新增或更新的系统API, 如果触发unique index则抛异常
     *
     * @param context 上下文,包含租户、登录用户等
     * @param appId 应用ID
     * @param version 版本
     * @param apiName api名称
     * @param params 参数
     * @param throwMsg 异常消息
     *
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    @Override
    public Map<String, Object> createOrUpdateIfDuplicateThrow(Context context, String appId, String version, String apiName, Map<String, Object> params,
                                                              String throwMsg) {
        return this.createOrUpdate(context, appId, version, apiName, params);
    }

    /**
     * list记录
     *
     * @param context 上下文,包含租户、登录用户等
     * @param appId 应用程序id
     * @param version 版本
     * @param apiName api名称
     * @param pageNo 页面没有
     * @param pageSize 页面大小
     * @param sortItems 类项目
     * @param params 参数Map
     *
     * @return {@link ListRecordsResult}
     */
    @Override
    @SuppressWarnings("unchecked")
    public ListRecordsResult listRecords(Context context, String appId, String version, String apiName,
                                         Long pageNo, Long pageSize, List<SortItem> sortItems,
                                         Map<String, Object> params) {

        QueryResult result = this.listRecordsV2(context, appId, version, apiName, pageNo, pageSize, sortItems, params);

        ListRecordsResult listResult = new ListRecordsResult();

        listResult.setPageSize(pageSize);
        listResult.setPageNo(pageNo);
        listResult.setCount(result.getCount());
        listResult.setData(result.getData());

        return listResult;
    }

    /**
     * 执行分页查询的系统API，查询多条数据
     *
     * @param context 上下文,包含租户、登录用户等
     * @param appId 应用程序id
     * @param version 版本
     * @param apiName api名称
     * @param pageNo 页面没有
     * @param pageSize 页面大小
     * @param sortItems 类项目
     * @param params 参数Map
     *
     * @return {@link QueryResult}
     */
    @Override
    public QueryResult listRecordsV2(Context context, String appId, String version, String apiName, Long pageNo, Long pageSize, List<SortItem> sortItems,
                                     Map<String, Object> params) {
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("pageNo", pageNo);
        paramMap.put("pageSize", pageSize);
        paramMap.put("params", params);
        if (CollectionUtils.isNotEmpty(sortItems)) {
            paramMap.put("sortItems", sortItems);
        }

        InvokeApiRequest listRequest = new InvokeApiRequest();
        listRequest.setAppId(appId);
        listRequest.setVersion(version);
        listRequest.setName(apiName);
        listRequest.setParams(paramMap);
        Map<String, Object> result = (Map<String, Object>) metadataApiServiceClient.invokeApi(context, listRequest);

        QueryResult listResult = new QueryResult();
        Long count = result.get("count") == null ? 0L : Long.parseLong(result.get("count").toString());
        listResult.setPageSize(pageSize.intValue());
        listResult.setPageNo(pageNo.intValue());
        listResult.setCount(count);
        List<Map<String, Object>> data = Collections.emptyList();
        if (Objects.nonNull(result.get(ApiResponseModel.DATA_KEY))) {
            data = (List<Map<String, Object>>) result.get(ApiResponseModel.DATA_KEY);
        }
        listResult.setData(data);

        return listResult;
    }

    /**
     * 删除 记录api
     *
     * @param context 上下文,包含租户、登录用户等
     * @param appId 应用程序id
     * @param version 版本
     * @param apiName api名称
     * @param id id
     */
    @Override
    public Boolean delete(Context context, String appId, String version, String apiName, String id) {
        Map<String, Object> params = Maps.newHashMapWithExpectedSize(2);
        params.put("id", id);
        InvokeApiRequest createProcessStageRequest = new InvokeApiRequest();
        createProcessStageRequest.setAppId(appId);
        createProcessStageRequest.setVersion(version);
        createProcessStageRequest.setName(apiName);
        createProcessStageRequest.setParams(params);

        Object obj = metadataApiServiceClient.invokeApi(context, createProcessStageRequest);
        if (obj instanceof Boolean) {
            return (Boolean) obj;
        }

        return Boolean.TRUE;
    }

    @Override
    public QueryResult invokeCustomerApi(Context context, String appId, String version, String apiName, Map<String, Object> params) {
        InvokeApiRequest invokeApiRequest = new InvokeApiRequest();
        invokeApiRequest.setAppId(appId);
        invokeApiRequest.setVersion(version);
        invokeApiRequest.setName(apiName);
        invokeApiRequest.setParams(params);

        Object result = metadataApiServiceClient.invokeApi(context, invokeApiRequest);
        if (result instanceof QueryResult) {
            return (QueryResult) result;
        }

        throw new RuntimeException("Invalid customer api.Please check your apiName");
    }

    /**
     * 调用自定义更新api
     *
     * @param context 上下文,包含租户、登录用户等
     * @param appId 应用程序id
     * @param version 版本
     * @param apiName api名称
     * @param params 参数Map
     *
     * @return {@link QueryResult}
     */
    @Override
    public QueryResult updateByCustomerApi(Context context, String appId, String version, String apiName, Map<String, Object> params) {
        return invokeCustomerApi(context, appId, version, apiName, params);
    }

    /**
     * 用户自定义更新api, 如果触发unique index则抛异常
     *
     * @param context 上下文,包含租户、登录用户等
     * @param appId 应用ID
     * @param version 版本
     * @param apiName api名称
     * @param params 参数
     * @param throwMsg 异常消息
     *
     * @return {@link QueryResult}
     */
    @Override
    public QueryResult updateByCustomerApiIfDuplicateThrow(Context context, String appId, String version, String apiName, Map<String, Object> params,
                                                           String throwMsg) {
        return this.updateByCustomerApi(context, appId, version, apiName, params);
    }

    /**
     * 调用自定义删除api
     *
     * @param context 上下文,包含租户、登录用户等
     * @param appId 应用程序id
     * @param version 版本
     * @param apiName api名称
     * @param params 参数Map
     *
     * @return {@link QueryResult}
     */
    @Override
    public QueryResult deleteByCustomerApi(Context context, String appId, String version, String apiName, Map<String, Object> params) {
        return invokeCustomerApi(context, appId, version, apiName, params);
    }

    /**
     * 调用自定义查询api
     *
     * @param context 上下文,包含租户、登录用户等
     * @param appId 应用程序id
     * @param version 版本
     * @param apiName api名称
     * @param params 参数Map
     *
     * @return {@link QueryResult}
     */
    @Override
    public QueryResult queryByCustomerApi(Context context, String appId, String version, String apiName, Map<String, Object> params) {
        return invokeCustomerApi(context, appId, version, apiName, params);
    }

    /**
     * 批量创建
     *
     * @param context 上下文,包含租户、登录用户等
     * @param appId 应用ID
     * @param version 版本
     * @param apiName api名称
     * @param params 参数
     *
     * @return {@link List}<{@link String}>
     */
    @Override
    public List<String> batchCreate(Context context, String appId, String version, String apiName, List<Map<String, Object>> params) {
        return null;
    }

    /**
     * 批量创建, 如果触发unique index则抛异常
     *
     * @param context 上下文,包含租户、登录用户等
     * @param appId 应用ID
     * @param version 版本
     * @param apiName api名称
     * @param params 参数
     * @param throwMsg 异常消息
     *
     * @return {@link List}<{@link String}>
     */
    @Override
    public List<String> batchCreateIfDuplicateThrow(Context context, String appId, String version, String apiName, List<Map<String, Object>> params,
                                                    String throwMsg) {
        return this.batchCreate(context, appId, version, apiName, params);
    }

    /**
     * 暂不支持,请使用SysApiInvokeManagerImpl
     */
    @Override
    public <T> PageResult<T> listAllRecord(Map<String, Object> params,
                                           Class<T> clazz, Supplier<QueryResult> supplier){
        throw new KABizException(KAResultCodes.KA_NOT_SUPPORT_EXCEPTION);
    }

    @Override
    public Map<String, Object> getById(Context context, String appId, String version, String id, String getBseTaskRecordV2, boolean relation) {
        return null;
    }
}
