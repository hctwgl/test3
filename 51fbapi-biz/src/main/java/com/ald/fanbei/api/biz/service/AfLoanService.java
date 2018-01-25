package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;

import com.ald.fanbei.api.biz.bo.loan.ApplyLoanBo;
import com.ald.fanbei.api.biz.bo.loan.LoanDBCfgBo;
import com.ald.fanbei.api.biz.bo.loan.LoanHomeInfoBo;
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
	
	/**
	 * 获取贷款首页信息
	 * @param userId
	 * @return
	 */
	LoanHomeInfoBo getHomeInfo(Long userId);
	
	/**
	 * 执行借贷
	 * @param bo
	 */
	void doLoan(ApplyLoanBo bo);
	
	/**
	 * 借贷成功回调
	 * @param loanId
	 * @param loanNo
	 * @param tradeNoOut
	 */
	void dealLoanSucc(String loanNo, String tradeNoOut);
	
	/**
	 * 借贷失败回调
	 * @param loanId
	 * @param loanNo
	 * @param tradeNoOut
	 */
	void dealLoanFail(String loanNo, String tradeNoOut, String msgOut);
	
	/**
	 * 获取数据库中配置有关贷款的全部配置信息
	 * @return
	 */
	LoanDBCfgBo getDBCfg();
	
	/**
	 * 获取用户白领贷分层日利率
	 * @param userId
	 * @return
	 */
	BigDecimal getUserLayDailyRate(Long userId);
	
}
