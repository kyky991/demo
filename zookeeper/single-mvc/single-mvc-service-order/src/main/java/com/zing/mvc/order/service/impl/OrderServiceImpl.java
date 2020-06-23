package com.zing.mvc.order.service.impl;

import java.util.UUID;


import com.zing.mvc.mapper.OrdersMapper;
import com.zing.mvc.order.service.IOrderService;
import com.zing.mvc.pojo.Orders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements IOrderService {

    final static Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrdersMapper ordersMapper;

    @Override
    public Orders getOrder(String orderId) {
        return ordersMapper.selectByPrimaryKey(orderId);
    }

    @Override
    public boolean createOrder(String itemId) {

        // 创建订单
        String oid = UUID.randomUUID().toString().replaceAll("-", "");
        Orders o = new Orders();
        o.setId(oid);
        o.setOrderNum(oid);
        o.setItemId(itemId);
        ordersMapper.insert(o);

        log.info("订单创建成功");

        return true;
    }

}

