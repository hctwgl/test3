package com.ald.fanbei.api.web.h5.api.loan;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowLegalService;
import com.ald.fanbei.api.biz.service.AfLoanService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountSenceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserAuthStatusService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.common.enums.SceneType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountSenceDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.yeepay.g3.utils.common.StringUtils;

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
	@Resource
	private AfUserAuthStatusService afUserAuthStatusService;
	
	@Resource
	private AfUserAccountSenceService afUserAccountSenceService;
	
	@Override
	public H5HandleResponse process(Context context) {
		H5HandleResponse resp = new H5HandleResponse(context.getId(),FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		
		boolean loginFlag = userId == null?false:true;
		if(loginFlag) {
			AfUserAuthDo authInfo = afUserAuthService.getUserAuthInfoByUserId(userId);
			AfUserAccountDo accInfo = userAccountService.getUserAccountByUserId(userId);
			AfUserBankcardDo userBankcard = afUserBankcardService.getUserMainBankcardByUserId(userId);
			
			resp.addResponseData("bldStatus", afUserAuthStatusService.getBldOpenStatus(userId));
			resp.addResponseData("realName", accInfo.getRealName());
			
			AfUserAccountSenceDo totalScene = afUserAccountSenceService.getByUserIdAndScene(SceneType.LOAN_TOTAL.getName(), userId);
			BigDecimal totalAmount = BigDecimal.ZERO;
			if(totalScene == null) { //TOTAL数据初始化
				totalScene = afUserAccountSenceService.initTotalLoan(accInfo);
			}
			totalAmount = totalScene.getAuAmount();
			
			resp.addResponseData("totalAmount",totalAmount);
			resp.addResponseData("useableAmount", afUserAccountSenceService.getTotalUsableAmount(accInfo));
			
			resp.addResponseData("isLogin", true);
			resp.addResponseData("isSecAuthzAllPass", afUserAuthService.allSupplementAuthPassed(authInfo));
			
			String basicStatus = authInfo.getBasicStatus();
			if(StringUtils.equals(basicStatus, "A") || StringUtils.isBlank(basicStatus)) {
				resp.addResponseData("authStatus", false);
			} else {
				resp.addResponseData("authStatus", true);
			}
			
			resp.addResponseData("isBindCard", userBankcard != null);
			resp.addResponseData("isRealAuthz", YesNoStatus.YES.getCode().equals(authInfo.getFacesStatus()));
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