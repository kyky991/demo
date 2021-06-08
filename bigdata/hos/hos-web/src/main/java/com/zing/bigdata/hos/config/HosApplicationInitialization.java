package com.zing.bigdata.hos.config;

import com.zing.bigdata.hos.core.user.model.CoreUtils;
import com.zing.bigdata.hos.core.user.model.SystemRole;
import com.zing.bigdata.hos.core.user.model.UserInfo;
import com.zing.bigdata.hos.core.user.service.IUserService;
import com.zing.bigdata.hos.server.service.IHosStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class HosApplicationInitialization implements ApplicationRunner {

    @Autowired
    private IUserService userService;

    @Autowired
    private IHosStoreService hosStoreService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        UserInfo userInfo = userService.getUserByName(CoreUtils.SYSTEM_USER);
        if (userInfo == null) {
            userInfo = new UserInfo(CoreUtils.SYSTEM_USER, "123456", "this is a super admin", SystemRole.SUPERADMIN);
            userService.addUser(userInfo);
        }

        hosStoreService.createSeqTable();
    }
}
