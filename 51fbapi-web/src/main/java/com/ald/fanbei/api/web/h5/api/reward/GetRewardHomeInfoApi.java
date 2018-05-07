package com.ald.fanbei.api.web.h5.api.reward;

import com.ald.fanbei.api.biz.bo.loan.ApplyLoanBo;
import com.ald.fanbei.api.biz.bo.loan.ApplyLoanBo.ReqParam;
import com.ald.fanbei.api.biz.service.AfLoanService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.ApplyLoanParam;
import com.ald.fanbei.api.web.validator.constraints.NeedLogin;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 发起贷款申请
 * @author ZJF
 * @类描述：申请贷款 参考{@link com.ald.fanbei.api.web.api.legalborrowV2.ApplyLegalBorrowCashV2Api}
 */
@NeedLogin
@Component("getRewardHomeInfoApi")
public class GetRewardHomeInfoApi implements H5Handle {

	@Resource
	AfResourceService afResourceService;
	
	@Override
	public H5HandleResponse process(Context context) {
		H5HandleResponse resp = new H5HandleResponse(context.getId(),FanbeiExceptionCode.SUCCESS);
		//活动规则
		AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype("REWARD_RULE");
		if(null != afResourceDo){
			resp.addResponseData("rewardRule",afResourceDo.getValue());
		}else {
			resp.addResponseData("rewardRule","");
		}
		//是否有补签

		//是否有余额

		//签到提醒

		//任务列表

		//banner

		//今天是否签到


		return resp;
	}
	

}
