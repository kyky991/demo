package com.zing.springcloudalibaba.feign;

import com.zing.springcloudalibaba.domain.query.LoginBody;
import com.zing.springcloudalibaba.domain.dto.UserDTO;
import com.zing.springcloudalibaba.domain.query.TimeQuery;
import com.zing.springcloudalibaba.feign.fallback.UserFeignFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

/**
 * @author Zing
 * @date 2020-07-11
 */
//@FeignClient(name = "user-service", configuration = UserFeignConfig.class)
@FeignClient(name = "user-service",
//        fallback = UserFeignFallback.class
        fallbackFactory = UserFeignFallbackFactory.class
)
public interface UserFeign {

    @GetMapping("/first")
    String first(@RequestParam Integer timeout);

    @GetMapping("/first")
    String first(@SpringQueryMap TimeQuery query);

    @PostMapping("/login")
    UserDTO login(@RequestBody LoginBody body);

    @GetMapping("/user/{id}")
    UserDTO user(@PathVariable("id") Integer id);
}
