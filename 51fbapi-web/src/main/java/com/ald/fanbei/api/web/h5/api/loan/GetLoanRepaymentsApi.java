package com.ald.fanbei.api.web.h5.api.loan;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfLoanRepaymentService;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfLoanRepaymentDo;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;

/**
 * @Description: 白领贷借款信息
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author yanghailong
 * @date 2018年1月22日
 */
@Component("getLoanRepaymentsApi")
public class GetLoanRepaymentsApi implements H5Handle {
	
	@Resource
	private AfLoanRepaymentService afLoanRepaymentService;
	
	@Override
	public H5HandleResponse process(Context context) {
		H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
		Long loanId = Long.valueOf(context.getData("loanId").toString());
		
		List<AfLoanRepaymentDo> rs = afLoanRepaymentService.listDtoByLoanId(loanId);
		resp.setResponseData(rs);
		return resp;
	}
	
}
