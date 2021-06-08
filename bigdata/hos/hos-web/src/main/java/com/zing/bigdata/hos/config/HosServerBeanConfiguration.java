package com.zing.bigdata.hos.config;

import com.zing.bigdata.hos.server.service.IHosStoreService;
import com.zing.bigdata.hos.server.service.impl.HdfsServiceImpl;
import com.zing.bigdata.hos.server.service.impl.HosStoreServiceImpl;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class HosServerBeanConfiguration {

    @Value("${hbase.zookeeper.quorum}")
    private String HBASE_ZOOKEEPER_QUORUM;

    @Value("${hbase.zookeeper.property.clientPort}")
    private String HBASE_ZOOKEEPER_PROPERTY_CLIENTPORT;

    // 获取hbase connection 注入bean
    @Bean
    public Connection getConnection() throws IOException {
        org.apache.hadoop.conf.Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", HBASE_ZOOKEEPER_QUORUM);
        configuration.set("hbase.zookeeper.property.clientPort", HBASE_ZOOKEEPER_PROPERTY_CLIENTPORT);
        return ConnectionFactory.createConnection(configuration);
    }

    @Bean(initMethod = "init")
    public HdfsServiceImpl hdfsService() throws Exception {
        return new HdfsServiceImpl();
    }

    @Bean
    public IHosStoreService hosStoreService(Connection connection, HdfsServiceImpl hdfsService) throws Exception {
        return new HosStoreServiceImpl(connection, hdfsService, HBASE_ZOOKEEPER_QUORUM);
    }

}
