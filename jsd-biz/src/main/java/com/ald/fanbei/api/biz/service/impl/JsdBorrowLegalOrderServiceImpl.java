package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowLegalOrderDao;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderDo;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderService;



/**
 * 极速贷ServiceImpl
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:07
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("jsdBorrowLegalOrderService")
public class JsdBorrowLegalOrderServiceImpl extends ParentServiceImpl<JsdBorrowLegalOrderDo, Long> implements JsdBorrowLegalOrderService {
	
    private static final Logger logger = LoggerFactory.getLogger(JsdBorrowLegalOrderServiceImpl.class);
   
    @Resource
    private JsdBorrowLegalOrderDao jsdBorrowLegalOrderDao;

		@Override
	public BaseDao<JsdBorrowLegalOrderDo, Long> getDao() {
		return jsdBorrowLegalOrderDao;
	}
}