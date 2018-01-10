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

import com.ald.fanbei.api.biz.bo.Auth51FundRespBo;
import com.ald.fanbei.api.biz.bo.assetside.AssetSideRespBo;
import com.ald.fanbei.api.biz.third.util.AssetSideEdspayUtil;
import com.ald.fanbei.api.biz.third.util.Auth51FundUtil;
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
	@Resource
	Auth51FundUtil auth51FundUtil;

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@RequestMapping(value = { "/giveBack" }, method = {RequestMethod.POST,RequestMethod.GET}, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Auth51FundRespBo giveBack(@RequestBody String requestData,HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonObj = JSON.parseObject(requestData);
		String orderSn = StringUtil.null2Str(jsonObj.get("order_id"));
		String status = StringUtil.null2Str(jsonObj.get("status"));
		String userId = StringUtil.null2Str(jsonObj.get("user_id"));
		logger.info("Auth51FundController giveBack,orderSn="+orderSn+",status=" + status + ",userId=" + userId);
		Auth51FundRespBo notifyRespBo = auth51FundUtil.giveBack(orderSn, status, userId);
		logger.info("Auth51FundController giveBac,orderSn="+orderSn+ ",status=" + status+ ",userId=" + userId+",returnMsg="+notifyRespBo.toString());
		return notifyRespBo;
	}
}
