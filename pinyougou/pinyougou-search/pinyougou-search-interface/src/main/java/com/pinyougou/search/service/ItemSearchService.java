package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {
    /**
     * 根据搜索关键字搜索商品列表
     * @param searchMap 搜索条件
     * @return 搜索结果      */
    Map<String,Object> search(Map<String, Object> searchMap);

    void importItemList(List<TbItem> itemList);

    void deleteItemByGoodsIds(List<Long> goodsIds);
}
