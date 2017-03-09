package com.ald.fanbei.api.web.api.auth;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.ZhimaAuthResultBo;
import com.ald.fanbei.api.biz.service.AfAuthZmService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.third.util.ZhimaUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfAuthZmDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.antgroup.zmxy.openplatform.api.response.ZhimaCreditIvsDetailGetResponse;
import com.antgroup.zmxy.openplatform.api.response.ZhimaCreditScoreGetResponse;
import com.antgroup.zmxy.openplatform.api.response.ZhimaCreditWatchlistiiGetResponse;

/**
 *@类现描述：调用芝麻信用来认证用户信用,包括获取芝麻信用分和行业反欺诈信息
 *@author chenjinhu 2017年2月16日 下午3:46:52
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("authCreditApi")
public class AuthCreditApi implements ApiHandle {
	
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfAuthZmService afAuthZmService;
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfResourceService afResourceService;

	@SuppressWarnings("deprecation")
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {

		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		
		String respBody = (String)requestDataVo.getParams().get("respBody");
		String sign = (String)requestDataVo.getParams().get("sign");
		
		ZhimaAuthResultBo zarb = ZhimaUtil.decryptAndVerifySign(respBody, sign);
		if(!zarb.isSuccess()){
			throw new FanbeiException(FanbeiExceptionCode.ZM_AUTH_ERROR);
		}
		String openId = zarb.getOpenId();
		
		AfUserAccountDo userAccount = afUserAccountService.getUserAccountByUserId(context.getUserId());
		String idNumber = userAccount.getIdNumber();
		String realName = userAccount.getRealName();
		ZhimaCreditScoreGetResponse scoreGetResp = ZhimaUtil.scoreGet(openId);
		ZhimaCreditIvsDetailGetResponse ivsDetailResp =  ZhimaUtil.ivsDetailGet(idNumber, realName, null, null, null);
		ZhimaCreditWatchlistiiGetResponse watchListResp = ZhimaUtil.watchlistiiGet(openId);
		
		//增加日志
		AfAuthZmDo authZm = new AfAuthZmDo();
		authZm.setIdNumber(idNumber);
		authZm.setRealName(realName);
		authZm.setUserId(context.getUserId());
		authZm.setOpenId(openId);
		authZm.setScoreResult(JSON.toJSONString(scoreGetResp));
		authZm.setIvsResult(JSON.toJSONString(ivsDetailResp));
		authZm.setWatchlistResult(JSON.toJSONString(watchListResp));
		scoreGetResp.isSuccess();
		
		AfUserAuthDo userAuth = new AfUserAuthDo();
		userAuth.setUserId(context.getUserId());
		if(scoreGetResp.isSuccess()){
			userAuth.setZmStatus(YesNoStatus.YES.getCode());
			userAuth.setGmtZm(new Date());
			userAuth.setZmScore(NumberUtil.objToIntDefault(scoreGetResp.getZmScore(), 0));
		}
		if(ivsDetailResp.isSuccess()){
			userAuth.setIvsStatus(YesNoStatus.YES.getCode());
			userAuth.setIvsScore(ivsDetailResp.getIvsScore().intValue());
			userAuth.setGmtIvs(new Date());
		}
		if(watchListResp.isSuccess()){
			userAuth.setWatchlistStatus(YesNoStatus.YES.getCode());
			userAuth.setWatchlistScore(watchListResp.getIsMatched()?YesNoStatus.YES.getCode():YesNoStatus.NO.getCode());
			userAuth.setGmtWatchlist(new Date());
		}
		afUserAuthService.updateUserAuth(userAuth);
		
		// TODO 计算信用分 更新userAccount
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
			resp.addResponseData("tooLow", 'Y');
			resp.addResponseData("creditAmount", creditAmount);
			resp.addResponseData("allowConsume", 'N');
		}else{
			for (int i = 0; i < arry.size(); i++) {
				JSONObject obj = arry.getJSONObject(i);
				int minScore = obj.getInteger("minScore");
				int maxScore = obj.getInteger("maxScore");
				BigDecimal amount = obj.getBigDecimal("amount");
				if(minScore<=sorce&&maxScore>sorce){
					creditAmount = amount;
				}
			}
			resp.addResponseData("tooLow", 'N');
			resp.addResponseData("creditAmount", creditAmount);
			resp.addResponseData("allowConsume", 'Y');
		}
		AfUserAccountDo account = new AfUserAccountDo();
		account.setAuAmount(creditAmount);
		account.setCreditScore(sorce);
		account.setUserId(context.getUserId());
		afUserAccountService.updateUserAccount(account);
		return resp;
	}
}
