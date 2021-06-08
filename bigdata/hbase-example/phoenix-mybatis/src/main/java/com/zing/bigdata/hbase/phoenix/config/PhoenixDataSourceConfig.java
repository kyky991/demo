package com.zing.bigdata.hbase.phoenix.config;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
public class PhoenixDataSourceConfig {

    @Value("${mybatis.config-location}")
     String CONFIG_LOCATION;

    @Value("${mybatis.mapper-locations}")
    String MAPPER_LOCATION;

    @Value("${mybatis.type-aliases-package}")
    String TYPE_ALIASES_PACKAGE;

//    @Bean
//    @Primary
//    @ConfigurationProperties(prefix = "spring.datasource")
//    public DataSourceProperties dataSourceProperties() {
//        return new DataSourceProperties();
//    }
//
//    @Bean
//    @ConfigurationProperties(prefix = "spring.datasource")
//    public DataSource phoenixDataSource(DataSourceProperties dataSourceProperties) {
//        return dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
//    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public HikariDataSource phoenixDataSource() {
        return new HikariDataSource();
    }

    @Bean
    public SqlSessionFactory phoenixSqlSessionFactory(DataSource phoenixDataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(phoenixDataSource);

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setConfigLocation(resolver.getResource(CONFIG_LOCATION));
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources(MAPPER_LOCATION));
        sqlSessionFactoryBean.setTypeAliasesPackage(TYPE_ALIASES_PACKAGE);
        return sqlSessionFactoryBean.getObject();
    }

}
