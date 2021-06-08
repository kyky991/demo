package com.zing.bigdata.hos.core.auth.service.impl;

import com.zing.bigdata.hos.core.auth.mapper.ServiceAuthMapper;
import com.zing.bigdata.hos.core.auth.mapper.TokenInfoMapper;
import com.zing.bigdata.hos.core.auth.model.ServiceAuth;
import com.zing.bigdata.hos.core.auth.model.TokenInfo;
import com.zing.bigdata.hos.core.auth.service.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class AuthServiceImpl implements IAuthService {

    @Autowired
    private TokenInfoMapper tokenInfoMapper;

    @Autowired
    private ServiceAuthMapper serviceAuthMapper;

    @Override
    public boolean addAuth(ServiceAuth auth) {
        serviceAuthMapper.addAuth(auth);
        return true;
    }

    @Override
    public boolean deleteAuth(String bucketName, String token) {
        serviceAuthMapper.deleteAuth(bucketName, token);
        return true;
    }

    @Override
    public boolean deleteAuthByBucket(String bucketName) {
        serviceAuthMapper.deleteAuthByBucket(bucketName);
        return true;
    }

    @Override
    public boolean deleteAuthByToken(String token) {
        serviceAuthMapper.deleteAuthByToken(token);
        return true;
    }

    @Override
    public ServiceAuth getServiceAuth(String bucketName, String token) {
        return serviceAuthMapper.getAuth(bucketName, token);
    }

    @Override
    public boolean addToken(TokenInfo tokenInfo) {
        tokenInfoMapper.addToken(tokenInfo);
        return true;
    }


    @Override
    public boolean updateToken(String token, int expireTime, boolean isActive) {
        tokenInfoMapper.updateToken(token, expireTime, isActive ? 1 : 0);
        return true;
    }

    @Override
    public boolean refreshToken(String token) {
        tokenInfoMapper.refreshToken(token, new Date());
        return true;
    }

    @Override
    public boolean deleteToken(String token) {
        tokenInfoMapper.deleteToken(token);
        //TODO delete auth
        serviceAuthMapper.deleteAuthByToken(token);
        return true;
    }

    @Override
    public boolean checkToken(String token) {
        TokenInfo tokenInfo = tokenInfoMapper.getTokenInfo(token);
        if (tokenInfo == null) {
            return false;
        }

        if (tokenInfo.isActive()) {
            Date nowDate = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(tokenInfo.getRefreshTime());
            calendar.add(Calendar.DATE, tokenInfo.getExpireTime());
            return nowDate.before(calendar.getTime());
        }

        return false;
    }

    @Override
    public TokenInfo getTokenInfo(String token) {
        return tokenInfoMapper.getTokenInfo(token);
    }

    @Override
    public List<TokenInfo> getTokenInfos(String creator) {
        return tokenInfoMapper.getTokenInfoList(creator);
    }
}
