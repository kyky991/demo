package com.zing.springcloudalibaba.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Zing
 */
@FeignClient(name = "storage-service")
public interface StorageClient {

    /**
     * 减库存
     *
     * @param productId 商品id
     * @param count     数量
     * @return 结果
     */
    @GetMapping("/feign/storage/reduceStock")
    int reduceStock(@RequestParam("productId") Long productId, @RequestParam("count") Integer count);

}
