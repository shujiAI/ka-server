package com.shujiai.ka.constant;

/**
 * 常量
 *
 * @author jiuyi
 */
public class KAConstant {

    public static final String TENANT_ID_KEY = "tenant_id";
    public static final String IDENTITY_ID_KEY = "identity_id";
    public final static String ONE_PAGE = "_NONE_PAGE";

    public final static String CUSTOM_API_PRE = "Custom";
    public final static String IDS = "ids";
    public final static String ID = "id";
    public final static String USER_ID = "userId";
    public static final String APP_ID_KEY = "appId";
    public static final String VERSION_KEY = "version";

    /**
     * parse from QueryResult
     */
    public static final String DATA = "data";
    public static final String COUNT = "count";
    public static final String EFFECTED_ROWS = "effectedRows";
    public final static String GMT_CREATE = "gmt_create";
    public final static String PAGE_SIZE_KEY = "pageSize";
    public final static String PAGE_NO_KEY = "pageNo";

    public final static int DEFAULT_PAGE_NO = 1;
    public final static int DEFAULT_PAGE_SIZE = 10;
    public final static int MAX_PAGE_SIZE = 1000;

    public final static String SPLIT_LINE = "/";


    public static final String SUCCESS = "success";


    /**
     * 每个项目的默认list id
     */
    public static final String DEFAULT_LIST_ID = "default_list_id";
    /**
     * metadata返回的触发unique index的异常code
     */
    public static final Integer DATABASE_UNIQUE_ERROR_CODE = 1004;
    /**
     * metadata返回的触发应用未发布的异常code
     */
    public static final Integer APP_UN_DEPLOYED = 1500;
    /**
     * metadata返回的触发应用api 不存在的异常code
     */
    public static final Integer APP_API_NOT_EXIST = 1072;
    /**
     * metadata返回的触发应用api 不存在的异常code （元数据字段校验失败）
     */
    public static final Integer APP_FIELD_CHECK_FALSE = 1016;
}
