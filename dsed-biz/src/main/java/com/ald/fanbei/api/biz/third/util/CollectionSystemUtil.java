package com.ald.fanbei.api.biz.third.util;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ald.fanbei.api.biz.bo.CollectionDataBo;
import com.ald.fanbei.api.biz.bo.CollectionSystemReqRespBo;
import com.ald.fanbei.api.common.enums.AfRepayCollectionType;
import com.ald.fanbei.api.common.enums.AfRepeatCollectionType;
import com.ald.fanbei.api.common.enums.DsedNoticeStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.util.*;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.alibaba.fastjson.JSON;

import javax.annotation.Resource;

/**
 * 
 * @类描述：和催收系统互调工具类
 * @author chengkang 2017年8月3日 16:55:23
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的 需加密参数 真实姓名 ， 身份证号， 手机号，邮箱，银行卡号
 */
@Component("collectionSystemUtil")
public class CollectionSystemUtil extends AbstractThird {

	private static String url = null;

	@Resource
	CommitRecordUtil commitRecordUtil;

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
	 * 都市e贷主动还款通知催收平台
	 * @return
	 */
	public void noticeCollect(List<Map<String,String>>  data) {
		try {
			Map<String,String>  params=new HashMap<>();
			params.put("orderNo",getOrderNo("XGXY"));
			params.put("info",JSON.toJSONString(data));
			params.put("companyId","");
			params.put("token","eyJhbGciOiJIUzI1NiIsImNvbXBhbnlJZCI6MywiYiI6MX0.eyJhdWQiOiJhbGQiLCJpc3MiOiJBTEQiLCJpYXQiOjE1MzAxNzI3MzB9.-ZCGIOHgHnUbtJoOChHSi2fFj_XHnIDJk3bF1zrGLSk");
			logger.info("dsed overdue notice collect request :" + JSON.toJSONString(params));
			String reqResult = HttpUtil.post("http://192.168.117.72:8080/api/ald/collect/v1/third/import", params);
			logThird(reqResult, "dsedNoticeCollect", JSON.toJSONString(data));
			logger.info("repaymentAchieve response :" + reqResult);
			if (StringUtil.isBlank(reqResult)) {
				throw new FanbeiException("dsed overdue notice collect request fail , reqResult is null");
			}
			if("success".equals(reqResult)){
				logger.info("send overdue push user collect request success");
			}else {
				logger.info("send overdue push user collect request fail"+JSON.toJSONString(params));
			}
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
			String reqResult = HttpUtil.post("http://192.168.117.72:8080/api/ald/collect/v1/import", params);
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
	 * 都市e贷主动还款通知催收平台
	 * @param reqBo
	 * @return
	 */
	public void consumerRepayment(Map<String, String> reqBo) {
		// APP还款类型写3 , 线下还款写4
		try {
			String reqResult = HttpUtil.post("http://192.168.117.72:8080/api/ald/collect/v1/third/repayment", reqBo);
			logger.info(getUrl() + "/api/ald/collect/v1/third/repayment");
			logger.info("repaymentAchieve response :" + reqResult);
			if (StringUtil.equals(reqResult.toUpperCase(), DsedNoticeStatus.SUCCESS.code)) {
				logger.info("consumerRepayment req success,reqResult" + reqResult);
			} else {
				throw new FanbeiException("consumerRepayment fail , reqResult is null");
			}
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
