package com.shujiai.ka;

/**
 * SysMetaApiUtil
 * 生成系统api包里面的Constant类:
 *
 * @author hxh
 * @date 2023/11/4
 * @time 20:15:18
 */
public class SysMetaApiUtil {

    public static String getSysMetaApiNameBy(String tableName, SysApiType type) {
        if (SysApiType.CREATE_OR_UPDATE.equals(type)) {
            String api = "CreateORUpdate" + tableName;
            System.out.println("public static final String SYS_CREATE_OR_UPDATE = \""+api+"\";");
        } else if (SysApiType.CREATE.equals(type)) {
            String api =  "Create" + tableName;
            System.out.println("public static final String SYS_CREATE = \""+api+"\";");
        } else if (SysApiType.DELETE.equals(type)) {
            String api =  "Delete" + tableName;
            System.out.println("public static final String SYS_DELETE = \""+api+"\";");
        } else if (SysApiType.GET.equals(type)) {
            String api =  "Get" + tableName + "V2";
            System.out.println("public static final String SYS_GET = \""+api+"\";");
        } else if (SysApiType.LIST.equals(type)) {
            String api =  "List" + tableName + "Records";
            System.out.println("public static final String SYS_LIST_RECORDS = \""+api+"\";");
        } else if (SysApiType.SEARCH.equals(type)) {
            String api =  "Search" + tableName;
            System.out.println("public static final String SYS_SEARCH = \""+api+"\";");
        } else if (SysApiType.UPDATE.equals(type)) {
            String api =  "Update" + tableName;
            System.out.println("public static final String SYS_UPDATE = \""+api+"\";");
        } else {
            return null;
        }

        return null;
    }

    public enum SysApiType {
        /**
         * 创建或更新
         */
        CREATE_OR_UPDATE,
        /**
         * 创建
         */
        CREATE,
        /**
         * 查询
         */
        GET,
        /**
         * 删除
         */
        DELETE,
        /**
         * 列表
         */
        LIST,
        /**
         * 搜索
         */
        SEARCH,
        /**
         * 更新
         */
        UPDATE,
    }
    public static void main(String[] args) {
        String table = "pr_exam_apply_plan";
        String tableName = underscoreToCamelCase(table);
        System.out.println(tableName+"Api.java");
        System.out.println();
        System.out.println("public static final String META_TABLE_NAME = \""+tableName+"\";");
        System.out.println("//系统api");
        for (SysApiType type : SysApiType.values()) {
            getSysMetaApiNameBy(tableName, type);
        }
        System.out.println("//自定义api");
    }

    private static String underscoreToCamelCase(String underscoreName) {
        if (underscoreName == null || underscoreName.isEmpty()) {
            return underscoreName;
        }

        StringBuilder camelCaseName = new StringBuilder();
        boolean nextUpperCase = false;

        for (int i = 0; i < underscoreName.length(); i++) {
            char c = underscoreName.charAt(i);

            if (c == '_') {
                nextUpperCase = true;
            } else if (nextUpperCase) {
                camelCaseName.append(Character.toUpperCase(c));
                nextUpperCase = false;
            } else {
                if (camelCaseName.length() == 0) {
                    // 对于第一个字符，总是大写
                    camelCaseName.append(Character.toUpperCase(c));
                } else {
                    camelCaseName.append(c);
                }
            }
        }

        return camelCaseName.toString();
    }
}
