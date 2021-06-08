package com.zing.bigdata.hos.web.controller;

import com.zing.bigdata.hos.core.ErrorCode;
import com.zing.bigdata.hos.core.auth.model.ServiceAuth;
import com.zing.bigdata.hos.core.auth.model.TokenInfo;
import com.zing.bigdata.hos.core.auth.service.IAuthService;
import com.zing.bigdata.hos.core.user.model.SystemRole;
import com.zing.bigdata.hos.core.user.model.UserInfo;
import com.zing.bigdata.hos.core.user.service.IUserService;
import com.zing.bigdata.hos.web.security.ContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hos/v1/sys")
public class ManageController extends BaseController {

    @Autowired
    IAuthService authService;

    @Autowired
    IUserService userService;

    /**
     * 创建用户
     *
     * @param username
     * @param password
     * @param detail
     * @param role
     * @return
     */
    @RequestMapping(value = "user", method = RequestMethod.POST)
    public Object createUser(@RequestParam("username") String username,
                             @RequestParam("password") String password,
                             @RequestParam(name = "detail", required = false, defaultValue = "") String detail,
                             @RequestParam(name = "role", required = false, defaultValue = "USER") String role) {
        UserInfo currentUser = ContextUtils.getCurrentUser();
        if (operationAccessService.checkSystemRole(currentUser.getSystemRole(), SystemRole.valueOf(role))) {
            UserInfo userInfo = new UserInfo(username, password, detail, SystemRole.valueOf(role));
            userService.addUser(userInfo);
            return getResult("success");
        }
        return getError(ErrorCode.ERROR_PERMISSION_DENIED, "NOT ADMIN");
    }

    /**
     * 删除用户
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "user", method = RequestMethod.DELETE)
    public Object deleteUser(@RequestParam("userId") String userId) {
        UserInfo currentUser = ContextUtils.getCurrentUser();
        if (operationAccessService.checkSystemRole(currentUser.getSystemRole(), userId)) {
            userService.deleteUser(userId);
            return getResult("success");
        }
        return getError(ErrorCode.ERROR_PERMISSION_DENIED, "PERMISSION DENIED");
    }

    @RequestMapping(value = "user", method = RequestMethod.PUT)
    public Object updateUserInfo(@RequestParam(name = "password", required = false, defaultValue = "") String password,
                                 @RequestParam(name = "detail", required = false, defaultValue = "") String detail) {
        UserInfo currentUser = ContextUtils.getCurrentUser();
        if (currentUser.getSystemRole().equals(SystemRole.VISITOR)) {
            return getError(ErrorCode.ERROR_PERMISSION_DENIED, "PERMISSION DENIED");
        }

        userService.updateUser(currentUser.getUserId(), password, detail);
        return getResult("success");
    }

    @RequestMapping(value = "user", method = RequestMethod.GET)
    public Object getUserInfo() {
        UserInfo currentUser = ContextUtils.getCurrentUser();
        return getResult(currentUser);
    }

    /**
     * 添加token
     *
     * @param expireTime
     * @param isActive
     * @return
     */
    @RequestMapping(value = "token", method = RequestMethod.POST)
    public Object createToken(@RequestParam(name = "expireTime", required = false, defaultValue = "7") Integer expireTime,
                              @RequestParam(name = "isActive", required = false, defaultValue = "true") String isActive) {
        UserInfo currentUser = ContextUtils.getCurrentUser();
        if (!currentUser.getSystemRole().equals(SystemRole.VISITOR)) {
            TokenInfo tokenInfo = new TokenInfo(currentUser.getUsername());
            tokenInfo.setExpireTime(expireTime);
            tokenInfo.setActive(Boolean.parseBoolean(isActive));
            authService.addToken(tokenInfo);
            return getResult(tokenInfo);
        }
        return getError(ErrorCode.ERROR_PERMISSION_DENIED, "NOT USER");
    }

    /**
     * 删除token
     *
     * @param token
     * @return
     */
    @RequestMapping(value = "token", method = RequestMethod.DELETE)
    public Object deleteToken(@RequestParam("token") String token) {
        UserInfo currentUser = ContextUtils.getCurrentUser();
        if (operationAccessService.checkTokenOwner(currentUser.getUsername(), token)) {
            authService.deleteToken(token);
            return getResult("success");
        }
        return getError(ErrorCode.ERROR_PERMISSION_DENIED, "PERMISSION DENIED");
    }


    @RequestMapping(value = "token", method = RequestMethod.PUT)
    public Object updateTokenInfo(@RequestParam("token") String token,
                                  @RequestParam(name = "expireTime", required = false, defaultValue = "7") Integer expireTime,
                                  @RequestParam(name = "isActive", required = false, defaultValue = "true") String isActive) {
        UserInfo currentUser = ContextUtils.getCurrentUser();
        if (operationAccessService.checkTokenOwner(currentUser.getUsername(), token)) {
            authService.updateToken(token, expireTime, Boolean.parseBoolean(isActive));
            return getResult("success");
        }

        return getError(ErrorCode.ERROR_PERMISSION_DENIED, "PERMISSION DENIED");
    }

    @RequestMapping(value = "token", method = RequestMethod.GET)
    public Object getTokenInfo(@RequestParam("token") String token) {
        UserInfo currentUser = ContextUtils.getCurrentUser();
        if (operationAccessService.checkTokenOwner(currentUser.getUsername(), token)) {
            TokenInfo tokenInfo = authService.getTokenInfo(token);
            return getResult(tokenInfo);
        }

        return getError(ErrorCode.ERROR_PERMISSION_DENIED, "PERMISSION DENIED");

    }

    @RequestMapping(value = "token/list", method = RequestMethod.GET)
    public Object getTokenInfoList() {
        UserInfo currentUser = ContextUtils.getCurrentUser();
        if (!currentUser.getSystemRole().equals(SystemRole.VISITOR)) {
            List<TokenInfo> tokenInfos = authService.getTokenInfos(currentUser.getUsername());
            return getResult(tokenInfos);
        }

        return getError(ErrorCode.ERROR_PERMISSION_DENIED, "PERMISSION DENIED");

    }

    @RequestMapping(value = "token/refresh", method = RequestMethod.POST)
    public Object refreshToken(@RequestParam("token") String token) {
        UserInfo currentUser = ContextUtils.getCurrentUser();
        if (operationAccessService.checkTokenOwner(currentUser.getUsername(), token)) {
            authService.refreshToken(token);
            return getResult("success");
        }

        return getError(ErrorCode.ERROR_PERMISSION_DENIED, "PERMISSION DENIED");
    }


    /**
     * 授权
     *
     * @param serviceAuth
     * @return
     */
    @RequestMapping(value = "auth", method = RequestMethod.POST)
    public Object createAuth(@RequestBody ServiceAuth serviceAuth) {
        UserInfo currentUser = ContextUtils.getCurrentUser();
        if (operationAccessService.checkBucketOwner(currentUser.getUsername(), serviceAuth.getBucketName())
                && operationAccessService.checkTokenOwner(currentUser.getUsername(), serviceAuth.getTargetToken())) {
            authService.addAuth(serviceAuth);
            return getResult("success");
        }
        return getError(ErrorCode.ERROR_PERMISSION_DENIED, "PERMISSION DENIED");
    }

    /**
     * 取消授权
     *
     * @param bucket
     * @param token
     * @return
     */
    @RequestMapping(value = "auth", method = RequestMethod.DELETE)
    public Object deleteAuth(@RequestParam("bucket") String bucket, @RequestParam("token") String token) {
        UserInfo currentUser = ContextUtils.getCurrentUser();
        if (operationAccessService.checkBucketOwner(currentUser.getUsername(), bucket)
                && operationAccessService.checkTokenOwner(currentUser.getUsername(), token)) {
            authService.deleteAuth(bucket, token);
            return getResult("success");
        }
        return getError(ErrorCode.ERROR_PERMISSION_DENIED, "PERMISSION DENIED");
    }

}
