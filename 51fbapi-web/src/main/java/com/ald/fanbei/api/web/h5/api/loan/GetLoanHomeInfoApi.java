package com.ald.fanbei.api.web.h5.api.loan;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowLegalService;
import com.ald.fanbei.api.biz.service.AfLoanService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
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
	@Resource
	private AfResourceService afResourceService;
	@Resource
	private AfUserAccountService userAccountService;
	@Resource
	private AfUserAuthService afUserAuthService;
	@Resource
	private AfUserBankcardService afUserBankcardService;
	
	@Override
	public H5HandleResponse process(Context context) {
		H5HandleResponse resp = new H5HandleResponse(context.getId(),FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		
		boolean loginFlag = userId == null?false:true;
		if(loginFlag) {
			AfUserAuthDo authInfo = afUserAuthService.getUserAuthInfoByUserId(userId);
			AfUserAccountDo accInfo = userAccountService.getUserAccountByUserId(userId);
			AfUserBankcardDo userBankcard = afUserBankcardService.getUserMainBankcardByUserId(userId);
			
			resp.addResponseData("isLogin", true);
			resp.addResponseData("isSecAuthzAllPass", afUserAuthService.allBasicAuthPassed(authInfo));
			resp.addResponseData("isBindCard", userBankcard != null);
			resp.addResponseData("isRealAuthz", YesNoStatus.YES.getCode().equals(authInfo.getRealnameStatus()));
			resp.addResponseData("rebateAmount", accInfo.getRebateAmount());
		}else {
			resp.addResponseData("isLogin", false);
			resp.addResponseData("isSecAuthzAllPass", false);
			resp.addResponseData("isBindCard", false);
			resp.addResponseData("isRealAuthz", false);
		}
		
		resp.addResponseData("bannerList", afResourceService.getLoanHomeListByType());
		resp.addResponseData("loanInfos", afLoanService.getHomeInfo(userId));
		resp.addResponseData("xdInfo", afBorrowLegalService.getHomeInfo(userId));
		
		return resp;
	}
	
}