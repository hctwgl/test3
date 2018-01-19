package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfLoanRepaymentDao;
import com.ald.fanbei.api.dal.domain.AfLoanRepaymentDo;
import com.ald.fanbei.api.biz.service.AfLoanRepaymentService;



/**
 * 贷款业务ServiceImpl
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2018-01-19 16:50:32
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afLoanRepaymentService")
public class AfLoanRepaymentServiceImpl extends ParentServiceImpl<AfLoanRepaymentDo, Long> implements AfLoanRepaymentService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfLoanRepaymentServiceImpl.class);
   
    @Resource
    private AfLoanRepaymentDao afLoanRepaymentDao;

		@Override
	public BaseDao<AfLoanRepaymentDo, Long> getDao() {
		return afLoanRepaymentDao;
	}
}