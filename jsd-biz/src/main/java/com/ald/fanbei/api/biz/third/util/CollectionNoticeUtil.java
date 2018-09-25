package com.ald.fanbei.api.biz.third.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ald.fanbei.api.common.util.DigestUtil;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.ConfigProperties;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.JsdNoticeStatus;
import com.ald.fanbei.api.common.exception.BizException;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.alibaba.fastjson.JSON;

/**
 * 
 * @类描述：和催收系统互调工具类
 * @author chengkang 2017年8月3日 16:55:23
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的 需加密参数 真实姓名 ， 身份证号， 手机号，邮箱，银行卡号
 */
@Component("collectionNoticeUtil")
public class CollectionNoticeUtil extends AbstractThird {

	private final String salt = "jsdpluscuishou";

	//收发路径
	private static String getReportUrl() {
		String url = ConfigProperties.get(Constants.CONFKEY_COLLECTION_REPORT_URL);
		return url;
	}
	//催收路径
	private static String getCollectUrl() {
		String urls = ConfigProperties.get(Constants.CONFKEY_COLLECTION_URL);
		return urls;
	}

	/**
	 * 获取订单号
	 * @param method 接口标识（固定4位）
	 */
	private static String getOrderNo(String method){
		return StringUtil.appendStrs("cs",method,System.currentTimeMillis());
	}

	/**
	 * 极速贷逾期通知催收平台
	 * @return
	 */
	public boolean noticeCollect(List<Map<String,String>>  data) {
		try {
			Map<String,String>  params=new HashMap<>();
			params.put("orderNo",getOrderNo("XGXY"));
			params.put("info",JSON.toJSONString(data));
			params.put("token",ConfigProperties.get(Constants.CONFKEY_COLLECTION_TOKEN));
			logger.info("jsd overdue notice collect request :" + JSON.toJSONString(params)+"url = "+getReportUrl());
			String url = getReportUrl() + "/api/ald/collect/v1/third/import";
			String reqResult = "";
			if (url.contains("https")){
				reqResult = HttpUtil.doHttpsPostIgnoreCert(url, JSON.toJSONString(params));
			}else {
				reqResult = HttpUtil.post(url, params);
			}
			logThird(reqResult, "noticeCollect", JSON.toJSONString(data));
			logger.info("repaymentAchieve response :" + reqResult);
			if (StringUtil.isBlank(reqResult)) {
				throw new BizException("dsed overdue notice collect request fail , reqResult is null");
			}
			if("success".equals(reqResult)){
				return true;
			}
			return false;
		} catch (Exception e) {
			logger.error("dsed overdue notice collect request error:", e);
			throw new BizException("dsed overdue notice collect request fail Exception is " + e + ",dsed overdue notice collect request send again");
		}
	}







	/**
	 * jsd主动还款通知催收平台
	 * @param reqBo
	 * @return
	 */
	public boolean consumerRepayment(Map<String, String> reqBo) {
		try {
			String url = getCollectUrl() + "/report/thirdRepayment";
			logger.info("consumerRepayment url :" + url);
			String reqResult = "";
			if (url.contains("https")){
				reqResult = HttpUtil.doHttpsPostIgnoreCert(url, JSON.toJSONString(reqBo));
			}else {
				reqResult = HttpUtil.post(url, reqBo);
			}logger.info("consumerRepayment response :" + reqResult);
			if (StringUtil.equals(JSON.parseObject(reqResult).get("data").toString().toUpperCase(), JsdNoticeStatus.SUCCESS.code)) {
				return true;
			}
			return false;
		} catch (Exception e) {
			logger.error("consumerRepayment error:", e);
			throw new BizException("consumerRepayment fail Exception is " + e + ",consumerRepayment send again");
		}
	}


	/**
	 * jsd续期成功通知催收平台
	 * @param reqBo
	 * @return
	 */
	public boolean collectRenewal(Map<String, String> reqBo) {
		// APP还款类型写3 , 线下还款写4
		try {
			reqBo.put("orderNo",getOrderNo("JSD"));
			reqBo.put("token",ConfigProperties.get(Constants.CONFKEY_COLLECTION_TOKEN));
			String url = getReportUrl() + "/api/ald/collect/v1/third/renewal";
			String reqResult = "";
			if (url.contains("https")){
				reqResult = HttpUtil.doHttpsPostIgnoreCert(url, getUrlParamsByMap(reqBo));
			}else {
				reqResult = HttpUtil.post(url, reqBo);
			}
			logger.info("collectRenewal response :" + reqResult);
			if (StringUtil.equals(reqResult.toUpperCase(), JsdNoticeStatus.SUCCESS.code)) {
				return true;
			}
			return false;
		} catch (Exception e) {
			logger.error("consumerRepayment error:", e);
			throw new BizException("consumerRepayment fail Exception is " + e + ",consumerRepayment send again");
		}
	}


	/**
	 * jsdplus 平账申请通知催收
	 * @param data 包含{dataId-即商品订单orderId ，reviewResult: PASS通过，REFUSE拒绝}
	 * @return
	 */
	public boolean collectReconciliateNotice(Map<String, String> data) {
		try {
			String dataId = data.get("dataId").toString();
			byte[] pd = DigestUtil.digestString(dataId.getBytes("UTF-8"), salt.getBytes(), Constants.DEFAULT_DIGEST_TIMES, Constants.SHA1);
			String sign = DigestUtil.encodeHex(pd);
			data.put("sign",sign);
			String url = getCollectUrl() + "/api/collect/third/thirdReconciliateCheck";
			String reqResult = "";
			if (url.contains("https")){
				reqResult = HttpUtil.doHttpsPostIgnoreCert(url, getUrlParamsByMap(data));
			}else {
				reqResult = HttpUtil.post(url, data);
			}
			logger.info("collectReconciliateNotice response :" + reqResult + "data = " +data );
			if (StringUtil.equals(reqResult.toUpperCase(), JsdNoticeStatus.SUCCESS.code)) {
				return true;
			}
			return false;
		} catch (Exception e) {
			logger.error("collectReconciliateNotice error:", e);
			throw new BizException("collectReconciliateNotice fail Exception is " + e + ",consumerRepayment send again");
		}
	}

	/**
	 * jsdplus 还款申请通知催收
	 * @param data 包含{tradeNo-即催收提交的还款流水号 ，reviewResult: PASS通过，REFUSE拒绝}
	 * @return
	 */
	public boolean collectRepayNotice(Map<String, String> data) {
		try {
			String dataId = data.get("tradeNo").toString();
			byte[] pd = DigestUtil.digestString(dataId.getBytes("UTF-8"), salt.getBytes(), Constants.DEFAULT_DIGEST_TIMES, Constants.SHA1);
			String sign = DigestUtil.encodeHex(pd);
			data.put("sign",sign);
			String url = getCollectUrl() + "/api/collect/third/thridRepaymentCheck";
			String reqResult = "";
			if (url.contains("https")){
				reqResult = HttpUtil.doHttpsPostIgnoreCert(url, getUrlParamsByMap(data));
			}else {
				reqResult = HttpUtil.post(url, data);
			}
			logger.info("collectRepayNotice response :" + reqResult + "data = " +data );
			if (StringUtil.equals(reqResult.toUpperCase(), JsdNoticeStatus.SUCCESS.code)) {
				return true;
			}
			return false;
		} catch (Exception e) {
			logger.error("collectRepayNotice error:", e);
			throw new BizException("collectRepayNotice fail Exception is " + e + ",consumerRepayment send again");
		}
	}

	/**
	 * 将map转换成url
	 *
	 * @param map
	 * @return
	 */
	public String getUrlParamsByMap(Map<String, String> map) {
		if (map == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			sb.append(entry.getKey() + "=" + entry.getValue());
			sb.append("&");
		}
		String s = sb.toString();
		if (s.endsWith("&")) {
			s = org.apache.commons.lang.StringUtils.substringBeforeLast(s, "&");
		}
		return s;
	}





}
