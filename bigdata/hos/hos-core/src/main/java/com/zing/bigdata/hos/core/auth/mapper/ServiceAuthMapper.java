package com.zing.bigdata.hos.core.auth.mapper;

import com.zing.bigdata.hos.core.auth.model.ServiceAuth;
import org.apache.ibatis.annotations.Param;

public interface ServiceAuthMapper {

    public void addAuth(@Param("auth") ServiceAuth auth);

    public void deleteAuth(@Param("bucket") String bucketName, @Param("token") String token);

    public void deleteAuthByToken(@Param("token") String token);

    public void deleteAuthByBucket(@Param("bucket") String bucketName);

    public ServiceAuth getAuth(@Param("bucket") String bucketName, @Param("token") String token);


}
