package com.zing.bigdata.hbase.phoenix.mapper;

import com.zing.bigdata.hbase.phoenix.model.UserInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface UserInfoMapper {

    public void addUser(@Param("user") UserInfo userInfo);

    public void deleteUser(@Param("userId") int userId);

    public UserInfo getUserById(@Param("userId") int userId);

    public UserInfo getUserByName(@Param("username") String username);

    public List<UserInfo> getUsers();

}
