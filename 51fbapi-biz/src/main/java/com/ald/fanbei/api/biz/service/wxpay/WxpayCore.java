/*
 *@Copyright (c) 2016, 杭州喜马拉雅家居有限公司 All Rights Reserved. 
 */
package com.ald.fanbei.api.biz.service.wxpay;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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


/**
 * @类描述：WxpayCore
 * 
 *@author hexin 2017年2月27日 下午17:03:05
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class WxpayCore {
	
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
	public static String toQueryString(Map<String, String> param,String wxKey) {
		WxURLBuilder ub = new WxURLBuilder();
		List<String> paramNames = new ArrayList<String>(param.keySet());
		Collections.sort(paramNames);
		for (String key : paramNames) {
			if (WxpayConfig.KEY_SIGN.equals(key))
				continue;

			String value = param.get(key);
			if (value == null || value.isEmpty())
				continue;

			ub.appendParam(key, value);
		}
		ub.append("&key=").append(wxKey);
		return (ub.toString());
	}

	// TO_XML
	public static String buildXMLBody(Map<String, String> orderData) {
		StringBuilder xml = new StringBuilder();
		List<String> paramNames = new ArrayList<String>(orderData.keySet());
		Collections.sort(paramNames);
		
		xml.append("<xml>");
		for (String k : paramNames) {
			String v = orderData.get(k);
			if (v != null)
				xml.append('<').append(k).append('>').append(v).append("</").append(k).append('>');
		}
		xml.append("</xml>");

		return (xml.toString());
	}
	
//	public static String executePostXML(String fullURL, String bodyXML) {
//		try {
//			String result = HttpsUtil.doPostToWx(fullURL, bodyXML, 10000, 10000);
//			return result;
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new AppException("request wx error",AppExceptionCode.REQ_WXPAY_ERR,e);
//		}
////		return (resp.getEntity().getContent());
//	}
}
