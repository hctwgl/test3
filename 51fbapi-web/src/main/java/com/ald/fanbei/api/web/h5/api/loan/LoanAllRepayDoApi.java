package com.ald.fanbei.api.web.h5.api.loan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.web.common.*;
import com.ald.fanbei.api.web.validator.bean.LoanRepayDoParam;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.thirdpay.ThirdBizType;
import com.ald.fanbei.api.biz.bo.thirdpay.ThirdPayTypeEnum;
import com.ald.fanbei.api.biz.service.impl.DsedLoanRepaymentServiceImpl.LoanRepayBo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfLoanRepaymentStatus;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.LoanAllRepayDoParam;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;


/**  
 * @Description: 都市e贷-提前还款
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author yanghailong
 * @date 2018年1月25日
 */
@Component("loanAllRepayDoApi")
@Validator("LoanAllRepayDoParam")
public class LoanAllRepayDoApi implements H5Handle {

	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	DsedLoanService dsedLoanService;
	@Resource
	DsedLoanPeriodsService dsedLoanPeriodsService;
	@Resource
	DsedUserBankcardService dsedUserBankcardService;
	@Resource
	DsedUserService dsedUserService;
	@Resource
	DsedLoanRepaymentService dsedLoanRepaymentService;


	@Override
	public H5HandleResponse process(Context context) {
		LoanAllRepayDoParam param = (LoanAllRepayDoParam) context.getParamEntity();
		Map<String, Object> data = new HashMap<String, Object>();
		String bankNo = param.bankNo;
		Long userId = param.userId;
		HashMap<String,Object> map = dsedUserBankcardService.getPayTypeByBankNoAndUserId(userId,bankNo);
		String payType = map.get("bankChannel").toString();
		DsedUserDo dsedUserDo = dsedUserService.getById(userId);
		LoanRepayBo bo = this.extractAndCheck(context, userId);
		bo.dsedUserDo = dsedUserDo;
		bo.remoteIp = context.getClientIp();
		bo.cardName = map.get("bankName").toString();
		data = this.dsedLoanRepaymentService.repay(bo,payType);
		H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
		resp.setResponseData(data);
		return resp;
	}

	
	private LoanRepayBo extractAndCheck(Context context, Long userId) {
		AfUserAccountDo userDo = afUserAccountService.getUserAccountByUserId(userId);
		if (userDo == null) {
			throw new FanbeiException("Account is invalid", FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
		}
		
		LoanRepayBo bo = new LoanRepayBo();
		bo.userId = userId;
		bo.userDo = userDo;

		LoanAllRepayDoParam param = (LoanAllRepayDoParam) context.getParamEntity();
		
		bo.userId = param.userId;
		bo.amount = param.amount;
		bo.bankNo = param.bankNo;
		bo.borrowNo = param.borrowNo;
		bo.isAllRepay = true;	// 标识提前还款
		
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
		DsedLoanDo dsedLoanDo = null;
		if((dsedLoanDo = dsedLoanService.getByLoanNo(bo.borrowNo)) == null ){
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_NOT_EXIST_ERROR);
		}
		bo.dsedLoanDo = dsedLoanDo;
		bo.loanId = dsedLoanDo.getRid();
		
		// 检查当前 借款 是否已在处理中
		DsedLoanRepaymentDo loanRepaymentDo = dsedLoanRepaymentService.getProcessLoanRepaymentByLoanId(dsedLoanDo.getRid());
		if(loanRepaymentDo != null) {
			throw new FanbeiException(FanbeiExceptionCode.LOAN_REPAY_PROCESS_ERROR);
		}
		
		// 检查 用户 是否多还钱(提前结清)
		BigDecimal shouldRepayAmount = dsedLoanRepaymentService.calculateAllRestAmount(dsedLoanDo.getRid());
		if(bo.repaymentAmount.compareTo(shouldRepayAmount) != 0) {
			throw new FanbeiException(FanbeiExceptionCode.LOAN_REPAY_AMOUNT_ERROR);
		}
		
		List<DsedLoanPeriodsDo> dsedLoanPeriodsDoList = dsedLoanPeriodsService.getNoRepayListByLoanId(dsedLoanDo.getRid());
		bo.dsedLoanPeriodsDoList = dsedLoanPeriodsDoList;
	}
	



}
