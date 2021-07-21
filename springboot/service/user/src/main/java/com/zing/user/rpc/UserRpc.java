package com.zing.user.rpc;

import com.zing.user.domain.dto.UserDTO;
import com.zing.user.entity.User;
import com.zing.user.exception.UserException;
import com.zing.user.service.IUserService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserRpc implements IUserRpc {

    @Autowired
    private IUserService userService;

    @Override
    public UserDTO get(Long id) {
        return userService.get(id);
    }

    @Override
    public List<UserDTO> list(List<Long> ids) {
        List<User> list = userService.listByIds(ids);

        List<UserDTO> results = new ArrayList<>();
        for (User user : list) {
            UserDTO dto = new UserDTO();
            BeanUtils.copyProperties(user, dto);
            results.add(dto);
        }
        if (true) {
            throw new UserException("测试异常处理 - [user rpc]");
        }
        return results;
    }

}
