package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfInterestReduceSchemeDao;
import com.ald.fanbei.api.dal.domain.AfInterestReduceSchemeDo;
import com.ald.fanbei.api.biz.service.AfInterestReduceSchemeService;



/**
 * 降息ServiceImpl
 * 
 * @author qiao
 * @version 1.0.0 初始化
 * @date 2018-03-29 13:41:22
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afInterestReduceSchemeService")
public class AfInterestReduceSchemeServiceImpl extends ParentServiceImpl<AfInterestReduceSchemeDo, Long> implements AfInterestReduceSchemeService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfInterestReduceSchemeServiceImpl.class);
   
    @Resource
    private AfInterestReduceSchemeDao afInterestReduceSchemeDao;

		@Override
	public BaseDao<AfInterestReduceSchemeDo, Long> getDao() {
		return afInterestReduceSchemeDao;
	}
}