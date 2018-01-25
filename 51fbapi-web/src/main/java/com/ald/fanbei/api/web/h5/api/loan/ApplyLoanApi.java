package com.ald.fanbei.api.web.h5.api.loan;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.loan.ApplyLoanBo;
import com.ald.fanbei.api.biz.service.AfLoanService;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BeanUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.ApplyLoanParam;

/**
 * 发起贷款申请
 * @author ZJF
 * @类描述：申请贷款 参考{@link com.ald.fanbei.api.web.api.legalborrowV2.ApplyLegalBorrowCashV2Api}
 */
@Component("applyLoanApi")
@Validator("applyLoanParam")
public class ApplyLoanApi implements H5Handle {

	@Resource
	private AfLoanService afLoanService;
	
	@Override
	public H5HandleResponse process(Context context) {
		H5HandleResponse resp = new H5HandleResponse(context.getId(),FanbeiExceptionCode.SUCCESS);
		
		ApplyLoanBo bo = new ApplyLoanBo();
		BeanUtil.copyProperties(bo.reqParam, (ApplyLoanParam)context.getParamEntity());
		bo.reqParam.ip = "";		// TODO
		bo.reqParam.appType = ""; 	// TODO
		bo.userId = context.getUserId();
		bo.userName = context.getUserName();
		
		afLoanService.doLoan(bo);
		
		return resp;
	}
	
}
