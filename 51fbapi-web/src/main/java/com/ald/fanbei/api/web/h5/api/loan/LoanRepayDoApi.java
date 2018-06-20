package com.ald.fanbei.api.web.h5.api.loan;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.enums.RepayType;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.*;
import org.springframework.stereotype.Component;


import com.ald.fanbei.api.biz.service.impl.DsedLoanRepaymentServiceImpl.LoanRepayBo;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.LoanRepayDoParam;


/**  
 * @Description: 白领贷-还款
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author yanghailong
 * @date 2018年1月22日
 */
@Component("loanRepayDoApi")
@Validator("LoanRepayDoParam")
public class LoanRepayDoApi implements ApiHandle {
	

	@Resource
	DsedUserService dsedUserService;
	@Resource
	DsedUserBankcardService dsedUserBankcardService;
	@Resource
	DsedLoanPeriodsService dsedLoanPeriodsService;
	@Resource
	DsedLoanRepaymentService dsedLoanRepaymentService;



	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		LoanRepayDoParam param = (LoanRepayDoParam) requestDataVo.getParamObj();
		Map<String, Object> data = new HashMap<String, Object>();
		String bankNo = param.bankNo;
		Long userId = param.userId;
		HashMap<String,Object> map = dsedUserBankcardService.getPayTypeByBankNoAndUserId(userId,bankNo);
		String payType = map.get("bankChannel").toString();
		DsedUserDo dsedUserDo = dsedUserService.getById(userId);
		if(StringUtil.equals(RepayType.WITHHOLD.getCode(),payType)){
			LoanRepayBo bo = this.extractAndCheck(requestDataVo, userId);
			bo.dsedUserDo = dsedUserDo;
			bo.remoteIp = CommonUtil.getIpAddr(request);
			bo.bankNo = bankNo;
			bo.cardName = map.get("bankName").toString();
			data = this.dsedLoanRepaymentService.repay(bo,payType);
			resp.setResponseData(data);
		}else if(StringUtil.equals(RepayType.KUAIJIE.getCode(),payType)){
			resp.setResponseData(data);
		}
		return resp;
	}
	
	
	private LoanRepayBo extractAndCheck(RequestDataVo requestDataVo, Long userId) {
		LoanRepayBo bo = new LoanRepayBo();
		bo.userId = userId;
		LoanRepayDoParam param = (LoanRepayDoParam) requestDataVo.getParamObj();
		bo.amount = param.amount;
		bo.borrowNo = param.borrowNo;
		bo.bankNo = param.bankNo;
		bo.curPeriod = param.curPeriod;
		checkPwdAndCard(bo);
		checkFrom(bo);
		return bo;
	}

	private void checkPwdAndCard(LoanRepayBo bo) {
		HashMap<String,Object> map = dsedUserBankcardService.getPayTypeByBankNoAndUserId(bo.userId,bo.bankNo);
		if (null == map) {
			throw new FanbeiException(FanbeiExceptionCode.USER_BANKCARD_NOT_EXIST_ERROR);
		}
		//还款金额是否大于银行单笔限额
		dsedUserBankcardService.checkUpsBankLimit(map.get("bankCode").toString(), map.get("bankChannel").toString(), bo.actualAmount);
		bo.cardName = map.get("bankName").toString();
		bo.cardNo = map.get("cardNumber").toString();
	}
	
	private void checkFrom(LoanRepayBo bo) {
		DsedLoanPeriodsDo dsedLoanPeriodsDo = null;
		if((dsedLoanPeriodsDo = dsedLoanPeriodsService.getLoanPeriodsByLoanNo(bo.borrowNo,bo.curPeriod)) == null ){
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_NOT_EXIST_ERROR);
		}
		bo.loanPeriodsDo = dsedLoanPeriodsDo;
		// 检查当前 借款 是否已在处理中
		DsedLoanRepaymentDo dsedLoanRepaymentDo = dsedLoanRepaymentService.getProcessLoanRepaymentByLoanId(dsedLoanPeriodsDo.getLoanId());
		if(dsedLoanRepaymentDo != null) {
			throw new FanbeiException(FanbeiExceptionCode.LOAN_REPAY_PROCESS_ERROR);
		}
		// 检查 用户还钱金额是否准确
		BigDecimal shouldRepayAmount = dsedLoanRepaymentService.calculateRestAmount(dsedLoanPeriodsDo);
		if(bo.amount.compareTo(shouldRepayAmount) > 0) {
			throw new FanbeiException(FanbeiExceptionCode.LOAN_REPAY_AMOUNT_ERROR);
		}
		
	}
	


}
