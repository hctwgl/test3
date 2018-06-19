package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.DsedLoanDao;
import com.ald.fanbei.api.dal.domain.DsedLoanDo;
import com.ald.fanbei.api.biz.service.DsedLoanService;



/**
 * 借款ServiceImpl
 * 
 * @author jilong
 * @version 1.0.0 初始化
 * @date 2018-06-19 09:48:48
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("dsedLoanService")
public class DsedLoanServiceImpl extends ParentServiceImpl<DsedLoanDo, Long> implements DsedLoanService {
	
    private static final Logger logger = LoggerFactory.getLogger(DsedLoanServiceImpl.class);
   
    @Resource
    private DsedLoanDao dsedLoanDao;

		@Override
	public BaseDao<DsedLoanDo, Long> getDao() {
		return dsedLoanDao;
	}
}