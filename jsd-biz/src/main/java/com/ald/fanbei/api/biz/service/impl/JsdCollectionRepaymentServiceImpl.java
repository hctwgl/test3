package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdCollectionRepaymentDao;
import com.ald.fanbei.api.dal.domain.JsdCollectionRepaymentDo;
import com.ald.fanbei.api.biz.service.JsdCollectionRepaymentService;



/**
 * ServiceImpl
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-09-16 11:51:40
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("jsdCollectionRepaymentService")
public class JsdCollectionRepaymentServiceImpl extends ParentServiceImpl<JsdCollectionRepaymentDo, Long> implements JsdCollectionRepaymentService {
	
    private static final Logger logger = LoggerFactory.getLogger(JsdCollectionRepaymentServiceImpl.class);
   
    @Resource
    private JsdCollectionRepaymentDao jsdCollectionRepaymentDao;

		@Override
	public BaseDao<JsdCollectionRepaymentDo, Long> getDao() {
		return jsdCollectionRepaymentDao;
	}

	@Override
	public JsdCollectionRepaymentDo getByRepayNo(String repayNo){
			return jsdCollectionRepaymentDao.getByRepayNo(repayNo);
	}
}