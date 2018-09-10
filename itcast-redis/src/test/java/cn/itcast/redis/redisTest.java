package cn.itcast.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-redis.xml")
public class redisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testString(){
        redisTemplate.boundValueOps("s_str").set("i_am_ljb；我来自传智播客");
        Object obj = redisTemplate.boundValueOps("s_str").get();
        System.out.println(obj);
    }

    @Test
    public void testHash(){
        redisTemplate.boundHashOps("h_key").put("f1", "v1");
        redisTemplate.boundHashOps("h_key").put("f2", "v2");
        List obj = redisTemplate.boundHashOps("h_key").values();
        System.out.println(obj);
    }

    @Test
    public void testList(){
        redisTemplate.boundListOps("l_key").leftPush("c");
        redisTemplate.boundListOps("l_key").leftPush("b");
        redisTemplate.boundListOps("l_key").rightPush("d");

        //起始索引号和结束索引号；如果为-1表示全部
        List obj = redisTemplate.boundListOps("l_key").range(0, -1);

        System.out.println(obj);
    }

    @Test
    public void testSet(){
        redisTemplate.boundSetOps("s_key").add("a", "b", "c");

        //获取集合中元素
        Object obj = redisTemplate.boundSetOps("s_key").members();
        System.out.println(obj);
    }

    //有序集合测试
    @Test
    public void testSortedSet(){
        //元素值，该元素对应的分值；默认在有序集合中元素根据分值升序排序
        redisTemplate.boundZSetOps("z_key").add("d", 5);
        redisTemplate.boundZSetOps("z_key").add("a", 10);
        redisTemplate.boundZSetOps("z_key").add("b", 7);

        Set set = redisTemplate.boundZSetOps("z_key").range(0, -1);
        System.out.println(set);
    }

}
