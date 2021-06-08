package com.zing.bigdata.hos.core.auth.mapper;

import com.zing.bigdata.hos.core.auth.model.TokenInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface TokenInfoMapper {

    public void addToken(@Param("token") TokenInfo tokenInfo);

    public void updateToken(@Param("token") String token, @Param("expireTime") int expireTime, @Param("isActive") int isActive);

    public void refreshToken(@Param("token") String token, @Param("refreshTime") Date refreshTime);

    public void deleteToken(@Param("token") String token);

    public TokenInfo getTokenInfo(@Param("token") String token);

    public List<TokenInfo> getTokenInfoList(@Param("creator") String creator);

}
