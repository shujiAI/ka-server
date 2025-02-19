package com.shujiai.ka.dubbo.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.shujiai.ka.constant.CommonConstant;
import com.shujiai.ka.constant.KAConstant;
import com.shujiai.ka.dubbo.consumer.MetadataApiServiceClient;
import com.shujiai.ka.dubbo.consumer.SysApiServiceConfigClient;
import com.shujiai.ka.exceptions.IgnoreBizException;
import com.shujiai.ka.model.ListRecordsResult;
import com.shujiai.ka.util.ContextCheck;
import com.shujiai.ka.util.KAConvertUtils;
import com.shujiai.ka.util.PageResult;
import com.shujiai.ka.util.PageUtil;
import com.shujiai.base.context.Context;
import com.shujiai.base.exception.BizException;
import com.shujiai.base.result.ResultCode;
import com.shujiai.metadata.facade.dto.SortItem;
import com.shujiai.metadata.facade.dto.request.AppMetaTableBatchInsertRequest;
import com.shujiai.metadata.facade.dto.request.BatchCreateRequest;
import com.shujiai.metadata.facade.dto.request.InvokeApiRequest;
import com.shujiai.metadata.facade.dto.sql.QueryResult;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * ApiInvokeManagerImpl
 *
 * @author hxh
 * @date 2023/11/4
 * @time 09:57:28
 * @descripton 优先使用这个manager，底层通过SysApiFacadeService实现，支持批量的功能，后续逐渐让meta 完善
 */
@Component
public class SysApiInvokeManagerImpl implements ApiInvokeManager {

//    private static final Logger serviceLogger = LoggerUtil.SERVICE_LOG.getLogger();

    @Autowired
    private SysApiServiceConfigClient sysApiFacadeServiceClient;

    @Autowired
    private MetadataApiServiceClient metadataApiServiceClient;

    /**
     * 批量插入
     *
     * @param context   上下文,包含租户、登录用户等
     * @param appId     应用ID
     * @param version   版本
     * @param tableName 表名
     * @param params    参数
     * @return {@link Boolean}
     */
    @Override
    public List<String> batchInsert(Context context, String appId, String version, String tableName, List<Map<String, Object>> params) {
        AppMetaTableBatchInsertRequest request = new AppMetaTableBatchInsertRequest();
        request.setAppId(appId);
        request.setAppVersion(version);
        request.setTableName(tableName);
        request.setBatchParam(params);

        return sysApiFacadeServiceClient.batchInsert(context, request);
    }

    /**
     * 批量更新
     *
     * @param context 上下文,包含租户、登录用户等
     * @param appId   应用ID
     * @param version 版本
     * @param apiName api名称
     * @param params  参数
     * @return {@link Boolean}
     */
    @Override
    public Boolean batchUpdateBySysApi(Context context, String appId, String version, String apiName, List<Map<String, Object>> params) {

        BatchCreateRequest request = new BatchCreateRequest();
        request.setAppId(appId);
        request.setVersion(version);
        request.setName(apiName);
        request.setBatchParams(params);

        return sysApiFacadeServiceClient.batchUpdateBySysApi(context, request);
    }

    /**
     * 批量更新
     *
     * @param context 上下文,包含租户、登录用户等
     * @param appId   应用ID
     * @param version 版本
     * @param apiName api名称
     * @param params  参数
     * @return {@link Boolean}
     */
    @Override
    public Boolean batchCreateOrUpdateBySysApi(Context context, String appId, String version, String apiName, List<Map<String, Object>> params) {

        BatchCreateRequest request = new BatchCreateRequest();
        request.setAppId(appId);
        request.setVersion(version);
        request.setName(apiName);
        request.setBatchParams(params);

        return sysApiFacadeServiceClient.batchUpdateBySysApi(context, request);
    }

    @Override
    public Map<String, Object> getById(Context context, String appId, String version, String id, String apiName) {
        ContextCheck.validParam(appId, CommonConstant.APP_ID);

        Map<String, Object> params = Maps.newHashMapWithExpectedSize(2);
        params.put("id", id);
        InvokeApiRequest getByIdRequest = new InvokeApiRequest();
        getByIdRequest.setAppId(appId);
        getByIdRequest.setVersion(version);
        getByIdRequest.setName(apiName);
        getByIdRequest.setParams(params);

        Map<String, Object> result = sysApiFacadeServiceClient.queryById(context, getByIdRequest);

        return result;
    }

    @Override
    public Boolean updateById(Context context, String appId, String version, String apiName, Map<String, Object> params) {
        ContextCheck.validParam(appId, CommonConstant.APP_ID);

        InvokeApiRequest updateRequest = new InvokeApiRequest();
        updateRequest.setAppId(appId);
        updateRequest.setVersion(version);
        updateRequest.setName(apiName);
        updateRequest.setParams(params);

        return sysApiFacadeServiceClient.updateBySysApi(context, updateRequest);
    }

    /**
     * 更新记录，如果触发重复unique index则 抛异常
     *
     * @param context  上下文,包含租户、登录用户等
     * @param appId    应用ID
     * @param version  版本
     * @param apiName  api名称
     * @param params   参数
     * @param throwMsg 异常消息
     * @return {@link Boolean}
     */
    @Override
    public Boolean updateByIdIfDuplicateThrow(Context context, String appId, String version, String apiName, Map<String, Object> params, String throwMsg) {
        ContextCheck.validParam(appId, CommonConstant.APP_ID);

        try {
            return this.updateById(context, appId, version, apiName, params);
        } catch (BizException we) {
            if (KAConstant.DATABASE_UNIQUE_ERROR_CODE.equals(we.getResultCode().getCode())) {
                throw new IgnoreBizException(
                        new ResultCode(we.getResultCode().getCode(), throwMsg, we.getResultCode().getLocalMessage()));
            }

            throw we;
        }
    }

    @Override
    public String create(Context context, String appId, String version, String apiName, Map<String, Object> params) {
        InvokeApiRequest createRequest = new InvokeApiRequest();
        createRequest.setAppId(appId);
        createRequest.setVersion(version);
        createRequest.setName(apiName);
        createRequest.setParams(params);

        return sysApiFacadeServiceClient.createBySysApi(context, createRequest);
    }

    /**
     * 创建api, 如果metadata的table设置了uniq index，会触发duplicate exception
     *
     * @param context  context
     * @param appId    应用id
     * @param version  版本
     * @param apiName  api
     * @param params   参数
     * @param throwMsg 异常消息
     * @return 主键id
     */
    @Override
    public String createIfDuplicateThrow(Context context, String appId, String version, String apiName, Map<String, Object> params, String throwMsg) {
        ContextCheck.validParam(appId, CommonConstant.APP_ID);

        try {
            return this.create(context, appId, version, apiName, params);
        } catch (BizException we) {
            if (KAConstant.DATABASE_UNIQUE_ERROR_CODE.equals(we.getResultCode().getCode())) {
                throw new IgnoreBizException(
                        new ResultCode(we.getResultCode().getCode(), throwMsg, we.getResultCode().getLocalMessage()));

            }

            throw we;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> createOrUpdate(Context context, String appId, String version, String apiName, Map<String, Object> params) {
        ContextCheck.validParam(appId, CommonConstant.APP_ID);

        InvokeApiRequest createOrUpdateRequest = new InvokeApiRequest();
        createOrUpdateRequest.setAppId(appId);
        createOrUpdateRequest.setVersion(version);
        createOrUpdateRequest.setName(apiName);
        createOrUpdateRequest.setParams(params);

        //SysApiFacadeService 没有提供，用meta的api client代替
        return (Map<String, Object>) metadataApiServiceClient.invokeApi(context, createOrUpdateRequest);
    }

    /**
     * 执行新增或更新的系统API, 如果触发unique index则抛异常
     *
     * @param context  上下文,包含租户、登录用户等
     * @param appId    应用ID
     * @param version  版本
     * @param apiName  api名称
     * @param params   参数
     * @param throwMsg 异常消息
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    @Override
    public Map<String, Object> createOrUpdateIfDuplicateThrow(Context context, String appId, String version, String apiName, Map<String, Object> params,
                                                              String throwMsg) {
        ContextCheck.validParam(appId, CommonConstant.APP_ID);

        try {
            return this.createOrUpdate(context, appId, version, apiName, params);
        } catch (BizException we) {
            if (KAConstant.DATABASE_UNIQUE_ERROR_CODE.equals(we.getResultCode().getCode())) {
                throw new IgnoreBizException(
                        new ResultCode(we.getResultCode().getCode(), throwMsg, we.getResultCode().getLocalMessage()));
            }

            throw we;
        }
    }

    @Override
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
     * @param context   上下文,包含租户、登录用户等
     * @param appId     应用程序id
     * @param version   版本
     * @param apiName   api名称
     * @param pageNo    页面没有
     * @param pageSize  页面大小
     * @param sortItems 类项目
     * @param params    参数Map
     * @return {@link QueryResult}
     */
    @Override
    public QueryResult listRecordsV2(Context context, String appId, String version, String apiName, Long pageNo, Long pageSize, List<SortItem> sortItems,
                                     Map<String, Object> params) {
        ContextCheck.validParam(appId, CommonConstant.APP_ID);

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

        QueryResult result = sysApiFacadeServiceClient.queryBySysListRecordApi(context, listRequest);
//        result.setHead(null);
        return result;
    }

    @Override
    public Boolean delete(Context context, String appId, String version, String apiName, String id) {
        ContextCheck.validParam(appId, CommonConstant.APP_ID);

        Map<String, Object> params = Maps.newHashMapWithExpectedSize(2);

        params.put("id", id);
        InvokeApiRequest deleteRequest = new InvokeApiRequest();
        deleteRequest.setAppId(appId);
        deleteRequest.setVersion(version);
        deleteRequest.setName(apiName);
        deleteRequest.setParams(params);

        return sysApiFacadeServiceClient.deleteBySysApi(context, deleteRequest);
    }

    @Override
    public QueryResult invokeCustomerApi(Context context, String appId, String version, String apiName, Map<String, Object> params) {

        ContextCheck.validParam(appId, CommonConstant.APP_ID);

        InvokeApiRequest invokeApiRequest = new InvokeApiRequest();
        invokeApiRequest.setAppId(appId);
        invokeApiRequest.setVersion(version);
        invokeApiRequest.setName(apiName);
        invokeApiRequest.setParams(params);

        Object result = metadataApiServiceClient.invokeApi(context, invokeApiRequest);
        if (result instanceof QueryResult) {
            QueryResult ret = (QueryResult) result;
            ret.setHead(((QueryResult) result).getHead());
            return ret;
        }

        throw new RuntimeException("Invalid customer api.Please check your apiName");
    }

    /**
     * 调用自定义更新api
     *
     * @param context 上下文,包含租户、登录用户等
     * @param appId
     * @param version
     * @param apiName
     * @param params
     * @return {@link QueryResult}
     */
    @Override
    public QueryResult updateByCustomerApi(Context context, String appId, String version, String apiName, Map<String, Object> params) {

        ContextCheck.validParam(appId, CommonConstant.APP_ID);

        InvokeApiRequest invokeApiRequest = new InvokeApiRequest();
        invokeApiRequest.setAppId(appId);
        invokeApiRequest.setVersion(version);
        invokeApiRequest.setName(apiName);
        invokeApiRequest.setParams(params);

        QueryResult result = sysApiFacadeServiceClient.updateByCustomerApi(context, invokeApiRequest);
//        result.setHead(null);
        return result;
    }

    /**
     * 用户自定义更新api, 如果触发unique index则抛异常
     *
     * @param context  上下文,包含租户、登录用户等
     * @param appId    应用ID
     * @param version  版本
     * @param apiName  api名称
     * @param params   参数
     * @param throwMsg 异常消息
     * @return {@link QueryResult}
     */
    @Override
    public QueryResult updateByCustomerApiIfDuplicateThrow(Context context, String appId, String version, String apiName, Map<String, Object> params,
                                                           String throwMsg) {
        ContextCheck.validParam(appId, CommonConstant.APP_ID);

        try {
            return this.updateByCustomerApi(context, appId, version, apiName, params);
        } catch (BizException we) {
            if (KAConstant.DATABASE_UNIQUE_ERROR_CODE.equals(we.getResultCode().getCode())) {
                throw new IgnoreBizException(
                        new ResultCode(we.getResultCode().getCode(), throwMsg, we.getResultCode().getLocalMessage()));
            }

            throw we;
        }
    }

    /**
     * 调用自定义删除api
     *
     * @param context 上下文,包含租户、登录用户等
     * @param appId
     * @param version
     * @param apiName
     * @param params
     * @return {@link QueryResult}
     */
    @Override
    public QueryResult deleteByCustomerApi(Context context, String appId, String version, String apiName, Map<String, Object> params) {
        ContextCheck.validParam(appId, CommonConstant.APP_ID);

        InvokeApiRequest invokeApiRequest = new InvokeApiRequest();
        invokeApiRequest.setAppId(appId);
        invokeApiRequest.setVersion(version);
        invokeApiRequest.setName(apiName);
        invokeApiRequest.setParams(params);

        QueryResult result = sysApiFacadeServiceClient.deleteByCustomerApi(context, invokeApiRequest);
//        result.setHead(null);
        return result;
    }

    /**
     * 调用自定义查询api
     *
     * @param context 上下文,包含租户、登录用户等
     * @param appId
     * @param version
     * @param apiName
     * @param params
     * @return {@link QueryResult}
     */
    @Override
    public QueryResult queryByCustomerApi(Context context, String appId, String version, String apiName, Map<String, Object> params) {
        ContextCheck.validParam(appId, CommonConstant.APP_ID);

        InvokeApiRequest invokeApiRequest = new InvokeApiRequest();
        invokeApiRequest.setAppId(appId);
        invokeApiRequest.setVersion(version);
        invokeApiRequest.setName(apiName);
        invokeApiRequest.setParams(params);

        QueryResult result = sysApiFacadeServiceClient.queryByCustomerApi(context, invokeApiRequest);
//        result.setHead(null);
        return result;
    }

    /**
     * 批量创建
     */
    @Override
    public List<String> batchCreate(Context context, String appId, String version, String apiName, List<Map<String, Object>> params) {
        ContextCheck.validParam(appId, CommonConstant.APP_ID);

        BatchCreateRequest request = new BatchCreateRequest();
        request.setAppId(appId);
        request.setVersion(version);
        request.setName(apiName);
        request.setBatchParams(params);

        return sysApiFacadeServiceClient.batchCreateBySysApi(context, request);
    }

    /**
     * 批量创建, 如果触发unique index则抛异常
     *
     * @param context  上下文,包含租户、登录用户等
     * @param appId    应用ID
     * @param version  版本
     * @param apiName  api名称
     * @param params   参数
     * @param throwMsg 异常消息
     * @return {@link List}<{@link String}>
     */
    @Override
    public List<String> batchCreateIfDuplicateThrow(Context context, String appId, String version, String apiName, List<Map<String, Object>> params,
                                                    String throwMsg) {
        ContextCheck.validParam(appId, CommonConstant.APP_ID);

        try {
            return this.batchCreate(context, appId, version, apiName, params);
        } catch (BizException we) {
            if (KAConstant.DATABASE_UNIQUE_ERROR_CODE.equals(we.getResultCode().getCode())) {
                throw new IgnoreBizException(
                        new ResultCode(we.getResultCode().getCode(), throwMsg, we.getResultCode().getLocalMessage()));
            }

            throw we;
        }
    }

    /**
     * 不分页获取获取全部的记录,这里返回PageResult是因为之前的代码很多是直接返回{@link QueryResult},是为了兼容之前的逻辑
     */
    @Override
    public <T> PageResult<T> listAllRecord(Map<String, Object> params,
                                           Class<T> clazz, Supplier<QueryResult> supplier) {
        //不分页
        long pageNo = 1L, pageSize = Long.valueOf(KAConstant.MAX_PAGE_SIZE);
        params.put(KAConstant.PAGE_NO_KEY, pageNo);
        params.put(KAConstant.PAGE_SIZE_KEY, pageSize);

        List<T> list = Lists.newArrayList();
        QueryResult queryResult;
        do {
            queryResult = supplier.get();
            pageNo++;
            params.put(KAConstant.PAGE_NO_KEY, pageNo);
            list.addAll(KAConvertUtils.transMapObjects(queryResult.getData(), clazz));
        } while (PageUtil.checkHasMoreRecord(pageNo, pageSize, queryResult.getData().size(), queryResult.getCount()));

        return new PageResult<>((long) list.size(), null, null, list);
    }

    @Override
    public Map<String, Object> getById(Context context, String appId, String version, String id, String apiName, boolean relation) {
        ContextCheck.validParam(appId, CommonConstant.APP_ID);

        Map<String, Object> params = Maps.newHashMapWithExpectedSize(2);
        params.put("id", id);
        params.put("_NONE_RELATION", relation);
        InvokeApiRequest getByIdRequest = new InvokeApiRequest();
        getByIdRequest.setAppId(appId);
        getByIdRequest.setVersion(version);
        getByIdRequest.setName(apiName);
        getByIdRequest.setParams(params);

        Map<String, Object> result = sysApiFacadeServiceClient.queryById(context, getByIdRequest);

        return result;
    }

}
