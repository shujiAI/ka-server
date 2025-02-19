package com.shujiai.ka.dubbo.consumer;

import cn.hutool.core.bean.BeanUtil;
import com.google.common.collect.Maps;
import com.shujiai.ka.constant.CommonConstant;
import com.shujiai.ka.constant.DbFieldConstant;
import com.shujiai.ka.constant.KAResultCodes;
import com.shujiai.ka.constant.QueryConstant;
import com.shujiai.ka.logger.LogUtil;
import com.shujiai.ka.logger.LoggerEnums;
import com.shujiai.ka.logger.LoggerFactory;
import com.shujiai.ka.model.QueryRes;
import com.shujiai.ka.util.*;
import com.shujiai.base.context.Context;
import com.shujiai.base.exception.BizException;
import com.shujiai.base.result.Result;
import com.shujiai.base.result.ResultCode;
import com.shujiai.metadata.facade.AppMetaTableDmlService;
import com.shujiai.metadata.facade.SysApiFacadeService;
import com.shujiai.metadata.facade.dto.request.AppMetaTableBatchInsertRequest;
import com.shujiai.metadata.facade.dto.request.BatchCreateRequest;
import com.shujiai.metadata.facade.dto.request.InvokeApiRequest;
import com.shujiai.metadata.facade.dto.sql.QueryResult;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * file desc
 *
 * @author jiuyi
 * @date 2021/12/2 11:14 上午
 */
@Component
public class SysApiServiceConfigClient {
    private static final LoggerFactory SAL_LOG = LogUtil.getLogger(SysApiServiceConfigClient.class, LoggerEnums.SAL_LOG);
    private static final int BATCH_MAX_SIZE = 200;

    /**
     * metadata返回的记录重复的code
     */
    public static final Integer DATABASE_UNIQUE_ERROR_CODE = 1004;

    /**
     * metadata返回的触发应用未发布的异常code
     */
    public static final Integer APP_UN_DEPLOYED = 1500;

    @Reference(check = false, parameters = {
            "batchCreateBySysApi.retries", "0",
            "batchCreateBySysApi.timeout", "60000",
            "batchUpdateBySysApi.retries","0",
            "batchUpdateBySysApi.timeout","60000",
            "queryByCustomerApi.retries", "1",
            "queryByCustomerApi.timeout", "60000",
            "queryBySysListRecordApi.retries", "1",
            "queryBySysListRecordApi.timeout", "60000"},
            group = "${dubbo.metadata.group.name}", version = "${dubbo.metadata.version}", timeout = 60000, retries = 0)
    private SysApiFacadeService sysApiFacadeService;

    @Reference(check = false, parameters = {
            "batchInsert.retries", "0",
            "batchInsert.timeout", "60000"},
            group = "${dubbo.metadata.group.name}", version = "${dubbo.metadata.version}", timeout = 60000, retries = 0)
    private AppMetaTableDmlService appMetaTableDmlService;

    /**
     * 批量创建
     */
    public List<String> batchCreateBySysApi(Context context, BatchCreateRequest request) {
        Result<List<String>> result = sysApiFacadeService.batchCreateBySysApi(context, request);

        handleMetaDataResult(result, request, "batchCreateBySysApi");

        return result.getData();
    }

    private <T> void handleMetaDataResult(Result<T> result, Object request, String apiName) {
        if (!result.getSuccess()) {
            ResultCode resultCode = new ResultCode(result.getCode(), result.getMessage(), result.getArgs());
            // 要忽略的code  || DATABASE_UNIQUE_ERROR_CODE.equals(result.getCode())
            if (APP_UN_DEPLOYED.equals(result.getCode())) {
                throw new BizException(resultCode);
            }

            SAL_LOG.error("DATA_API={}, result={}, param={}", apiName, result.getMessage(), request);
            throw new BizException(resultCode);
        }
    }

    /**
     * 批量插入
     *
     * @param context 上下文,包含租户、登录用户等
     * @param request 请求
     * @return {@link List}<{@link String}>
     */
    public List<String> batchInsert(Context context, AppMetaTableBatchInsertRequest request) {
        Result<List<String>> result = appMetaTableDmlService.batchInsert(context, request);

        handleMetaDataResult(result, request, "batchInsert");

        return result.getData();
    }

    public Boolean batchUpdateBySysApi(Context context, BatchCreateRequest request) {
        Result<Boolean> result = sysApiFacadeService.batchUpdateBySysApi(context, request);

        handleMetaDataResult(result, request, "batchUpdateBySysApi");

        return result.getData();
    }

    public Boolean updateBySysApi(Context context, InvokeApiRequest request) {
        Result<Boolean> result = sysApiFacadeService.updateBySysApi(context, request);

        handleMetaDataResult(result, request, "updateBySysApi");

        return result.isSuccess() && result.getData();
    }

    public <T> QueryRes<T> queryById(Context context, InvokeApiRequest request, Class<T> clazz) {
        Result<Map<String, Object>> result = sysApiFacadeService.queryById(context, request);

        handleMetaDataResult(result, request, "queryById");

        Result<Object> res2 = new Result<>(result);
        return MetadataResultParseUtil.parseMetadataResult(res2, clazz);
    }

    public Map<String, Object> queryById(Context context, InvokeApiRequest request) {
        Result<Map<String, Object>> result = sysApiFacadeService.queryById(context, request);

        handleMetaDataResult(result, request, "queryById");

        return result.getData();
    }

    public String createBySysApi(Context context, InvokeApiRequest request) {
        Result<String> result = sysApiFacadeService.createBySysApi(context, request);

        handleMetaDataResult(result, request, "createBySysApi");

        return result.getData();
    }

    public String createOrUpdateBySysApi(Context context, InvokeApiRequest request) {
        if (!StringUtils.hasText(request.getName())) {
            throw new BizException(KAResultCodes.API_INVALID);
        }
        Result<String> result = sysApiFacadeService.createOrUpdateBySysApi(context, request);

        handleMetaDataResult(result, request, "createOrUpdateBySysApi");

        return result.getData();
    }

    public Boolean deleteBySysApi(Context context, InvokeApiRequest request) {
        Result<Boolean> result = sysApiFacadeService.deleteBySysApi(context, request);

        handleMetaDataResult(result, request, "deleteBySysApi");

        return result.getData();
    }

    public <T> QueryRes<T> updateByCustomerApi(Context context, InvokeApiRequest request, Class<T> clazz) {
        Result<QueryResult> result = sysApiFacadeService.updateByCustomerApi(context, request);

        handleMetaDataResult(result, request, "updateByCustomerApi");

        return MetadataResultParseUtil.transMetadataResult(result, clazz);
    }

    public QueryResult updateByCustomerApi(Context context, InvokeApiRequest request) {
        Result<QueryResult> result = sysApiFacadeService.updateByCustomerApi(context, request);

        handleMetaDataResult(result, request, "updateByCustomerApi");

        return result.getData();
    }

    public <T> QueryRes<T> deleteByCustomerApi(Context context, InvokeApiRequest request, Class<T> clazz) {
        Result<QueryResult> result = sysApiFacadeService.deleteByCustomerApi(context, request);

        handleMetaDataResult(result, request, "deleteByCustomerApi");

        return MetadataResultParseUtil.transMetadataResult(result, clazz);
    }

    public QueryResult deleteByCustomerApi(Context context, InvokeApiRequest request) {
        Result<QueryResult> result = sysApiFacadeService.deleteByCustomerApi(context, request);

        handleMetaDataResult(result, request, "deleteByCustomerApi");

        return result.getData();

    }

    public <T> QueryRes<T> queryByCustomerApi(Context context, InvokeApiRequest request, Class<T> clazz) {
        Result<QueryResult> result = sysApiFacadeService.queryByCustomerApi(context, request);

        handleMetaDataResult(result, request, "queryByCustomerApi");

        return MetadataResultParseUtil.transMetadataResult(result, clazz);
    }

    public QueryResult queryByCustomerApi(Context context, InvokeApiRequest request) {
        Result<QueryResult> result = sysApiFacadeService.queryByCustomerApi(context, request);

        handleMetaDataResult(result, request, "queryByCustomerApi");

        return result.getData();

    }

    public <T> QueryRes<T> queryBySysListRecordApi(Context context, InvokeApiRequest request, Class<T> clazz) {
        Result<QueryResult> result = sysApiFacadeService.queryBySysListRecordApi(context, request);

        handleMetaDataResult(result, request, "queryBySysListRecordApi");

        return MetadataResultParseUtil.transMetadataResult(result, clazz);
    }

    public QueryResult queryBySysListRecordApi(Context context, InvokeApiRequest request) {
        Result<QueryResult> result = sysApiFacadeService.queryBySysListRecordApi(context, request);

        handleMetaDataResult(result, request, "queryBySysListRecordApi");

        return result.getData();
    }

    /**
     * 统计
     *
     * @param context
     * @param appId
     * @param apiName
     * @param param
     * @param totalField
     * @return
     */
    public long countByParam(Context context, String appId, String apiName, Map<String, Object> param, String totalField) {
        Map<String, Object> totalMap = this.queryByMap(context, appId
                , param
                , apiName);
        totalField = StringUtils.hasText(totalField) ? totalField : CommonConstant.TOTAL;
        if (!CollectionUtils.isEmpty(totalMap) && totalMap.containsKey(totalField)) {
            return (Long) totalMap.get(totalField);
        }
        return 0L;
    }

    public <T> List<T> listByParam(Context context, String appId, String apiName, Map<String, Object> param, Class<T> clazz) {
        QueryResult queryResult = this.listQueryByParam(context, appId, apiName, param);
        if (Objects.isNull(queryResult) || CollectionUtils.isEmpty(queryResult.getData())) {
            return new ArrayList<>();
        }
        return queryResult.getData().stream().map(s -> BeanConvertUtil.mapToBean(s, clazz)).collect(Collectors.toList());

    }

    public <T> List<T> listByParam(Context context, String appId, String apiName, Map<String, Object> param,
                                   List<SearchSortDTO> sortItems, Class<T> clazz) {
        QueryResult queryResult = this.listQueryByParam(context, appId, apiName, param, sortItems);
        if (Objects.isNull(queryResult) || CollectionUtils.isEmpty(queryResult.getData())) {
            return new ArrayList<>();
        }
        return queryResult.getData().stream().map(s -> BeanConvertUtil.mapToBean(s, clazz)).collect(Collectors.toList());
    }

    public List<Map<String, Object>> listMapByParam(Context context, String appId, String apiName, Map<String, Object> param) {
        QueryResult queryResult = this.listQueryByParam(context, appId, apiName, param);
        return queryResult.getData();

    }

    public QueryResult listQueryByParam(Context context, String appId, String apiName, Map<String, Object> params) {
        return listQueryByParam(context, appId, apiName, params, null);
    }

    public QueryResult listQueryByParam(Context context, String appId, String apiName, Map<String, Object> params, List<SearchSortDTO> sortItems) {
        if (CollectionUtils.isEmpty(params)) {
            params = Maps.newHashMap();
        }
        InvokeApiRequest invokeApiRequest = new InvokeApiRequest();
        invokeApiRequest.setName(apiName);
        invokeApiRequest.setAppId(appId);
        if (Objects.isNull(params.get(CommonConstant.PAGE_NO_KEY)) || Objects.isNull(params.get(CommonConstant.PAGE_SIZE_KEY))) {
            params.put(DbFieldConstant.NONE_PAGE, true);
        } else {
            if (Objects.isNull(params.get(CommonConstant.PAGE_SIZE_KEY))) {
                params.put(CommonConstant.PAGE_SIZE_KEY, QueryConstant.PAGE_SIZE_10);
            }
        }


        //系统API
        if (Character.isUpperCase(apiName.charAt(0))) {
            Integer pageNo = Objects.nonNull(params.get(CommonConstant.PAGE_NO_KEY)) ? (Integer) params.get(CommonConstant.PAGE_NO_KEY) : QueryConstant.PAGE_NO;
            Integer pageSize = Objects.nonNull(params.get(CommonConstant.PAGE_SIZE_KEY)) ? (Integer) params.get(CommonConstant.PAGE_SIZE_KEY) : CommonConstant.PAGE_SIZE;
            ApiSearchRequest listParam = ApiSearchRequest.builder()
                    .params(params).pageNo(pageNo).pageSize(pageSize).sortItems(sortItems)
                    .build();
            invokeApiRequest.setParams(BeanUtil.beanToMap(listParam));

            Result<QueryResult> result = sysApiFacadeService.queryBySysListRecordApi(context, invokeApiRequest);
            if (!result.isSuccess()) {
                SAL_LOG.error("DATA_API={}, result={}, param={}", apiName, result.getMessage(), invokeApiRequest);
                throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
            }

            return result.getData();
        }


        invokeApiRequest.setParams(params);
        //自定义API
        Result<QueryResult> result = sysApiFacadeService.queryByCustomerApi(context, invokeApiRequest);
        if (!result.isSuccess()) {
            SAL_LOG.error("DATA_API={}, result={}, param={}", apiName, result.getMessage(), invokeApiRequest);
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        if (Objects.isNull(result.getData())) {
            SAL_LOG.error("DATA_API={}, result={}, param={}", apiName, result.getMessage(), invokeApiRequest);
            throw new BizException(KAResultCodes.META_RESPONSE_EXCEPTION);
        }
        if (Objects.isNull(result.getData().getCount())) {
            QueryResult queryResult = new QueryResult();
            queryResult.setData(result.getData().getData());
            queryResult.setCount((long) result.getData().getData().size());
            Integer pageNo = Objects.nonNull(params.get(CommonConstant.PAGE_NO_KEY)) ? (Integer) params.get(CommonConstant.PAGE_NO_KEY) : 1;
            Integer pageSize = Objects.nonNull(params.get(CommonConstant.PAGE_SIZE_KEY)) ? (Integer) params.get(CommonConstant.PAGE_SIZE_KEY) : queryResult.getCount().intValue();
            queryResult.setPageNo(pageNo);
            queryResult.setPageSize(pageSize);
            return queryResult;
        }

        return result.getData();
    }


    /**
     * 批量更新或添加
     * 批量更新或添加时尽量调用这个接口
     *
     * @param context
     * @param request
     * @return
     */
    public Boolean batchCreateOrUpdate(Context context, BatchCreateRequest request) {
        Result<Boolean> result = sysApiFacadeService.batchCreateOrUpdateBySysApi(context, request);
        if (!result.isSuccess()) {
            SAL_LOG.error("DATA_API={}, result={}, param={}", request.getName(), result.getMessage(), request);
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }

        return result.getSuccess();
    }

    /**
     * 直插数据表
     * 这是一个高效API
     * 不支持关联表操作，不支持插入之后发起流程。
     * 在没有加密的情况下dev环境插入1000条数据3s 优于原接口10倍左右。
     *
     * @param context
     * @param request
     */
    public List<String> batchEfficientInsert(Context context, AppMetaTableBatchInsertRequest request) {
        Result<List<String>> result = appMetaTableDmlService.batchInsert(context, request);
        if (!result.isSuccess()) {
            SAL_LOG.error("DATA_API={}, result={}, param={}", request.getTableName(), result.getMessage(), request);
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    public <T> T queryByMap(Context context, String appId, Map<String, Object> param, String apiName, Class<T> clazz) {
        Map<String, Object> data = this.queryByMap(context, appId, param, apiName);
        if (Objects.nonNull(data)) {
            return BeanUtil.toBean(data, clazz);
        }
        return null;
    }

    public Map<String, Object> queryById(Context context, String appId, String id, String apiName) {
        if (!StringUtils.hasText(id)) {
            return null;
        }
        Map<String, Object> param = Maps.newHashMap();
        param.put(DbFieldConstant.ID, id);
        return this.queryByMap(context, appId, param, apiName);
    }

    /**
     * 获取对象
     *
     * @param context
     * @param appId
     * @param id
     * @param apiName
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T queryById(Context context, String appId, String id, String apiName, Class<T> clazz) {
        if (!StringUtils.hasText(id)) {
            return null;
        }
        Map<String, Object> param = Maps.newHashMap();
        param.put(DbFieldConstant.ID, id);
        Map<String, Object> data = this.queryByMap(context, appId, param, apiName);
        if (Objects.nonNull(data)) {
            return BeanConvertUtil.mapToBean(data, clazz);
        }
        return null;
    }

    public Map<String, Object> queryByMap(Context context, String appId, Map<String, Object> params, String apiName) {
        InvokeApiRequest invokeApiRequest = new InvokeApiRequest();
        invokeApiRequest.setAppId(appId);
        invokeApiRequest.setName(apiName);
        if (!CollectionUtils.isEmpty(params)) {
            Map<String, Object> where = Maps.newHashMap();
            for (Map.Entry<String, Object> item : params.entrySet()) {
                if (StringUtils.hasText(item.getKey()) && !ObjectUtils.isEmpty(item.getValue())) {
                    where.put(item.getKey(), item.getValue());
                }
            }
            invokeApiRequest.setParams(where);
        }

        //系统API
        if (Character.isUpperCase(apiName.charAt(0))) {
            Result<Map<String, Object>> result = sysApiFacadeService.queryById(context, invokeApiRequest);
            if (!result.isSuccess()) {
                SAL_LOG.error("DATA_API={}, result={}, param={}", apiName, result.getMessage(), invokeApiRequest);
                throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
            }

            return result.getData();
        }

        //自定义API
        Result<QueryResult> result = sysApiFacadeService.queryByCustomerApi(context, invokeApiRequest);
        if (!result.isSuccess()) {
            SAL_LOG.error("DATA_API={}, result={}, param={}", apiName, result.getMessage(), invokeApiRequest);
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }

        if (Objects.isNull(result.getData()) || CollectionUtils.isEmpty(result.getData().getData())) {
            return null;
        }
        return result.getData().getData().get(0);
    }

    public Boolean updateByMap(Context context, String appId, Map<String, Object> params, String apiName) {
        if (CollectionUtils.isEmpty(params)) {
            return Boolean.FALSE;
        }
        InvokeApiRequest invokeApiRequest = new InvokeApiRequest();
        invokeApiRequest.setAppId(appId);
        invokeApiRequest.setName(apiName);
        Map<String, Object> where = Maps.newHashMap();
        for (Map.Entry<String, Object> item : params.entrySet()) {
            if (StringUtils.hasText(item.getKey())) {
                where.put(item.getKey(), item.getValue());
            }
        }
        invokeApiRequest.setParams(where);

        //系统API
        if (Character.isUpperCase(apiName.charAt(0))) {
            Result<Boolean> result = sysApiFacadeService.updateBySysApi(context, invokeApiRequest);
            if (!result.isSuccess()) {
                SAL_LOG.error("DATA_API={}, result={}, param={}", apiName, result.getMessage(), invokeApiRequest);
                throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
            }

            return result.getData();
        }

        //自定义API
        Result<QueryResult> result = sysApiFacadeService.updateByCustomerApi(context, invokeApiRequest);
        if (!result.isSuccess()) {
            SAL_LOG.error("DATA_API={}, result={}, param={}", apiName, result.getMessage(), invokeApiRequest);
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }

        return result.isSuccess();
    }

    public Boolean deleteByMap(Context context, String appId, Map<String, Object> params, String apiName) throws BizException {
        if (CollectionUtils.isEmpty(params)) {
            return Boolean.FALSE;
        }
        InvokeApiRequest invokeApiRequest = new InvokeApiRequest();
        invokeApiRequest.setAppId(appId);
        invokeApiRequest.setName(apiName);
        Map<String, Object> where = Maps.newHashMap();
        for (Map.Entry<String, Object> item : params.entrySet()) {
            if (StringUtils.hasText(item.getKey()) && !ObjectUtils.isEmpty(item.getValue())) {
                where.put(item.getKey(), item.getValue());
            }
        }
        invokeApiRequest.setParams(where);

        //系统API
        if (Character.isUpperCase(apiName.charAt(0))) {
            Result<Boolean> result = sysApiFacadeService.deleteBySysApi(context, invokeApiRequest);
            if (!result.isSuccess()) {
                SAL_LOG.error("DATA_API={}, result={}, param={}", apiName, result.getMessage(), invokeApiRequest);
                throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
            }

            return result.getData();
        }

        //自定义API
        Result<QueryResult> result = sysApiFacadeService.deleteByCustomerApi(context, invokeApiRequest);
        if (!result.isSuccess()) {
            SAL_LOG.error("DATA_API={}, result={}, param={}", apiName, result.getMessage(), invokeApiRequest);
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }

        return result.isSuccess();
    }

    public Boolean deleteById(Context context, String appId, String id, String apiName) throws BizException {
        if (!StringUtils.hasText(id)) {
            SAL_LOG.warn("id is null");
            return Boolean.FALSE;
        }
        Map<String, Object> where = Maps.newHashMap();
        where.put(DbFieldConstant.ID, id);
        return this.deleteByMap(context, appId, where, apiName);
    }

    public Boolean batchUpdateById(Context context, String appId, String version, String apiName, List<Map<String, Object>> params) {
        if (CollectionUtils.isEmpty(params)) {
            return true;
        }
        ContextCheck.validParam(appId, CommonConstant.APP_ID);
        if (params.size() > BATCH_MAX_SIZE) {
            // 分页去批处理
            int offsetSize = BATCH_MAX_SIZE;
            int size = params.size();
            int number = size / (offsetSize + 1) + 1;
            for (int index = 0; index < number; ++index) {
                List<Map<String, Object>> subList;
                if (index == number - 1) {
                    // 最后一次分割
                    subList = params.subList(index * offsetSize, size);
                } else {
                    subList = params.subList(index * offsetSize, (index + 1) * offsetSize);
                }
                batchUpdateById(context, appId, version, apiName, subList);
            }
            return true;
        }

        BatchCreateRequest batchCreateRequest = new BatchCreateRequest();
        batchCreateRequest.setAppId(appId);
        batchCreateRequest.setVersion(version);
        batchCreateRequest.setName(apiName);
        batchCreateRequest.setBatchParams(params);

        return batchUpdateBySysApi(context, batchCreateRequest);
    }
}
