package com.zing.bigdata.hos.web.security.impl;

import com.zing.bigdata.hos.common.BucketModel;
import com.zing.bigdata.hos.core.auth.model.ServiceAuth;
import com.zing.bigdata.hos.core.auth.model.TokenInfo;
import com.zing.bigdata.hos.core.auth.service.IAuthService;
import com.zing.bigdata.hos.core.user.model.CoreUtils;
import com.zing.bigdata.hos.core.user.model.SystemRole;
import com.zing.bigdata.hos.core.user.model.UserInfo;
import com.zing.bigdata.hos.core.user.service.IUserService;
import com.zing.bigdata.hos.server.service.IBucketService;
import com.zing.bigdata.hos.web.security.IOperationAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultOperationAccessService implements IOperationAccessService {

    @Autowired
    private IUserService userService;

    @Autowired
    private IAuthService authService;

    @Autowired
    private IBucketService bucketService;

    @Override
    public UserInfo checkLogin(String username, String password) {
        UserInfo userInfo = userService.getUserByName(username);
        if (userInfo == null) {
            return null;
        }
        return userInfo.getPassword().equals(CoreUtils.getMd5Password(password)) ? userInfo : null;
    }

    @Override
    public boolean checkSystemRole(SystemRole systemRole1, SystemRole systemRole2) {
        if (systemRole1.equals(SystemRole.SUPERADMIN)) {
            return true;
        }
        return systemRole1.equals(SystemRole.ADMIN) && systemRole2.equals(SystemRole.USER);
    }

    @Override
    public boolean checkSystemRole(SystemRole systemRole, String userId) {
        if (systemRole.equals(SystemRole.SUPERADMIN)) {
            return true;
        }
        UserInfo userInfo = userService.getUserById(userId);
        if (userInfo == null) {
            return false;
        }
        return systemRole.equals(SystemRole.ADMIN) && userInfo.getSystemRole().equals(SystemRole.USER);
    }

    @Override
    public boolean checkTokenOwner(String username, String token) {
        TokenInfo tokenInfo = authService.getTokenInfo(token);
        return tokenInfo.getCreator().equals(username);
    }

    @Override
    public boolean checkBucketOwner(String username, String bucketName) {
        BucketModel bucketModel = bucketService.getBucketByName(bucketName);
        return bucketModel.getCreator().equals(username);
    }

    @Override
    public boolean checkPermission(String token, String bucket) {
        if (authService.checkToken(token)) {
            ServiceAuth serviceAuth = authService.getServiceAuth(bucket, token);
            if (serviceAuth != null) {
                return true;
            }
        }
        return false;
    }
}
