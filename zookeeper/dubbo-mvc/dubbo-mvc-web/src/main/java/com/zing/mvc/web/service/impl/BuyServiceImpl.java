package com.zing.mvc.web.service.impl;

import com.zing.mvc.item.service.IItemService;
import com.zing.mvc.order.service.IOrderService;
import com.zing.mvc.utils.DistributedLock;
import com.zing.mvc.web.service.IBuyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BuyServiceImpl implements IBuyService {

    final static Logger log = LoggerFactory.getLogger(BuyServiceImpl.class);

    @Autowired
    private IItemService itemService;

    @Autowired
    private IOrderService ordersService;

    @Autowired
    private DistributedLock distributedLock;

    @Override
    public void doBuyItem(String itemId) {
        // 减少库存
        itemService.displayReduceAmount(itemId, 1);

        // 创建订单
        ordersService.createOrder(itemId);
    }

    @Override
    public boolean displayBuy(String itemId) {

        // 执行订单流程之前使得当前业务获得分布式锁
        distributedLock.getLock();

        int buyCounts = 3;

        // 1. 判断库存
        int stockCounts = itemService.getItemCounts(itemId);
        if (stockCounts < buyCounts) {
            log.info("库存剩余{}件，用户需求量{}件，库存不足，订单创建失败...", stockCounts, buyCounts);
            // 释放锁，让下一个请求获得锁
            distributedLock.releaseLock();
            return false;
        }

        // 模拟处理业务
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
            distributedLock.releaseLock();
        }

        // 2. 创建订单
        boolean isOrderCreated = ordersService.createOrder(itemId);

        // 3. 创建订单成功后，扣除库存
        if (isOrderCreated) {
            log.info("订单创建成功....");
            itemService.displayReduceAmount(itemId, buyCounts);
        } else {
            log.info("订单创建失败...");
            // 释放锁，让下一个请求获得锁
            distributedLock.releaseLock();
            return false;
        }

        // 释放锁，让下一个请求获得锁
        distributedLock.releaseLock();
        return true;
    }

}

