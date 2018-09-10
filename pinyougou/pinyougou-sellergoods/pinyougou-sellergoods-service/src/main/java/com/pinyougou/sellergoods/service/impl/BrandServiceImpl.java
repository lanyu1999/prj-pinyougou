package com.pinyougou.sellergoods.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.BrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import com.pinyougou.content.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service(interfaceClass = BrandService.class)
public class BrandServiceImpl extends BaseServiceImpl <TbBrand> implements BrandService{
    @Autowired
    private BrandMapper brandMapper;

    public List<TbBrand> queryAll() {
        return brandMapper.queryAll();
    }

    @Override
    public List<TbBrand> testPage(Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        return  brandMapper.selectAll();
    }

    @Override
    public List<Map<String, Object>> selectOptionList() {
        return  brandMapper.selectOptionList();
    }

    @Override
    public PageResult search(TbBrand brand, Integer page, Integer rows) {
        //分页查询
        PageHelper.startPage(page, rows);
        //创建一个查询对象
        Example example = new Example(TbBrand.class);
        //创建一个查询条件对象
        Example.Criteria criteria = example.createCriteria();
        //根据名字查询
        if(!StringUtils.isEmpty(brand.getName())){
            criteria.andLike("name","%"+brand.getName()+"%");
        }
        //根据首字母查询
        if(!StringUtils.isEmpty(brand.getFirstChar())){
            criteria.andEqualTo("firstChar",brand.getFirstChar()) ;
        }

        List<TbBrand> list = brandMapper.selectByExample(example);
        PageInfo<TbBrand> pageInfo = new PageInfo<>(list);
        return  new PageResult(pageInfo.getTotal(),pageInfo.getList());
    }
}
