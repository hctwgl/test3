package com.ald.fanbei.api.web.h5.api.loan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;

/**
 * 获取借钱首页信息
 * @author ZJF
 * @类描述：申请贷款 参考{@link com.ald.fanbei.api.web.api.legalborrowV2.GetLegalBorrowCashHomeInfoV2Api}
 */
@Component("getBorrowHomeInfoApi")
public class GetBorrowHomeInfoApi implements H5Handle {

	@Override
	public H5HandleResponse process(Context context) {
		H5HandleResponse resp = new H5HandleResponse(context.getId(),FanbeiExceptionCode.SUCCESS);
		
		// banner 信息
		List<Map<String,Object>> bannerList = new ArrayList<>();
		resp.addResponseData("bannerList", bannerList);
		
		
		// 获取白领贷(bld)信息 TODO 
		Map<String,Object> bldInfo = new HashMap<>();
		resp.addResponseData("bldInfo", bldInfo);
		
		
		// 获取小额贷(xd)信息 TODO 
		Map<String, Object> xdInfo = new HashMap<>();
		resp.addResponseData("xdInfo", xdInfo);
		
		return resp;
	}
	
}