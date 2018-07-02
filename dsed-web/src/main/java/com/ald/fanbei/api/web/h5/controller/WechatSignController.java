package com.ald.fanbei.api.web.h5.controller;

import java.net.URLDecoder;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.wxpay.WechatSignatureData;
import com.ald.fanbei.api.biz.service.wxpay.WxSignBase;
import com.ald.fanbei.api.biz.util.WxUtil;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.H5CommonResponse;

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
@RestController
@RequestMapping(value = "/wechat", produces = "application/json;charset=UTF-8")
public class WechatSignController extends H5Controller {
    @Resource
    AfResourceService afResourceService;

    @RequestMapping(value = "/getSign", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public String getSign(HttpServletRequest request, HttpServletResponse response) {
	String resultStr = H5CommonResponse.getNewInstance(false, "验签失败").toString();
	WechatSignatureData wechatSignatureData = new WechatSignatureData();
	String url = null;
	try {
	    String urlOld = request.getParameter("url");
	    urlOld =URLDecoder.decode(urlOld,"utf-8");
	    logger.info("++++++++++request url 1111:" + urlOld);
	    if (StringUtil.isNotBlank(urlOld)) {
		int index = urlOld.indexOf("#");
		if (index > 0) {
		    url = urlOld.substring(0, index);
		} else {
		    url = urlOld;
		}
	    }
	    logger.info("++++++++++request url 22222:" + urlOld);

	    // 获取secret 和appId
	    AfResourceDo afResourceDo = afResourceService.getWechatConfig();
	    // value = appId ; value1= secret

	    if (null != afResourceDo) {

		String nonceStr = DigestUtil.MD5(UUID.randomUUID().toString());
		Long timestamp = System.currentTimeMillis() / 1000;
		String appId = afResourceDo.getValue();
		String secret = afResourceDo.getValue1();
		String ticket = WxUtil.getJsapiTicket(appId, secret);

		String content = new StringBuilder("jsapi_ticket=").append(ticket).append("&noncestr=").append(nonceStr).append("&timestamp=").append(timestamp).append("&url=").append(url).toString();
		logger.info("getSign content is:{}", content);
		byte[] in = content.getBytes("UTF-8");
		byte[] out = WxSignBase.SHA1Digest(in);
		String sign = WxSignBase.bytesToHex(out);
		logger.info("getSign sign is:{}", sign);

		wechatSignatureData.setAppId(appId);
		wechatSignatureData.setNonceStr(nonceStr);
		wechatSignatureData.setSign(sign);
		wechatSignatureData.setTimestamp(timestamp);
		resultStr = H5CommonResponse.getNewInstance(true, "验签成功", "", wechatSignatureData).toString();
	    }
	} catch (FanbeiException e) {
	    logger.error("getSign error", e);
	} catch (Exception e) {
	    logger.error("getSign error", e);
	}

	return resultStr;
    }

    @RequestMapping(value = "/getOpenId", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @Deprecated
    public String getOpenId(HttpServletRequest request, HttpServletResponse response) {
	String resultStr = "";
	try {
	    String code = ObjectUtils.toString(request.getParameter("code"));
	    if (StringUtil.isNotBlank(code)) {
		String openId = WxUtil.getOpenidByCode(code);
		resultStr = H5CommonResponse.getNewInstance(true, "succeed to get openId", "", openId).toString();
	    }
	} catch (FanbeiException e) {
	    resultStr = H5CommonResponse.getNewInstance(false, "getOpenId error", "", e.getErrorCode().getDesc()).toString();
	} catch (Exception e) {
	    resultStr = H5CommonResponse.getNewInstance(false, "getOpenId error", "", e).toString();
	}
	return resultStr;
    }

}
