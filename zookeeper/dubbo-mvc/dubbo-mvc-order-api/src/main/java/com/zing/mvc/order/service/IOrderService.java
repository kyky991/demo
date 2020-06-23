package com.zing.mvc.order.service;

import com.zing.mvc.order.pojo.Orders;

public interface IOrderService {

	/**
	 * @Description: 根据订单id查询订单
	 */
	Orders getOrder(String orderId);
	
	/**
	 * @Description: 下订单
	 */
	boolean createOrder(String itemId);

}

