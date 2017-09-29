package com.ald.fanbei.api.web.h5.controller;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ald.fanbei.api.biz.service.wxpay.WechatSignatureData;
import com.ald.fanbei.api.biz.service.wxpay.WxSignBase;
import com.ald.fanbei.api.biz.service.wxpay.WxUtil;
import com.ald.fanbei.api.biz.service.wxpay.WxpayCore;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.StringUtil;

import antlr.StringUtils;

/**
 * 
 * <p>
 * Title:H5GGShareController
 * <p>
 * <p>
 * Description:
 * <p>
 * 
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author qiao
 * @date 2017-9-27 15:32:33
 *
 */
@RestController("/wechatSign")
public class WechatSignController extends H5Controller{
	@Autowired
	WxUtil wxUtil;
	@Autowired
	WxSignBase wxSignBase;
	@RequestMapping(value = "/getSign",method = RequestMethod.POST,produces= "text/html;charset=UTF-8")
	public String getSign(HttpServletRequest request , HttpServletResponse response) {
		String resultStr = "";
		WechatSignatureData wechatSignatureData = new WechatSignatureData();
		String url = null;
		String urlOld = request.getRequestURI();
		if (StringUtil.isNotBlank(urlOld)) {
			int index = urlOld.indexOf("#");
			if (index > 0 ) {
				url = urlOld.substring(0, index);
			}else{
				url = urlOld;
			}
		}
		String nonceStr = DigestUtil.MD5(UUID.randomUUID().toString());
		Long timestamp = System.currentTimeMillis() / 1000;
		String ticket = wxUtil.getJsapiTicket();
		String appId = wxUtil.getWxAppId();
		String content = new StringBuilder("jsapi_ticket=").append(ticket).append("&noncestr=").append(nonceStr)
				.append("&timestamp=").append(timestamp).append("&url=").append(url).toString();
		logger.info("getSign content is:{}", content);
		byte[] in = content.getBytes();
		byte[] out = wxSignBase.SHA1Digest(in);
		String sign = new String(out);
		logger.info("getSign sign is:{}", sign);
		
		wechatSignatureData.setAppId(appId);
		wechatSignatureData.setNonceStr(nonceStr);
		wechatSignatureData.setSign(sign);
		wechatSignatureData.setTimestamp(timestamp);
		
		
		return resultStr;
	}
	
}
