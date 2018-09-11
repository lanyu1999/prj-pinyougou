package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.ContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service(interfaceClass = ContentService.class)
public class ContentServiceImpl extends BaseServiceImpl<TbContent> implements ContentService {

    @Override
    public void add(TbContent tbContent) {
        super.add(tbContent);
        updateContentInRedisByCategoryId(tbContent.getCategoryId());
    }

    private void updateContentInRedisByCategoryId(Long categoryId) {
        try {
            redisTemplate.boundHashOps("content").delete(categoryId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(TbContent tbContent) {
        TbContent oldContent = super.findOne(tbContent.getId());
        super.update(tbContent);
        //是否修改了内容分类,如果修改内容分类则需要将新旧分类对应的内容列表都更新
        if(!oldContent .getCategoryId().equals(tbContent.getCategoryId())) {
            updateContentInRedisByCategoryId(oldContent.getCategoryId());
        }
        updateContentInRedisByCategoryId(tbContent.getCategoryId());
    }

    @Override
    public void deleteByIds(Serializable[] ids) {
        //根据内容id集合查询内容列表,然后更新该内容的分类对应的内容列表缓存
        Example example = new Example(TbContent.class);
        example.createCriteria().andIn("id", Arrays.asList(ids));
        List<TbContent> contentList = contentMapper.selectByExample(example);
        if(contentList != null && contentList.size()>0){
            for (TbContent tbContent : contentList) {
                updateContentInRedisByCategoryId(tbContent.getCategoryId());
            }
        }
        //删除内容
        super.deleteByIds(ids);
    }

    @Autowired
    private ContentMapper contentMapper;
    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public List<TbContent> findContentListByCategoryId(Long categoryId) {
        List<TbContent> list = null;
        try {
            //先从缓存中查找
            list = (List<TbContent>) redisTemplate.boundHashOps("content").get(categoryId);
            if (list != null) {
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();
        //有效广告
        criteria.andEqualTo("status", "1");

        //内容分类
        criteria.andEqualTo("categoryId", categoryId);

        //排序；设置排序属性，desc降序
        example.orderBy("sortOrder").desc();

        list = contentMapper.selectByExample(example);
        try {
            redisTemplate.boundHashOps("content").put(categoryId, list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;


    }

    @Override
    public PageResult search(Integer page, Integer rows, TbContent content) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(content.get***())){
            criteria.andLike("***", "%" + content.get***() + "%");
        }*/

        List<TbContent> list = contentMapper.selectByExample(example);
        PageInfo<TbContent> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }
}
