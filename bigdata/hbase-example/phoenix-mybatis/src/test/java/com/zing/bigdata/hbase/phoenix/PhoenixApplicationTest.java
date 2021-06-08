package com.zing.bigdata.hbase.phoenix;

import com.zing.bigdata.hbase.phoenix.mapper.UserInfoMapper;
import com.zing.bigdata.hbase.phoenix.model.UserInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {PhoenixApplication.class})
public class PhoenixApplicationTest {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Test
    public void addUser() {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(3);
        userInfo.setName("Jerry");
        userInfoMapper.addUser(userInfo);
    }

    @Test
    public void getUserById() {
        UserInfo userInfo = userInfoMapper.getUserById(1);
        System.out.println(String.format("id=%s;name=%s", userInfo.getId(), userInfo.getName()));
    }

    @Test
    public void getUserByName() {
        UserInfo userInfo = userInfoMapper.getUserByName("Jerry");
        System.out.println(String.format("id=%s;name=%s", userInfo.getId(), userInfo.getName()));
    }

    @Test
    public void deleteUser() {
        userInfoMapper.deleteUser(1);

        List<UserInfo> userInfos = userInfoMapper.getUsers();
        for (UserInfo userInfo : userInfos) {
            System.out.println(String.format("id=%s;name=%s", userInfo.getId(), userInfo.getName()));
        }
    }
}