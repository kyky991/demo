package com.zing.springcloudalibaba.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Zing
 */
@FeignClient(name = "account-service")
public interface AccountClient {

    /**
     * 减
     *
     * @param userId userId
     * @param amount 金额
     * @return 结果
     */
    @PostMapping("/feign/account/reduce")
    int reduce(@RequestParam("userId") Long userId, @RequestParam("amount") Long amount);

}
