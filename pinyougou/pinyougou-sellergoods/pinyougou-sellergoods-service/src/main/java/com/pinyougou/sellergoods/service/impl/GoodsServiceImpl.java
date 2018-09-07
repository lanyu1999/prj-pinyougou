package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service(interfaceClass = GoodsService.class)
public class GoodsServiceImpl extends BaseServiceImpl<TbGoods> implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsDescMapper goodDescMapper;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private ItemCatMapper itemCatMapper;
    @Autowired
    private SellerMapper sellerMapper;
    @Autowired
    private BrandMapper brandMapper;

    @Override
    public Goods findGoodsById(Long id) {
        Goods  goods = new Goods();
//        查询商品spu
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        goods.setGoods(tbGoods);
//        查询商品描述
        TbGoodsDesc tbGoodsDesc = goodDescMapper.selectByPrimaryKey(id);
        goods.setGoodsDesc(tbGoodsDesc);
//        查询行频sku列表
        Example example = new Example(TbItem.class);
        example.createCriteria().andEqualTo("goodsId",id);
        List<TbItem> itemList = itemMapper.selectByExample(example);
        goods.setItemList(itemList);
        return goods;


    }

    @Override
    public void addGoods(Goods goods) {
        //新增商品基础信息
        goodsMapper.insertSelective(goods.getGoods());
        //新增商品描述信息
        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
        goodDescMapper.insertSelective(goods.getGoodsDesc());
        // 新增商品 SKU

        saveItemList(goods);


    }

    /**
     * 保存SKU动态数据
     * @param goods
     */

    private void saveItemList(Goods goods) {
        if("1".equals(goods.getGoods().getIsEnableSpec())){
            for (TbItem item : goods.getItemList()) {
                String title = goods.getGoods().getGoodsName();
                // 组合规格选项形成 SKU
                Map<String, Object> map = JSON.parseObject(item.getSpec());
                Set<Map.Entry<String, Object>> entries = map.entrySet();
                for (Map.Entry<String, Object> entry : entries) {
                    title += " " + entry.getValue().toString();
                }

                item.setTitle(title);

                // 图片
                List<Map> imgList = JSONArray.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
                if (imgList != null && imgList.size() > 0) {
                    // 将商品的第一张图作为 sku的图片
                    item.setImage(imgList.get(0).get("url").toString());
                }
                // 商品分类 id
                item.setCategoryid(goods.getGoods().getCategory3Id());
                // 商品分类名称
                TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
                item.setCategory(itemCat.getName());

                // 创建时间
                item.setCreateTime(new Date());
                // 更新时间
                item.setUpdateTime(item.getCreateTime());
                //SPU 商品 id
                item.setGoodsId(goods.getGoods().getId());
                // 商家 id
                item.setSellerId(goods.getGoods().getSellerId());
                // 商家名称
                TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
                item.setSeller(seller.getName());
                // 品牌名称
                TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
                item.setBrand(brand.getName());

                itemMapper.insertSelective(item);
            }
        }else {

        }
    }

    @Override
    public PageResult search(Integer page, Integer rows, TbGoods goods) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(goods.getSellerId())){
            criteria.andLike("sellerId", "%" + goods.getSellerId() + "%");
        }
        if(!StringUtils.isEmpty(goods.getAuditStatus())){
            criteria.andLike("auditStatus", "%" + goods.getAuditStatus() + "%");
        }
        if(!StringUtils.isEmpty(goods.getGoodsName())){
            criteria.andLike("goodsName", "%" + goods.getGoodsName() + "%");
        }

        List<TbGoods> list = goodsMapper.selectByExample(example);
        PageInfo<TbGoods> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }


    private void setItemValue(TbItem item, Goods goods) {
        //查询品牌
        TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
        item.setBrand(brand.getName());

        //商品分类第3级的中文名称
        TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
        item.setCategory(itemCat.getName());
        //商品分类id
        item.setCategoryid(itemCat.getId());

        item.setCreateTime(new Date());
        item.setGoodsId(goods.getGoods().getId());

        //获取spu的第一张图片
        if (!StringUtils.isEmpty(goods.getGoodsDesc().getItemImages())) {
            //将图片json格式字符串转换为一个Json对象
            List<Map> images = JSONArray.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);

            item.setImage(images.get(0).toString());
        }

        //设置商家数据
        TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());

        item.setSeller(seller.getName());
        item.setSellerId(seller.getSellerId());

        item.setUpdateTime(item.getCreateTime());
    }
}
