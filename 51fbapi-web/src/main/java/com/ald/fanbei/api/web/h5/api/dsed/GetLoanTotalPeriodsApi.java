package com.ald.fanbei.api.web.h5.api.dsed;


import com.ald.fanbei.api.biz.service.DsedLoanProductService;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.web.common.DsedH5HandleResponse;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.validator.constraints.NeedLogin;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * 贷款发起前确认
 * @author ZJF
 */
@Component("dsedLoanTotalPeriodsApi")
public class GetLoanTotalPeriodsApi implements H5Handle {

	@Resource
	private DsedLoanProductService dsedLoanProductService;

	@Override
	public DsedH5HandleResponse process(Context context) {
		HashMap<String,Object> data = new HashMap<String,Object>();
		DsedH5HandleResponse resp = new DsedH5HandleResponse("1","",data);
//		String prdType = context.getData("prdType").toString();
		String prdType = "DSED_LOAN";
		Integer periods = dsedLoanProductService.getMaxPeriodsByPrdType(prdType);

		data.put("periods", periods);
		return resp;
	}
	
}
