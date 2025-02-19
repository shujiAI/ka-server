package com.shujiai.ka.dubbo.manager;

import com.shujiai.ka.model.ListRecordsResult;
import com.shujiai.ka.util.PageResult;
import com.shujiai.base.context.Context;
import com.shujiai.metadata.facade.dto.SortItem;
import com.shujiai.metadata.facade.dto.sql.QueryResult;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * ApiInvokeManager
 *
 * @author hxh
 * @date 2023/11/4
 * @time 09:54:53
 */
public interface ApiInvokeManager {

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
    List<String> batchInsert(Context context, String appId, String version, String tableName, List<Map<String, Object>> params);

    /**
     * 批量修改
     *
     * @param context   上下文,包含租户、登录用户等
     * @param appId     应用ID
     * @param version   版本
     * @param tableName 表名
     * @param params    参数
     * @return {@link Boolean}
     */
    Boolean batchUpdateBySysApi(Context context, String appId, String version, String tableName, List<Map<String, Object>> params);

    /**
     * 批量创建或修改
     *
     * @param context   上下文,包含租户、登录用户等
     * @param appId     应用ID
     * @param version   版本
     * @param tableName 表名
     * @param params    参数
     * @return {@link Boolean}
     */
    Boolean batchCreateOrUpdateBySysApi(Context context, String appId, String version, String tableName, List<Map<String, Object>> params);

    /**
     * 执行查询的系统Api，根据主键id查询
     *
     * @param context context
     * @param appId   应用id
     * @param version 版本
     * @param id      主键id
     * @param apiName api名称
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    Map<String, Object> getById(Context context, String appId, String version, String id, String apiName);

    /**
     * 执行更新的系统Api，根据主键id更新
     *
     * @param context context
     * @param appId   应用id
     * @param version 版本
     * @param apiName api名称
     * @param params  更新参数（包含主键id字段）
     * @return {@link Boolean}
     */
    Boolean updateById(Context context, String appId, String version, String apiName, Map<String, Object> params);

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
    Boolean updateByIdIfDuplicateThrow(Context context, String appId, String version, String apiName, Map<String, Object> params, String throwMsg);

    /**
     * 执行新增的系统API
     *
     * @param context context
     * @param appId   应用id
     * @param version 版本
     * @param apiName api名称
     * @param params  参数
     * @return 主键id
     */
    String create(Context context, String appId, String version, String apiName, Map<String, Object> params);

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
    String createIfDuplicateThrow(Context context, String appId, String version, String apiName, Map<String, Object> params, String throwMsg);

    /**
     * 执行新增或更新的系统API
     *
     * @param context context
     * @param appId   应用id
     * @param version 版本
     * @param apiName api名称
     * @param params  参数里不包含id会执行create操作，包含id会执行update操作
     * @return 执行create操作时，Map里会返回id字段；执行update操作时会返回success字段
     */
    Map<String, Object> createOrUpdate(Context context, String appId, String version, String apiName, Map<String, Object> params);

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
    Map<String, Object> createOrUpdateIfDuplicateThrow(Context context, String appId, String version, String apiName, Map<String, Object> params,
                                                       String throwMsg);

    /**
     * 执行分页查询的系统API，查询多条数据
     *
     * @param context   context
     * @param appId     应用id
     * @param version   版本
     * @param apiName   api名称
     * @param pageNo    页号
     * @param pageSize  页大小
     * @param sortItems 页大小
     * @param params    过滤参数
     * @return 数据列表
     */
    ListRecordsResult listRecords(Context context, String appId, String version,
                                  String apiName, Long pageNo, Long pageSize, List<SortItem> sortItems,
                                  Map<String, Object> params);

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
    QueryResult listRecordsV2(Context context, String appId, String version,
                              String apiName, Long pageNo, Long pageSize, List<SortItem> sortItems,
                              Map<String, Object> params);

    /**
     * 执行删除的系统API
     *
     * @param context context
     * @param appId   应用id
     * @param version 版本
     * @param apiName api名称
     * @param id      主键id
     * @return {@link Boolean}
     */
    Boolean delete(Context context, String appId, String version, String apiName, String id);

    /**
     * 调用自定义API
     *
     * @param context 上下文,包含租户、登录用户等
     * @param appId   应用程序id
     * @param version 版本
     * @param apiName api名称
     * @param params  参数Map
     * @return {@link QueryResult}
     */
    QueryResult invokeCustomerApi(Context context, String appId, String version, String apiName, Map<String, Object> params);

    /**
     * 调用自定义更新api
     *
     * @param context 上下文,包含租户、登录用户等
     * @param appId   应用程序id
     * @param version 版本
     * @param apiName api名称
     * @param params  参数
     * @return {@link QueryResult}
     */
    QueryResult updateByCustomerApi(Context context, String appId, String version, String apiName, Map<String, Object> params);

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
    QueryResult updateByCustomerApiIfDuplicateThrow(Context context, String appId, String version, String apiName, Map<String, Object> params, String throwMsg);

    /**
     * 调用自定义删除api
     *
     * @param context 上下文,包含租户、登录用户等
     * @param appId   应用程序id
     * @param version 版本
     * @param apiName api名称
     * @param params  参数
     * @return {@link QueryResult}
     */
    QueryResult deleteByCustomerApi(Context context, String appId, String version, String apiName, Map<String, Object> params);

    /**
     * 调用自定义查询api
     *
     * @param context 上下文,包含租户、登录用户等
     * @param appId   应用程序id
     * @param version 版本
     * @param apiName api名称
     * @param params  参数
     * @return {@link QueryResult}
     */
    QueryResult queryByCustomerApi(Context context, String appId, String version, String apiName, Map<String, Object> params);

    /**
     * 批量创建
     *
     * @param context 上下文,包含租户、登录用户等
     * @param appId   应用ID
     * @param version 版本
     * @param apiName api名称
     * @param params  参数
     * @return {@link List}<{@link String}>
     */
    List<String> batchCreate(Context context, String appId, String version, String apiName, List<Map<String, Object>> params);

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
    List<String> batchCreateIfDuplicateThrow(Context context, String appId, String version, String apiName, List<Map<String, Object>> params, String throwMsg);

    /**
     * 不进行分页,查询全部数据,自行进行回调
     */
    <T> PageResult<T> listAllRecord(Map<String, Object> params, Class<T> clazz, Supplier<QueryResult> supplier);

    Map<String, Object> getById(Context context, String appId, String version, String id, String apiName, boolean relation);
}
