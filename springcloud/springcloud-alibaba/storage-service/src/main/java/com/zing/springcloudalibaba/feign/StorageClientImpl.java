package com.zing.springcloudalibaba.feign;

import com.zing.springcloudalibaba.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Zing
 */
@RestController
public class StorageClientImpl implements StorageClient {

    @Autowired
    private StorageService storageService;

    @Override
    public int reduceStock(Long productId, Integer count) {
        return storageService.reduce(productId, count);
    }
}
