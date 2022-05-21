package com.zing.test.es.repository;

import com.zing.test.es.domain.Item;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ItemRepository extends ElasticsearchRepository<Item, String> {

    List<Item> findByTitle(String title);

}
