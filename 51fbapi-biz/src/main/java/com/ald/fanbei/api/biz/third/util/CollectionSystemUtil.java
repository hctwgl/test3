package com.ald.fanbei.api.biz.third.util;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.CollectionDataBo;
import com.ald.fanbei.api.biz.bo.CollectionOperatorNotifyReqBo;
import com.ald.fanbei.api.biz.bo.CollectionOperatorNotifyRespBo;
import com.ald.fanbei.api.biz.bo.CollectionSystemReqRespBo;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderCashService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalRepaymentService;
import com.ald.fanbei.api.biz.service.AfRepaymentBorrowCashService;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.biz.util.CommitRecordUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfRepayCollectionType;
import com.ald.fanbei.api.common.enums.AfRepeatCollectionType;
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
import com.ald.fanbei.api.dal.dao.AfBorrowLegalOrderCashDao;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.AfRepaymentBorrowCashDo;
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
	AfBorrowLegalOrderCashDao afBorrowLegalOrderCashDao;
	@Resource
	AfBorrowLegalOrderCashService afBorrowLegalOrderCashService;
	@Resource
	AfBorrowLegalRepaymentService afBorrowLegalRepaymentService;
	@Resource
	AfBorrowCashService afBorrowCashService;
	@Resource
	AfRepaymentBorrowCashService afRepaymentBorrowCashService;

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
	 * 51返呗主动还款通知催收平台
	 * 
	 * @param repayNo
	 *            --还款编号
	 * @param borrowNo
	 *            --借款单号
	 * @param cardNumber
	 *            --卡号
	 * @param cardName
	 *            --银行卡名称（支付方式）
	 * @param amount
	 *            --还款金额
	 * @param restAmount
	 *            --剩余未还金额
	 * @param repayAmount
	 *            --理论应还款金额
	 * @param overdueAmount
	 *            --逾期手续费
	 * @param repayAmountSum
	 *            --已还总额
	 * @param rateAmount
	 *            --利息
	 * @return
	 */
	public CollectionSystemReqRespBo consumerRepayment(String repayNo, String borrowNo, String cardNumber,
			String cardName, String repayTime, String tradeNo, BigDecimal amount, BigDecimal restAmount,
			BigDecimal repayAmount, BigDecimal overdueAmount, BigDecimal repayAmountSum, BigDecimal rateAmount) {
		CollectionDataBo data = new CollectionDataBo();
		Map<String, String> reqBo = new HashMap<String, String>();
		reqBo.put("repay_no", repayNo);
		reqBo.put("borrow_no", borrowNo);
		reqBo.put("card_number", cardNumber);
		reqBo.put("card_name", cardName);
		reqBo.put("repay_time", repayTime);
		if (StringUtil.isEmpty(tradeNo)) {
			tradeNo = repayNo;
		}
		reqBo.put("trade_no", tradeNo);
		reqBo.put("amount", amount.multiply(BigDecimalUtil.ONE_HUNDRED) + "");
		reqBo.put("rest_amount", restAmount.multiply(BigDecimalUtil.ONE_HUNDRED) + "");
		reqBo.put("repay_amount", repayAmount.multiply(BigDecimalUtil.ONE_HUNDRED) + "");
		reqBo.put("overdue_amount", overdueAmount.multiply(BigDecimalUtil.ONE_HUNDRED) + "");
		reqBo.put("repay_amount_sum", repayAmountSum.multiply(BigDecimalUtil.ONE_HUNDRED) + "");
		reqBo.put("rate_amount", rateAmount.multiply(BigDecimalUtil.ONE_HUNDRED) + "");

		String json = JsonUtil.toJSONString(reqBo);
		data.setData(json);// 数据集合
		data.setSign(DigestUtil.MD5(json));
		String timestamp = DateUtil.getDateTimeFullAll(new Date());
		data.setTimestamp(timestamp);
		// APP还款类型写3 , 线下还款写4
		data.setChannel(AfRepayCollectionType.APP.getCode());
		try {
			logger.info("repaymentAchieve request :", JSON.toJSONString(data));
			String reqResult = HttpUtil.doHttpsPostIgnoreCertUrlencoded(
					getUrl() + "/api/getway/repayment/repaymentAchieve", getUrlParamsByMap(data));
			logger.info("repaymentAchieve response :", reqResult);
			if (StringUtil.isBlank(reqResult)) {
				throw new FanbeiException("consumerRepayment fail , reqResult is null");
			} else {
				logger.info("consumerRepayment req success,reqResult" + reqResult);
			}

			CollectionSystemReqRespBo respInfo = JSONObject.parseObject(reqResult, CollectionSystemReqRespBo.class);
			if (respInfo != null && StringUtil.equals("200", respInfo.getCode())) {
				return respInfo;
			} else {
				throw new FanbeiException(
						"consumerRepayment fail , respInfo info is " + JSONObject.toJSONString(respInfo));
			}
		} catch (Exception e) {
			logger.error("consumerRepayment error:", e);
			commitRecordUtil.addRecord(AfRepeatCollectionType.APP_REPAYMENT.getCode(), borrowNo, json,
					getUrl() + "/api/getway/repayment/repaymentAchieve");
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

	/**
	 * 51返呗续期通知接口
	 * 
	 * @param borrow_no
	 *            借款单号
	 * @param renewal_no
	 *            续期编号
	 * @param renewal_num
	 *            续借期数
	 * @return
	 * 
	 **/
	public CollectionSystemReqRespBo renewalNotify(String borrowNo, String renewalNo, Integer renewalNum,
			String renewalAmount) {

		CollectionDataBo data = new CollectionDataBo();
		Map<String, String> reqBo = new HashMap<String, String>();
		reqBo.put("borrow_no", borrowNo);
		reqBo.put("renewal_no", renewalNo);
		reqBo.put("renewal_num", renewalNum + "");
		reqBo.put("renewal_amount", renewalAmount);

		String json = JsonUtil.toJSONString(reqBo);
		data.setData(json);// 数据集合
		data.setSign(DigestUtil.MD5(json));
		String timestamp = DateUtil.getDateTimeFullAll(new Date());
		data.setTimestamp(timestamp);
		try {
			logger.info("renewalNotify request :", data);
			String reqResult = HttpUtil.doHttpsPostIgnoreCertUrlencoded(
					getUrl() + "/api/getway/repayment/renewalAchieve", getUrlParamsByMap(data));
			logger.info("renewalNotify response :", reqResult);
			if (StringUtil.isBlank(reqResult)) {
				throw new FanbeiException("renewalNotify fail , reqResult is null");
			} else {
				logger.info("renewalNotify req success,reqResult" + reqResult);
			}
			CollectionSystemReqRespBo respInfo = JSONObject.parseObject(reqResult, CollectionSystemReqRespBo.class);
			if (respInfo != null && StringUtil.equals("200", respInfo.getCode())) {
				return respInfo;
			} else {
				throw new FanbeiException("renewalNotify fail , respInfo info is " + JSONObject.toJSONString(respInfo));
			}
		} catch (Exception e) {
			commitRecordUtil.addRecord(AfRepeatCollectionType.APP_RENEWAL.getCode(), borrowNo, json,
					getUrl() + "/api/getway/repayment/renewalAchieve");
			throw new FanbeiException("renewalNotify fail Exception is " + e + ",renewalNotify send again");
		}

	}

	public CollectionOperatorNotifyRespBo offlineRepaymentNotify(String timestamp, String data, String sign) {
		// 响应数据,默认成功
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
				String tradeNo = obj.getString("trade_no"); // 三方交易流水号
				String isBalance = obj.getString("is_balance");
				
				if (StringUtil.isAllNotEmpty(repayNo, borrowNo, repayType, repayTime, repayAmount, restAmount, tradeNo, isBalance)) {
					AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashInfoByBorrowNo(borrowNo);
					if(afBorrowCashDo == null) {
						notifyRespBo.resetMsgInfo(FanbeiThirdRespCode.BORROW_CASH_NOT_EXISTS);
						return notifyRespBo;
					}
					
					String respCode = FanbeiThirdRespCode.SUCCESS.getCode();
					
					//合规线下还款
					if(afBorrowLegalOrderCashDao.tuchByBorrowId(afBorrowCashDo.getRid()) != null) {
						AfBorrowLegalOrderCashDo orderCashDo = afBorrowLegalOrderCashService.getBorrowLegalOrderCashByBorrowId(afBorrowCashDo.getRid());
						afBorrowLegalOrderCashService.checkOfflineRepayment(afBorrowCashDo, orderCashDo, repayAmount ,tradeNo);
						afBorrowLegalRepaymentService.offlineRepay(orderCashDo, borrowNo, repayType, repayTime, repayAmount, restAmount, tradeNo, isBalance);
					} else {//旧版线下还款
						AfRepaymentBorrowCashDo existItem = afRepaymentBorrowCashService.getRepaymentBorrowCashByTradeNo(afBorrowCashDo.getRid(), tradeNo);
						if (existItem != null) {
							logger.error("offlineRepaymentNotify exist trade_no");
							notifyRespBo.resetMsgInfo(FanbeiThirdRespCode.COLLECTION_THIRD_NO_EXIST);
							return notifyRespBo;
						}
						afBorrowLegalOrderCashService.checkOfflineRepayment(afBorrowCashDo, null, repayAmount, tradeNo);
						respCode = afRepaymentBorrowCashService.dealOfflineRepaymentSucess(repayNo, borrowNo,
								repayType, repayTime,
								NumberUtil.objToBigDecimalDivideOnehundredDefault(repayAmount, BigDecimal.ZERO),
								NumberUtil.objToBigDecimalDivideOnehundredDefault(restAmount, BigDecimal.ZERO), tradeNo,
								isBalance);
						notifyRespBo.resetMsgInfo(FanbeiThirdRespCode.findByCode(respCode));					}
				} else {
					notifyRespBo.resetMsgInfo(FanbeiThirdRespCode.REQUEST_PARAM_NOT_EXIST);
				}
			} else {
				notifyRespBo.resetMsgInfo(FanbeiThirdRespCode.REQUEST_INVALID_SIGN_ERROR);
			}
		} catch(FanbeiException e) {
			logger.error("offlineRepaymentNotify fanbei error", e);
			notifyRespBo.setCode(e.getErrorCode().getCode());
			notifyRespBo.setMsg(e.getErrorCode().getErrorMsg());
		} catch (Exception e) {
			logger.error("offlineRepaymentNotify error", e);
			notifyRespBo.resetMsgInfo(FanbeiThirdRespCode.SYSTEM_ERROR);
		}finally {
			notifyRespBo.setSign(DigestUtil.MD5(JsonUtil.toJSONString(notifyRespBo)));
		}
		
		return notifyRespBo;
	}
}
