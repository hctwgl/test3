package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfVisualH5Dao;
import com.ald.fanbei.api.dal.domain.AfVisualH5Do;
import com.ald.fanbei.api.biz.service.AfVisualH5Service;



/**
 * 可视化H5ServiceImpl
 * 
 * @author 周锐
 * @version 1.0.0 初始化
 * @date 2018-04-09 11:02:49
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afVisualH5Service")
public class AfVisualH5ServiceImpl extends ParentServiceImpl<AfVisualH5Do, Long> implements AfVisualH5Service {
	
    private static final Logger logger = LoggerFactory.getLogger(AfVisualH5ServiceImpl.class);
   
    @Resource
    private AfVisualH5Dao afVisualH5Dao;

		@Override
	public BaseDao<AfVisualH5Do, Long> getDao() {
		return afVisualH5Dao;
	}

	@Override
	public int updateById(AfVisualH5Do afVisualH5Do) {
		return afVisualH5Dao.updateById(afVisualH5Do);
	}
	@Override
	public AfVisualH5Do getById(Long id){
		return afVisualH5Dao.getById(id);
	}

}