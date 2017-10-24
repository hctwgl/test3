package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfDeRandomPropertyDao;
import com.ald.fanbei.api.dal.domain.AfDeRandomPropertyDo;
import com.ald.fanbei.api.biz.service.de.AfDeRandomPropertyService;



/**
 * 双十一砍价ServiceImpl
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-10-17 11:40:20
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afDeRandomPropertyService")
public class AfDeRandomPropertyServiceImpl extends ParentServiceImpl<AfDeRandomPropertyDo, Long> implements AfDeRandomPropertyService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfDeRandomPropertyServiceImpl.class);
   
    @Resource
    private AfDeRandomPropertyDao afDeRandomPropertyDao;

		@Override
	public BaseDao<AfDeRandomPropertyDo, Long> getDao() {
		return afDeRandomPropertyDao;
	}
}