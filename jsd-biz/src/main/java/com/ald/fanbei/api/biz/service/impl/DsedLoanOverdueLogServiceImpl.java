package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.common.util.DateUtil;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.DsedLoanOverdueLogService;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.DsedLoanOverdueLogDao;
import com.ald.fanbei.api.dal.domain.DsedLoanOverdueLogDo;

import java.util.Date;


/**
 * 都市e贷借款逾期记录表ServiceImpl
 * 
 * @author gaojibin
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:42:43
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("dsedLoanOverdueLogService")
public class DsedLoanOverdueLogServiceImpl extends ParentServiceImpl<DsedLoanOverdueLogDo, Long> implements DsedLoanOverdueLogService {
	
    @Resource
    private DsedLoanOverdueLogDao dsedLoanOverdueLogDao;

		@Override
	public BaseDao<DsedLoanOverdueLogDo, Long> getDao() {
		return dsedLoanOverdueLogDao;
	}

	@Override
	public int addLoanOverdueLog(DsedLoanOverdueLogDo loanOverdueLogDo) {
		return dsedLoanOverdueLogDao.addLoanOverdueLog(loanOverdueLogDo) ;
	}

	@Override
	public DsedLoanOverdueLogDo getLoanOverDueLogByNow(String  periodsId ) {
		return dsedLoanOverdueLogDao.getLoanOverDueLogByNow(periodsId);
	}
}