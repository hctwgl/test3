package com.ald.fanbei.api.biz.third.util;

import java.util.HashMap;
import java.util.Map;

import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.antgroup.zmxy.openplatform.api.DefaultZhimaClient;
import com.antgroup.zmxy.openplatform.api.ZhimaResponse;
import com.antgroup.zmxy.openplatform.api.request.ZhimaAuthInfoAuthorizeRequest;
import com.antgroup.zmxy.openplatform.api.request.ZhimaAuthInfoAuthqueryRequest;
import com.antgroup.zmxy.openplatform.api.request.ZhimaCreditIvsDetailGetRequest;
import com.antgroup.zmxy.openplatform.api.request.ZhimaCreditScoreGetRequest;
import com.antgroup.zmxy.openplatform.api.request.ZhimaCreditWatchlistiiGetRequest;
import com.antgroup.zmxy.openplatform.api.response.ZhimaAuthInfoAuthorizeResponse;
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
	private static String PRODUCT_CODE = "w1010100100000000022";
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
			String appId = "appId";// TODO ;
			String privateKey = "privateKey";// TODO
			String zhimaPublicKey = "zhimaPublicKey";// TODO
			ZhimaClient = new DefaultZhimaClient(gatewayUrl, appId, privateKey,
					zhimaPublicKey);
		}
		return ZhimaClient;
	}

	/**
	 * 行业关注名单
	 * 
	 * @param openId
	 * @return
	 */
	public static ZhimaCreditWatchlistiiGetResponse creditWatchlistiiGet(String openId) {
		ZhimaCreditWatchlistiiGetRequest req = new ZhimaCreditWatchlistiiGetRequest();
		req.setPlatform(DEFAULT_PLATFORM);
		req.setOpenId(openId);
		req.setProductCode(PRODUCT_CODE);
		req.setTransactionId("hygzmd_" + openId + "_" + System.currentTimeMillis());
		try {
			ZhimaCreditWatchlistiiGetResponse resp = (ZhimaCreditWatchlistiiGetResponse) getZhimaClient().execute(req);
			dealWithZhimaResp(resp, "creditWatchlistiiGet", openId);
			return resp;
		} catch (Exception e) {
			logger.error("watchlistii error;|" + openId, e);
			throw new FanbeiException(FanbeiExceptionCode.ZM_ERROR);
		}
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
		req.setProductCode(PRODUCT_CODE);
		req.setTransactionId(openId + "_" + System.currentTimeMillis());
		try {
			ZhimaCreditScoreGetResponse resp = (ZhimaCreditScoreGetResponse) getZhimaClient().execute(req);
			dealWithZhimaResp(resp, "scoreGet", openId);
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
	public ZhimaCreditIvsDetailGetResponse isvDetailGet(String idNumber,String realName,String mobile,String email,String address) {
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
		req.setProductCode(PRODUCT_CODE);
//		req.setScene(scene);
		req.setTransactionId("fqz_" + mobile + "_" + System.currentTimeMillis());
//		req.setWifimac(wifimac);
		try{
			ZhimaCreditIvsDetailGetResponse resp = (ZhimaCreditIvsDetailGetResponse)getZhimaClient().execute(req);
			dealWithZhimaResp(resp, "isvDetailGet", idNumber,realName,mobile,email,address);
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
	public ZhimaAuthInfoAuthqueryResponse authQuery(String idNumber,String realName){
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
	public ZhimaAuthInfoAuthorizeResponse authorize(String idNumber,String realName,String uid){
		ZhimaAuthInfoAuthorizeRequest req = new ZhimaAuthInfoAuthorizeRequest();
		Map<String,String> bizParamsMap = new HashMap<String, String>();
		bizParamsMap.put("auth_code", "M_H5");
		bizParamsMap.put("state", uid);
		req.setBizParams(JSON.toJSONString(bizParamsMap));
		req.setChannel(CHANNEL);
		Map<String,String> identityParamsMap = new HashMap<String, String>();
		identityParamsMap.put("certNo", idNumber);
		identityParamsMap.put("certType", DEFAULT_CERTtYPE);
		identityParamsMap.put("name", realName);
		req.setIdentityParam(JSON.toJSONString(identityParamsMap));
		req.setIdentityType(DEFAULT_IDENTITY_TYPE);
		req.setPlatform("sq_" + idNumber + "_" + System.currentTimeMillis());
		try{
			ZhimaAuthInfoAuthorizeResponse resp = (ZhimaAuthInfoAuthorizeResponse)getZhimaClient().execute(req);
			dealWithZhimaResp(resp, "authorize", idNumber,realName,uid);
			return resp;
		}catch(Exception e){
			logger.error(StringUtil.appendStrs("authorize error;|",idNumber,"|",realName,"|",uid), e);
			throw new FanbeiException(FanbeiExceptionCode.ZM_ERROR);
		}
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
		thirdLog.info(StringUtil.appendStrs("methodName=", methodName,",params=", sb.toString() + ",resp=" + resp));
		if (resp.isSuccess()) {
			return;
		} else {
			throw new FanbeiException("调用芝麻失败", FanbeiExceptionCode.ZM_ERROR);
		}
	}

}
