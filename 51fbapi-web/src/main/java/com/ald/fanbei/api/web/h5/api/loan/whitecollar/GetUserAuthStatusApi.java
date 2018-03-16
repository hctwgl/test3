package com.ald.fanbei.api.web.h5.api.loan.whitecollar;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.validator.constraints.NeedLogin;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;

@NeedLogin
@Component("getUserAuthStatusApi")
public class GetUserAuthStatusApi implements H5Handle {

	@Resource
	AfUserAuthService afUserAuthService;

	@Resource
	RiskUtil riskUtil;

	@Resource
	AfUserAccountService afUserAccountService;

	@Resource
	AfResourceService afResourceService;

	@Override
	public H5HandleResponse process(Context context) {
		H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
		Map<String, Object> data = Maps.newHashMap();
		Long userId = context.getUserId();

		// 获取用户认证信息
		AfUserAuthDo userAuthInfo = afUserAuthService.getUserAuthInfoByUserId(userId);
		if (userAuthInfo == null) {
			throw new FanbeiException(FanbeiExceptionCode.USER_AUTH_INFO_NOT_EXIST);
		}

		String fundStatus = userAuthInfo.getFundStatus();
		String jinpoStatus = userAuthInfo.getJinpoStatus();
		String onlinebankStatus = userAuthInfo.getOnlinebankStatus();

		if (StringUtils.isBlank(fundStatus)) {
			fundStatus = "A";
		}
		if (StringUtils.isBlank(jinpoStatus)) {
			jinpoStatus = "A";
		}

		if (StringUtils.isBlank(onlinebankStatus)) {
			onlinebankStatus = "A";
		}
		// 判断是否已过期

		AfResourceDo userAuthDay = afResourceService.getSingleResourceBytype("USER_AUTH_DAY");
		JSONArray userAuthDayArray = JSON.parseArray(userAuthDay.getValue());
		Integer fundDay = 0;
		Integer insuranceDay = 0;
		Integer bankDay = 0;
		for (int i = 0; i < userAuthDayArray.size(); i++) {
			JSONObject obj = userAuthDayArray.getJSONObject(i);
			if (obj.getString("type").equals("fund")) {
				fundDay = obj.getInteger("day");
			} else if (obj.getString("type").equals("insurance")) {
				insuranceDay = obj.getInteger("day");
			} else if (obj.getString("type").equals("bank")) {
				bankDay = obj.getInteger("day");
			}
		}

		Date gmtFund = userAuthInfo.getGmtFund();
		Date gmtJinpo = userAuthInfo.getGmtJinpo();
		Date gmtOnlinebank = userAuthInfo.getGmtOnlinebank();

		Date nowDate = new Date();
		if (StringUtils.equals("Y", fundStatus) && nowDate.after(DateUtil.addDays(gmtFund, fundDay))) {
			fundStatus = "P";
		}
		if (StringUtils.equals("Y", jinpoStatus) && nowDate.after(DateUtil.addDays(gmtJinpo, insuranceDay))) {
			jinpoStatus = "P";
		}
		if (StringUtils.equals("Y", onlinebankStatus) && nowDate.after(DateUtil.addDays(gmtOnlinebank, bankDay))) {
			onlinebankStatus = "P";
		}

		data.put("fundStatus", fundStatus);
		data.put("jinpoStatus", jinpoStatus);

		// 网银认证
		data.put("onlinebankStatus", onlinebankStatus);

		AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(userId);

		String idNumber = afUserAccountDo.getIdNumber();

		String fundRiskOrderNo = riskUtil.getOrderNo("fund",
				idNumber.substring(idNumber.length() - 4, idNumber.length()));

		data.put("fundAuthParam", fundRiskOrderNo + "," + userId + ",2");

		String sociRiskOrderNo = riskUtil.getOrderNo("soci",
				idNumber.substring(idNumber.length() - 4, idNumber.length()));

		data.put("jinpoAuthParam", sociRiskOrderNo + "," + userId + ",2");

		String onbkRiskOrderNo = riskUtil.getOrderNo("onbk",
				idNumber.substring(idNumber.length() - 4, idNumber.length()));

		// 网银认证
		data.put("onlinebankAuthParam", onbkRiskOrderNo + "," + userId + ",2");

		resp.setResponseData(data);
		return resp;
	}

}
