package com.ald.fanbei.api.web.api.auth;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.bo.Auth51FundRespBo;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.third.util.Auth51FundUtil;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.SupplyCertifyStatus;
import com.ald.fanbei.api.common.exception.Auth51FundRespCode;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.AuthFundSecret;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
/**
 * 51公积金回调的url
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018年1月9日下午3:53:34
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("authFundNewGiveBackApi")
public class AuthFundNewGiveBackApi implements ApiHandle {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Resource
	Auth51FundUtil auth51FundUtil;
	@Resource
	AfUserAuthService afUserAuthService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		String orderSn = ObjectUtils.toString(requestDataVo.getParams().get("orderSn"));
		try {
			int result=auth51FundUtil.giveBack(orderSn,userId+"");
			if (result == 1) {
				//保存认证的状态为认证中
				AfUserAuthDo authDo = new AfUserAuthDo();
				authDo.setUserId(NumberUtil.objToLongDefault(userId, 0l));
				authDo.setGmtFund(new Date(System.currentTimeMillis()));
				authDo.setFundStatus(SupplyCertifyStatus.WAIT.getCode());
				afUserAuthService.updateUserAuth(authDo);
			}
        } catch (Exception e) {
			logger.error("error = " + e);
			resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.AUTH_FUND_SUBMIT_ERROR);
        }
		return resp;
	}
}
