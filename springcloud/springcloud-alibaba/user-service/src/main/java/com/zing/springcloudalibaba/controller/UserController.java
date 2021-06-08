package com.zing.springcloudalibaba.controller;

import com.zing.springcloudalibaba.domain.UserInfo;
import com.zing.springcloudalibaba.domain.dto.UserDTO;
import com.zing.springcloudalibaba.domain.query.LoginBody;
import com.zing.springcloudalibaba.mapper.UserInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Zing
 * @date 2020-07-11
 */
@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @PostMapping("/login")
    public UserDTO login(@RequestBody LoginBody body) {
        log.info(body.toString());
        return new UserDTO(body.getUsername());
    }

    @GetMapping("/user/{id}")
    public UserDTO user(@PathVariable Integer id) {
        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(id);
        if (userInfo == null) {
            return null;
        }
        return new UserDTO(userInfo.getUsername());
    }
}
