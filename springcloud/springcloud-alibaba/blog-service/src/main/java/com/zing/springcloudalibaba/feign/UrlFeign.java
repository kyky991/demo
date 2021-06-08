package com.zing.springcloudalibaba.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Zing
 * @date 2020-07-11
 */
@FeignClient(name = "${user-service-feign.name}", url = "${user-service-feign.url}")
public interface UrlFeign {

    @GetMapping("/first")
    String first(@RequestParam Integer timeout);

}
