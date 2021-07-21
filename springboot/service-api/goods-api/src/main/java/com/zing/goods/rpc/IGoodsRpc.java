package com.zing.goods.rpc;

import com.zing.goods.domain.dto.GoodsDTO;

import java.util.List;

public interface IGoodsRpc {

    GoodsDTO get(Long id);

    List<GoodsDTO> list(List<Long> ids);

    int save();

}
