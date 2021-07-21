package com.zing.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zing.user.entity.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper extends BaseMapper<User> {

    int count(@Param("name") String name);

}
