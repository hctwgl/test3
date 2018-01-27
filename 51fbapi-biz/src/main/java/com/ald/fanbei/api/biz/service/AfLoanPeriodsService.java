package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.List;

import com.ald.fanbei.api.dal.domain.AfLoanPeriodsDo;

/**
 * 贷款业务Service
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2018-01-19 16:50:32
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfLoanPeriodsService extends ParentService<AfLoanPeriodsDo, Long>{
	
	List<Object> resolvePeriods(BigDecimal amount, Long userId, int periods, String loanNo, String prdType);
	
	BigDecimal calcuRestAmount(AfLoanPeriodsDo period);

	AfLoanPeriodsDo getLastActivePeriodByLoanId(Long loanId);

	List<AfLoanPeriodsDo> listByLoanId(Long loanId);

	List<AfLoanPeriodsDo> getNoRepayListByLoanId(Long rid);
	
}
