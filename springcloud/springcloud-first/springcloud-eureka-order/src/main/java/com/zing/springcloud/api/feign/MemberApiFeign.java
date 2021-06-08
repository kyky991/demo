package com.zing.springcloud.api.feign;

import com.zing.springcloud.api.fallback.HystrixMemberApiFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Zing
 * @date 2019-11-14
 */
@FeignClient(name = "app-member", fallback = HystrixMemberApiFallback.class)
public interface MemberApiFeign {

    @RequestMapping("/member/{ms}")
    public String member(@PathVariable("ms") Integer ms);

}
