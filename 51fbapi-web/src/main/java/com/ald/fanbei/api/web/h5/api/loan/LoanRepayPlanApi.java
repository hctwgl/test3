package com.ald.fanbei.api.web.h5.api.loan;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;

/**
 * @Description: 白领贷还款计划
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author yanghailong
 * @date 2018年1月22日
 */
@Component("loanRepayPlanApi")
public class LoanRepayPlanApi implements H5Handle {

	@Resource
	AfResourceService afResourceService;
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfUserBankcardService afUserBankcardService;
	@Resource
	AfUserAccountService afUserAccountService;
	
	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	RiskUtil riskUtil;
	
	@Override
	public H5HandleResponse process(Context context) {
		
		H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
		Long loanId = (Long) context.getData("loanId");
		
		
		return resp;
	}

}
