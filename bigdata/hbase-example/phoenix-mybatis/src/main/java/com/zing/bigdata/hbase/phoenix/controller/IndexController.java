package com.zing.bigdata.hbase.phoenix.controller;

import com.zing.bigdata.hbase.phoenix.mapper.UserInfoMapper;
import com.zing.bigdata.hbase.phoenix.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class IndexController {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @RequestMapping("/addUser")
    public void addUser(UserInfo userInfo) {
        userInfoMapper.addUser(userInfo);
    }

    @RequestMapping("/getUserById")
    public UserInfo getUserById(Integer id) {
        UserInfo userInfo = userInfoMapper.getUserById(id);
        System.out.println(String.format("id=%s;name=%s", userInfo.getId(), userInfo.getName()));
        return userInfo;
    }

    @RequestMapping("/getUserByName")
    public UserInfo getUserByName(String name) {
        UserInfo userInfo = userInfoMapper.getUserByName(name);
        System.out.println(String.format("id=%s;name=%s", userInfo.getId(), userInfo.getName()));
        return userInfo;
    }

    @RequestMapping("/deleteUser")
    public void deleteUser(Integer id) {
        userInfoMapper.deleteUser(id);

        List<UserInfo> userInfos = userInfoMapper.getUsers();
        for (UserInfo userInfo : userInfos) {
            System.out.println(String.format("id=%s;name=%s", userInfo.getId(), userInfo.getName()));
        }
    }

}
