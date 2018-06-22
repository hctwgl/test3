package com.ald.fanbei.api.web.h5.api.dsed;

import com.ald.fanbei.api.biz.service.AfLoanPeriodsService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.DsedLoanPeriodsService;
import com.ald.fanbei.api.biz.service.DsedUserBankcardService;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.DsedUserBankcardDo;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.validator.constraints.NeedLogin;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * 贷款发起前确认
 * @author ZJF
 */
@NeedLogin
@Component("dsedLoanDetailApi")
public class DsedLoanDetailApi implements H5Handle {

	@Resource
	private DsedLoanPeriodsService dsedLoanPeriodsService;

	@Resource
	private DsedUserBankcardService dsedUserBankcardService;
	
	@Override
	public H5HandleResponse process(Context context) {
		H5HandleResponse resp = new H5HandleResponse(context.getId(),FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();

		String prdType = context.getData("prdType").toString();
		BigDecimal amount = new BigDecimal(context.getData("amount").toString());
		int periods = Integer.valueOf(context.getData("periods").toString());
		
		List<Object> periodDos = dsedLoanPeriodsService.resolvePeriods(amount, context.getUserId(), periods, null, prdType);
		periodDos.remove(0);
		DsedUserBankcardDo cardDo = dsedUserBankcardService.getUserMainBankcardByUserId(userId);
		cardDo.setCardNumber(dsedUserBankcardService.hideCardNumber(cardDo.getCardNumber()));
		
		resp.addResponseData("periodsInfo", periodDos);
		resp.addResponseData("cardInfo", cardDo);
		
		return resp;
	}
	
}
