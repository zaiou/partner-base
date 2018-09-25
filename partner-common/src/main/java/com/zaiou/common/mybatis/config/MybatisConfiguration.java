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
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * @Description: mybatis配置
 * @auther: LB 2018/9/21 10:52
 * @modify: LB 2018/9/21 10:52
 */
@Slf4j
@Configuration
@Import({ DataSourceConfiguration.class })
@ConditionalOnProperty(name = "spring.datasource.enable", havingValue = "true", matchIfMissing = true)
@MapperScan(basePackages = "com.zaiou.**.mybatis.mapper", sqlSessionFactoryRef = "writeSqlSessionFactory")
@Primary
public class MybatisConfiguration {

    @Resource(name = "writeDataSource")
    private DataSource writeDataSource;

    @Value("${mybatis.mapper-locations}")
    private String mapper_locations;

    @Value("${mybatis.config-location}")
    private String config_location;

    @Value("${mybatis.type-aliases-package}")
    private String typeAliasesPackage;

    @Bean("writeSqlSessionFactory")
    @Primary
    public SqlSessionFactory writeSqlSessionFactory() throws Exception {
        log.info("-----------------Write sqlSessionFactory init-----------------");
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(writeDataSource);
        sqlSessionFactoryBean.setTypeAliasesPackage(typeAliasesPackage);

        PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setConfigLocation(pathMatchingResourcePatternResolver.getResource(config_location));
        sqlSessionFactoryBean.setMapperLocations(pathMatchingResourcePatternResolver.getResources(mapper_locations));

        return sqlSessionFactoryBean.getObject();
    }

}
