package cn.itcast.test;

import com.pinyougou.pojo.TbItem;
import org.apache.http.conn.util.PublicSuffixList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sun.reflect.generics.scope.Scope;

import java.math.BigDecimal;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-solr.xml")
public class solrTest {
    @Autowired
    private SolrTemplate solrTemplate;

    //新增更新
    @Test
    public void test1() {
        TbItem tbItem = new TbItem();
        tbItem.setId(1L);
        tbItem.setTitle("中兴手机Axon M 折叠双屏高端手机");
        tbItem.setBrand("中兴");
        tbItem.setPrice(new BigDecimal(1000));
        tbItem.setSeller("中兴旗舰店");
        tbItem.setCategory("手机");
        solrTemplate.saveBean(tbItem);
        solrTemplate.commit();
    }

    //根据主键删除
    @Test
    public void test2() {
        solrTemplate.deleteById("1");
        solrTemplate.commit();
    }

    //根据条件删除
    @Test
    public void test3() {
        SimpleQuery query = new SimpleQuery("*:*");
        solrTemplate.delete(query);

    }

    //根据关键子分页查询
    @Test
    public void testQuery1() {
        SimpleQuery query = new SimpleQuery("*:*");
        query.setOffset(20);//分页其实索引 默认为0
        query.setRows(20);//分页结束索引:默认为0
        ScoredPage<TbItem> scoredPage = solrTemplate.queryForPage(query, TbItem.class);
        showPage(scoredPage);
    }

    private void showPage(ScoredPage<TbItem> scoredPage) {
        System.out.println("总记录数为: " + scoredPage.getTotalElements());
        System.out.println("总页数为: " + scoredPage.getTotalPages());
        List<TbItem> tbItemList = scoredPage.getContent();
        for (TbItem tbItem : tbItemList) {
            System.out.println(tbItem);
        }
    }

    //多条件查询
    @Test
    public void testQuery2() {
        SimpleQuery query = new SimpleQuery();
        //contains表示查询的该域包含关键字的那些文件是不会分词的,is会分词;
        Criteria criteria1 = new Criteria("item_title").contains("中兴");
        query.addCriteria(criteria1); Criteria criteria2 = new Criteria("item_price").greaterThanEqual("1000");
        query.addCriteria(criteria2);
        //分页其实索引默认为0  分页大小默认为10
        ScoredPage<TbItem> scoredPage = solrTemplate.queryForPage(query,TbItem.class);
        showPage(scoredPage);

    }
}
