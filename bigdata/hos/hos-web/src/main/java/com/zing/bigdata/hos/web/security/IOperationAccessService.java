package com.zing.bigdata.hos.web.security;

import com.zing.bigdata.hos.core.user.model.SystemRole;
import com.zing.bigdata.hos.core.user.model.UserInfo;

public interface IOperationAccessService {

    public UserInfo checkLogin(String username, String password);

    public boolean checkSystemRole(SystemRole systemRole1, SystemRole systemRole2);

    public boolean checkSystemRole(SystemRole systemRole, String userId);

    public boolean checkTokenOwner(String username, String token);

    public boolean checkBucketOwner(String username, String bucketName);

    public boolean checkPermission(String token, String bucket);

}
