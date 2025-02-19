package com.shujiai.ka.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Date;

@Configuration
@MapperScan("com.shujiai.ka.mapper")
public class MybatisPlusConfig {
    @Value("${default.datasource.url}")
    private String url;
    @Value("${default.datasource.username}")
    private String username;
    @Value("${default.datasource.password}")
    private String password;
    @Value("${default.datasource.driver-class-name}")
    private String className;

    @Bean("dataSource")
    public DataSource sysDataSource() {
        //初始化默认数据源
        DruidDataSource sysDataSource = new DruidDataSource();
        sysDataSource.setUrl(url);
//        sysDataSource.setUsername(EncryptUtils.decrypt(username));
//        sysDataSource.setPassword(EncryptUtils.decrypt(password));
        sysDataSource.setUsername(username);
        sysDataSource.setPassword(password);
        sysDataSource.setDriverClassName(className);
        sysDataSource.setName("sysDataSource");
        return sysDataSource;
    }

    @Bean
    public MetaObjectHandler gmtCreateModifiedHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                this.setFieldValByName("gmtCreate", new Date(), metaObject);
                this.setFieldValByName("gmtModified", new Date(), metaObject);
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                this.setFieldValByName("gmtModified", new Date(), metaObject);
            }
        };
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
//        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {
//            @Override
//            public Expression getTenantId() {
//                return new StringValue(TenantContext.getTenant());
//            }
//
//            @Override
//            public String getTenantIdColumn() {
//                return TENANT_ID_COLUMN;
//            }
//
//            // 这是 default 方法,默认返回 false 表示所有表都需要拼多租户条件
//            @Override
//            public boolean ignoreTable(String tableName) {
//                return IGNORE_TENANT_ID_TABLES.contains(tableName);
//            }
//        }));
        // 如果用了分页插件注意先 add TenantLineInnerInterceptor 再 add PaginationInnerInterceptor
        // 用了分页插件必须设置 MybatisConfiguration#useDeprecatedExecutor = false
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        paginationInnerInterceptor.setMaxLimit(-1L);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        return interceptor;
    }
}
