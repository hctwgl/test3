package com.ald.fanbei.api.web.api.auth;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 *@类现描述：信用卡认证
 *@author fmai 2017年7月10日 16:22:42
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("authCreditCardApi")
public class AuthCreditCardApi implements ApiHandle {

	@Resource
	RiskUtil riskUtil;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfResourceService afResourceService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		
		//增加提现开关
		AfResourceDo resourceInfo = afResourceService.getSingleResourceBytype(Constants.RES_CREDIT_CARD_SWITCH);
		if (resourceInfo != null && resourceInfo.getValue().equals(YesNoStatus.NO.getCode())) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FUNCTION_REPAIRING_ERROR);
		}
		Long userId = context.getUserId();
		
		AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(userId);
		
		String idNumber = afUserAccountDo.getIdNumber();
		
		String riskOrderNo = riskUtil.getOrderNo("cred", idNumber.substring(idNumber.length() - 4, idNumber.length()));
		
		StringBuffer transPara = new StringBuffer();
		transPara.append(riskOrderNo).append(",").append(userId);
		
		resp.addResponseData("transPara", transPara);
		return resp;
	}

}
