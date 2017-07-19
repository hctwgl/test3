package com.ald.fanbei.api.web.api.auth;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 *@类现描述：公积金认证
 *@author fmai 2017年7月10日 15:03:42
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("authFundApi")
public class AuthFundApi implements ApiHandle {

	@Resource
	RiskUtil riskUtil;
	@Resource
	AfUserAccountService afUserAccountService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		
		Long userId = context.getUserId();
		
		AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(userId);
		
		String idNumber = afUserAccountDo.getIdNumber();
		
		String riskOrderNo = riskUtil.getOrderNo("fund", idNumber.substring(idNumber.length() - 4, idNumber.length()));
		
		StringBuffer transPara = new StringBuffer();
		transPara.append(riskOrderNo).append(",").append(userId);
		
		resp.addResponseData("transPara", transPara);
		return resp;
	}

}
