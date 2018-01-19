package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfLoanProductDao;
import com.ald.fanbei.api.dal.domain.AfLoanProductDo;
import com.ald.fanbei.api.biz.service.AfLoanProductService;



/**
 * 贷款业务ServiceImpl
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2018-01-19 16:50:31
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afLoanProductService")
public class AfLoanProductServiceImpl extends ParentServiceImpl<AfLoanProductDo, Long> implements AfLoanProductService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfLoanProductServiceImpl.class);
   
    @Resource
    private AfLoanProductDao afLoanProductDao;

		@Override
	public BaseDao<AfLoanProductDo, Long> getDao() {
		return afLoanProductDao;
	}
}