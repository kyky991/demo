package com.zing.bigdata.hos.web.controller;

import com.google.common.base.Strings;
import com.zing.bigdata.hos.core.ErrorCode;
import com.zing.bigdata.hos.core.user.model.UserInfo;
import com.zing.bigdata.hos.web.security.ContextUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class LoginController extends BaseController {

    @RequestMapping("/login")
    @ResponseBody
    public Object login(String username, String password, HttpSession session) throws IOException {
        if (Strings.isNullOrEmpty(username) || Strings.isNullOrEmpty(password)) {
            return getError(ErrorCode.ERROR_PERMISSION_DENIED, "error");
        }

        UserInfo userInfo = operationAccessService.checkLogin(username, password);
        if (userInfo != null) {
            session.setAttribute(ContextUtils.SESSION_KEY, userInfo.getUserId());
            return getResult("success");
        } else {
            return getError(ErrorCode.ERROR_PERMISSION_DENIED, "error");
        }
    }

    @RequestMapping("/logout")
    @ResponseBody
    public Object logout(HttpSession session) throws IOException {
        session.removeAttribute(ContextUtils.SESSION_KEY);
        return getResult("success");
    }

}
