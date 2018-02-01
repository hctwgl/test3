package com.ald.fanbei.api.web.h5.api.loan.whitecollar;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.validator.constraints.NeedLogin;
import com.google.common.collect.Maps;

@Component("getUserAuthStatusApi")
@NeedLogin
public class GetUserAuthStatusApi implements H5Handle {
	
	@Resource
	AfUserAuthService afUserAuthService;
	
	@Resource
	RiskUtil riskUtil;
	
	@Resource
	AfUserAccountService afUserAccountService;

	@Override
	public H5HandleResponse process(Context context) {
		H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
		Map<String,Object> data = Maps.newHashMap();
		Long userId = context.getUserId();
		
		// 获取用户认证信息
		AfUserAuthDo userAuthInfo =  afUserAuthService.getUserAuthInfoByUserId(userId);
		if(userAuthInfo == null) {
			throw new FanbeiException(FanbeiExceptionCode.USER_AUTH_INFO_NOT_EXIST);
		}
		
		String fundStatus = userAuthInfo.getFundStatus();
		String jinpoStatus = userAuthInfo.getJinpoStatus();
		String chsiStatus = userAuthInfo.getChsiStatus();
		String zhengxinStatus = userAuthInfo.getZhengxinStatus();
		if(StringUtils.isBlank(fundStatus)) {
			fundStatus = "A";
		}
		if(StringUtils.isBlank(jinpoStatus)) {
			jinpoStatus = "A";
		}
		if(StringUtils.isBlank(chsiStatus)) {
			chsiStatus = "A";
		}
		if(StringUtils.isBlank(zhengxinStatus)) {
			zhengxinStatus = "A";
		}
		data.put("fundStatus", fundStatus);
		data.put("jinpoStatus", jinpoStatus);
		data.put("zhengxinStatus", zhengxinStatus);

		//网银认证
		data.put("onlinebankStatus", "Y");
		
		AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(userId);
		
		String idNumber = afUserAccountDo.getIdNumber();
		
		String fundRiskOrderNo = riskUtil.getOrderNo("fund", idNumber.substring(idNumber.length() - 4, idNumber.length()));

		data.put("fundAuthParam", fundRiskOrderNo + "," + userId);

		String zxinRiskOrderNo = riskUtil.getOrderNo("zxin", idNumber.substring(idNumber.length() - 4, idNumber.length()));

		data.put("zxinAuthParam", zxinRiskOrderNo + "," + userId);
		
		String sociRiskOrderNo = riskUtil.getOrderNo("soci", idNumber.substring(idNumber.length() - 4, idNumber.length()));

		data.put("jinpoAuthParam", sociRiskOrderNo + "," + userId);

		// 网银认证
		data.put("onlinebankAuthParam", sociRiskOrderNo + "," + userId);

		resp.setResponseData(data);
		return resp;
	}

}
