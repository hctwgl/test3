package com.ald.fanbei.api.web.api.auth;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.third.util.ZhimaUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 *@类现描述：客户端向芝麻信用发起授权时需要appid,sign,param参数来向支付宝做授权，服务端为客户端封装这些参数并返回给客户端
 *@author chenjinhu 2017年2月17日 上午10:27:33
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("authZhimaApi")
public class AuthZhimaApi implements ApiHandle {
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfResourceService afResourceService;
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		AfUserAccountDo userAccount = afUserAccountService.getUserAccountByUserId(context.getUserId());
		String idNumber = userAccount.getIdNumber();
		String realName = userAccount.getRealName();

		String authParamUrl =  ZhimaUtil.authorize(idNumber, realName);
		AfResourceDo zhimaNewUrl= afResourceService.getSingleResourceBytype("zhimaNewUrl");

		if(zhimaNewUrl==null){
			resp.addResponseData("authUrl", authParamUrl);
		}else{
			resp.addResponseData("authUrl", zhimaNewUrl.getValue()+"?userId="+ AesUtil.encryptToBase64(userAccount.getUserId().toString(),"123"));
		}
		
		return resp;
	}

}
