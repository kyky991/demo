package com.zing.goods.rpc;

import com.zing.goods.domain.dto.GoodsDTO;
import com.zing.goods.entity.Goods;
import com.zing.goods.entity.Sku;
import com.zing.goods.exception.GoodsException;
import com.zing.goods.mapper.GoodsMapper;
import com.zing.goods.mapper.SkuMapper;
import com.zing.user.exception.UserException;
import com.zing.user.rpc.IUserRpc;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class GoodsRpc implements IGoodsRpc {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Reference
    private IUserRpc userRpc;

    @Override
    public GoodsDTO get(Long id) {
        Goods goods = goodsMapper.selectById(id);
        if (goods == null) {
            return null;
        }
        GoodsDTO dto = new GoodsDTO();
        BeanUtils.copyProperties(goods, dto);
        return dto;
    }

    @Override
    public List<GoodsDTO> list(List<Long> ids) {
        List<Goods> list = goodsMapper.selectBatchIds(ids);

        List<GoodsDTO> results = new ArrayList<>();
        for (Goods user : list) {
            GoodsDTO dto = new GoodsDTO();
            BeanUtils.copyProperties(user, dto);
            results.add(dto);
        }

        try {
            userRpc.list(ids);
        } catch (UserException e) {
            throw new GoodsException("测试捕获异常 - [goods rpc] - " + e.getMessage());
        }

        if (true) {
            throw new GoodsException("测试异常处理 - [goods rpc]");
        }
        return results;
    }

    @Override
    public int save() {
        Goods goods = new Goods();
        goods.setName(RandomStringUtils.randomAlphanumeric(20));
        goods.setNo(UUID.randomUUID().toString());
        int row = goodsMapper.insert(goods);

        ((GoodsRpc) AopContext.currentProxy()).addSku(goods.getId());

        if (true) {
            throw new RuntimeException();
        }

        return row;
    }

    @Transactional(rollbackFor = Exception.class)
    public int addSku(Long goodsId) {
        Sku sku = new Sku();
        sku.setGoodsId(goodsId);
        sku.setName(RandomStringUtils.randomAlphanumeric(15));
        sku.setPrice(RandomUtils.nextLong(0, 10000));
        sku.setStock(RandomUtils.nextInt(0, 10000));
        return skuMapper.insert(sku);
    }

}
