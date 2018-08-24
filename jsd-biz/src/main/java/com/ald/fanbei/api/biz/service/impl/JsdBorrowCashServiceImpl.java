package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowCashDao;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;



/**
 * 极速贷ServiceImpl
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("jsdBorrowCashService")
public class JsdBorrowCashServiceImpl extends ParentServiceImpl<JsdBorrowCashDo, Long> implements JsdBorrowCashService {
	
    private static final Logger logger = LoggerFactory.getLogger(JsdBorrowCashServiceImpl.class);
   
    @Resource
    private JsdBorrowCashDao jsdBorrowCashDao;

		@Override
	public BaseDao<JsdBorrowCashDo, Long> getDao() {
		return jsdBorrowCashDao;
	}

	@Override
	public JsdBorrowCashDo getByBorrowNo(String borrowNo) {
		return jsdBorrowCashDao.getByBorrowNo(borrowNo);
	}
}