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
		// Request
		ApplyLoanBo bo = new ApplyLoanBo();
		BeanUtil.copyProperties(bo.reqParam, param);
		bo.userId = context.getUserId();
		afLoanService.doLoan(bo);
		
		// Response
		
		return resp;
	}
	
}
