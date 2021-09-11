package com.zing.test.repository;

import com.zing.test.domain.Item;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ItemRepository extends ElasticsearchRepository<Item, String> {

    List<Item> findByTitle(String title);

}
