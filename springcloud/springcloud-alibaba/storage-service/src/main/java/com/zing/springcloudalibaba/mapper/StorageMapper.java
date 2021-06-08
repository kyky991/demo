package com.zing.springcloudalibaba.mapper;

import com.zing.springcloudalibaba.domain.Storage;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.BaseMapper;

/**
 * @author Zing
 */
public interface StorageMapper extends BaseMapper<Storage> {

    /**
     * 减库存
     *
     * @param id    商品id
     * @param count 数量
     * @return 结果
     */
    int reduceStock(@Param("id") Long id, @Param("count") Integer count);

}
