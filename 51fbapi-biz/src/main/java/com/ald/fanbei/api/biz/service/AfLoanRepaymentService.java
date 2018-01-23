package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;

import com.ald.fanbei.api.biz.service.impl.AfLoanRepaymentServiceImpl.LoanRepayBo;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfLoanDo;
import com.ald.fanbei.api.dal.domain.AfLoanRepaymentDo;
import com.ald.fanbei.api.dal.domain.AfRepaymentBorrowCashDo;

/**
 * 贷款业务Service
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2018-01-19 16:50:32
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfLoanRepaymentService extends ParentService<AfLoanRepaymentDo, Long>{
	
	void repay(LoanRepayBo bo);
	
	void offlineRepay(AfBorrowCashDo cashDo, String borrowNo, 
			String repayType, String repayTime, String repayAmount,
			String restAmount, String outTradeNo, String isBalance,String repayCardNum,String operator,String isAdmin);
	
	void dealRepaymentSucess(String tradeNo, String outTradeNo);
	void dealRepaymentSucess(String tradeNo, String outTradeNo, final AfRepaymentBorrowCashDo repaymentDo,String operator);
	
	void dealRepaymentFail(String outTradeNo, String tradeNo,boolean isNeedMsgNotice,String errorMsg);
	
	/**
	 * 计算剩余应还的金额
	 * @param cashDo
	 * @param orderCashDo
	 * @return
	 */
	BigDecimal calculateRestAmount(Long loanPeriodsId);

	AfLoanRepaymentDo getProcessLoanRepaymentByLoanId(Long loanId);
}
