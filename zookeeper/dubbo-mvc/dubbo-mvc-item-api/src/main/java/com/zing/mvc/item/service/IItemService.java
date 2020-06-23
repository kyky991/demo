package com.zing.mvc.item.service;

import com.zing.mvc.item.pojo.Item;

public interface IItemService {

	/**
	 * @Description: 根据商品id获取商品
	 */
	Item getItem(String itemId);
	
	/**
	 * @Description: 查询商品库存
	 */
	int getItemCounts(String itemId);
	
	/**
	 * @Description: 购买商品成功后减少库存
	 */
	void displayReduceAmount(String itemId, int buyCounts);

}

