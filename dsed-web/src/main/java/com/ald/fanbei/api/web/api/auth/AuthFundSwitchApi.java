package com.ald.fanbei.api.web.api.auth;

import java.net.URLEncoder;
import java.util.Date;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.Auth51FundRespBo;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.exception.Auth51FundRespCode;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.AuthFundSecret;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 51公积金开关接口
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018年3月14日下午2:10:20
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("authFundSwitchApi")
public class AuthFundSwitchApi implements ApiHandle {

	@Resource
	AfResourceService afResourceService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		try {
			AfResourceDo resourceDo = afResourceService.getConfigByTypesAndSecType(ResourceType.AUTH_FUND.getCode(), AfResourceSecType.AUTH_FUND_SWITCH.getCode());
			String fundSwitch =null;
			String type = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
			if(context.getAppVersion()<409){
				fundSwitch="0";
			}else{
				if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(type)) {
					fundSwitch=resourceDo.getValue1();
				}else{
					fundSwitch=	resourceDo.getValue();
				}
			}
			resp.addResponseData("fundSwitch", fundSwitch);
		} catch (Exception e) {
			logger.error("error = " + e);
			return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.FAILED);
		}
		return resp;
	}
}
