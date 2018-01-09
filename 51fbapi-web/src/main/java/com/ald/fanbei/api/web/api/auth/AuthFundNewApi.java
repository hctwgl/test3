package com.ald.fanbei.api.web.api.auth;

import java.util.Date;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.Auth51FundRespBo;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.SupplyCertifyStatus;
import com.ald.fanbei.api.common.exception.Auth51FundRespCode;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.AuthFundSecret;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
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
	RiskUtil riskUtil;
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfUserAccountService afUserAccountService;
	
	@Resource
	BizCacheUtil  bizCacheUtil;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		String token =null;
		//获取token
		try {
			token = (String) bizCacheUtil.getObject(Constants.AUTH_51FUND_TOKEN);
			if (null == token) {
				TreeMap<String, String> paramSortedMap = new TreeMap<>();
		        paramSortedMap.put("appKey", "1DE714C387E641E987078EC625666D92");
		        paramSortedMap.put("timestamp", String.valueOf(new Date().getTime()) );

		        String mapStr = AuthFundSecret.paramTreeMapToString(paramSortedMap);
		        String newStr = mapStr + "&appSecret=6658179F17D844E093635571F41A337AC99CD26B";
		        String sign = AuthFundSecret.signToHexStr(AuthFundSecret.ALGORITHMS_MD5, newStr).toUpperCase();
		        TreeMap<String, Object> resultSortedMap = new TreeMap<>();
		        resultSortedMap.put("sign",sign);
		        resultSortedMap.put("params",paramSortedMap);
		        String postParams=JSON.toJSONString(resultSortedMap);
		        String respResult=HttpUtil.doHttpPostJsonParam("https://t.51gjj.com/gjj/getToken", postParams);
				if (StringUtil.isBlank(respResult)) {
					logger.error("getToken req success,respResult is null,AssetPackageNo=");
//					return new ApiHandleResponse(requestDataVo.getId(), "");
				}else {
					Auth51FundRespBo respInfo = JSONObject.parseObject(respResult, Auth51FundRespBo.class);
					if (Auth51FundRespCode.SUCCESS.getCode().equals(respInfo.getCode())) {
						JSONObject data = JSON.parseObject(respInfo.getData());
						token=data.getString("token");
						bizCacheUtil.saveObject(Constants.AUTH_51FUND_TOKEN, token, Constants.SECOND_OF_ONE_HALF_HOUR);
						logger.info("transBorrowerInfo to wallet req success,AssetPackageNo="+",respInfo"+respInfo.getMessage());
					}else {
						//三方处理错误
						Auth51FundRespCode failResp = Auth51FundRespCode.findByCode(respInfo.getCode());
						logger.error("getToken req success,resp fail,errorCode="+respInfo.getCode()+",errorInfo"+(failResp!=null?failResp.getDesc():""));
//						return new ApiHandleResponse(requestDataVo.getId(), "");
					}
				}
			}
		} catch (Exception e) {
//			return new ApiHandleResponse(requestDataVo.getId(), "");
		}
		
		//获取订单号
		String orderSn =null;
		try {
			TreeMap<String, String> paramSortedMap = new TreeMap<>();
	        paramSortedMap.put("appKey", "1DE714C387E641E987078EC625666D92");
	        paramSortedMap.put("timestamp", String.valueOf(new Date().getTime()));
	        paramSortedMap.put("token", token);
	        String mapStr = AuthFundSecret.paramTreeMapToString(paramSortedMap);
	        String newStr = mapStr + "&appSecret=6658179F17D844E093635571F41A337AC99CD26B";
	        String sign = AuthFundSecret.signToHexStr(AuthFundSecret.ALGORITHMS_MD5, newStr).toUpperCase();
	        TreeMap<String, Object> resultSortedMap = new TreeMap<>();
	        resultSortedMap.put("sign",sign);
	        resultSortedMap.put("params",paramSortedMap);
	        String respResult = HttpUtil.doHttpPostJsonParam("https://t.51gjj.com/gjj/getOrderSn", JSON.toJSONString(resultSortedMap));
	        if (StringUtil.isBlank(respResult)) {
				logger.error("getToken req success,respResult is null,AssetPackageNo=");
//				return new ApiHandleResponse(requestDataVo.getId(), "");
			}else {
				Auth51FundRespBo respInfo = JSONObject.parseObject(respResult, Auth51FundRespBo.class);
				if (Auth51FundRespCode.SUCCESS.getCode().equals(respInfo.getCode())) {
					JSONObject data = JSON.parseObject(respInfo.getData());
					orderSn=data.getString("orderSn");
					logger.info("transBorrowerInfo to wallet req success,AssetPackageNo="+",respInfo"+respInfo.getMessage());
				}else {
					//三方处理错误
					Auth51FundRespCode failResp = Auth51FundRespCode.findByCode(respInfo.getCode());
					logger.error("getToken req success,resp fail,errorCode="+respInfo.getCode()+",errorInfo"+(failResp!=null?failResp.getDesc():""));
//					return new ApiHandleResponse(requestDataVo.getId(), "");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		//拼接前端url
	    TreeMap<String, String> paramSortedMap = new TreeMap<>();
	    paramSortedMap.put("appKey", "1DE714C387E641E987078EC625666D92");
	    paramSortedMap.put("timestamp", String.valueOf(new Date().getTime()));
	    paramSortedMap.put("token", token);
	    paramSortedMap.put("orderSn", orderSn);
	    String mapStr = AuthFundSecret.paramTreeMapToString(paramSortedMap);
	    String newStr = mapStr + "&appSecret=6658179F17D844E093635571F41A337AC99CD26B";
	    String sign = AuthFundSecret.signToHexStr(AuthFundSecret.ALGORITHMS_MD5, newStr).toUpperCase();
	    
	    paramSortedMap.put("sign", sign);
	    paramSortedMap.put("redirectUrl", "https://testapp.51fanbei.com/third/51fund/giveBack");
	    String urlParams=AuthFundSecret.paramTreeMapToString(paramSortedMap);
	    String urlFull = "https://t.51gjj.com/gjj?"+urlParams;
	    resp.addResponseData("url", urlFull);
		return resp;
	}

}
