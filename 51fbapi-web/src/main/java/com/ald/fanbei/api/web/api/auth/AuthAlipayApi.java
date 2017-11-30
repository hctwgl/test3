package com.ald.fanbei.api.web.api.auth;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.SupplyCertifyStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 *@类现描述：支付宝认证
 *@author fmai 2017年7月23日 12:17:42
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("authAlipayApi")
public class AuthAlipayApi implements ApiHandle {

	@Resource
	RiskUtil riskUtil;
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfResourceService afResourceService;
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		
//		return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FUNCTION_REPAIRING_ERROR);
		
		Long userId = context.getUserId();
		AfResourceDo afResource= afResourceService.getSingleResourceBytype("ali_auth_close");
		if(afResource==null||afResource.getValue().equals(YesNoStatus.YES.getCode())){
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.ALIPAY_CERTIFIED_UNDER_MAINTENANCE);
		}
//		if(true){
//			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.ALIPAY_CERTIFIED_UNDER_MAINTENANCE);
//		}
		AfUserAuthDo afUserAuthDo = afUserAuthService.getUserAuthInfoByUserId(userId);
		if (afUserAuthDo != null && afUserAuthDo.getAlipayStatus().equals(SupplyCertifyStatus.WAIT.getCode())) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.RISK_OREADY_FINISH_ERROR);
		}
		
		AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(userId);
		
		String idNumber = afUserAccountDo.getIdNumber();
		
		String riskOrderNo = riskUtil.getOrderNo("alip", idNumber.substring(idNumber.length() - 4, idNumber.length()));
		
		StringBuffer transPara = new StringBuffer();
		transPara.append(riskOrderNo).append(",").append(userId);
		
		resp.addResponseData("transPara", transPara);
		return resp;
	}

}
