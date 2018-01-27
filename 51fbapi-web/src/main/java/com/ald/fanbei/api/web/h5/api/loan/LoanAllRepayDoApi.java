package com.ald.fanbei.api.web.h5.api.loan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfLoanPeriodsService;
import com.ald.fanbei.api.biz.service.AfLoanRepaymentService;
import com.ald.fanbei.api.biz.service.AfLoanService;
import com.ald.fanbei.api.biz.service.AfRenewalDetailService;
import com.ald.fanbei.api.biz.service.AfRepaymentBorrowCashService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.impl.AfLoanRepaymentServiceImpl.LoanRepayBo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfBorrowCashRepmentStatus;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.dao.AfLoanDao;
import com.ald.fanbei.api.dal.dao.AfLoanPeriodsDao;
import com.ald.fanbei.api.dal.domain.AfLoanDo;
import com.ald.fanbei.api.dal.domain.AfLoanPeriodsDo;
import com.ald.fanbei.api.dal.domain.AfLoanRepaymentDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.validator.Validator;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;


/**  
 * @Description: 白领贷-提前还款
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author yanghailong
 * @date 2018年1月25日
 */
@Component("loanAllRepayDo")
@Validator("LoanAllRepayDoParam")
public class LoanAllRepayDoApi implements H5Handle {
	
	@Resource
	AfUserCouponService afUserCouponService;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfUserBankcardService afUserBankcardService;
	
	@Resource
	AfLoanRepaymentService afLoanRepaymentService;
	@Resource
	AfRepaymentBorrowCashService afRepaymentBorrowCashService;
	@Resource
	AfRenewalDetailService afRenewalDetailService;
	@Resource
	AfLoanService afLoanService;
	@Resource
	AfLoanPeriodsService afLoanPeriodsService;


	@Override
	public H5HandleResponse process(Context context) {
		LoanRepayBo bo = this.extractAndCheck(context);
		bo.remoteIp = context.getClientIp();
		
		this.afLoanRepaymentService.repay(bo);
		
		H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
		Map<String, Object> data = Maps.newHashMap();
		data.put("rid", bo.loanId);
		data.put("amount", bo.repayAmount.setScale(2, RoundingMode.HALF_UP));
		data.put("gmtCreate", new Date());
		data.put("status", AfBorrowCashRepmentStatus.YES.getCode());
		if(bo.userCouponDto != null) {
			data.put("couponAmount", bo.userCouponDto.getAmount());
		}
		if(bo.rebateAmount.compareTo(BigDecimal.ZERO) > 0) {
			data.put("userAmount", bo.rebateAmount);
		}
		data.put("actualAmount", bo.actualAmount);
		data.put("cardName", bo.cardName);
		data.put("cardNumber", bo.cardNo);
		data.put("repayNo", bo.tradeNo);
		data.put("jfbAmount", BigDecimal.ZERO);
		
		resp.setResponseData(data);
		
		return resp;
	}
	
	
	private LoanRepayBo extractAndCheck(Context context) {
		AfUserAccountDo userDo = afUserAccountService.getUserAccountByUserId(context.getUserId());
		if (userDo == null) {
			throw new FanbeiException("Account is invalid", FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
		}
		
		LoanRepayBo bo = new LoanRepayBo();
		bo.userId = context.getUserId();
		bo.userDo = userDo;
		
		Map<String, Object> dataMap = context.getDataMap();
		
		bo.repayAmount = (BigDecimal) dataMap.get("repayAmount");
		bo.rebateAmount = (BigDecimal) dataMap.get("rebateAmount");
		bo.actualAmount = (BigDecimal) dataMap.get("actualAmount");
		bo.payPwd = (String) dataMap.get("payPwd");
		bo.cardId = (Long) dataMap.get("cardId");
		bo.couponId = (Long) dataMap.get("couponId");
		bo.loanId = (Long) dataMap.get("loanId");
//		bo.loanPeriodsId = (Long) dataMap.get("loanPeriodsId");
		
		if (bo.cardId == -1) {// -1-微信支付，-2余额支付，>0卡支付（包含组合支付）
			throw new FanbeiException(FanbeiExceptionCode.WEBCHAT_NOT_USERD);
		}
		if (bo.cardId == -3) {// -3支付宝支付
			throw new FanbeiException(FanbeiExceptionCode.ZFB_NOT_USERD);
		}
		
		checkPwdAndCard(bo);
		checkFrom(bo);
		checkAmount(bo);
		
		return bo;
	}
	
	private void checkPwdAndCard(LoanRepayBo bo) {
		if (bo.cardId == -2 || bo.cardId > 0) { // -1-微信支付，-3支付宝支付，-2余额支付，>0卡支付（包含组合支付）
			String finalPwd = UserUtil.getPassword(bo.payPwd, bo.userDo.getSalt());
			if (!StringUtils.equals(finalPwd, bo.userDo.getPassword())) {
				throw new FanbeiException("Password is error",FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
			}
			
			bo.cardName = Constants.DEFAULT_USER_ACCOUNT;
			
			if(bo.cardId > 0) {
				AfUserBankcardDo card = afUserBankcardService.getUserBankcardById(bo.cardId);
				if (null == card) { throw new FanbeiException(FanbeiExceptionCode.USER_BANKCARD_NOT_EXIST_ERROR); }
				bo.cardName = card.getBankName();
				bo.cardNo = card.getCardNumber();
			}
		}
	}
	
	private void checkFrom(LoanRepayBo bo) {
		AfLoanDo loanDo = null;
		if((loanDo = afLoanService.getById(bo.loanId)) == null ){
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_NOT_EXIST_ERROR);
		}
		bo.loanDo = loanDo;
		
		// 检查当前 借款 是否已在处理中
		AfLoanRepaymentDo loanRepaymentDo = afLoanRepaymentService.getProcessLoanRepaymentByLoanId(bo.loanId);
		if(loanRepaymentDo != null) {
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_REPAY_PROCESS_ERROR);
		}
		
		// 检查 用户 是否多还钱(提前结清)
		BigDecimal shouldRepayAmount = afLoanRepaymentService.calculateAllRestAmount(loanDo.getRid());
		if(bo.repayAmount.compareTo(shouldRepayAmount) != 0) {
			throw new FanbeiException(FanbeiExceptionCode.LOAN_REPAY_AMOUNT_ERROR);
		}
		
		List<AfLoanPeriodsDo> loanPeriodsDoList = afLoanPeriodsService.getNoRepayListByLoanId(loanDo.getRid());
		bo.loanPeriodsDoList = loanPeriodsDoList;
	}
	
	private void checkAmount(LoanRepayBo bo) { //TODO
		AfUserCouponDto userCouponDto = afUserCouponService.getUserCouponById(bo.couponId);
		bo.userCouponDto = userCouponDto;
		if (null != userCouponDto && !userCouponDto.getStatus().equals(CouponStatus.NOUSE.getCode())) {
			logger.error("extractAndCheckParams.coupon" + JSON.toJSONString(userCouponDto));
			throw new FanbeiException(FanbeiExceptionCode.USER_COUPON_ERROR);
		}

		//用户账户余额校验添加
        if(bo.userDo.getRebateAmount().compareTo(bo.rebateAmount)<0){
        	throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_MONEY_LESS);
        }
		
		BigDecimal calculateAmount = bo.repayAmount;
		
		// 使用优惠券结算金额
		if (userCouponDto != null) {
			calculateAmount = BigDecimalUtil.subtract(bo.repayAmount, userCouponDto.getAmount());
			if (calculateAmount.compareTo(BigDecimal.ZERO) <= 0) {
				logger.info(bo.userDo.getUserName() + "coupon repayment");
				bo.rebateAmount = BigDecimal.ZERO;
				calculateAmount = BigDecimal.ZERO;
			}
		}
		
		// 余额处理
		if (bo.rebateAmount.compareTo(BigDecimal.ZERO) > 0 && calculateAmount.compareTo(bo.userDo.getRebateAmount()) > 0) {
			calculateAmount = BigDecimalUtil.subtract(calculateAmount, bo.userDo.getRebateAmount());
			bo.rebateAmount = bo.userDo.getRebateAmount();
		} else if (bo.rebateAmount.compareTo(BigDecimal.ZERO) > 0 && calculateAmount.compareTo(bo.userDo.getRebateAmount()) <= 0) {
			bo.rebateAmount = calculateAmount;
			calculateAmount = BigDecimal.ZERO;
		}
		
		// 对比
		if (bo.actualAmount.compareTo(calculateAmount) != 0) {
			throw new FanbeiException(FanbeiExceptionCode.LOAN_REPAY_AMOUNT_ERROR);
		}
		
	}

}
