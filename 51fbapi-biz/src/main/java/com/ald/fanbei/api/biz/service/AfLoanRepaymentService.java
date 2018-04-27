package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ald.fanbei.api.biz.service.impl.AfLoanRepaymentServiceImpl.LoanRepayBo;
import com.ald.fanbei.api.dal.domain.AfLoanDo;
import com.ald.fanbei.api.dal.domain.AfLoanPeriodsDo;
import com.ald.fanbei.api.dal.domain.AfLoanRepaymentDo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 贷款业务Service
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2018-01-19 16:50:32
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfLoanRepaymentService{
	
    	Map<String, Object> repay(LoanRepayBo bo,String bankPayType);
	
	void offlineRepay(AfLoanDo loanDo, String loanNo,
					  String repayType, String repayAmount,
					  String restAmount, String outTradeNo, String isBalance, String repayCardNum, String operator, String isAdmin, boolean isAllRepay, Long repaymentId, List<HashMap> periodsList);

	void dealRepaymentSucess(String tradeNo, String outTradeNo);
	void dealRepaymentSucess(String tradeNo, String outTradeNo, final AfLoanRepaymentDo repaymentDo,String operator,Long collectionRepaymentId,List<HashMap> periodsList);
	
	void dealRepaymentFail(String outTradeNo, String tradeNo,boolean isNeedMsgNotice,String errorMsg);
	
	/**
	 * 计算每期应还的金额
	 * @return
	 */
	BigDecimal calculateRestAmount(Long loanPeriodsId);

	/**
	 * 计算提前还款应还的金额
	 * @return
	 */
	BigDecimal calculateAllRestAmount(Long rid);

	BigDecimal calculateBillRestAmount(Long rid);

	/**
	 * 判断当前分期是否可以还款（是否已出账）
	 * @return
	 */
	boolean canRepay(AfLoanPeriodsDo loanPeriodsDo);
	
	
	/**
	 * @Description:  根据还款金额，匹配实际需还的期数信息
	 * @return  List<AfLoanPeriodsDo>
	 */
	public List<AfLoanPeriodsDo> getLoanPeriodsIds(Long loanId, BigDecimal repaymentAmount);

	AfLoanRepaymentDo getProcessLoanRepaymentByLoanId(Long loanId);

	/**
	 * 获取还款处理中的数量
	 *
	 * @author wangli
	 * @date 2018/4/14 12:23
	 */
	int getProcessLoanRepaymentNumByLoanId(Long loanId);

	List<AfLoanRepaymentDo> listDtoByLoanId(Long loanId);
}
