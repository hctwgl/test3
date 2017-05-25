/*
 *@Copyright (c) 2016, 杭州喜马拉雅家居有限公司 All Rights Reserved. 
 */
package com.ald.fanbei.api.biz.service.wxpay;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.HttpsUtil;


/**
 * @类描述：WxpayCore
 * 
 *@author hexin 2017年2月27日 下午17:03:05
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class WxpayCore {
	
	 protected static Logger   logger           = LoggerFactory.getLogger(WxpayCore.class);
	
	/**
	 * 发起退款请求
	 * @param url 退款接口地址
	 * @param body 退款请求报文
	 * @param mchId 微信商户好的machId
	 * @param certPath 微信商户号的证书路径
	 * @return
	 * @throws Exception
	 */
	public static String refundPost(String url, String body,String mchId,String certPath) throws Exception {
		String result = "";
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		FileInputStream instream = new FileInputStream(new File(certPath));
		try {
			keyStore.load(instream, mchId.toCharArray());
		} finally {
			instream.close();
		}

		// Trust own CA and all self-signed certs
		SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, mchId.toCharArray()).build();
		// Allow TLSv1 protocol only
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" }, null,
				SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
		CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
		try {
			HttpPost httppost = new HttpPost(url);
			httppost.setEntity(new StringEntity(body));
			System.out.println("executing request" + httppost.getRequestLine());

			CloseableHttpResponse response = httpclient.execute(httppost);
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent(),"utf-8"));
					String text;
					while ((text = bufferedReader.readLine()) != null) {
						result = result + text;
					}
				}
				EntityUtils.consume(entity);
			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}
		return result;
	}

	/**
	 * 传入微信的参数按key_value用&拼接，安装key顺序排序
	 * @param param 需要传入微信的参数
	 * @param wxKey 微信key
	 * @return
	 */
	public static String toQueryString(Map<String, Object> param) {
		WxURLBuilder ub = new WxURLBuilder();
		List<String> paramNames = new ArrayList<String>(param.keySet());
		Collections.sort(paramNames);
		for (String key : paramNames) {
			if (WxpayConfig.KEY_SIGN.equals(key))
				continue;

			String value = param.get(key)+"";
			if (value == null || value.isEmpty())
				continue;

			ub.appendParam(key, value);
		}
		ub.append("&key=").append(AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_WX_KEY), ConfigProperties.get(Constants.CONFKEY_AES_KEY)));
		return (ub.toString());
	}

	// TO_XML
	public static String buildXMLBody(Map<String, Object> orderData) {
		StringBuilder xml = new StringBuilder();
		List<String> paramNames = new ArrayList<String>(orderData.keySet());
		Collections.sort(paramNames);
		
		xml.append("<xml>");
		for (String k : paramNames) {
			String v = orderData.get(k)+"";
			if (v != null)
				xml.append('<').append(k).append('>').append(v).append("</").append(k).append('>');
		}
		xml.append("</xml>");

		return (xml.toString());
	}
	
	/**
	 * 构建微信支付订单的参数，需要传入到微信那边的参数
	 *@return
	 *@throws UnsupportedEncodingException
	 */
	public static Map<String,Object> buildWxOrderParam(String orderNo,String goodsName,BigDecimal totalFee,String notifyUrl,String attach) throws UnsupportedEncodingException{
		String outTradeNo = orderNo;
		String body = goodsName;
		Map<String,Object> orderData = new HashMap<String,Object>();
		logger.info("wx pay order notifyUrl"+ notifyUrl);
		if(Constants.INVELOMENT_TYPE_ONLINE.equals(ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE)) ||Constants.INVELOMENT_TYPE_PRE_ENV.equals(ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE))){
			orderData.put("total_fee", BigDecimalUtil.multiply(totalFee, new BigDecimal(100)).intValue()+"");
		}else{
			orderData.put("total_fee", "1");
			body = "测试单_" + body;
		}
		String detail = body;
		if(body.length() > 128){
			body = body.substring(0,128);
		}
		orderData.put("appid", WxpayConfig.WX_APP_ID);
		String mchId = WxpayConfig.WX_MCH_ID;
		orderData.put("mch_id", mchId);
		String nonceStr = DigestUtil.MD5(UUID.randomUUID().toString());
		orderData.put("nonce_str", nonceStr);
		orderData.put("body", body);
		orderData.put("detail", detail);
		orderData.put("attach", attach);
		orderData.put("out_trade_no", outTradeNo);
		orderData.put("spbill_create_ip", "104.128.80.228");//115.198.201.99
		orderData.put("notify_url", notifyUrl);
		orderData.put("trade_type", "APP");
		
		StringBuilder sb = new StringBuilder().append(WxpayCore.toQueryString(orderData));

		String sign = WxSignBase.byteToHex(WxSignBase.MD5Digest(sb.toString().getBytes("utf-8")));
		orderData.put("sign", sign.toUpperCase());
		return orderData;
	}
	
	public static String executePostXML(String fullURL, String bodyXML) {
		try {
			String result = HttpsUtil.doPostToWx(fullURL, bodyXML, 10000, 10000);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new FanbeiException("request wx error",FanbeiExceptionCode.REQ_WXPAY_ERR,e);
		}
//		return (resp.getEntity().getContent());
	}
}
