package com.ald.fanbei.api.web.h5.api.loan;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.loan.ApplyLoanBo;
import com.ald.fanbei.api.biz.bo.loan.ApplyLoanBo.ReqParam;
import com.ald.fanbei.api.biz.service.AfLoanService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.ApplyLoanParam;
import com.ald.fanbei.api.web.validator.constraints.NeedLogin;

/**
 * 发起贷款申请
 * @author ZJF
 * @类描述：申请贷款 参考{@link com.ald.fanbei.api.web.api.legalborrowV2.ApplyLegalBorrowCashV2Api}
 */
@NeedLogin
@Component("applyLoanApi")
@Validator("applyLoanParam")
public class ApplyLoanApi implements H5Handle {

	@Resource
	private AfLoanService afLoanService;
	@Resource
	private AfUserService afUserService;
	
	@Override
	public H5HandleResponse process(Context context) {
		H5HandleResponse resp = new H5HandleResponse(context.getId(),FanbeiExceptionCode.SUCCESS);
		
		ApplyLoanBo bo = new ApplyLoanBo();
		map((ApplyLoanParam)context.getParamEntity(), bo);
		
		bo.reqParam.ip = context.getClientIp();
		String reqId = context.getId();
		bo.reqParam.appType = reqId.startsWith("i") ? "alading_ios" : "alading_and";
		bo.reqParam.appName = reqId.substring(reqId.lastIndexOf("_") + 1, reqId.length());
		
		bo.userId = context.getUserId();
		bo.userName = context.getUserName();
		
		afUserService.checkPayPwd(bo.reqParam.payPwd, context.getUserId());
		
		afLoanService.doLoan(bo);
		
		return resp;
	}
	
	public void map(ApplyLoanParam p, ApplyLoanBo bo) {
		ReqParam rp = bo.reqParam;
		rp.amount = p.amount;
		rp.prdType = p.prdType;
		rp.periods = p.periods;
		rp.payPwd = p.payPwd;
		rp.remark = p.remark;
		rp.loanRemark = p.loanRemark;
		rp.repayRemark = p.repayRemark;
		rp.city = p.city;
		rp.province = p.province;
		rp.county = p.county;
		rp.address = p.address;
		rp.latitude = p.latitude;
		rp.longitude = p.longitude;
		rp.blackBox = p.blackBox;
		rp.bqsBlackBox = p.bqsBlackBox;
	}
	
}
