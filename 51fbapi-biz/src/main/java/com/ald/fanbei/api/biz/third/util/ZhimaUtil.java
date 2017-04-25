package com.ald.fanbei.api.biz.third.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.ald.fanbei.api.biz.bo.ZhimaAuthResultBo;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.antgroup.zmxy.openplatform.api.DefaultZhimaClient;
import com.antgroup.zmxy.openplatform.api.ZhimaApiException;
import com.antgroup.zmxy.openplatform.api.ZhimaResponse;
import com.antgroup.zmxy.openplatform.api.request.ZhimaAuthInfoAuthorizeRequest;
import com.antgroup.zmxy.openplatform.api.request.ZhimaAuthInfoAuthqueryRequest;
import com.antgroup.zmxy.openplatform.api.request.ZhimaCreditIvsDetailGetRequest;
import com.antgroup.zmxy.openplatform.api.request.ZhimaCreditScoreGetRequest;
import com.antgroup.zmxy.openplatform.api.request.ZhimaCreditWatchlistiiGetRequest;
import com.antgroup.zmxy.openplatform.api.response.ZhimaAuthInfoAuthqueryResponse;
import com.antgroup.zmxy.openplatform.api.response.ZhimaCreditIvsDetailGetResponse;
import com.antgroup.zmxy.openplatform.api.response.ZhimaCreditScoreGetResponse;
import com.antgroup.zmxy.openplatform.api.response.ZhimaCreditWatchlistiiGetResponse;

/**
 * 
 *@类描述：芝麻信用工具类
 *@author 陈金虎 2017年1月20日 下午2:38:40
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class ZhimaUtil extends AbstractThird {

	private static DefaultZhimaClient ZhimaClient = null;
	private static String DEFAULT_PLATFORM = "zmop";
//	private static String PRODUCT_CODE = "w1010100100000000022";
	private static String CHANNEL = "app";
	private static String DEFAULT_IDENTITY_TYPE = "2";
	private static String DEFAULT_CERTtYPE = "IDENTITY_CARD";
	private static String DEFAULT_CERT_TYPE = "100";

	/**
	 * 获取芝麻客户端示例
	 * 
	 * @return
	 */
	private synchronized static DefaultZhimaClient getZhimaClient() {
		if (ZhimaClient == null) {
			String gatewayUrl = "https://zmopenapi.zmxy.com.cn/openapi.do";
			String appId = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_ZHIMA_APPID), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
			String privateKey =  AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_ZHIMA_PRIKEY), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
			String zhimaPublicKey =  AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_ZHIMA_PUBKEY), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
			ZhimaClient = new DefaultZhimaClient(gatewayUrl, appId, privateKey,zhimaPublicKey);
		}
		return ZhimaClient;
	}

	/**
	 * 行业关注名单
	 * 
	 * @param openId
	 * @return
	 */
	public static ZhimaCreditWatchlistiiGetResponse watchlistiiGet(String openId) {
		ZhimaCreditWatchlistiiGetRequest req = new ZhimaCreditWatchlistiiGetRequest();
		req.setPlatform(DEFAULT_PLATFORM);
		req.setOpenId(openId);
		req.setProductCode("w1010100100000000022");
		String transactionId = getTransactionId(CommonUtil.getRandomNumber(13));
		req.setTransactionId(transactionId);
		try {
			ZhimaCreditWatchlistiiGetResponse resp = (ZhimaCreditWatchlistiiGetResponse) getZhimaClient().execute(req);
			dealWithZhimaResp(resp, "creditWatchlistiiGet",transactionId, openId);
			return resp;
		} catch (Exception e) {
			logger.error("watchlistii error;|" + openId, e);
			throw new FanbeiException(FanbeiExceptionCode.ZM_ERROR);
		}
	}
	
	public static ZhimaAuthResultBo decryptAndVerifySign(String respBody,String sign){
		ZhimaAuthResultBo zarb = new ZhimaAuthResultBo();
		
		try{
			zarb.setSuccess(false);
			String result = getZhimaClient().decryptAndVerifySign(respBody , sign);
			thirdLog.info(StringUtil.appendStrs("methodName=decryptAndVerifySign",",params=", respBody , "|" , sign));
			if(StringUtil.isBlank(result)){
				throw new FanbeiException("zhima auth error",FanbeiExceptionCode.ZM_AUTH_ERROR);
			}
			String[] resultFields = result.split("&");
			for(String item:resultFields){
				if(StringUtil.isNotBlank(item)){
					String[] keyValue = item.split("=");
					switch(keyValue[0]){
						case "success":
							zarb.setSuccess(new Boolean(keyValue[1]));
							break;
						case "open_id":
							zarb.setOpenId(keyValue[1]);
							break;
						case "app_id":
							zarb.setAppId(keyValue[1]);
							break;
						case "error_code":
							zarb.setErrorCode(keyValue[1]);
							break;
						case "error_message":
							zarb.setErrorMssage(keyValue[1]);
						default:
							break;
					}
				}
			}
			
		}catch(Exception e){
			logger.error(StringUtil.appendStrs("methodName=decryptAndVerifySign", ",params=", respBody , "|" , sign),e);
		}
		
		return zarb;
	}

	/**
	 * 获取芝麻信用评分
	 * 
	 * @param openId
	 * @return
	 */
	public static ZhimaCreditScoreGetResponse scoreGet(String openId) {
		ZhimaCreditScoreGetRequest req = new ZhimaCreditScoreGetRequest();
		req.setPlatform(DEFAULT_PLATFORM);
		req.setOpenId(openId);
		req.setProductCode("w1010100100000000001");
		String transactionId = getTransactionId(CommonUtil.getRandomNumber(13));
		req.setTransactionId(transactionId);
		try {
			ZhimaCreditScoreGetResponse resp = (ZhimaCreditScoreGetResponse) getZhimaClient().execute(req);
			dealWithZhimaResp(resp, "scoreGet", transactionId,openId);
			return resp;
		} catch (Exception e) {
			logger.error("scoreGet error,openId=" + openId, e);
			throw new FanbeiException(FanbeiExceptionCode.ZM_ERROR);
		}
	}

	/**
	 * 反欺诈信息验证
	 * @param idNumber 身份证号
	 * @param realName 真实姓名
	 * @param mobile 手机号
	 * @param email 邮箱地址
	 * @param address 地址
	 * @return
	 */
	public static ZhimaCreditIvsDetailGetResponse ivsDetailGet(String idNumber,String realName,String mobile,String email,String address) {
		ZhimaCreditIvsDetailGetRequest req = new ZhimaCreditIvsDetailGetRequest();
		req.setAddress(address);;
//		req.setBankCard(bankCard);
		req.setCertNo(idNumber);
		req.setCertType(DEFAULT_CERT_TYPE);
		req.setChannel(CHANNEL);
		req.setEmail(email);
//		req.setExtParams(extParams);
//		req.setImei(imei);
//		req.setImsi(imsi);
//		req.setIp(ip);
//		req.setMac(mac);
		req.setMobile(mobile);
		req.setName(realName);
		req.setPlatform(DEFAULT_PLATFORM);
		req.setProductCode("w1010100000000000103");
//		req.setScene(scene);
		String transactionId = getTransactionId(CommonUtil.getRandomNumber(13));
		req.setTransactionId(transactionId);
//		req.setWifimac(wifimac);
		try{
			ZhimaCreditIvsDetailGetResponse resp = (ZhimaCreditIvsDetailGetResponse)getZhimaClient().execute(req);
			dealWithZhimaResp(resp, "isvDetailGet", transactionId,idNumber,realName,mobile,email,address);
			return resp;
		}catch(Exception e){
			logger.error(StringUtil.appendStrs("isvDetailGet error;|",idNumber,"|",realName,"|",mobile,"|",email,"|",address), e);
			throw new FanbeiException(FanbeiExceptionCode.ZM_ERROR);
		}

	}
	
	/**
	 * 查询用户是否授权芝麻信用
	 * @param idNumber 身份证号
	 * @param realName 真实姓名
	 * @return
	 */
	public static ZhimaAuthInfoAuthqueryResponse authQuery(String idNumber,String realName){
		ZhimaAuthInfoAuthqueryRequest req = new ZhimaAuthInfoAuthqueryRequest();
		Map<String,String> paramMap = new HashMap<String,String>();
		paramMap.put("certNo", idNumber);
		paramMap.put("certType", DEFAULT_CERTtYPE);
		paramMap.put("name", realName);
		req.setChannel(CHANNEL);
		req.setIdentityParam(JSON.toJSONString(paramMap));
		req.setIdentityType(DEFAULT_IDENTITY_TYPE);
		req.setPlatform(DEFAULT_PLATFORM);
		try{
			ZhimaAuthInfoAuthqueryResponse resp = (ZhimaAuthInfoAuthqueryResponse)getZhimaClient().execute(req);
			dealWithZhimaResp(resp, "authQuery", idNumber,realName);
			return resp;
		}catch(Exception e){
			logger.error(StringUtil.appendStrs("authQuery error;|",idNumber,"|",realName), e);
			throw new FanbeiException(FanbeiExceptionCode.ZM_ERROR);
		}
	}
	
	/**
	 * 芝麻信用授权
	 * @param idNumber 身份证号
	 * @param name 真实姓名
	 * @param uid
	 * @return
	 */
	public static String authorize(String idNumber,String realName) {
//		Map<String,String> result = new HashMap<String, String>();
		ZhimaAuthInfoAuthorizeRequest req = new ZhimaAuthInfoAuthorizeRequest();
		req.setChannel("apppc");
		req.setPlatform("zmop");
		req.setApiVersion("1.0");
		req.setIdentityType("2");// 必要参数
		Map<String,String> identityParam = new LinkedHashMap<String, String>();
		identityParam.put("name", realName);
		identityParam.put("certType", "IDENTITY_CARD");
		identityParam.put("certNo", idNumber);
		Map<String,String> bizParam = new LinkedHashMap<String, String>();
		bizParam.put("auth_code", "M_H5");
		bizParam.put("channelType", "app");
		bizParam.put("state", idNumber.substring(8) + "_" + System.currentTimeMillis());
		
		req.setIdentityParam(JSON.toJSONString(identityParam));
		req.setBizParams(JSON.toJSONString(bizParam));
		
//		Map<String,String> paramsMap = new HashMap<String, String>();
		try {
			System.out.println(identityParam);
			System.out.println(bizParam);
			String url = getZhimaClient().generatePageRedirectInvokeUrl(req);
			System.out.println("url" + url);
			return url;
//			String paramsStr = url.substring(url.indexOf("?")+1);
//			String[] paramsArr = paramsStr.split("&");
//			for(String item:paramsArr){
//				String itemKey = item.substring(0,item.indexOf("="));
//				String itemValue = item.substring(item.indexOf("=")+1);
//				paramsMap.put(itemKey, itemValue);
//			}
			
		} catch (ZhimaApiException e) {
			logger.error(StringUtil.appendStrs("authorize error;|",idNumber,"|",realName), e);
			throw new FanbeiException(FanbeiExceptionCode.ZM_ERROR);
		}
		
//		result.put("param", paramsMap.get("param"));
//		result.put("sign", paramsMap.get("sign"));
//		result.put("appId", paramsMap.get("app_id"));
		
//		return result;
	}
	
	
	/**
	 * 同意处理芝麻信用返回
	 * @param resp
	 * @param methodName
	 * @param param
	 */
	private static void dealWithZhimaResp(ZhimaResponse resp,String methodName, String... param) {
		StringBuffer sb = new StringBuffer();
		for (String item : param) {
			sb = sb.append("|").append(item);
		}
		thirdLog.info(StringUtil.appendStrs("methodName=", methodName,",params=", sb.toString() + ",resp=" + resp==null?"":JSON.toJSONString(resp)));
		if (resp.isSuccess()) {
			return;
		} else {
			throw new FanbeiException("调用芝麻失败", FanbeiExceptionCode.ZM_ERROR);
		}
	}
	
	
	private static String getTransactionId(String last13Characts){
		return System.currentTimeMillis() + last13Characts;
	}
	
	public static void main(String[] args) {
		try {
			String result = getZhimaClient().decryptAndVerifySign("siaE0N11pnSM5rYCr6sPO+a+EZJCWv8bNh8B34Pmk2sUo+WdUuukzIvw0TZNxObD/EuAbW42o8eihZ/GKWTDmS7ZALjFFO2Ld5KIoqX7Ax22t3ifYMKawqXk7OYm7IfIn7DR4dqNjkrtSapK7zRuroDI7WDvBWRidAjUL2FKX50Mq/pR1SFELqkmH1qnGmeGNOLjiFqdX6MVahJ+DtPDH2HrgEok2QoTz5Tt0mQ9jnU2S2W7VSORu/gEn472CIGKu1LVjUz1xdwZqOYIH9O4bdnvTJ4u2WcixKsgr2gZ7iE4NCbb0l+4E1dJo0RHU/lBNlQK9DdDhvk/txXDBvp6xQ==" , 
					"bGy95Psra4PvWfMge6Tq1GCDrM4D9HFKAufljqWsnRuRJMTtihOXJyS22wSsAGRlo+XV0pqV+qotg22TSoVQjeAitrv+mcgb9KZsrjC4NFMJUepqRrH2Hm/30FTabFnAhkWxRYde1bvSOnTk6l6GDz3tG91G9fgkBzTqT9nExoM=");
			System.out.println(result);
		} catch (ZhimaApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	//芝麻开放平台地址
//    private String gatewayUrl     = "https://zmopenapi.zmxy.com.cn/openapi.do";
//    //商户应用 Id
//    private String appId          = "1000743";
//    //商户 RSA 私钥
//    private String privateKey     = "MIICeQIBADANBgkqhkiG9w0BAQEFAASCAmMwggJfAgEAAoGBAOD588mHWwjgaVsAdAE2ralhuEwM07C1P4Lph1tvQLwijADSyyDeTpqHEOT78F2dsmpRRqvgLS77cWnegHQw297A76nibBZn4sewefnSMM3ApJAKAi1naEi4NzrM+dHGPZ4Idb3Az7ALfFqKeQ2m7G86RR03kjpqtzcCBwNCLiCNAgMBAAECgYEAnbJXEiJQy24SK3mr1tXu8NXQi25KTIkflbH/8TWQmM9Wd5VKUSXCz0pxqzB2Egjh8Og7s2qWAWK64szWGZvN4YssWFN7Kn0HLput5VmV9pd+KfVx3Lf3BsIfF9AvJeQOTffvOkhaLKPZKhFqg4FBhrEV8gduR9Ai1FyImAXWfUECQQD+yvNQyIsomd0B3W6yf+eWrr4bCbtWCiuIii5/H9PEhxIQLRoYUcPAgnlMrGt9sYbBpBJsE5qfEN1Ln7LgYHjpAkEA4grWESJyPzKPm5VZUVB9aSly6NSRQ9MU4GB6qMgubU9q7vLGAsR9kuM+b70ojePfnkk4jOwcOJfTPCel5hMkBQJBAJGkxU0KNbGxsgmc3+gdAO67WGPwPivCiHv2MPnt4YlXhFXG0kHQi0sBygCwFom07sjF1tn8osgGRdkyondr7fECQQCBCXaSaXuWoCJiyqsmRDCTa9nxGAelFEaCoBDlcQEv3XpJ1cU7pzeYNqlZ2D3iYgcxsNLbf53MoL8xQ+DsqliRAkEA7Tq1edlV24HivSkGbjYEv+fBJA6f99LIHb/FGrneh8i5MXxC9312hNz5CNnfoY1czWfEWdnalK5DlN9GZwz2ew==";
//    //芝麻 RSA 公钥
//    private String zhimaPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDbhnWS/fonmssn+yHVrXkS9BCVpZbWruFs/Ajj8J8wU2557JUebK/HYIoB+FkYGHLj3z7gwlRcSRwUbkub/Ov3mW+NBd4XKLCQEweu19ttO+93ebvYFb29JZJ5vTP8XNQmdq5/yAZI+bXgbMoSbIQdmFBi0QgR8hsIywS5qlctoQIDAQAB";
// 
//    public void  testZhimaAuthInfoAuthorize() {
//        ZhimaAuthInfoAuthorizeRequest req = new ZhimaAuthInfoAuthorizeRequest();
//        req.setChannel("apppc");
//        req.setPlatform("zmop");
//               req.setIdentityType("1");// 必要参数        
//               req.setIdentityParam("{\"name\":\"张三\",\"certType\":\"IDENTITY_CARD\",\"certNo\":\"330100xxxxxxxxxxxx\"}");// 必要参数        
//               req.setBizParams("{\"auth_code\":\"M_H5\",\"channelType\":\"app\",\"state\":\"商户自定义\"}");//        
//                DefaultZhimaClient client = new DefaultZhimaClient(gatewayUrl, appId, privateKey,
//            zhimaPublicKey);
//        try {
//            String url = client.generatePageRedirectInvokeUrl(req);  
//			System.out.println(url);
//			String paramsStr = url.substring(url.indexOf("?")+1);
//			System.out.println(paramsStr);
//			String[] paramsArr = paramsStr.split("&");
//			Map<String,String> paramsMap = new HashMap<String, String>();
//			for(String item:paramsArr){
//				String itemKey = item.substring(0,item.indexOf("="));
//				String itemValue = item.substring(item.indexOf("=")+1);
//				paramsMap.put(itemKey, itemValue);
//			}
//			System.out.println(paramsMap);
//        } catch (ZhimaApiException e) {
//            e.printStackTrace();
//        }
//    }
// 
//    public static void main(String[] args) {
//    	ZhimaUtil result = new  ZhimaUtil();
//        result.testZhimaAuthInfoAuthorize();
//    }
}
