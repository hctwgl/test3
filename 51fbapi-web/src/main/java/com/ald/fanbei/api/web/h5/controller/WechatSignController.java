package com.ald.fanbei.api.web.h5.controller;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ald.fanbei.api.biz.service.wxpay.WechatSignatureData;
import com.ald.fanbei.api.biz.service.wxpay.WxSignBase;
import com.ald.fanbei.api.biz.service.wxpay.WxUtil;
import com.ald.fanbei.api.biz.service.wxpay.WxpayCore;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.web.common.H5CommonResponse;

import antlr.StringUtils;
import io.netty.util.internal.ObjectUtil;

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
@RestController("/wechat")
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
		try{
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
		String ticket = WxUtil.getJsapiTicket();
		String appId = WxUtil.getWxAppId();
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
		resultStr = H5CommonResponse.getNewInstance(true, "验签成功", "", wechatSignatureData).toString();
		}catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false,"验签失败","",e.getErrorCode().getDesc()).toString();
			logger.error("getSign error" ,e);
		}catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false,"验签失败","",e.getMessage()).toString();
			logger.error("getSign error" ,e);
		}
		
		return resultStr;
	}
	
	@RequestMapping(value = "/getOpenId",method = RequestMethod.POST,produces= "text/html;charset=UTF-8")
	public String getOpenId(HttpServletRequest request , HttpServletResponse response) {
		String resultStr = "";
		try{
			String code = ObjectUtils.toString(request.getParameter("code"));
			if (StringUtil.isNotBlank(code)) {
				String openId = WxUtil.getOpenidByCode(code);
				resultStr = H5CommonResponse.getNewInstance(true, "succeed to get openId", "", openId).toString();
			}
		}catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "getOpenId error", "", e.getErrorCode().getDesc()).toString();
		}catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "getOpenId error", "", e).toString();
		}
		return resultStr;
	}
	
}
