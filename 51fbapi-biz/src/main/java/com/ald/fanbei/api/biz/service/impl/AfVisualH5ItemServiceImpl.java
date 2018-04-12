package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfVisualH5ItemDao;
import com.ald.fanbei.api.dal.domain.AfVisualH5ItemDo;
import com.ald.fanbei.api.biz.service.AfVisualH5ItemService;

import java.util.HashMap;
import java.util.List;


/**
 * 可视化H5ServiceImpl
 * 
 * @author 周锐
 * @version 1.0.0 初始化
 * @date 2018-04-09 11:02:49
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afVisualH5ItemService")
public class AfVisualH5ItemServiceImpl extends ParentServiceImpl<AfVisualH5ItemDo, Long> implements AfVisualH5ItemService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfVisualH5ItemServiceImpl.class);
   
    @Resource
    private AfVisualH5ItemDao afVisualH5ItemDao;

		@Override
	public BaseDao<AfVisualH5ItemDo, Long> getDao() {
		return afVisualH5ItemDao;
	}

	@Override
	public List<HashMap> getCouponByVisualId(Long visualId) {
		return afVisualH5ItemDao.getCouponByVisualId(visualId);
	}
}