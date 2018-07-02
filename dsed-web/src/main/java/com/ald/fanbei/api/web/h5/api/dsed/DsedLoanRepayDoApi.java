package com.ald.fanbei.api.web.h5.api.dsed;

import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.service.impl.DsedLoanRepaymentServiceImpl.LoanRepayBo;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.web.common.DsedH5Handle;
import com.ald.fanbei.api.web.common.DsedH5HandleResponse;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.LoanRepayDoParam;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


/**  
 * @Description: dsed 确认还款
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author chefeipeng
 * @date 2018年6月22日
 */
@Component("dsedLoanRepayDoApi")
@Validator("LoanRepayDoParam")
public class DsedLoanRepayDoApi implements DsedH5Handle {
	

	@Resource
	DsedUserService dsedUserService;
	@Resource
	DsedUserBankcardService dsedUserBankcardService;
	@Resource
	DsedLoanPeriodsService dsedLoanPeriodsService;
	@Resource
	DsedLoanRepaymentService dsedLoanRepaymentService;
	@Resource
	DsedLoanService dsedLoanService;



	@Override
	public DsedH5HandleResponse process(Context context) {
		DsedH5HandleResponse resp = new DsedH5HandleResponse(200, "成功");
		LoanRepayDoParam param = (LoanRepayDoParam) context.getParamEntity();
		Map<String, Object> data = new HashMap<String, Object>();
		String bankNo = param.bankNo;
		Long userId = context.getUserId();
		HashMap<String,Object> map = dsedUserBankcardService.getPayTypeByBankNoAndUserId(userId,bankNo);
		String payType = map.get("bankChannel").toString();
		DsedUserDo dsedUserDo = dsedUserService.getById(userId);
		LoanRepayBo bo = this.extractAndCheck(context, userId);
		bo.dsedUserDo = dsedUserDo;
		bo.remoteIp = context.getClientIp();
		bo.bankNo = bankNo;
		bo.cardName = map.get("bankName").toString();
		data = this.dsedLoanRepaymentService.repay(bo,payType);
		Map<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("payMethod",payType);
		hashMap.put("busiFlag",bo.tradeNo);
		resp.setData(hashMap);
		return resp;
	}


	private LoanRepayBo extractAndCheck(Context context, Long userId) {
		LoanRepayBo bo = new LoanRepayBo();
		bo.userId = userId;
		LoanRepayDoParam param = (LoanRepayDoParam) context.getParamEntity();
		bo.amount = param.amount;
		bo.borrowNo = param.borrowNo;
		bo.bankNo = param.bankNo;
		bo.curPeriod = param.curPeriod;
		bo.isAllRepay = false;	// 标识提前还款
		checkPwdAndCard(bo);
		checkFrom(bo);
		return bo;
	}

	private void checkPwdAndCard(LoanRepayBo bo) {
		HashMap<String,Object> map = dsedUserBankcardService.getPayTypeByBankNoAndUserId(bo.userId,bo.bankNo);
		if (null == map) {
			throw new FanbeiException("user not exist error", FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
		}
		//还款金额是否大于银行单笔限额
		dsedUserBankcardService.checkUpsBankLimit(map.get("bankCode").toString(), map.get("bankChannel").toString(), bo.amount);
		bo.cardName = map.get("bankName").toString();
		bo.cardNo = map.get("cardNumber").toString();
	}
	
	private void checkFrom(LoanRepayBo bo) {
		DsedLoanDo dsedLoanDo = null;
		if((dsedLoanDo = dsedLoanService.getByLoanNo(bo.borrowNo)) == null ){
			throw new FanbeiException("borrow cash not exist",FanbeiExceptionCode.BORROW_CASH_NOT_EXIST_ERROR);
		}
		bo.dsedLoanDo = dsedLoanDo;
		bo.loanId = dsedLoanDo.getRid();
		// 检查当前 借款 是否已在处理中
		DsedLoanRepaymentDo dsedLoanRepaymentDo = dsedLoanRepaymentService.getProcessLoanRepaymentByLoanId(dsedLoanDo.getRid());
		if(dsedLoanRepaymentDo != null) {
			throw new FanbeiException("loan repay not exist",FanbeiExceptionCode.LOAN_REPAY_PROCESS_ERROR);
		}


		// 检查 用户还钱金额是否准确
		DsedLoanPeriodsDo dsedLoanPeriodsDo = dsedLoanPeriodsService.getLoanPeriodsByLoanNoAndNper(bo.borrowNo,bo.curPeriod);
		BigDecimal shouldRepayAmount = dsedLoanRepaymentService.calculateRestAmount(dsedLoanPeriodsDo);
		bo.dsedLoanPeriodsDoList.add(dsedLoanPeriodsDo);
		bo.repaymentAmount = dsedLoanPeriodsDo.getAmount();
		if(bo.amount.compareTo(shouldRepayAmount) > 0) {
			throw new FanbeiException("loan repay amount error",FanbeiExceptionCode.LOAN_REPAY_AMOUNT_ERROR);
		}

	}
	


}
