package com.zaiou.common.mybatis.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * @Description:
 * @auther: LB 2018/9/25 11:26
 * @modify: LB 2018/9/25 11:26
 */
@Slf4j
@Configuration
@Import({ DataSourceConfiguration.class })
@ConditionalOnProperty(name = "spring.datasource.enable", havingValue = "true", matchIfMissing = true)
@MapperScan(basePackages = "com.zaiou.**.mybatis.mapper.read", sqlSessionFactoryRef = "readSqlSessionFactory")
public class MybatisReadConfiguration {

    @Resource(name = "readDataSource")
    private DataSource readDataSource;

    @Value("${mybatis.read.mapper-locations}")
    private String mapper_locations;

    @Value("${mybatis.config-location}")
    private String config_location;

    @Value("${mybatis.type-aliases-package}")
    private String typeAliasesPackage;

    @Value("${mybatis.read.mapper-locations.enable:false}")
    private boolean enable;

    @Bean("readSqlSessionFactory")
    public SqlSessionFactory readSqlSessionFactory() throws Exception {
        log.info("-----------------Read sqlSessionFactory init-----------------");
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(readDataSource);
        sqlSessionFactoryBean.setTypeAliasesPackage(typeAliasesPackage);

        PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setConfigLocation(pathMatchingResourcePatternResolver.getResource(config_location));
        if (enable) {
            sqlSessionFactoryBean
                    .setMapperLocations(pathMatchingResourcePatternResolver.getResources(mapper_locations));
        }
        return sqlSessionFactoryBean.getObject();
    }

}
