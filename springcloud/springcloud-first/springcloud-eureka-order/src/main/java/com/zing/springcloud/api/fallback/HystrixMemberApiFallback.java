package com.zing.springcloud.api.fallback;

import com.zing.springcloud.api.feign.MemberApiFeign;
import org.springframework.stereotype.Component;

/**
 * @author Zing
 * @date 2019-11-15
 */
@Component
public class HystrixMemberApiFallback implements MemberApiFeign {

    @Override
    public String member(Integer ms) {
        return "hystrix fallback class:" + ms;
    }

}
