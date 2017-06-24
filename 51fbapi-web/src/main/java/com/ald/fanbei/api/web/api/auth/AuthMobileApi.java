package com.ald.fanbei.api.web.api.auth;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.RiskOperatorRespBo;
import com.ald.fanbei.api.biz.bo.risk.MoXieReqBo;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;

/**
 *@类现描述：手机运营商认证
 *@author hexin 2017年3月24日 下午18:11:42
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("authMobileApi")
public class AuthMobileApi implements ApiHandle {

	@Resource
	RiskUtil riskUtil;
	@Resource
	AfUserAccountService afUserAccountService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		
		//调风控
		RiskOperatorRespBo respBo = riskUtil.operator(context.getUserId()+"", context.getUserName());
		if(!respBo.isSuccess()){
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.AUTH_MOBILE_ERROR);
		}
		
		//获取基本地址成功时，本服务拼接用户信息串（可选，主要为了减少用户信息填写步骤操作），请求三方H5
		AfUserAccountDo currUserAccount = afUserAccountService.getUserAccountByUserId(userId);
		MoXieReqBo moXieReqBo = new MoXieReqBo(StringUtil.null2Str(currUserAccount.getUserName()), StringUtil.null2Str(currUserAccount.getIdNumber()), StringUtil.null2Str(currUserAccount.getRealName()));
		String reqExtraInfoJsonStr = JSON.toJSONString(moXieReqBo);
		String reqUrl = respBo.getUrl()+"&loginParams="+reqExtraInfoJsonStr;
		
		resp.addResponseData("url",reqUrl);
		return resp;
	}

}
