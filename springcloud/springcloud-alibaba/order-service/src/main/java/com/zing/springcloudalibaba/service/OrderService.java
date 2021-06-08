package com.zing.springcloudalibaba.service;

import com.zing.springcloudalibaba.domain.Order;
import com.zing.springcloudalibaba.feign.AccountClient;
import com.zing.springcloudalibaba.feign.StorageClient;
import com.zing.springcloudalibaba.mapper.OrderMapper;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.shardingsphere.core.strategy.keygen.SnowflakeShardingKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Zing
 */
@Slf4j
@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private StorageClient storageClient;

    @Autowired
    private AccountClient accountClient;

    @Autowired
    private SnowflakeShardingKeyGenerator orderKeyGenerator;

    @GlobalTransactional(rollbackFor = Exception.class)
    public Order createOrder(Long productId, Integer count) {
        log.info("------->交易开始");

        Order order = new Order();
        order.setId((Long) orderKeyGenerator.generateKey());
        order.setName(RandomStringUtils.randomAlphanumeric(20));
        order.setProductId(productId);
        order.setCount(count);
        order.setPoints(0);
        order.setCreateTime(new Date());
        orderMapper.insert(order);

        // 远程方法 扣减库存
        storageClient.reduceStock(productId, count);

        log.info("------->交易结束");

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("异常");
    }
}
