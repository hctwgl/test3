package com.ald.fanbei.api.web.api.auth;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.TongdunResultBo;
import com.ald.fanbei.api.biz.service.AfAuthTdService;
import com.ald.fanbei.api.biz.service.AfAuthYdService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.TongdunUtil;
import com.ald.fanbei.api.biz.third.util.ZhimaUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.Base64;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfAuthTdDo;
import com.ald.fanbei.api.dal.domain.AfAuthYdDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONObject;
/**
 * 
 *@类现描述：人脸识别认证结果，app端做完人脸识别之后把结果发给服务端
 *
 *@author chenjinhu 2017年2月15日 下午3:34:46
 *@version 2.0 重构版本
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("authFaceApi")
public class AuthFaceApi implements ApiHandle {
	
	private final static String  RESULT_AUTH_TRUE = "T";
	
	private static final String TONGDUN_CODE_WAIT_FOR_REPORT = "204";//还未出报告
	
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfAuthYdService afAuthYdService;
	@Resource
	private AfAuthTdService afAuthTdService;
	@Resource
	private AfUserService afUserService;
	@Resource
	private AfUserAccountService afUserAccountService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		String result = (String)requestDataVo.getParams().get("result");
		String resultAuth = (String)requestDataVo.getParams().get("resultAuth");
		String idNumber = (String)requestDataVo.getParams().get("idNumber");
		String realName = (String)requestDataVo.getParams().get("realName");
		
		if(StringUtil.isBlank(idNumber) || StringUtil.isBlank(realName)){
			throw new FanbeiException("authRealnameApi param error",FanbeiExceptionCode.PARAM_ERROR);
		}
		
		idNumber = new String(Base64.decode(idNumber));
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
		
		//TODO 更新user_account中身份证号和真实姓名
		AfUserAccountDo afUserAccountDo = new AfUserAccountDo();
		afUserAccountDo.setUserId(context.getUserId());
		afUserAccountDo.setRealName(realName);
		afUserAccountDo.setIdNumber(idNumber);
		afUserAccountService.updateUserAccount(afUserAccountDo);
		
		AfUserDo afUserDo = new AfUserDo();
		afUserDo.setRid(context.getUserId());
		afUserDo.setRealName(realName);
		afUserService.updateUser(afUserDo);
		
		//人脸识别
		if(StringUtil.equals(resultAuth, RESULT_AUTH_TRUE)){
			AfUserAuthDo userAuth = new AfUserAuthDo();
			userAuth.setUserId(context.getUserId());
			userAuth.setYdStatus(YesNoStatus.YES.getCode());
			userAuth.setFacesStatus(YesNoStatus.YES.getCode());
			userAuth.setSimilarDegree(new BigDecimal((String)JSONObject.parseObject(result).get("be_idcard")));
			userAuth.setRealnameScore(authResult.getFinalScore());
			userAuth.setRealnameStatus(YesNoStatus.YES.getCode());
			userAuth.setGmtRealname(new Date());
			afUserAuthService.updateUserAuth(userAuth);
		}else{
			AfUserAuthDo userAuthDo = new AfUserAuthDo();
			userAuthDo.setUserId(context.getUserId());
			userAuthDo.setRealnameScore(authResult.getFinalScore());
			userAuthDo.setRealnameStatus(YesNoStatus.YES.getCode());
			userAuthDo.setGmtRealname(new Date());
			afUserAuthService.updateUserAuth(userAuthDo);
		}
		
		//新增有盾数据
		AfAuthYdDo afAuthYdDo = new AfAuthYdDo(); 
		afAuthYdDo.setType("FACE_APP");
		afAuthYdDo.setUserId(context.getUserId());
		afAuthYdDo.setAuthParam("");
		afAuthYdDo.setAuthResult(result);
		afAuthYdService.addAuthYd(afAuthYdDo);
		String authParamUrl =  ZhimaUtil.authorize(idNumber, realName);
		resp.addResponseData("zmxyAuthUrl", authParamUrl);
		resp.addResponseData("realNameScore", authResult.getFinalScore());
		return resp;
	}

}
