package com.zing.springcloudalibaba.mapper;

import com.zing.springcloudalibaba.domain.Account;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.BaseMapper;

/**
 * @author Zing
 */
public interface AccountMapper extends BaseMapper<Account> {

    /**
     * 减
     *
     * @param userId userId
     * @param amount 数量
     * @return 结果
     */
    int reduce(@Param("userId") Long userId, @Param("amount") Long amount);

}
