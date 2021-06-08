package com.zing.springcloudalibaba.feign.fallback;

import com.zing.springcloudalibaba.domain.query.LoginBody;
import com.zing.springcloudalibaba.domain.dto.UserDTO;
import com.zing.springcloudalibaba.domain.query.TimeQuery;
import com.zing.springcloudalibaba.feign.UserFeign;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Zing
 * @date 2020-07-12
 */
@Slf4j
@Component
public class UserFeignFallbackFactory implements FallbackFactory<UserFeign> {
    @Override
    public UserFeign create(Throwable throwable) {
        return new UserFeign() {
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
                log.warn(throwable.toString());
                return new UserDTO("降级用户");
            }

            @Override
            public UserDTO user(Integer id) {
                log.warn(throwable.toString());
                return new UserDTO("降级用户");
            }
        };
    }

}
