package com.zing.springcloudalibaba.service;

import com.zing.springcloudalibaba.mapper.AccountMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Zing
 */
@Slf4j
@Service
public class AccountService {

    @Autowired
    private AccountMapper accountMapper;

    public int reduce(Long userId, Long amount) {
        log.info("------->扣余额开始");
        int stock = accountMapper.reduce(userId, amount);
        log.info("------->扣余额结束");

        if (stock == 0) {
            throw new RuntimeException("异常");
        }
        return stock;
    }
}
