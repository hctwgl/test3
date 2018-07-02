package com.ald.fanbei.api.web.api.auth;

import java.net.URLEncoder;
import java.util.Date;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.Auth51FundRespBo;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.Auth51FundRespCode;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.AuthFundSecret;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 51公积金认证
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018年1月9日下午1:40:28
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Component("authFundNewApi")
public class AuthFundNewApi implements ApiHandle {

	@Resource
	BizCacheUtil  bizCacheUtil;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		String token =null;
		String appKey = ConfigProperties.get(Constants.CONFKEY_NEWFUND_APPKEY);
		String secret = ConfigProperties.get(Constants.CONFKEY_NEWFUND_SECRET);
		//获取token
		try {
			token = (String) bizCacheUtil.getObject(Constants.AUTH_51FUND_TOKEN);
			if (null == token) {
				TreeMap<String, String> paramSortedMap = new TreeMap<>();
		        paramSortedMap.put("appKey", appKey);
		        paramSortedMap.put("timestamp", String.valueOf(new Date().getTime()) );
		        String mapStr = AuthFundSecret.paramTreeMapToString(paramSortedMap);
		        String newStr = mapStr + "&appSecret=" + secret;
		        String sign = AuthFundSecret.signToHexStr(AuthFundSecret.ALGORITHMS_MD5, newStr).toUpperCase();
		        TreeMap<String, Object> resultSortedMap = new TreeMap<>();
		        resultSortedMap.put("sign",sign);
		        resultSortedMap.put("params",paramSortedMap);
		        String postParams=JSON.toJSONString(resultSortedMap);
		        String url="https://t.51gjj.com/gjj/getToken";
		        logger.info("getToken url  = {} params = {} ", url,postParams);
		        String respResult=HttpUtil.doHttpPostJsonParam(url, postParams);
		        logger.info("getToken result  = {}", respResult);
				if (StringUtil.isBlank(respResult)) {
					logger.error("getToken fail,result is null");
					return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.AUTH_FUND_GETTOKEN_ERROR );
				}else {
					Auth51FundRespBo respInfo = JSONObject.parseObject(respResult, Auth51FundRespBo.class);
					if (Auth51FundRespCode.SUCCESS.getCode().equals(respInfo.getCode())) {
						JSONObject data = JSON.parseObject(respInfo.getData());
						token=data.getString("token");
						bizCacheUtil.saveObject(Constants.AUTH_51FUND_TOKEN, token, Constants.SECOND_OF_ONE_HALF_HOUR);
						logger.info("getToken fail,token="+token+",respInfo"+respInfo.getMessage());
					}else {
						//三方处理错误
						Auth51FundRespCode failResp = Auth51FundRespCode.findByCode(respInfo.getCode());
						logger.error("getToken fail,errorCode="+respInfo.getCode()+",errorInfo"+(failResp!=null?failResp.getDesc():""));
						return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.AUTH_FUND_GETTOKEN_ERROR);
					}
				}
			}
		} catch (Exception e) {
			logger.error("getToken fail,error=" + e);
			return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.AUTH_FUND_GETTOKEN_ERROR );
		}
		
		//获取订单号
		String orderSn =null;
		try {
			TreeMap<String, String> paramSortedMap = new TreeMap<>();
	        paramSortedMap.put("appKey", appKey);
	        paramSortedMap.put("timestamp", String.valueOf(new Date().getTime()));
	        paramSortedMap.put("token", token);
	        String mapStr = AuthFundSecret.paramTreeMapToString(paramSortedMap);
	        String newStr = mapStr + "&appSecret=" + secret;
	        String sign = AuthFundSecret.signToHexStr(AuthFundSecret.ALGORITHMS_MD5, newStr).toUpperCase();
	        TreeMap<String, Object> resultSortedMap = new TreeMap<>();
	        resultSortedMap.put("sign",sign);
	        resultSortedMap.put("params",paramSortedMap);
	        String url="https://t.51gjj.com/gjj/getOrderSn";
	        logger.info("getOrderSn url  = {} params = {} ", url,JSON.toJSONString(resultSortedMap));
	        String respResult = HttpUtil.doHttpPostJsonParam(url, JSON.toJSONString(resultSortedMap));
	        logger.info("getOrderSn result  = {}", respResult);
	        if (StringUtil.isBlank(respResult)) {
	        	logger.error("getOrderSn fail,result is null");
				return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.AUTH_FUND_GETORDERSN_ERROR );
			}else {
				Auth51FundRespBo respInfo = JSONObject.parseObject(respResult, Auth51FundRespBo.class);
				if (Auth51FundRespCode.SUCCESS.getCode().equals(respInfo.getCode())) {
					JSONObject data = JSON.parseObject(respInfo.getData());
					orderSn=data.getString("orderSn");
					logger.info("getOrderSn  success,token=" + token + " orderSn="+orderSn+",respInfo"+respInfo.getMessage());
				}else {
					//三方处理错误
					Auth51FundRespCode failResp = Auth51FundRespCode.findByCode(respInfo.getCode());
					logger.error("getOrderSn fail,errorCode="+respInfo.getCode()+",errorInfo"+(failResp!=null?failResp.getDesc():""));
					return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.AUTH_FUND_GETORDERSN_ERROR);
				}
			}
		} catch (Exception e) {
			logger.error("getOrderSn fail,error=" + e);
			return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.AUTH_FUND_GETORDERSN_ERROR );
		}
		
		//拼接客户端url调用51公积金H5
		try {
			TreeMap<String, String> paramSortedMap = new TreeMap<>();
		    paramSortedMap.put("appKey", appKey);
		    paramSortedMap.put("timestamp", String.valueOf(new Date().getTime()));
		    paramSortedMap.put("token", token);
		    paramSortedMap.put("orderSn", orderSn);
		    String mapStr = AuthFundSecret.paramTreeMapToString(paramSortedMap);
		    String newStr = mapStr + "&appSecret=" + secret;
		    String sign = AuthFundSecret.signToHexStr(AuthFundSecret.ALGORITHMS_MD5, newStr).toUpperCase();
		    paramSortedMap.put("sign", sign);
		    String redirectUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST)+"/third/newFund/giveBack";
		    paramSortedMap.put("redirectUrl", redirectUrl);
		    paramSortedMap.put("userId", userId+"");
		    String urlParams=AuthFundSecret.paramTreeMapToString(paramSortedMap);
		    String urlFull = "https://t.51gjj.com/gjj?"+urlParams;
		    logger.info("token=" + token + " orderSn="+orderSn+"url=" + urlFull+"redirectUrl="+redirectUrl+"userId="+userId);
		    resp.addResponseData("url", urlFull);
		} catch (Exception e) {
			logger.error("error = " + e);
			return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.FAILED);
		}
		return resp;
	}
}
