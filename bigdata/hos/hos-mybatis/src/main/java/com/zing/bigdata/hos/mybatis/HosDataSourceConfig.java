package com.zing.bigdata.hos.mybatis;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.zing.bigdata.hos.**.mapper", sqlSessionFactoryRef = "hosSqlSessionFactory")
public class HosDataSourceConfig {

    @Value("${mybatis.config-location}")
    String CONFIG_LOCATION;

    @Value("${mybatis.mapper-locations}")
    String MAPPER_LOCATIONS;

    @Value("${mybatis.type-aliases-package}")
    String TYPE_ALIASES_PACKAGE;

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public HikariDataSource phoenixDataSource() {
        return new HikariDataSource();
    }

    @Bean
    public SqlSessionFactory hosSqlSessionFactory(DataSource hosDataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(hosDataSource);

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setTypeAliasesPackage(TYPE_ALIASES_PACKAGE);
        sqlSessionFactoryBean.setConfigLocation(resolver.getResource(CONFIG_LOCATION));
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources(MAPPER_LOCATIONS));
        return sqlSessionFactoryBean.getObject();
    }
}
