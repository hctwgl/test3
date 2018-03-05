package com.ald.fanbei.api.web.api.auth;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.SupplyCertifyStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.common.util.RequestUtil;
import com.yeepay.g3.utils.common.StringUtils;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Component("authOnlinebankApi")
public class AuthOnlinebankApi implements ApiHandle {
	@Resource
	RiskUtil riskUtil;
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfResourceService afResourceService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();

		AfUserAuthDo afUserAuthDo = afUserAuthService.getUserAuthInfoByUserId(userId);
		if (afUserAuthDo != null && afUserAuthDo.getOnlinebankStatus().equals(SupplyCertifyStatus.WAIT.getCode())) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.RISK_OREADY_FINISH_ERROR);
		}

		// AfResourceDo afResource=
		// afResourceService.getSingleResourceBytype("chsi_auth_close");
		// if(afResource==null||afResource.getValue().equals(YesNoStatus.YES.getCode())){
		// return new ApiHandleResponse(requestDataVo.getId(),
		// FanbeiExceptionCode.CHSI_CERTIFIED_UNDER_MAINTENANCE);
		// }else{
		// if(afResource.getValue1().equals(YesNoStatus.YES.getCode())&&request.getRequestURL().indexOf("//app")!=-1){//线上关闭
		// return new ApiHandleResponse(requestDataVo.getId(),
		// FanbeiExceptionCode.CHSI_CERTIFIED_UNDER_MAINTENANCE);
		// }
		// }

		AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(userId);

		String idNumber = afUserAccountDo.getIdNumber();

		String riskOrderNo = riskUtil.getOrderNo("onbk", idNumber.substring(idNumber.length() - 4, idNumber.length()));

		StringBuffer transPara = new StringBuffer();
		transPara.append(riskOrderNo).append(",").append(userId);
		Integer appVersion = context.getAppVersion();
		String type = "2";
		if(appVersion < 407) {
			type = "1";
		}
		transPara.append(",").append(type);
		resp.addResponseData("transPara", transPara);
		return resp;
	}
}
