package com.zing.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zing.user.domain.dto.UserDTO;
import com.zing.user.entity.User;
import com.zing.user.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @GetMapping("/page")
    public Page<User> page() {
        Page<User> page = userService.page(new Page<>(1, 10));
        log.info("{} {} {}", page.getRecords(), page.getTotal(), page.getPages());
        return page;
    }

    @GetMapping("/{id}")
    public UserDTO info(@PathVariable Long id) {
        UserDTO user = userService.get(id);
        int count = userService.count();
        log.info("{}", count);
        return user;
    }

    @PostMapping
    public int save(@RequestBody UserDTO dto) {
        dto.setGender(RandomUtils.nextInt(0, 3));
        dto.setName(RandomStringUtils.randomAlphanumeric(10));
        return userService.save(dto);
    }

}
