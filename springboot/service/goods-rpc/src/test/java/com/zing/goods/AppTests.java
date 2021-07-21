package com.zing.goods;

import com.zing.goods.mapper.GoodsMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppTests {

    @Autowired
    private GoodsMapper goodsMapper;

    @Test
    public void test() {
        goodsMapper.selectById(1);
    }

}
