package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfCategoryOprationDao;
import com.ald.fanbei.api.dal.domain.AfCategoryOprationDo;
import com.ald.fanbei.api.biz.service.AfCategoryOprationService;



/**
 * 分类运营位配置ServiceImpl
 * 
 * @author liutengyuan
 * @version 1.0.0 初始化
 * @date 2018-04-11 19:59:13
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afCategoryOprationService")
public class AfCategoryOprationServiceImpl extends ParentServiceImpl<AfCategoryOprationDo, Long> implements AfCategoryOprationService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfCategoryOprationServiceImpl.class);
   
    @Resource
    private AfCategoryOprationDao afCategoryOprationDao;

		@Override
	public BaseDao<AfCategoryOprationDo, Long> getDao() {
		return afCategoryOprationDao;
	}

		@Override
		public AfCategoryOprationDo getByCategoryId(Long cateGoryId) {
			return afCategoryOprationDao.getByCategoryId(cateGoryId);
		}
}