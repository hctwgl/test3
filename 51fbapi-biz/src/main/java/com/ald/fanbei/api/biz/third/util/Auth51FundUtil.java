package com.ald.fanbei.api.biz.third.util;

import java.util.Date;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.Auth51FundRespBo;
import com.ald.fanbei.api.biz.bo.RiskRespBo;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.exception.Auth51FundRespCode;
import com.ald.fanbei.api.common.util.AuthFundSecret;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 51公积金对接的工具类
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018年1月10日上午11:46:13
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("auth51FundUtil")
public class Auth51FundUtil extends AbstractThird {

	@Resource
	BizCacheUtil  bizCacheUtil;
	@Resource
	RiskUtil riskUtil;
	
	public void giveBack(String orderSn,String userId) throws Exception {
		// 响应数据,默认成功
		String appKey = ConfigProperties.get(Constants.CONFKEY_NEWFUND_APPKEY);
		String secret = ConfigProperties.get(Constants.CONFKEY_NEWFUND_SECRET);
		if (null == orderSn) {
			throw new Exception("51公积金回调的url参数有误");
		}
		TreeMap<String, String> paramSortedMap = new TreeMap<>();
        paramSortedMap.put("appKey", appKey);
        paramSortedMap.put("timestamp", String.valueOf(new Date().getTime()));
        String token = (String) bizCacheUtil.getObject(Constants.AUTH_51FUND_TOKEN);
        paramSortedMap.put("token", token);
        paramSortedMap.put("orderSn", orderSn);
        String mapStr = AuthFundSecret.paramTreeMapToString(paramSortedMap);
        String newStr = mapStr + "&appSecret="+secret;
        String sign = AuthFundSecret.signToHexStr(AuthFundSecret.ALGORITHMS_MD5, newStr).toUpperCase();
        TreeMap<String, Object> resultSortedMap = new TreeMap<>();
        resultSortedMap.put("sign",sign);
        resultSortedMap.put("type","gjj");
        resultSortedMap.put("params",paramSortedMap);
        String gjjUrl="https://t.51gjj.com/gjj/getGjjData";
        String params = JSONObject.toJSONString(resultSortedMap);
        logger.info("get51Gjj url = {},params = {}",gjjUrl,params);
        String respResult = HttpUtil.doHttpPostJsonParam(gjjUrl, params);
        logger.info("get51Gjj result = {}", respResult);
		if (StringUtil.isBlank(respResult)) {
			logger.error("getGjjData result is null,orderSn=" + orderSn);
			throw new Exception("获取用户公积金信息为null");
		}else {
			Auth51FundRespBo respInfo = JSONObject.parseObject(respResult, Auth51FundRespBo.class);
			if (Auth51FundRespCode.SUCCESS.getCode().equals(respInfo.getCode())) {
				String respData = respInfo.getData();
				//推送公积金信息给风控
				RiskRespBo riskRespBo = riskUtil.FundNotifyRisk(respData,userId,token,orderSn);
			}else {
				//三方处理错误
				Auth51FundRespCode failResp = Auth51FundRespCode.findByCode(respInfo.getCode());
				logger.error("getGjjData result fail,errorCode="+respInfo.getCode()+",errorInfo"+(failResp!=null?failResp.getDesc():""));
			}
		}
	}
}
