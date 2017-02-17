package com.ald.fanbei.api.web.api.auth;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.TongdunResultBo;
import com.ald.fanbei.api.biz.service.AfAuthTdService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.third.util.TongdunUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfAuthTdDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 *@类现描述：实名认证，认真用户身份证和真实姓名是否匹配,调用同盾贷前审核服务接口
 *@author chenjinhu 2017年2月16日 上午10:28:10
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("authRealnameApi")
public class AuthRealnameApi implements ApiHandle {
	
	private static final String TONGDUN_CODE_WAIT_FOR_REPORT = "204";//还未出报告
	
	@Resource
	AfAuthTdService afAuthTdService;
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfUserAccountService afUserAccountService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		
		String idNumber = (String)requestDataVo.getParams().get("idNumber");
		String realName = (String)requestDataVo.getParams().get("realName");
		if(StringUtil.isBlank(idNumber) || StringUtil.isBlank(realName)){
			throw new FanbeiException("authRealnameApi param error",FanbeiExceptionCode.PARAM_ERROR);
		}
		
		String reportId = TongdunUtil.applyPreloan(idNumber, realName, context.getMobile(), null);
		if(StringUtil.isBlank(reportId)){
			return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.AUTH_REALNAME_ERROR);
		}
		CommonUtil.sleepMilliSeconds(CommonUtil.getRandomNum(3000));
		TongdunResultBo authResult = TongdunUtil.queryPreloan(reportId);
		while(StringUtil.equals(TONGDUN_CODE_WAIT_FOR_REPORT, authResult.getReasonCode())){
			CommonUtil.sleepMilliSeconds(CommonUtil.getRandomNum(3000));
			authResult = TongdunUtil.queryPreloan(reportId);
		}
		
		//存库，更新userAuth状态
		AfAuthTdDo afAuthTdDo = new AfAuthTdDo();
		afAuthTdDo.setReportId(reportId);
		afAuthTdDo.setAuthResult(authResult.getResultStr());
		afAuthTdDo.setUserId(context.getUserId());
		afAuthTdService.addAuthTd(afAuthTdDo);
		
		if(!authResult.isSuccess()){
			return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.AUTH_REALNAME_ERROR);
		}
		
		AfUserAuthDo userAuthDo = new AfUserAuthDo();
		userAuthDo.setUserId(context.getUserId());
		userAuthDo.setRealnameScore(authResult.getFinalScore());
		userAuthDo.setRealnameStatus(YesNoStatus.YES.getCode());
		userAuthDo.setGmtRealname(new Date());
		afUserAuthService.updateUserAuth(userAuthDo);
		
		//TODO 更新user_account中身份证号和真实姓名
		
		//TODO 触发邀请人获得奖励规则
		
		return resp;
	}
	
	
}
