package com.zing.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zing.user.domain.dto.UserDTO;
import com.zing.user.entity.User;
import com.zing.user.mapper.UserMapper;
import com.zing.user.service.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDTO get(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return null;
        }
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }

    @Override
    public int save(UserDTO dto) {
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        return userMapper.insert(user);
    }

    @Override
    public int count(String name) {
        return userMapper.count(name);
    }
}
