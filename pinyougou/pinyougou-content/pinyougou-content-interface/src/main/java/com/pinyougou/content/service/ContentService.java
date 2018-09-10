package com.pinyougou.content.service;

import com.pinyougou.pojo.TbContent;
import com.pinyougou.vo.PageResult;

public interface ContentService extends BaseService<TbContent> {

    PageResult search(Integer page, Integer rows, TbContent content);
}