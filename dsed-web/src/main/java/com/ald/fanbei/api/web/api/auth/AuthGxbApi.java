package com.ald.fanbei.api.web.api.auth;


import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.AuthGxbRespBo;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.dto.AfUserDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 公信宝认证
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018年1月30日上午11:08:18
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("authGxbApi")
public class AuthGxbApi implements ApiHandle {

	@Resource
	AfUserService afUserService;
	@Resource
	RiskUtil riskUtil;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		HashMap<String, Object> map=new HashMap<String, Object>();
		Long userId = context.getUserId();
		String appId = ConfigProperties.get(Constants.AUTH_GXB_APPID);
		String appSecurity = ConfigProperties.get(Constants.AUTH_GXB_APPSECURITY);
//		String appId = AesUtil.decrypt(ConfigProperties.get(Constants.AUTH_GXB_APPID),ConfigProperties.get(Constants.CONFKEY_AES_KEY));
//		String appSecurity = AesUtil.decrypt(ConfigProperties.get(Constants.AUTH_GXB_APPSECURITY),ConfigProperties.get(Constants.CONFKEY_AES_KEY));
		String sequenceNo=userId+"gxb"+System.currentTimeMillis();
		String authItem="ecommerce";
		String timestamp=DateUtil.getCurrSecondTimeStamp()+"";
		AfUserDto afUserDto= afUserService.getUserInfoByUserId(userId);
		String name=afUserDto.getRealName();
		String phone=afUserDto.getUserName();
	    String idcard=afUserDto.getIdNumber();
	    map.put("appId",appId);
	    map.put("sign", DigestUtils.md5Hex(appId+appSecurity+authItem+timestamp+sequenceNo));
	    map.put("sequenceNo",sequenceNo);
	    map.put("authItem",authItem);
	    map.put("timestamp",timestamp);
	    map.put("name",name);
	    map.put("phone",phone);
	    map.put("idcard",idcard);
	    try {
	    	logger.info("mapInfo:"+map.toString());
	    	String respResult = HttpUtil.doHttpsPostIgnoreCertJSON("https://prod.gxb.io/crawler/auth/v2/get_auth_token", JSON.toJSONString(map));
	    	if (StringUtil.isBlank(respResult)) {
	    		logger.error("getAuthToken respResult is null");
				return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.AUTH_GXB_GETTOKEN_ERROR );
			}else{
				AuthGxbRespBo respInfo = JSONObject.parseObject(respResult, AuthGxbRespBo.class);
					if ("1".equals(respInfo.getRetCode())) {
					JSONObject data = JSON.parseObject(respInfo.getData());
					String token=data.getString("token");
					logger.info("getAuthToken resp success, token="+token+",respInfo"+respInfo.getRetMsg());
					String riskUrl = ConfigProperties.get(Constants.CONFKEY_RISK_URL);
				    String returnUrl = riskUtil.getUrl()+"/tpp/gxbdata/alipay/notify.htm";
				    String urlFull = "https://prod.gxb.io/v2/auth?returnUrl="+returnUrl+"&token="+token;
				    logger.info("url=" + urlFull+"userId="+userId);
				    resp.addResponseData("url", urlFull);
				}else {
					//三方处理错误
					logger.error("getAuthToken resp fail,errorCode="+respInfo.getRetCode()+",errorInfo"+respInfo.getRetMsg());
					return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.AUTH_GXB_GETTOKEN_ERROR);
				}
			}
		} catch (Exception e) {
			logger.error("error = " + e);
			return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.FAILED);
		}
		return resp;
	}	
}
