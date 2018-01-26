package com.ald.fanbei.api.web.h5.api.loan;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowLegalService;
import com.ald.fanbei.api.biz.service.AfLoanService;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;

/**
 * 获取借钱首页信息
 * @author ZJF
 * @类描述：申请贷款 参考{@link com.ald.fanbei.api.web.api.legalborrowV2.GetLegalBorrowCashHomeInfoV2Api}
 */
@Component("getLoanHomeInfoApi")
public class GetLoanHomeInfoApi implements H5Handle {

	@Resource
	private AfBorrowLegalService afBorrowLegalService;
	
	@Resource
	private AfLoanService afLoanService;
	
	@Override
	public H5HandleResponse process(Context context) {
		H5HandleResponse resp = new H5HandleResponse(context.getId(),FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		
		resp.addResponseData("bannerList", null);// banner 信息 TODO
		resp.addResponseData("bldInfo", afLoanService.getHomeInfo(userId));
		resp.addResponseData("xdInfo", afBorrowLegalService.getHomeInfo(userId));
		
		return resp;
	}
	
}