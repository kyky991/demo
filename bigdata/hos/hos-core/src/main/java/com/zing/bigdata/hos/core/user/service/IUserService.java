package com.zing.bigdata.hos.core.user.service;

import com.zing.bigdata.hos.core.user.model.UserInfo;

public interface IUserService {

    public boolean addUser(UserInfo userInfo);

    public boolean deleteUser(String userId);

    public boolean updateUser(String userId, String password, String detail);

    public UserInfo getUserById(String userId);

    public UserInfo getUserByName(String username);

    public UserInfo checkPassword(String username, String password);

}
