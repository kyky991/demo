package com.zing.bigdata.hos.server.service.impl;

import com.zing.bigdata.hos.common.BucketModel;
import com.zing.bigdata.hos.core.auth.model.ServiceAuth;
import com.zing.bigdata.hos.core.auth.service.IAuthService;
import com.zing.bigdata.hos.core.user.model.UserInfo;
import com.zing.bigdata.hos.server.mapper.BucketModelMapper;
import com.zing.bigdata.hos.server.service.IBucketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class BucketServiceImpl implements IBucketService {

    @Autowired
    private BucketModelMapper bucketModelMapper;

    @Autowired
    private IAuthService authService;

    @Override
    public boolean addBucket(UserInfo userInfo, String bucketName, String detail) {
        BucketModel bucketModel = new BucketModel(bucketName, userInfo.getUserId(), detail);
        bucketModelMapper.addBucket(bucketModel);

        // TODO add auth for bucket and user
        ServiceAuth serviceAuth = new ServiceAuth();
        serviceAuth.setAuthTime(new Date());
        serviceAuth.setTargetToken(userInfo.getUserId());
        serviceAuth.setBucketName(bucketName);
        authService.addAuth(serviceAuth);
        return true;
    }

    @Override
    public boolean deleteBucket(String bucketName) {
        bucketModelMapper.deleteBucket(bucketName);
        // TODO delete auth for bucket
        authService.deleteAuthByBucket(bucketName);
        return true;
    }

    @Override
    public boolean updateBucket(String bucketName, String detail) {
        bucketModelMapper.updateBucket(bucketName, detail);
        return true;
    }

    @Override
    public BucketModel getBucketById(String bucketId) {
        return bucketModelMapper.getBucket(bucketId);
    }

    @Override
    public BucketModel getBucketByName(String bucketName) {
        return bucketModelMapper.getBucketByName(bucketName);
    }

    @Override
    public List<BucketModel> getBucketByCreator(String creator) {
        return bucketModelMapper.getBucketByCreator(creator);
    }

    @Override
    public List<BucketModel> getUserBuckets(String token) {
        return bucketModelMapper.getUserAuthorizedBuckets(token);
    }

}
