package com.zing.springcloudalibaba.feign.fallback;

import com.zing.springcloudalibaba.domain.query.LoginBody;
import com.zing.springcloudalibaba.domain.dto.UserDTO;
import com.zing.springcloudalibaba.domain.query.TimeQuery;
import com.zing.springcloudalibaba.feign.UserFeign;
import org.springframework.stereotype.Component;

/**
 * @author Zing
 * @date 2020-07-12
 */
@Component
public class UserFeignFallback implements UserFeign {
    @Override
    public String first(Integer timeout) {
        return null;
    }

    @Override
    public String first(TimeQuery query) {
        return null;
    }

    @Override
    public UserDTO login(LoginBody body) {
        return new UserDTO("降级用户");
    }

    @Override
    public UserDTO user(Integer id) {
        return new UserDTO("降级用户");
    }
}
