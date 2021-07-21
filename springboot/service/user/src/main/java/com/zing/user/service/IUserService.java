package com.zing.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zing.user.domain.dto.UserDTO;
import com.zing.user.entity.User;

public interface IUserService extends IService<User> {

    UserDTO get(Long id);

    int save(UserDTO dto);

    int count(String name);

}
