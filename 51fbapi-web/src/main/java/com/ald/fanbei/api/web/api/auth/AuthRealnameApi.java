package com.ald.fanbei.api.web.api.auth;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfAuthTdService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.util.CouponSceneRuleEnginerUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 *@类现描述：实名认证，认真用户身份证和真实姓名是否匹配,调用同盾贷前审核服务接口
 *@author chenjinhu 2017年2月16日 上午10:28:10
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("authRealnameApi")
public class AuthRealnameApi implements ApiHandle {
	
	@Resource
	private AfAuthTdService afAuthTdService;
	@Resource
	private AfUserService afUserService;
	@Resource
	private AfUserAuthService afUserAuthService;
	@Resource
	private AfUserAccountService afUserAccountService;
	@Resource
	private CouponSceneRuleEnginerUtil couponSceneRuleEnginerUtil;
	@Resource
	AfResourceService afResourceService;
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		AfUserAccountDo account = afUserAccountService.getUserAccountByUserId(context.getUserId());
		
		AfUserAuthDo userAuthDo = new AfUserAuthDo();
		userAuthDo.setUserId(context.getUserId());
		userAuthDo.setRealnameScore(0);
		userAuthDo.setRealnameStatus(YesNoStatus.YES.getCode());
		userAuthDo.setGmtRealname(new Date());
		afUserAuthService.updateUserAuth(userAuthDo);
		
		//触发邀请人获得奖励规则
		AfUserDo userDo = afUserService.getUserById(context.getUserId());
		if(userDo.getRecommendId() > 0l){
			couponSceneRuleEnginerUtil.realNameAuth(context.getUserId(), userDo.getRecommendId());
		}
		resp.addResponseData("realNameScore", userAuthDo.getRealnameScore());
		resp.addResponseData("realNameStatus", userAuthDo.getRealnameStatus());
		resp.addResponseData("creditAssessTime", userAuthDo.getGmtModified());

		AfUserAuthDo auth = afUserAuthService.getUserAuthInfoByUserId(context.getUserId());

		AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_CREDIT_SCORE);
		int sorce = BigDecimalUtil.getCreditScore(new BigDecimal(auth.getZmScore()), new BigDecimal(auth.getIvsScore()), 
				new BigDecimal(auth.getRealnameScore()),new BigDecimal(resource.getValue()), 
				new BigDecimal(resource.getValue1()), new BigDecimal(resource.getValue2()));
		AfResourceDo creditPz = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_CREDIT_SCORE_AMOUNT);
		JSONArray arry = JSON.parseArray(creditPz.getValue());
		BigDecimal creditAmount = BigDecimal.ZERO;
		int min = Integer.parseInt(creditPz.getValue1());//最小分数
		if(sorce<min){
			resp.addResponseData("creditLevel", "信用较差");
		}else{
			for (int i = 0; i < arry.size(); i++) {
				JSONObject obj = arry.getJSONObject(i);
				int minScore = obj.getInteger("minScore");
				int maxScore = obj.getInteger("maxScore");
				BigDecimal amount = obj.getBigDecimal("amount");
				if(minScore<=sorce&&maxScore>sorce){
					creditAmount = amount;
					String desc = obj.getString("desc");
					if(minScore<=sorce&&maxScore>sorce){
						resp.addResponseData("creditLevel", desc);
					}
				}
			}
		}
		AfUserAccountDo accountDo = new AfUserAccountDo();
		accountDo.setAuAmount(creditAmount);
		accountDo.setCreditScore(sorce);
		accountDo.setUserId(context.getUserId());
		logger.info("auAmount="+creditAmount+",creditScore="+sorce+",userId="+account.getUserId());
		afUserAccountService.updateUserAccount(account);
		
		
		return resp;
	}
	
	
}
