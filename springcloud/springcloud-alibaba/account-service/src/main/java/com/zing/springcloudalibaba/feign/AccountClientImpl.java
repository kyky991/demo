package com.zing.springcloudalibaba.feign;

import com.zing.springcloudalibaba.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Zing
 */
@RestController
public class AccountClientImpl implements AccountClient {

    @Autowired
    private AccountService accountService;

    @Override
    public int reduce(Long userId, Long amount) {
        return accountService.reduce(userId, amount);
    }
}
