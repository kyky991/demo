package com.zing.mvc.item.service.impl;

import com.zing.mvc.item.service.IItemService;
import com.zing.mvc.mapper.ItemMapper;
import com.zing.mvc.pojo.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ItemServiceImpl implements IItemService {

    @Autowired
    private ItemMapper itemMapper;

    @Override
    public Item getItem(String itemId) {
        return itemMapper.selectByPrimaryKey(itemId);
    }

    @Override
    public int getItemCounts(String itemId) {
        Item item = itemMapper.selectByPrimaryKey(itemId);
        return item.getAmount();
    }

    @Override
    public void displayReduceAmount(String itemId, int buyCounts) {

//		int a  = 1 / 0;

        Item reduceItem = new Item();
        reduceItem.setId(itemId);
        reduceItem.setBuyCounts(buyCounts);
        itemMapper.reduceAmount(reduceItem);
    }

}

