package com.ald.fanbei.api.biz.third.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.JsdNoticeStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.alibaba.fastjson.JSON;

/**
 * 
 * @类描述：和催收系统互调工具类
 * @author chengkang 2017年8月3日 16:55:23
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的 需加密参数 真实姓名 ， 身份证号， 手机号，邮箱，银行卡号
 */
@Component("collectionSystemUtil")
public class CollectionSystemUtil extends AbstractThird {

	private static String url = null;

	private static String getUrl() {
		if (url == null) {
			url = ConfigProperties.get(Constants.CONFKEY_COLLECTION_URL);
			return url;
		}
		return url;
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
			params.put("orderNo",getOrderNo("JSD"));
			params.put("info",JSON.toJSONString(data));
			params.put("token","eyJhbGciOiJIUzI1NiIsImNvbXBhbnlJZCI6Nn0.eyJhdWQiOiI2IiwiaXNzIjoiQUxEIiwiaWF0IjoxNTM2NjMyODQxfQ.NPLQiwpOsS1FPnCaIal2X9AaRk3R_fRFkCFfbRbNvIQ");
			logger.info("jsd overdue notice collect request :" + JSON.toJSONString(params)+"url = "+getUrl());
//			String url = getUrl() + "/api/ald/collect/v1/third/import";
			String url = "http://192.168.156.40:8080/api/ald/collect/v1/third/import";
			String reqResult = "";
			if (url.contains("https")){
				reqResult = HttpUtil.doHttpsPostIgnoreCert(url, JSON.toJSONString(params));
			}else {
				reqResult = HttpUtil.post(url, params);
			}
			logThird(reqResult, "noticeCollect", JSON.toJSONString(data));
			logger.info("repaymentAchieve response :" + reqResult);
			if (StringUtil.isBlank(reqResult)) {
				throw new FanbeiException("dsed overdue notice collect request fail , reqResult is null");
			}
			if("success".equals(reqResult)){
				return true;
			}
			return false;
		} catch (Exception e) {
			logger.error("dsed overdue notice collect request error:", e);
			throw new FanbeiException("dsed overdue notice collect request fail Exception is " + e + ",dsed overdue notice collect request send again");
		}
	}


	/**
	 * 都市e贷主动还款通知催收平台（补偿）
	 * @return
	 */
	public boolean noticeRiskCollect(Map<String,String>  data) {
		try {
//			String url = "http://192.168.110.70:8080/api/ald/collect/v1/third/import";
			String url = getUrl() + "/api/ald/collect/v1/third/import";
			String reqResult = "";
			if (url.contains("https")){
				reqResult = HttpUtil.doHttpsPostIgnoreCert(url, JSON.toJSONString(data));
			}else {
				reqResult = HttpUtil.post(url, data);
			}
			logThird(reqResult, "dsedNoticeCollect", JSON.toJSONString(data));
			logger.info("repaymentAchieve response :" + reqResult);
			if (StringUtil.isBlank(reqResult)) {
				throw new FanbeiException("dsed overdue notice collect request fail , reqResult is null");
			}
			if("success".equals(reqResult)){
				return true;
			}
			return false;
		} catch (Exception e) {
			logger.error("dsed overdue notice collect request error:", e);
			throw new FanbeiException("dsed overdue notice collect request fail Exception is " + e + ",dsed overdue notice collect request send again");
		}
	}

	/**
	 * 还款通知请求
	 * @param data
	 * @return
	 */
	public boolean  dsedRePayNoticeRequest(HashMap<String, String> data ){
		try {
			Map<String, String> params = new HashMap<>();
			params.put("info",JSON.toJSONString(data));
			params.put("token","eyJhbGciOiJIUzI1NiIsImNvbXBhbnlJZCI6MywiYiI6MX0.eyJhdWQiOiJhbGQiLCJpc3MiOiJBTEQiLCJpYXQiOjE1MzAxNzI3MzB9.-ZCGIOHgHnUbtJoOChHSi2fFj_XHnIDJk3bF1zrGLSk");
			String url = getUrl() + "/api/ald/collect/v1/third/import";
			String reqResult = "";
			if (url.contains("https")){
				reqResult = HttpUtil.doHttpsPostIgnoreCert(url, JSON.toJSONString(params));
			}else {
				reqResult = HttpUtil.post(url, params);
			}
			logThird(reqResult, "dsedRePayCollect", JSON.toJSONString(data));
			if (StringUtil.isBlank(reqResult)) {
				throw new FanbeiException("dsed overdue notice collect request fail , reqResult is null");
			}
			if("success".equals(reqResult)){
				logger.info("send overdue push user collect request success");
				return true;
			}else {
				logger.info("send overdue push user collect request fail"+JSON.toJSONString(params));
				return false;
			}
		}catch (Exception e){
			logger.info("rePayNoticeRequest request fail",e);
		}

		return false;

	}


	/**
	 * jsd主动还款通知催收平台
	 * @param reqBo
	 * @return
	 */
	public boolean consumerRepayment(Map<String, String> reqBo) {
		// APP还款类型写3 , 线下还款写4
		try {
//			String url = getUrl() + "/report/thirdRepayment";

			String url = "http://192.168.152.21:8003/report/thirdRepayment";
			logger.info("consumerRepayment url :" + url);
			String reqResult = "";
			if (url.contains("https")){
				reqResult = HttpUtil.doHttpsPostIgnoreCert(url, JSON.toJSONString(reqBo));
			}else {
				reqResult = HttpUtil.post(url, reqBo);
			}logger.info("repaymentAchieve response :" + reqResult);
			if (StringUtil.equals(JSON.parseObject(reqResult).get("data").toString().toUpperCase(), JsdNoticeStatus.SUCCESS.code)) {
				return true;
			}
			return false;
		} catch (Exception e) {
			logger.error("consumerRepayment error:", e);
			throw new FanbeiException("consumerRepayment fail Exception is " + e + ",consumerRepayment send again");
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
			reqBo.put("token","eyJhbGciOiJIUzI1NiIsImNvbXBhbnlJZCI6Nn0.eyJhdWQiOiI2IiwiaXNzIjoiQUxEIiwiaWF0IjoxNTM2NjYwMTcyfQ.WVXxSkwrujC-DCZoJdqf9zPCNhbIbOF9aWbiH0hSGNo");
//			String url = "http://192.168.156.40:8080/api/ald/collect/v1/third/renewal";
			String url = getUrl() + "/api/ald/collect/v1/third/renewal";
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
			throw new FanbeiException("consumerRepayment fail Exception is " + e + ",consumerRepayment send again");
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
