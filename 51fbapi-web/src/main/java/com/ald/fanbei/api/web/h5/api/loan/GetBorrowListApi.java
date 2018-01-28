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
 * H5借钱首页-右上角获取借钱记录
 * @author ZJF
 */
@Component("getBorrowListApi")
public class GetBorrowListApi implements H5Handle {

	@Resource
	private AfBorrowLegalService afBorrowLegalService;
	@Resource
	private AfLoanService afLoanService;
	
	@Override
	public H5HandleResponse process(Context context) {
		H5HandleResponse resp = new H5HandleResponse(context.getId(),FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		
		// TODO sink数据
		
		return resp;
	}
	
}
