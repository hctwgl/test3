package com.ald.fanbei.api.biz.third.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.CollectionOperatorNotifyReqBo;
import com.ald.fanbei.api.biz.bo.CollectionOperatorNotifyRespBo;
import com.ald.fanbei.api.biz.bo.RiskDataBo;
import com.ald.fanbei.api.biz.bo.RiskRespBo;
import com.ald.fanbei.api.biz.service.AfRepaymentBorrowCashService;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiThirdRespCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.JsonUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

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
	AfRepaymentBorrowCashService afRepaymentBorrowCashService;
	
	private static String getUrl() {
		if (url == null) {
			url = ConfigProperties.get(Constants.CONFKEY_COLLECTION_URL);
			return url;
		}
		return url;
	}
	
	/**
	 * 51返呗主动还款通知催收平台
	 * 
	 * @param repayNo
	 *            --还款编号
	 * @param borrowNo
	 *            --借款单号
	 * @param cardNumber
	 *            --卡号
	 * @param cardName
	 *  	      --银行卡名称（支付方式）
	 * @param amount
	 *  	      --还款金额
	 * @param restAmount
	 *  	      --剩余未还金额
	 * @param repayAmount
	 *  	      --理论应还款金额
	 * @param overdueAmount
	 *  	      --逾期手续费
	 * @param repayAmountSum
	 *  	      --已还总额
	 * @param rateAmount
	 *  	      --利息
	 * @return
	 */
	public RiskRespBo consumerRepayment(String repayNo,String borrowNo,String cardNumber,String cardName,String repayTime,String tradeNo,BigDecimal amount,
			BigDecimal restAmount,BigDecimal repayAmount,BigDecimal overdueAmount,BigDecimal repayAmountSum,
			BigDecimal rateAmount) {
		Map<String,String> reqBo=new HashMap<String,String>();
		reqBo.put("repay_no", repayNo);
		reqBo.put("borrow_no", borrowNo);
		reqBo.put("card_number", cardNumber);
		reqBo.put("card_name", cardName);
		
		reqBo.put("repay_time", repayTime);
		reqBo.put("trade_no", tradeNo);
		reqBo.put("amount", amount.multiply(BigDecimalUtil.ONE_HUNDRED).toString());
		reqBo.put("rest_amount", restAmount.multiply(BigDecimalUtil.ONE_HUNDRED).toString());
		reqBo.put("repay_amount", repayAmount.multiply(BigDecimalUtil.ONE_HUNDRED).toString());
		reqBo.put("overdue_amount", overdueAmount.multiply(BigDecimalUtil.ONE_HUNDRED).toString());
		reqBo.put("repay_amount_sum", repayAmountSum.multiply(BigDecimalUtil.ONE_HUNDRED).toString());
		reqBo.put("rate_amount", rateAmount.multiply(BigDecimalUtil.ONE_HUNDRED).toString());
		
		String json = JsonUtil.toJSONString(reqBo);
		RiskDataBo data=new RiskDataBo();
		Date da=new Date();
		String times = new SimpleDateFormat(DateUtil.DATE_TIME_FULL_ALL).format(da);
		data.setTimestamp(times);
		data.setSign(DigestUtil.MD5(json));
		data.setData(json);//数据集合
		String reqResult = HttpUtil.post(getUrl() + "/api/getway/repayment/repaymentAchieve", data);
		if (StringUtil.isBlank(reqResult)) {
			throw new FanbeiException("主动还款通知失败");
		}
		RiskRespBo riskResp = JSONObject.parseObject(reqResult, RiskRespBo.class);
		if (riskResp != null && FanbeiThirdRespCode.SUCCESS.getCode().equals(riskResp.getCode())) {
			riskResp.setSuccess(true);
			return riskResp;
		} else {
			throw new FanbeiException("主动还款通知失败");
		}
	}


	/**
	 * 51返呗续期通知接口
	 * @param borrow_no  	借款单号
	 * @param renewal_no  	续期编号
	 * @param renewal_num	续借期数
	 * @return 
	 * 
	 * **/
	public static RiskRespBo renewalNotify(String borrowNo, String renewalNo, Integer renewalNum,String renewalAmount){
		
		Map<String,String> reqBo=new HashMap<String,String>();
		reqBo.put("borrow_no", borrowNo);
		reqBo.put("renewal_no", renewalNo);
		reqBo.put("renewal_num", renewalNum.toString());
		reqBo.put("renewal_amount", renewalAmount);
	
		String json = JsonUtil.toJSONString(reqBo);
		RiskDataBo data=new RiskDataBo();
		Date da=new Date();
		String times = new SimpleDateFormat(DateUtil.DATE_TIME_FULL_ALL).format(da);
		data.setTimestamp(times);
		data.setSign(DigestUtil.MD5(json));
		data.setData(json);//数据集合
		String reqResult = HttpUtil.post(getUrl() + "/api/getway/repayment/renewalAchieve", data);
		
		if (StringUtil.isBlank(reqResult)) {
			throw new FanbeiException("续期通知失败");
		}
		RiskRespBo riskResp = JSONObject.parseObject(reqResult, RiskRespBo.class);
		if (riskResp != null && FanbeiThirdRespCode.SUCCESS.getCode().equals(riskResp.getCode())) {
			riskResp.setSuccess(true);
			return riskResp;
		} else {
			throw new FanbeiException("续期通知失败");
		}
	}
	
	/**
	 * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
	 * @param params 需要排序并参与字符拼接的参数组
	 * @return 拼接后字符串
	 */
	public static String createLinkString(Map<String, String> params) {
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		String prestr = "";
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);
			prestr = prestr + value;
		}
		return prestr;
	}
	
	public CollectionOperatorNotifyRespBo offlineRepaymentNotify(String timestamp, String data, String sign) {
		//响应数据,默认成功
		CollectionOperatorNotifyRespBo notifyRespBo = new CollectionOperatorNotifyRespBo(FanbeiThirdRespCode.SUCCESS); 
		try {
			CollectionOperatorNotifyReqBo reqBo = new CollectionOperatorNotifyReqBo();
			reqBo.setData(data);
			reqBo.setSign(DigestUtil.MD5(data));
			logThird(sign, "offlineRepaymentNotify", reqBo);
			if (StringUtil.equals(sign, reqBo.getSign())) {// 验签成功
				JSONObject obj = JSON.parseObject(data);
				String repayNo = obj.getString("repay_no");
				String borrowNo = obj.getString("borrow_no");
				String repayType = obj.getString("repay_type");
				String repayTime = obj.getString("repay_time");
				String repayAmount = obj.getString("repay_amount");
				String restAmount = obj.getString("rest_amount");
				String tradeNo = obj.getString("trade_no");
				String isBalance = obj.getString("is_balance");
				
				//参数校验
				if(StringUtil.isAllNotEmpty(repayNo,borrowNo,repayType,repayTime,repayAmount,restAmount,tradeNo,isBalance)){
					//业务处理
					String respCode = afRepaymentBorrowCashService.dealOfflineRepaymentSucess(repayNo, borrowNo, repayType, repayTime, NumberUtil.objToBigDecimalDivideOnehundredDefault(repayAmount, BigDecimal.ZERO), NumberUtil.objToBigDecimalDivideOnehundredDefault(restAmount, BigDecimal.ZERO), tradeNo, isBalance);
					FanbeiThirdRespCode respInfo = FanbeiThirdRespCode.findByCode(respCode);
					notifyRespBo.resetMsgInfo(respInfo);
				}else{
					notifyRespBo.resetMsgInfo(FanbeiThirdRespCode.REQUEST_PARAM_NOT_EXIST);
				}
			}else{
				notifyRespBo.resetMsgInfo(FanbeiThirdRespCode.REQUEST_INVALID_SIGN_ERROR);
			}
		} catch (Exception e) {
			logger.error("offlineRepaymentNotify error",e);
			notifyRespBo.resetMsgInfo(FanbeiThirdRespCode.SYSTEM_ERROR);
		}
		String resDataJson = JsonUtil.toJSONString(notifyRespBo);
		notifyRespBo.setSign(DigestUtil.MD5(resDataJson));
		return notifyRespBo;
	}
}
