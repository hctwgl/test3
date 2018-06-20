package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.DsedLoanPeriodsDao;
import com.ald.fanbei.api.dal.domain.DsedLoanPeriodsDo;
import com.ald.fanbei.api.biz.service.DsedLoanPeriodsService;

import java.util.HashMap;


/**
 * 都市易贷借款期数表ServiceImpl
 * 
 * @author gaojibin
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:44:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("dsedLoanPeriodsService")
public class DsedLoanPeriodsServiceImpl extends ParentServiceImpl<DsedLoanPeriodsDo, Long> implements DsedLoanPeriodsService {
	
    private static final Logger logger = LoggerFactory.getLogger(DsedLoanPeriodsServiceImpl.class);
   
    @Resource
    private DsedLoanPeriodsDao dsedLoanPeriodsDao;

		@Override
	public BaseDao<DsedLoanPeriodsDo, Long> getDao() {
		return dsedLoanPeriodsDao;
	}

	/**
	 * 通过编号查询借款信息
	 * @param loanNo
	 * @author
	 * @returnchefeipeng
	 */
	@Override
	public DsedLoanPeriodsDo getLoanPeriodsByLoanNo(String loanNo,int nper){
		return dsedLoanPeriodsDao.getLoanPeriodsByLoanNo(loanNo,nper);
	}
}