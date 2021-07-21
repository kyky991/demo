package com.zing.goods.repository;

import com.zing.goods.entity.Goods;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GoodsRepository extends CrudRepository<Goods, Long> {

    List<Goods> findByNameLike(String name);
}
