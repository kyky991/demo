package com.zing.bigdata.hos.server.mapper;

import com.zing.bigdata.hos.common.BucketModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BucketModelMapper {

    void addBucket(@Param("bucket") BucketModel bucketModel);

    int updateBucket(@Param("bucketName") String bucketName, @Param("detail") String detail);

    int deleteBucket(@Param("bucketName") String bucketName);

    BucketModel getBucket(@Param("bucketId") String bucketId);

    BucketModel getBucketByName(@Param("bucketName") String bucketName);

    List<BucketModel> getBucketByCreator(@Param("creator") String creator);

    List<BucketModel> getUserAuthorizedBuckets(@Param("token") String token);

}
