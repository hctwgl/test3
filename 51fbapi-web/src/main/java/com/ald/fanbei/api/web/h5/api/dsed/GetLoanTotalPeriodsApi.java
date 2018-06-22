package com.ald.fanbei.api.web.h5.api.dsed;

import com.ald.fanbei.api.biz.service.AfLoanPeriodsService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.DsedLoanProductService;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
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
@Component("dsedLoanTotalPeriodsApi")
public class GetLoanTotalPeriodsApi implements H5Handle {

	@Resource
	private DsedLoanProductService dsedLoanProductService;

	@Override
	public H5HandleResponse process(Context context) {
		H5HandleResponse resp = new H5HandleResponse(context.getId(),FanbeiExceptionCode.SUCCESS);
		String prdType = context.getData("prdType").toString();
		Integer periods = dsedLoanProductService.getMaxPeriodsByPrdType(prdType);
		resp.addResponseData("periods", periods);
		return resp;
	}
	
}
