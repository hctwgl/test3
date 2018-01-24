package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.biz.bo.loan.ApplyLoanBo;
import com.ald.fanbei.api.biz.service.impl.AfLoanServiceImpl.LoanHomeInfoBo;
import com.ald.fanbei.api.dal.domain.AfLoanDo;

/**
 * 贷款业务Service
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2018-01-19 16:50:31
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfLoanService extends ParentService<AfLoanDo, Long>{
	
	LoanHomeInfoBo getHomeInfo(Long userId);
	
	void doLoan(ApplyLoanBo bo);
	
}
