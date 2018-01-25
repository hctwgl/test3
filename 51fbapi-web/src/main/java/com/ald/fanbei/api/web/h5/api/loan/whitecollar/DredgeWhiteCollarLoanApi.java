package com.ald.fanbei.api.web.h5.api.loan.whitecollar;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.DredgeWhiteCollarLoanParam;
import com.google.common.collect.Maps;

@Component("dredgeWhiteCollarLoanApi")
@Validator("dredgeWhiteCollarLoanParam")
public class DredgeWhiteCollarLoanApi implements H5Handle {

	@Override
	public H5HandleResponse process(Context context) {
		H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
		Map<String,Object> data = Maps.newHashMap();
		Long userId = context.getUserId();
		// 用户未登录
		if(userId == null) {
			throw new FanbeiException(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR);
		}
		DredgeWhiteCollarLoanParam param = (DredgeWhiteCollarLoanParam) context.getParamEntity();
		
		
		
		resp.setResponseData(data);
		return resp;
	}

}
