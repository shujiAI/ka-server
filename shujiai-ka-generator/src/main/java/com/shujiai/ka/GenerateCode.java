package com.shujiai.ka;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 代码生成：快速生成
 * <a href = "https://baomidou.com/pages/981406/#%E6%95%B0%E6%8D%AE%E5%BA%93%E9%85%8D%E7%BD%AE-datasourceconfig">mybatis-plus generator</a>
 * </p>
 *
 * @author zhanghp
 * @date 2022-07-16 14:53
 */
public class GenerateCode {

    public static void main(String[] args) {
        generate();
    }

    /**
     * 代码生成
     */
    public static void generate() {
        String projectPath = System.getProperty("user.dir") + "/shujiai-ka-generator/src/main/java";
        System.out.println(projectPath);

        FastAutoGenerator.create("jdbc:mysql://192.168.88.135:3306/lumm?characterEncoding=UTF-8&useUnicode=true&useSSL=false&tinyInt1isBit=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai",
                        "root",
                        "root1234")

                // 全局配置
                .globalConfig(builder -> {
                    builder
                            .enableSwagger() // 是否启用swagger注解
                            .author("shujiai") // 作者名称
                            .dateType(DateType.ONLY_DATE) // 时间策略
                            .commentDate("yyyy-MM-dd HH:mm:ss") // 注释日期
                            .outputDir(projectPath) // 输出目录
                            .fileOverride(); // 覆盖已生成文件
//                            .disableOpenDir(); // 生成后禁止打开所生成的系统目录
                })

                // 包配置
                .packageConfig(builder -> {
                    builder
                            .parent("com.shujiai.ka") // 父包名
//                            .moduleName("generate") // 模块包名
                            .entity("entity") // 实体类包名
                            .service("service") // service包名
                            .serviceImpl("service.impl") // serviceImpl包名
                            .mapper("mapper") // mapper包名
                            .controller("controller") // controller包名
                            .other("model") // 自定义包名
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, projectPath + "/mappers")); // xml位置
                })

                // 策略配置
                .strategyConfig(builder -> {
                    builder
//                            .addTablePrefix("t_") // 增加过滤表前缀
//                            .addTableSuffix("_db") // 增加过滤表后缀
//                            .addFieldPrefix("t_") // 增加过滤字段前缀
//                            .addFieldSuffix("_field") // 增加过滤字段后缀
//                            .addInclude("label") // 表匹配
                            .addInclude("sys_tenant")

                            // Entity 策略配置
                            .entityBuilder()
                            .enableLombok() // 开启lombok
//                            .enableChainModel() // 链式
                            .enableRemoveIsPrefix() // 开启boolean类型字段移除is前缀
                            .enableTableFieldAnnotation() //开启生成实体时生成的字段注解
//                            .versionColumnName("version") // 乐观锁数据库字段
//                            .versionPropertyName("version") // 乐观锁实体类名称
                            .logicDeleteColumnName("is_deleted") // 逻辑删除数据库中字段名
                            .logicDeletePropertyName("is_deleted") // 逻辑删除实体类中的字段名
                            .naming(NamingStrategy.underline_to_camel) // 表名 下划线 -》 驼峰命名
                            .columnNaming(NamingStrategy.underline_to_camel) // 字段名 下划线 -》 驼峰命名
                            .idType(IdType.ASSIGN_ID) // 主键生成策略 雪花算法生成id
                            .formatFileName("%s") // Entity 文件名称
                            .addTableFills(new Column("gmt_create", FieldFill.INSERT)) // 表字段填充
                            .addTableFills(new Column("gmt_modified", FieldFill.INSERT_UPDATE)) // 表字段填充
//                            .enableColumnConstant()
//                            .enableActiveRecord()

                            // Controller 策略配置
                            .controllerBuilder()
                            .enableRestStyle() // 开启@RestController
                            .formatFileName("%sController") // Controller 文件名称

                            // Service 策略配置
                            .serviceBuilder()
                            .formatServiceFileName("%sService") // Service 文件名称
                            .formatServiceImplFileName("%sServiceImpl") // ServiceImpl 文件名称

                            // Mapper 策略配置
                            .mapperBuilder()
                            .enableMapperAnnotation() // 开启@Mapper
                            .enableBaseColumnList() // 启用 columnList (通用查询结果列)
                            .enableBaseResultMap() // 启动resultMap
                            .formatMapperFileName("%sMapper") // Mapper 文件名称
                            .formatXmlFileName("%sMapper"); // Xml 文件名称
                })

                // 注入配置
                .injectionConfig(builder -> {

                    // tag::custom file
                    // 自定义vo，ro，qo等数据模型
                    Map<String, String> customFile = new HashMap<>();
//                    customFile.put(CustomEnum.VO.getModelSuffix(), CustomEnum.VO.getPath());
//                    customFile.put(CustomEnum.RO.getModelSuffix(), CustomEnum.RO.getPath());
//                    customFile.put(CustomEnum.QO.getModelSuffix(), CustomEnum.QO.getPath());
//                    // 自定义MapStruct
//                    customFile.put(CustomEnum.CONVERTER.getModelSuffix(), CustomEnum.CONVERTER.getPath());

                    customFile.put(CustomEnum.Request.getModelSuffix(), CustomEnum.Request.getPath());
                    customFile.put(CustomEnum.PageRequest.getModelSuffix(), CustomEnum.PageRequest.getPath());
                    customFile.put(CustomEnum.DubboService.getModelSuffix(), CustomEnum.DubboService.getPath());
                    customFile.put(CustomEnum.DubboServiceImpl.getModelSuffix(), CustomEnum.DubboServiceImpl.getPath());
                    customFile.put(CustomEnum.DTO.getModelSuffix(), CustomEnum.DTO.getPath());
                    // end::custom file

                    builder.customFile(customFile); // 自定义模板
                })
                .templateEngine(new EnhanceVelocityTemplateEngteteine())
                .execute();
    }


    /**
     * 代码生成器支持自定义[DTO\VO等]模版
     */
    public final static class EnhanceVelocityTemplateEngteteine extends VelocityTemplateEngine {
        @Override
        public Map<String, Object> getObjectMap(@NotNull ConfigBuilder config, @NotNull TableInfo tableInfo) {
            // 获取实体类名字
            String entityName = tableInfo.getEntityName();
            // 获取object map
            Map<String, Object> objectMap = super.getObjectMap(config, tableInfo);
            // 获取Other的盘符路径
            String otherPath = (String) ((Map<String, Object>) objectMap.get("package")).get("Other");
            // 自定义枚举
            List<String> modelList = CustomEnum.getModel();

            String modelName = entityName.replace("Sys","");
            // 循环
            modelList.forEach(it -> {
                // 转小写
                String var = it.toLowerCase();
                if("dubbo".equals(var)){
                    objectMap.put(var, otherPath + "." + var + "." + modelName + "Service");
                }else if("dubboimpl".equals(var)){
                    objectMap.put(var, otherPath + "." + var + "." + modelName + "ServiceImpl");
                }else if("pagerequest".equals(var)){
                    objectMap.put(var, otherPath + ".request." + entityName + it);
                }else{
                    // 存入object map
                    objectMap.put(var, otherPath + "." + var + "." + entityName + it);
                }

            });
//            // 自定义converter
//            objectMap.put("commonConverter", "com.zhanghp.common.converter.CommonConverter");
//            // converter utils
//            objectMap.put("converterUtil", "com.zhanghp.common.converter.utils.ConverterUtil");
//            // 分页
//            objectMap.put("pageParent", "com.zhanghp.common.model.PageParent");
//            // utils
//            objectMap.put("objectUtils", "com.zhanghp.common.utils.ObjectUtils");
//            objectMap.put("r", "com.zhanghp.common.response.R");
            return objectMap;
        }

        /**
         * 文件输出路径
         *
         * @param customFile 自定义文件map
         * @param tableInfo  表信息
         * @param objectMap  对象map
         */
        @Override
        protected void outputCustomFile(@NotNull Map<String, String> customFile, @NotNull TableInfo tableInfo, @NotNull Map<String, Object> objectMap) {
            // 获取实体类名字
            String entityName = tableInfo.getEntityName();
            // 获取other包盘符路径
            String otherPath = this.getPathInfo(OutputFile.other);

            String modelName = entityName.replace("Sys","") + "Service";
            String dubboPath = otherPath.substring(0,otherPath.lastIndexOf("\\") + 1) + "dubbo";
            // 输出自定义java模板
            customFile.forEach((key, value) -> {
                // 输出路径
                String fileName = otherPath + File.separator + key.toLowerCase() + File.separator + entityName + key + ".java";
                if("dubbo".equals(key)){
                    fileName = dubboPath + File.separator  + modelName  + ".java";
                }else if("dubboImpl".equals(key)){
                    fileName = dubboPath + File.separator  + modelName  + "Impl.java";
                }else if("PageRequest".equals(key)){
                    fileName = otherPath + File.separator + "request" + File.separator + entityName + key + ".java";
                }

                // 输出velocity的java模板
                this.outputFile(new File(fileName), objectMap, value);
            });
        }

    }
}
