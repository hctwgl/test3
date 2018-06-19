package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.DsedLoanRepaymentDao;
import com.ald.fanbei.api.dal.domain.DsedLoanRepaymentDo;
import com.ald.fanbei.api.biz.service.DsedLoanRepaymentService;



/**
 * 都市易贷借款还款表ServiceImpl
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:45:15
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("dsedLoanRepaymentService")
public class DsedLoanRepaymentServiceImpl extends ParentServiceImpl<DsedLoanRepaymentDo, Long> implements DsedLoanRepaymentService {
	
    private static final Logger logger = LoggerFactory.getLogger(DsedLoanRepaymentServiceImpl.class);
   
    @Resource
    private DsedLoanRepaymentDao dsedLoanRepaymentDao;

		@Override
	public BaseDao<DsedLoanRepaymentDo, Long> getDao() {
		return dsedLoanRepaymentDao;
	}
}