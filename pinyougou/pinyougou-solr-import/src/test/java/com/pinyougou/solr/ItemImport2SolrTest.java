package com.pinyougou.solr;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/spring/*.xml")
public class ItemImport2SolrTest {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private ItemMapper itemMapper;
    @Test
    public void test () {
        //获取已经通过审核的商品列表
        TbItem tbItem =  new TbItem();
        tbItem.setStatus("1");
        List<TbItem> tbItemList = itemMapper.select(tbItem);
        //转换商品规格
        for (TbItem item : tbItemList) {
            Map map = JSON.parseObject(item.getSpec(), Map.class);
            item.setSpecMap(map);
        }
        //导入商品列表到solr
        solrTemplate.saveBeans(tbItemList);
        solrTemplate.commit();
    }

}
