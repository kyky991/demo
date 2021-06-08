package com.zing.bigdata.hos.core.auth.service;

import com.zing.bigdata.hos.core.auth.model.ServiceAuth;
import com.zing.bigdata.hos.core.auth.model.TokenInfo;

import java.util.List;

public interface IAuthService {

    public boolean addAuth(ServiceAuth auth);

    public boolean deleteAuth(String bucketName, String token);

    public boolean deleteAuthByBucket(String bucketName);

    public boolean deleteAuthByToken(String token);

    public ServiceAuth getServiceAuth(String bucketName, String token);

    //-------------------- token ------------------------------
    public boolean addToken(TokenInfo tokenInfo);

    public boolean updateToken(String token, int expireTime, boolean isActive);

    public boolean refreshToken(String token);

    public boolean deleteToken(String token);

    public boolean checkToken(String token);

    public TokenInfo getTokenInfo(String token);

    public List<TokenInfo> getTokenInfos(String creator);

}
