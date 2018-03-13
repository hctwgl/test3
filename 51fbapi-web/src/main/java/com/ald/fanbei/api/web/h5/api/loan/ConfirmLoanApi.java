package com.ald.fanbei.api.web.h5.api.loan;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfLoanPeriodsService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.validator.constraints.NeedLogin;

/**
 * 贷款发起前确认
 * @author ZJF
 */
@NeedLogin
@Component("confirmLoanApi")
public class ConfirmLoanApi implements H5Handle {

	@Resource
	private AfLoanPeriodsService afLoanPeriodsService;
	
	@Resource
	private AfUserBankcardService afUserBankcardService;
	
	@Override
	public H5HandleResponse process(Context context) {
		H5HandleResponse resp = new H5HandleResponse(context.getId(),FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		
		String prdType = context.getData("prdType").toString();
		BigDecimal amount = new BigDecimal(context.getData("amount").toString());
		int periods = Integer.valueOf(context.getData("periods").toString());
		
		List<Object> periodDos = afLoanPeriodsService.resolvePeriods(amount, context.getUserId(), periods, null, prdType);
		periodDos.remove(0);
		AfUserBankcardDo cardDo = afUserBankcardService.getUserMainBankcardByUserId(userId);
		cardDo.setCardNumber(afUserBankcardService.hideCardNumber(cardDo.getCardNumber()));
		
		resp.addResponseData("periodsInfo", periodDos);
		resp.addResponseData("cardInfo", cardDo);
		
		return resp;
	}
	
}
