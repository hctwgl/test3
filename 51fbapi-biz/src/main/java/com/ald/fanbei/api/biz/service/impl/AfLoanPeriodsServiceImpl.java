package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfLoanPeriodsService;
import com.ald.fanbei.api.dal.dao.AfLoanPeriodsDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfLoanPeriodsDo;



/**
 * 贷款业务ServiceImpl
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2018-01-19 16:50:32
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afLoanPeriodsService")
public class AfLoanPeriodsServiceImpl extends ParentServiceImpl<AfLoanPeriodsDo, Long> implements AfLoanPeriodsService {
	
    @Resource
    private AfLoanPeriodsDao afLoanPeriodsDao;
    
    @Override
	public List<AfLoanPeriodsDo> resolvePeriods(BigDecimal amount, Long userId, int periods,
				Long loanId, String loanNo, String prdType){
		
		//TODO 计算每期信息
		
		return null;
	}
    
    public BigDecimal calcuRestAmount(AfLoanPeriodsDo p) {
    	return p.getAmount().add(p.getRepaidServiceFee()).add(p.getRepaidInterestFee()).add(p.getOverdueAmount())
    		.add(p.getServiceFee()).add(p.getInterestFee()).add(p.getOverdueAmount())
    		.subtract(p.getRepayAmount());
    }
    
	@Override
	public BaseDao<AfLoanPeriodsDo, Long> getDao() {
		return afLoanPeriodsDao;
	}
}