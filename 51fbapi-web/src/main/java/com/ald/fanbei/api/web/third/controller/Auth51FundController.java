package com.ald.fanbei.api.web.third.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.bo.assetside.AssetSideRespBo;
import com.ald.fanbei.api.biz.third.util.AssetSideEdspayUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 51公积金回调接口
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018年1月9日下午3:53:34
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/third/51fund")
public class Auth51FundController {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@RequestMapping(value = { "/giveBack" }, method = {RequestMethod.POST,RequestMethod.GET}, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public AssetSideRespBo giveBack(@RequestBody String requestData,HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonObj = JSON.parseObject(requestData);
		String orderSn = StringUtil.null2Str(jsonObj.get("orderSn"));
		String status = StringUtil.null2Str(jsonObj.get("status"));
		String timestamp = StringUtil.null2Str(jsonObj.get("timestamp"));
		logger.info("Auth51FundController giveBack,orderSn="+orderSn+",status=" + status + ",timestamp=" + timestamp);
		
//		AssetSideRespBo notifyRespBo = Auth51FundUtil.giveBackCreditInfo(sendTime, data, sign,appId);
//		logger.info("EdspayController giveBackCreditInfo,appId="+appId+ ",sendTime=" + sendTime+",returnMsg="+notifyRespBo.toString());
//		return notifyRespBo;
		return null;
	}
}
