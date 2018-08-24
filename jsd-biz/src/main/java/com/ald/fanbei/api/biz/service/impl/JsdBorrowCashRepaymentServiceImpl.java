package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowCashRepaymentDao;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRepaymentDo;
import com.ald.fanbei.api.biz.service.JsdBorrowCashRepaymentService;



/**
 * 极速贷ServiceImpl
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("jsdBorrowCashRepaymentService")
public class JsdBorrowCashRepaymentServiceImpl extends ParentServiceImpl<JsdBorrowCashRepaymentDo, Long> implements JsdBorrowCashRepaymentService {
	
    private static final Logger logger = LoggerFactory.getLogger(JsdBorrowCashRepaymentServiceImpl.class);
   
    @Resource
    private JsdBorrowCashRepaymentDao jsdBorrowCashRepaymentDao;

		@Override
	public BaseDao<JsdBorrowCashRepaymentDo, Long> getDao() {
		return jsdBorrowCashRepaymentDao;
	}

	@Override
	public JsdBorrowCashRepaymentDo getLastByBorrowId(Long borrowId) {
		return jsdBorrowCashRepaymentDao.getLastByBorrowId(borrowId);
	}
}