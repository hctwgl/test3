package com.ald.fanbei.api.biz.third.util.yibaopay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yeepay.g3.facade.yop.ca.dto.DigitalEnvelopeDTO;
import com.yeepay.g3.facade.yop.ca.dto.DigitalSignatureDTO;
import com.yeepay.g3.facade.yop.ca.enums.CertTypeEnum;
import com.yeepay.g3.facade.yop.ca.enums.DigestAlgEnum;
import com.yeepay.g3.frame.yop.ca.DigitalEnvelopeUtils;
import com.yeepay.g3.sdk.yop.client.*;
import com.yeepay.g3.sdk.yop.utils.InternalConfig;

import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

public class YeepayService {

	//yop接口应用URI地址
	public static final String BASE_URL = "baseURL";
	public static final String TRADEORDER_URL = "tradeOrderURI";
	public static final String ORDERQUERY_URL = "orderQueryURI";
	public static final String REFUND_URL = "refundURI";
	public static final String REFUNDQUERY_URL = "refundQueryURI";
	public static final String MULTIORDERQUERY_URL = "multiOrderQueryURI";
	public static final String ORDERCLOSE_URL = "orderCloseURI";
	public static final String CASHIER_URL ="CASHIER";
	
	//接口参数
	public static final String[] TRADEORDER = {"parentMerchantNo","merchantNo","orderId","orderAmount","timeoutExpress","requestDate","redirectUrl","notifyUrl","goodsParamExt","paymentParamExt","industryParamExt","memo","riskParamExt","csUrl"};
	public static final String[] ORDERQUERY = {"parentMerchantNo","merchantNo","orderId","uniqueOrderNo"};
	public static final String[] REFUND = {"parentMerchantNo","merchantNo","orderId","uniqueOrderNo","refundRequestId","refundAmount","description","memo","notifyUrl"};
	public static final String[] REFUNDQUERY = {"parentMerchantNo","merchantNo","refundRequestId","orderId","uniqueRefundNo"};
	public static final String[] MULTIORDERQUERY = {"status","parentMerchantNo","merchantNo","requestDateBegin","requestDateEnd","pageNo","pageSize"};
	public static final String[] ORDERCLOSE = {"orderId","parentMerchantNo","merchantNo","uniqueOrderNo","description"};
	
	//支付方式
	public static final String[] CASHIER = {"merchantNo","token","timestamp","directPayType","cardType","userNo","userType"};
	public static final String[] WECHATGZH = {"merchantNo","token","timestamp","appId","openId","userNo","userType"};
	public static final String[] DIRECT = {"merchantNo","token","timestamp","directPayType","cardType","userNo","userType"};
	public static final String[] SCAN = {"merchantNo","token","timestamp","codeType","code","storeCode","deviceSn"};
	public static final String[] REDIRECT = {"merchantNo","parentMerchantNo","orderId"};



	//获取对应的请求地址
	public static String getUrl(String payType){
		return Configuration.getInstance().getValue(payType);
	}




	//获取对应的请求参数
	public static String[] getParams(String payType){
		String[] params = null;
		if("CASHIER".equals(payType))
			params = CASHIER;
		if("WECHATGZH".equals(payType))
			params = WECHATGZH;
		if("DIRECT".equals(payType))
			params = DIRECT;
		if("CASHIER".equals(payType))
			params = CASHIER;
		if("SCAN".equals(payType))
			params = SCAN;
		if("REDIRECT".equals(payType))
			params = REDIRECT;
		return params;
	}
	
	//拼接支付链接
	public static String getUrl(String payType, Map<String,String> paramValues) throws UnsupportedEncodingException{
		StringBuffer url = new StringBuffer();
		url.append(getUrl(payType));
		StringBuilder stringBuilder = new StringBuilder();
		String[] params = getParams(payType);
		System.out.println(paramValues);
		for (int i = 0; i < params.length; i++) {
			String name = params[i];
//			System.out.println("name:"+name);
//			System.out.println("paramValue:"+paramValues.get(name));
			String value =null;
			if(paramValues.get(name) !=null) {
				value = URLEncoder.encode(paramValues.get(name), "UTF-8");
			}
			if(i != 0){
				stringBuilder.append("&");
			}
			stringBuilder.append(name+"=");
			if(value!=null){
				stringBuilder.append(value);
			}
		}
		System.out.println(stringBuilder);
		String sign = getSign(stringBuilder.toString());
		url.append("?sign="+sign+"&"+stringBuilder);
		return url.toString();
	}


	//获取父商编
	public static String getParentMerchantNo(){
		return Configuration.getInstance().getValue("parentMerchantNo");
	}

	//获取子商编
	public static String getMerchantNo(){
		return Configuration.getInstance().getValue("merchantNo");
	}


	//获取密钥P12
	public static PrivateKey getSecretKey(){
		InternalConfig internalConfig = InternalConfig.Factory.getInternalConfig();
		PrivateKey isvPrivateKey = internalConfig.getISVPrivateKey(CertTypeEnum.RSA2048);
		return isvPrivateKey;
	}

	//获取公钥
	public static PublicKey getPublicKey(){
		InternalConfig internalConfig = InternalConfig.Factory.getInternalConfig();
		PublicKey isvPublicKey = internalConfig.getYopPublicKey(CertTypeEnum.RSA2048);
		return isvPublicKey;
	}

	//获取sign
	public static String getSign(String stringBuilder){
		String appKey = "OPR:"+getMerchantNo();
		PrivateKey isvPrivateKey = getSecretKey();
		DigitalSignatureDTO digitalSignatureDTO = new DigitalSignatureDTO();
		digitalSignatureDTO.setAppKey(appKey);
		digitalSignatureDTO.setCertType(CertTypeEnum.RSA2048);
		digitalSignatureDTO.setDigestAlg(DigestAlgEnum.SHA256);
		digitalSignatureDTO.setPlainText(stringBuilder.toString());
		String sign = DigitalEnvelopeUtils.sign0(digitalSignatureDTO,isvPrivateKey);
		return sign;
	}
	
	/**
	 * 请求YOP接口
	 * params 请求参数,parentMerchantNo除外
	 * uri 请求yop的应用URI地址
	 * paramSign 请求参数的验签顺序
	 */
	public static Map<String, String> requestYOP(Map<String, String> params, String uri, String[] paramSign){

		StringBuilder sb = new StringBuilder();
		Map<String, String> result = new HashMap<String, String>();
		String BASE_URL = getUrl("baseURL");
		String parentMerchantNo = YeepayService.getParentMerchantNo();
		String merchantNo = YeepayService.getMerchantNo();
		params.put("parentMerchantNo", parentMerchantNo);
		params.put("merchantNo", merchantNo);

		YopRequest request = new YopRequest("OPR:"+merchantNo,"",BASE_URL);

		for (int i = 0; i < paramSign.length; i ++) {
			String key = paramSign[i];
			request.addParam(key, params.get(key));
		}


		YopResponse response = YiBaoClient.postRsa(uri,request);
//		YopResponse response = YopClient3.postRsa(uri, request);
		
		System.out.println(response.toString());
		if("FAILURE".equals(response.getState())){
			if(response.getError() != null)
			result.put("code",response.getError().getCode());
			result.put("message",response.getError().getMessage());
			return result;
		}
		if (response.getStringResult() != null) {
			result = parseResponse(response.getStringResult());
		}
		result.put("sign",response.getSign());
		result.put("timeStamp",response.getTs().toString());
		return result;
	}

	private static RestTemplate getRestTemplate(YopRequest request) {
		if (null == request.getConnectTimeout() && null == request.getReadTimeout()) {
			return null;
		} else {
			int connectTimeout = null != request.getConnectTimeout() ? request.getConnectTimeout().intValue() : YopConfig.getConnectTimeout();
			int readTimeout = null != request.getReadTimeout() ? request.getReadTimeout().intValue() : YopConfig.getReadTimeout();
			return new YopRestTemplate(connectTimeout, readTimeout);
		}
	}



	//将获取到的response转换成json格式
	public static Map<String, String> parseResponse(String response){
		
		Map<String,String> jsonMap  = new HashMap<>();
		jsonMap	= JSON.parseObject(response, 
				new TypeReference<TreeMap<String,String>>() {});
		
		return jsonMap;
	}

	/**
	 *
	 * @param response
	 * @return
	 */
	public static Map<String, String> callback(String response){
		DigitalEnvelopeDTO dto = new DigitalEnvelopeDTO();
		dto.setCipherText(response);
		Map<String,String> jsonMap  = new HashMap<>();
		try {
			InternalConfig internalConfig = InternalConfig.Factory.getInternalConfig();
			PrivateKey privateKey = internalConfig.getISVPrivateKey(CertTypeEnum.RSA2048);
			PublicKey publicKey = internalConfig.getYopPublicKey(CertTypeEnum.RSA2048);
			dto = DigitalEnvelopeUtils.decrypt(dto, privateKey, publicKey);
			System.out.println(dto.getPlainText());
			jsonMap = parseResponse(dto.getPlainText());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonMap;
	}
}
