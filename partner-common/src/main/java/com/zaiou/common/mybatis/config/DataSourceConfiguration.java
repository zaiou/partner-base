package com.zaiou.common.mybatis.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * @Description: 数据源配置
 * @auther: LB 2018/9/21 10:58
 * @modify: LB 2018/9/21 10:58
 */
@Configuration
@ConditionalOnProperty(name = "spring.datasource.enable", havingValue = "true", matchIfMissing = true)
@Slf4j
public class DataSourceConfiguration {

    /**
     * 数据库连接池类型
     */
    @Value("${spring.datasource.pool.type}")
    private Class<? extends DataSource> dataSourceType;

    @Bean(name = "writeDataSource", destroyMethod = "close", initMethod = "init")
    @ConfigurationProperties(prefix = "spring.datasource.write")
    @Primary
    public DataSource writeDataSource() {
        log.info("-------------------- 初始化wriite数据源 ---------------------");
        return DataSourceBuilder.create().type(dataSourceType).build();
    }

    @Bean(name = "readDataSource", destroyMethod = "close", initMethod = "init")
    @ConfigurationProperties(prefix = "spring.datasource.read")
    public DataSource readDataSource() {
        log.info("-------------------- Read Data Source init ---------------------");
        return DataSourceBuilder.create().type(dataSourceType).build();
    }

}
