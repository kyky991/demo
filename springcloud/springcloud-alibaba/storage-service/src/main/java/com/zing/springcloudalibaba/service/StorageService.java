package com.zing.springcloudalibaba.service;

import com.zing.springcloudalibaba.feign.AccountClient;
import com.zing.springcloudalibaba.mapper.StorageMapper;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Zing
 */
@Slf4j
@Service
public class StorageService {

    @Autowired
    private StorageMapper storageMapper;

    @Autowired
    private AccountClient accountClient;

    @GlobalTransactional(rollbackFor = Exception.class)
    public int reduce(Long productId, Integer count) {
        log.info("------->扣减库存开始");
        int stock = storageMapper.reduceStock(productId, count);
        log.info("------->扣减库存结束");

        int r = accountClient.reduce(1L, 10L);

        log.info("account: {}", r);

        if (stock == 0) {
            throw new RuntimeException("异常");
        }
        return stock;
    }
}
