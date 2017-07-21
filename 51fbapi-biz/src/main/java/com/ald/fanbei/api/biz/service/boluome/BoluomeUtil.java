package com.ald.fanbei.api.biz.service.boluome;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.BoluomePushPayRequestBo;
import com.ald.fanbei.api.biz.bo.BoluomePushPayResponseBo;
import com.ald.fanbei.api.biz.bo.BoluomePushRefundRequestBo;
import com.ald.fanbei.api.biz.bo.BoluomePushRefundResponseBo;
import com.ald.fanbei.api.biz.service.AfOrderPushLogService;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.enums.PayStatus;
import com.ald.fanbei.api.common.enums.PushStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfOrderPushLogDo;
import com.alibaba.fastjson.JSONObject;


/**
 * 菠萝觅工具类
 * @类描述：
 * @author xiaotianjian 2017年3月24日下午9:07:55
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("boluomeUtil")
public class BoluomeUtil extends AbstractThird{
	
	protected static Logger   logger = LoggerFactory.getLogger(BoluomeUtil.class);
	
	@Resource
	AfOrderPushLogService afOrderPushLogService;
	
	private static String pushPayUrl = null;
	private static String pushRefundUrl = null;
	private static Long SUCCESS_CODE = 1000L;

	public BoluomePushPayResponseBo pushPayStatus(Long orderId, String orderNo, String thirdOrderNo,PushStatus pushStatus, Long userId, BigDecimal amount){
		BoluomePushPayRequestBo reqBo = new BoluomePushPayRequestBo();
		reqBo.setOrderId(thirdOrderNo);
		reqBo.setStatus(pushStatus.getCode());
		reqBo.setAmount(amount);
		reqBo.setUserId(userId);
		reqBo.setTimestamp(System.currentTimeMillis());
		reqBo.setSign(BoluomeCore.builSign(reqBo));
		logger.info("pushPayStatus begin, reqBo = {}", reqBo);
		String reqResult = HttpUtil.doHttpPostJsonParam(getPushPayUrl(), JSONObject.toJSONString(reqBo));
		logThird(reqResult, "pushPayStatus", reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException(FanbeiExceptionCode.PUSH_BRAND_ORDER_STATUS_FAILED);
		}
		BoluomePushPayResponseBo responseBo = JSONObject.parseObject(reqResult,BoluomePushPayResponseBo.class);
		logger.info("pushPayStatus result , responseBo = {}", responseBo);
		if(responseBo != null && responseBo.getCode().equals(SUCCESS_CODE) ){
			responseBo.setSuccess(true);
			afOrderPushLogService.addOrderPushLog(buildPushLog(orderId, orderNo, pushStatus, true, JSONObject.toJSONString(reqBo), reqResult));
		}else{
			responseBo.setSuccess(false);
			afOrderPushLogService.addOrderPushLog(buildPushLog(orderId, orderNo, pushStatus, false, JSONObject.toJSONString(reqBo), reqResult));
		}
		return responseBo;
	}
	public BoluomePushPayResponseBo pushPayStatusForOrder(Long orderId, String orderNo, String thirdOrderNo,PushStatus pushStatus, Long userId, BigDecimal amount){
		BoluomePushPayRequestBo reqBo = new BoluomePushPayRequestBo();
		reqBo.setOrderId(thirdOrderNo);
		reqBo.setStatus(pushStatus.getCode());
		reqBo.setAmount(amount);
		reqBo.setUserId(userId);
		reqBo.setTimestamp(System.currentTimeMillis());
		reqBo.setSign(BoluomeCore.builSign(reqBo));
		logger.info("pushPayStatus begin, reqBo = {}", reqBo);
		String reqResult = HttpUtil.doHttpPostJsonParam(getPushPayUrl(), JSONObject.toJSONString(reqBo));
		logThird(reqResult, "pushPayStatus", reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException(FanbeiExceptionCode.PUSH_BRAND_ORDER_STATUS_FAILED);
		}
		BoluomePushPayResponseBo responseBo = JSONObject.parseObject(reqResult,BoluomePushPayResponseBo.class);
		logger.info("pushPayStatus result , responseBo = {}", responseBo);
		if(responseBo != null && responseBo.getCode().equals(SUCCESS_CODE) ){
			responseBo.setSuccess(true);
			afOrderPushLogService.addOrderPushLog(buildPushLog(orderId, orderNo, pushStatus, true, JSONObject.toJSONString(reqBo), reqResult));
		}else{
			responseBo.setSuccess(false);
			afOrderPushLogService.addOrderPushLog(buildPushLog(orderId, orderNo, pushStatus, false, JSONObject.toJSONString(reqBo), reqResult));
		}
		return responseBo;
	}
	
	public BoluomePushRefundResponseBo pushRefundStatus(Long orderId, String orderNo, String thirdOrderNo,PushStatus pushStatus, Long userId, BigDecimal amount, String refundNo){
		BoluomePushRefundRequestBo reqBo = new BoluomePushRefundRequestBo();
		reqBo.setOrderId(thirdOrderNo);
		reqBo.setStatus(pushStatus.getCode());
		reqBo.setAmount(amount);
		reqBo.setUserId(userId);
		reqBo.setTimestamp(System.currentTimeMillis());
		reqBo.setRefundNo(refundNo);
		reqBo.setSign(BoluomeCore.builSign(reqBo));
		logger.info("pushRefundStatus begin, reqBo = {}", reqBo);
		String reqResult = HttpUtil.doHttpPostJsonParam(getPushRefundUrl(), JSONObject.toJSONString(reqBo));
		logThird(reqResult, "pushRefundStatus", reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException(FanbeiExceptionCode.PUSH_BRAND_ORDER_STATUS_FAILED);
		}
		BoluomePushRefundResponseBo responseBo = JSONObject.parseObject(reqResult,BoluomePushRefundResponseBo.class);
		logger.info("pushRefundStatus result , responseBo = {}", responseBo);
		if(responseBo != null && responseBo.getCode().equals(SUCCESS_CODE) ){
			afOrderPushLogService.addOrderPushLog(buildPushLog(orderId, orderNo, pushStatus, true, JSONObject.toJSONString(reqBo), reqResult));
			responseBo.setSuccess(true);
			return responseBo;
		}else{
			afOrderPushLogService.addOrderPushLog(buildPushLog(orderId, orderNo, pushStatus, false, JSONObject.toJSONString(reqBo), reqResult));
			responseBo.setSuccess(false);
			return responseBo;
		}
	}
	
	private static String getPushPayUrl(){
		if(pushPayUrl==null){
			pushPayUrl = ConfigProperties.get(Constants.CONFKEY_BOLUOME_PUSH_PAY_URL);
			return pushPayUrl;
		}
		return pushPayUrl;
	}
	
	private static String getPushRefundUrl(){
		if(pushRefundUrl==null){
			pushRefundUrl = ConfigProperties.get(Constants.CONFKEY_BOLUOME_PUSH_REFUND_URL);
			return pushRefundUrl;
		}
		return pushRefundUrl;
	}

	public static OrderStatus parseOrderType(String orderStatusStr) {
		//1.已经下单 2.待支付 3.已支付 4.已完成 6.退款中 7.退款成功 8.已取消 9.处理中 11.等待退款
		int orderStatus = Integer.parseInt(orderStatusStr);
		OrderStatus status = null;
		if (orderStatus == 1 || orderStatus == 2) {
			status = OrderStatus.NEW;
		} else if (orderStatus == 3 || orderStatus == 9) {
			status = OrderStatus.PAID;
		} else if (orderStatus == 4 ) {
			status = OrderStatus.FINISHED;
		} else if (orderStatus == 6) {
			status = OrderStatus.DEAL_REFUNDING;
		} else if (orderStatus == 11) {
			status = OrderStatus.WAITING_REFUND;
		} else if (orderStatus == 8 || orderStatus == 7) {
			status = OrderStatus.CLOSED;
		} 
		return status;
	}
	
	public static PayStatus parsePayStatus(String orderStatusStr) {
		//1.已经下单 2.待支付 3.已支付 4.已完成 6.退款中 7.已经退款 8.已取消 9.处理中 11.等待退款
		int orderStatus = Integer.parseInt(orderStatusStr);
		PayStatus status = null;
		if (orderStatus == 1 || orderStatus == 2 || orderStatus == 8) {
			status = PayStatus.NOTPAY;
		}
		if (orderStatus == 3 || orderStatus == 4 || orderStatus == 9 ) {
			status = PayStatus.PAYED;
		} else if (orderStatus == 6 || orderStatus == 11 || orderStatus == 7) {
			status = PayStatus.REFUND;
		}
		return status;
	}
	
	private AfOrderPushLogDo buildPushLog(Long orderId, String orderNo, PushStatus pushStatus, boolean status, String params, String result) {
		AfOrderPushLogDo pushLog = new AfOrderPushLogDo();
		pushLog.setOrderId(orderId);
		pushLog.setOrderNo(orderNo);
		pushLog.setParams(params);
		pushLog.setResult(result);
		pushLog.setStatus(status+StringUtils.EMPTY);
		pushLog.setType(pushStatus.getCode());
		return pushLog;
	}
	
} 
