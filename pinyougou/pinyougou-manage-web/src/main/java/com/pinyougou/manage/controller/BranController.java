package com.pinyougou.manage.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BranController {
    @Reference
    private BrandService brandService;

    @GetMapping("/findAll")
    public List<TbBrand> findAll() {
        return brandService.queryAll();
    }

    /**
     * 根据分页信息分页查询品牌数据
     *
     * @param page 页号
     * @param rows 页大小
     * @return 品牌列表
     */
    @GetMapping("/testPage")
    public List<TbBrand> testPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                  @RequestParam(value = "rows", defaultValue = "5") Integer rows) {
        //return brandService.testPage(page, rows);
        return (List<TbBrand>) brandService.findByPage(page, rows).getRows();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                               @RequestParam(value = "rows", defaultValue = "5") Integer rows) {
        return brandService.findByPage(page, rows);
    }

    @PostMapping("/add")
    public Result add(@RequestBody TbBrand tbBrand) {
        try {
            brandService.add(tbBrand);
            return Result.Ok("新增成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("新增失败");
    }

    @GetMapping("/findOne")
    public TbBrand findOne(Long id) {
        return brandService.findOne(id);
    }

    /**
     * 修改数据
     */
    @PostMapping("/update")
    public Result update(@RequestBody TbBrand tbBrand) {
        try {
            brandService.update(tbBrand);
            return Result.Ok("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("添加失败");
    }


    //批量删除

    @GetMapping("delete")
    public Result delete(Long[] ids) {
        try {
            brandService.deleteByIds(ids);
            return Result.Ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    ////    根据分页条件查询
    @PostMapping("/search")
    public PageResult search(@RequestBody TbBrand brand, @RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "rows", defaultValue = "10") Integer
                                     rows) {
        return brandService.search(brand, page, rows);

    }


}