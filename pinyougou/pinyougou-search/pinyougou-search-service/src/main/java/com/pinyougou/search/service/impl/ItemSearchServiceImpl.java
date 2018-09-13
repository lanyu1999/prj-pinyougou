package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.ctc.wstx.util.StringUtil;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service(interfaceClass = ItemSearchService.class)
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 根据搜索关键字搜索商品列表
     *
     * @param searchMap 搜索条件
     * @return 搜索结果
     */
    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {
        Map<String, Object> resultMap = new HashMap<>();
//        SimpleQuery query = new SimpleQuery();
        //创建高亮搜索对象
        SimpleHighlightQuery query = new SimpleHighlightQuery();
        //查询条件
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //设置分页信息
        Integer pageNo = 1;
        Integer pageSize = 20;
        if(searchMap.get("pageNo")!=null){
            pageNo = Integer.parseInt(searchMap.get("pageNo").toString());
        }
        if(searchMap.get("pageSize")!=null){
            pageSize = Integer.parseInt(searchMap.get("pageSize").toString());
        }
        query.setOffset((pageNo-1));
        query.setRows(pageSize);


        //设置高亮
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title");//高亮域
        highlightOptions.setSimplePrefix("<em style='color:red'>");//高亮其实标签
        highlightOptions.setSimplePostfix("</em>");//高亮结束标签
        query.setHighlightOptions(highlightOptions);
        //根据商品分类过滤查询:
        if (!StringUtils.isEmpty(searchMap.get("category"))) {
            Criteria categoryCriteria = new Criteria("item_category").is(searchMap.get("category"));
            SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(categoryCriteria);
            query.addFilterQuery(simpleFilterQuery);
        }
        if (!StringUtils.isEmpty(searchMap.get("brand"))) {
            Criteria brandCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(brandCriteria);
            query.addFilterQuery(simpleFilterQuery);
        }
        if (searchMap.get("spec") != null) {
            Map<String, Object> map = (Map<String, Object>) searchMap.get("spec");
            Set<Map.Entry<String, Object>> entrySet = map.entrySet();
            for (Map.Entry<String, Object> entry : entrySet) {
                Criteria specCriteria = new Criteria("item_spec_" + entry.getKey()).is(entry.getValue());
                SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(specCriteria);
                query.addFilterQuery(simpleFilterQuery);
            }

        }
        if (!StringUtils.isEmpty(searchMap.get("price"))) {
            //获取起始价格
            String[] prices = searchMap.get("price").toString().split("-");
            //价格大于等与起始价格
            Criteria startCriteria = new Criteria("item_price").greaterThanEqual(prices[0]);
            SimpleFilterQuery startPriceFilterQuery = new SimpleFilterQuery(startCriteria);
            query.addFilterQuery(startPriceFilterQuery);
            //价格雄安与等于结束价格
            if (!"*".equals(prices[1])) {
                //价格小于等于起始价格
                Criteria endCriteria = new Criteria("item_price").lessThanEqual(prices[1]);
                SimpleFilterQuery endPriceFilterQuery = new SimpleFilterQuery(endCriteria);
                query.addFilterQuery(endPriceFilterQuery);
            }
        }

        //查询
        HighlightPage<TbItem> itemHighlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);

        // 处理高亮标题
        List<HighlightEntry<TbItem>> highlighted = itemHighlightPage.getHighlighted();
        if (highlighted != null && highlighted.size() > 0) {
            for (HighlightEntry<TbItem> entry : highlighted) {
                List<HighlightEntry.Highlight> highlights = entry.getHighlights();
                if (highlights != null && highlights.size() > 0 && highlights.get(0).getSnipplets() != null) {
                    // 设置高亮标题
                    entry.getEntity().setTitle(highlights.get(0).getSnipplets().get(0));
                }
            }
        }
        //设置返回的商品列表
        resultMap.put("rows", itemHighlightPage.getContent());
        resultMap.put("totalPages",itemHighlightPage.getTotalPages());
        resultMap.put("total",itemHighlightPage.getTotalElements());
        return resultMap;
    }
}
