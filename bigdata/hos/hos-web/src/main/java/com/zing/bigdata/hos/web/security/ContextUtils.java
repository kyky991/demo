package com.zing.bigdata.hos.web.security;

import com.zing.bigdata.hos.core.user.model.UserInfo;

public class ContextUtils {

    public static final String SESSION_KEY = "USER_TOKEN";

    private static ThreadLocal<UserInfo> userInfoThreadLocal = new ThreadLocal<>();

    public static UserInfo getCurrentUser() {
        return userInfoThreadLocal.get();
    }

    public static void setCurrentUser(UserInfo userInfo) {
        userInfoThreadLocal.set(userInfo);
    }

    public static void clear() {
        userInfoThreadLocal.remove();
    }

}
