package com.zing.bigdata.hos.core.user.mapper;

import com.zing.bigdata.hos.core.user.model.UserInfo;
import org.apache.ibatis.annotations.Param;

public interface UserInfoMapper {

    public void addUser(@Param("userInfo") UserInfo userInfo);

    public void deleteUser(@Param("userId") String userId);

    public int updateUser(@Param("userId") String userId, @Param("password") String password, @Param("detail") String detail);

    public UserInfo getUserById(@Param("userId") String userId);

    public UserInfo getUserByName(@Param("username") String username);

    public UserInfo checkPassword(@Param("username") String username, @Param("password") String password);

}
