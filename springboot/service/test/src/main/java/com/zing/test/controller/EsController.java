package com.zing.test.controller;

import com.zing.test.domain.Item;
import com.zing.test.repository.ItemRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EsController {

    @Autowired
    private ItemRepository itemRepository;

    @GetMapping("/item/{id}")
    public Object index(@PathVariable("id") String id) {
        return itemRepository.findById(id);
    }

    @GetMapping("/item/{id}")
    public Object all() {
        Iterable<Item> all = itemRepository.findAll();
        return all;
    }

    @GetMapping("/item/add")
    public Object index() {
        Item item = new Item();
        item.setTitle(RandomStringUtils.randomAlphanumeric(10));
        item.setBrand(RandomStringUtils.randomAlphanumeric(5));
        item.setCategory(RandomStringUtils.randomAlphabetic(3));
        item.setPrice(RandomUtils.nextDouble(0, 100));
        item.setImages(RandomStringUtils.randomAlphanumeric(50));
        return itemRepository.save(item);
    }

    @GetMapping("/item/search")
    public Object search(String category) {
        MatchQueryBuilder matchQuery = QueryBuilders.matchQuery("category", category);
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        builder.withQuery(matchQuery);

        Page<Item> search = itemRepository.search(builder.build());
        return search;
    }

}
