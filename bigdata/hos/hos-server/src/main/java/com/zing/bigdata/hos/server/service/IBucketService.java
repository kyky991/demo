package com.zing.bigdata.hos.server.service;

import com.zing.bigdata.hos.common.BucketModel;
import com.zing.bigdata.hos.core.user.model.UserInfo;

import java.util.List;

public interface IBucketService {

    public boolean addBucket(UserInfo userInfo, String bucketName, String detail);

    public boolean deleteBucket(String bucketName);

    public boolean updateBucket(String bucketName, String detail);

    public BucketModel getBucketById(String bucketId);

    public BucketModel getBucketByName(String bucketName);

    public List<BucketModel> getBucketByCreator(String creator);

    public List<BucketModel> getUserBuckets(String token);
}
