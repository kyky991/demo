package com.zing.bigdata.hos.web.security;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.zing.bigdata.hos.core.auth.model.TokenInfo;
import com.zing.bigdata.hos.core.auth.service.IAuthService;
import com.zing.bigdata.hos.core.user.model.SystemRole;
import com.zing.bigdata.hos.core.user.model.UserInfo;
import com.zing.bigdata.hos.core.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;

@Component
public class SecurityInterceptor implements HandlerInterceptor {

    private static Cache<String, UserInfo> userInfoCache = CacheBuilder.newBuilder().expireAfterWrite(20, TimeUnit.MINUTES).build();

    @Autowired
    private IAuthService authService;

    @Autowired
    private IUserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getRequestURI().equals("/login")) {
            return true;
        }

        String token = "";
        HttpSession session = request.getSession();
        if (session.getAttribute(ContextUtils.SESSION_KEY) != null) {
            token = session.getAttribute(ContextUtils.SESSION_KEY).toString();
        } else {
            token = request.getHeader("X-Auth-Token");
        }
        TokenInfo tokenInfo = authService.getTokenInfo(token);
        if (tokenInfo == null) {
            response.sendRedirect("/login");
            return false;
        }

        UserInfo userInfo = userInfoCache.getIfPresent(tokenInfo.getToken());
        if (userInfo == null) {
            userInfo = userService.getUserById(token);
            if (userInfo == null) {
                userInfo = new UserInfo();
                userInfo.setUserId(token);
                userInfo.setUsername("visitor");
                userInfo.setDetail("this is a visitor");
                userInfo.setSystemRole(SystemRole.VISITOR);
            }
            userInfoCache.put(tokenInfo.getToken(), userInfo);
        }
        ContextUtils.setCurrentUser(userInfo);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
