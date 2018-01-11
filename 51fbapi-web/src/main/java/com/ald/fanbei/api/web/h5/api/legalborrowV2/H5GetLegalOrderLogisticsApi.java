package com.ald.fanbei.api.web.h5.api.legalborrowV2;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;

@Component("h5GetLegalOrderLogisticsApi")
public class H5GetLegalOrderLogisticsApi implements H5Handle{

	@Override
	public H5HandleResponse process(Context context) {
		H5HandleResponse resp = new H5HandleResponse(context.getId(),FanbeiExceptionCode.SUCCESS);
		
		
		return resp;
	}

}
