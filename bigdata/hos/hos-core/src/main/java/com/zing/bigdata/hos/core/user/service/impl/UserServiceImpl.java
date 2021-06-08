package com.zing.bigdata.hos.core.user.service.impl;

import com.google.common.base.Strings;
import com.zing.bigdata.hos.core.auth.model.TokenInfo;
import com.zing.bigdata.hos.core.auth.service.IAuthService;
import com.zing.bigdata.hos.core.user.mapper.UserInfoMapper;
import com.zing.bigdata.hos.core.user.model.CoreUtils;
import com.zing.bigdata.hos.core.user.model.UserInfo;
import com.zing.bigdata.hos.core.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class UserServiceImpl implements IUserService {

    //set expireTime is better
    private static final long REFRESH_TIME = 4670409600000L;
    private static final int EXPIRE_TIME = 7;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    IAuthService authService;

    @Override
    public boolean addUser(UserInfo userInfo) {
        userInfoMapper.addUser(userInfo);

        //TODO add token
        Date date = new Date();
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setToken(userInfo.getUserId());
        tokenInfo.setActive(true);
        tokenInfo.setExpireTime(EXPIRE_TIME);
        tokenInfo.setRefreshTime(date);
        tokenInfo.setCreator(CoreUtils.SYSTEM_USER);
        tokenInfo.setCreateTime(date);
        authService.addToken(tokenInfo);
        return true;
    }

    @Override
    public boolean deleteUser(String userId) {
        userInfoMapper.deleteUser(userId);
        //TODO delete token
        authService.deleteToken(userId);
        authService.deleteAuthByToken(userId);
        return true;
    }

    @Override
    public boolean updateUser(String userId, String password, String detail) {
        userInfoMapper.updateUser(userId, Strings.isNullOrEmpty(password) ? null : CoreUtils.getMd5Password(password), Strings.emptyToNull(detail));
        return true;
    }

    @Override
    public UserInfo getUserById(String userId) {
        return userInfoMapper.getUserById(userId);
    }

    @Override
    public UserInfo getUserByName(String username) {
        return userInfoMapper.getUserByName(username);
    }

    @Override
    public UserInfo checkPassword(String username, String password) {
        return userInfoMapper.checkPassword(username, password);
    }
}
