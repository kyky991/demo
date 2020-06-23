package com.zing.mvc.web.service;

public interface IBuyService {

	/**
	 * @Description: 购买商品
	 */
	void doBuyItem(String itemId);
	
	boolean displayBuy(String itemId);
}

