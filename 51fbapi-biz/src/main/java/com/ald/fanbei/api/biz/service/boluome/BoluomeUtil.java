package com.ald.fanbei.api.biz.service.boluome;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.BoluomeOrderCancelRequestBo;
import com.ald.fanbei.api.biz.bo.BoluomeOrderCancelResponseBo;
import com.ald.fanbei.api.biz.bo.BoluomeOrderSearchRequestBo;
import com.ald.fanbei.api.biz.bo.BoluomeOrderSearchResponseBo;
import com.ald.fanbei.api.biz.bo.BoluomePushPayRequestBo;
import com.ald.fanbei.api.biz.bo.BoluomePushPayResponseBo;
import com.ald.fanbei.api.biz.bo.BoluomePushRefundRequestBo;
import com.ald.fanbei.api.biz.bo.BoluomePushRefundResponseBo;
import com.ald.fanbei.api.biz.service.AfInterestFreeRulesService;
import com.ald.fanbei.api.biz.service.AfOrderPushLogService;
import com.ald.fanbei.api.biz.service.AfShopService;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.PayStatus;
import com.ald.fanbei.api.common.enums.PushStatus;
import com.ald.fanbei.api.common.enums.ShopPlantFormType;
import com.ald.fanbei.api.common.enums.UnitType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfInterestFreeRulesDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfOrderPushLogDo;
import com.ald.fanbei.api.dal.domain.AfShopDo;
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
	@Resource
	AfShopService afShopService;
	@Resource
	GeneratorClusterNo generatorClusterNo;
	@Resource
	AfInterestFreeRulesService afInterestFreeRulesService;
	
	private static String pushPayUrl = null;
	private static String pushRefundUrl = null;
	private static String orderSearchUrl = null;
	private static String orderCancelUrl = null;
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

	public  AfOrderDo orderSearch(String thirdOrderNo) throws UnsupportedEncodingException{
 		BoluomeOrderSearchRequestBo reqBo = new BoluomeOrderSearchRequestBo();
		String appKey = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_BOLUOME_APPKEY), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
		reqBo.setOrderId(thirdOrderNo);
		reqBo.setTimestamp(System.currentTimeMillis());
		reqBo.setSign(BoluomeCore.builOrderSign(reqBo));
		reqBo.setAppKey(appKey);
		logger.info("OrderSearch begin, reqBo = {}", reqBo);
		String url = getOrderSearchUrl()+"?"+"appKey="+appKey+"&orderId="+reqBo.getOrderId()+"&timestamp="+reqBo.getTimestamp()+"&sign="+reqBo.getSign();
		String reqResult = HttpUtil.doGet(url,10);
		JSONObject result = JSONObject.parseObject(reqResult);
		AfOrderDo orderInfo = new AfOrderDo();
		if ("1000".equals(result.getString("code"))) {
			BoluomeOrderSearchResponseBo responseBo = JSONObject.parseObject(result.getString("data"),BoluomeOrderSearchResponseBo.class);
			logger.info("OrderSearch result , responseBo = {}", responseBo);
			
			String orderId = responseBo.getOrderId();
			String orderType = responseBo.getOrderType().toUpperCase();
			String orderTitle = responseBo.getOrderTitle();
			String userId = responseBo.getUserId();
			BigDecimal price = responseBo.getPrice();
			Integer status = responseBo.getStatus();
			Long createdTime = responseBo.getCreatedTime();
			Long expiredTime = responseBo.getExpiredTime();
			String detailUrl = responseBo.getDetailUrl();
			String channel = responseBo.getChannel().toUpperCase();
			AfShopDo shopInfo = afShopService.getShopByPlantNameAndTypeAndServiceProvider(ShopPlantFormType.BOLUOME.getCode(), orderType, channel);
			orderInfo.setThirdOrderNo(orderId);
			orderInfo.setGoodsName(orderTitle);
			orderInfo.setOrderType(OrderType.BOLUOME.getCode());
			orderInfo.setSecType(orderType);
			orderInfo.setUserId(StringUtils.isNotEmpty(userId)  ? Long.parseLong(userId) : null);
//		orderInfo.setMobile(userPhone);
			//有可能没有价格
			BigDecimal priceAmount = price != null ? price : BigDecimal.ZERO;
			orderInfo.setPriceAmount(priceAmount);
			orderInfo.setGmtPayEnd(expiredTime != null  ? new Date(System.currentTimeMillis() + expiredTime) : null);
			orderInfo.setThirdDetailUrl(detailUrl);
			orderInfo.setStatus(status != null ? BoluomeUtil.parseOrderType(status + StringUtils.EMPTY).getCode() : null);
			orderInfo.setGmtCreate(createdTime != null ? new Date(createdTime) : null);
			orderInfo.setOrderNo(generatorClusterNo.getOrderNo(OrderType.BOLUOME));
			orderInfo.setUserCouponId(0l);
			orderInfo.setGoodsId(0l);
			orderInfo.setOpenId(StringUtils.EMPTY);
			orderInfo.setGoodsIcon(shopInfo.getLogo());
			orderInfo.setCount(1);
			orderInfo.setPriceAmount(priceAmount);
			orderInfo.setSaleAmount(priceAmount);
			orderInfo.setActualAmount(priceAmount);
			orderInfo.setShopName(StringUtils.EMPTY);
			orderInfo.setPayStatus(status != null ? BoluomeUtil.parsePayStatus(status + StringUtils.EMPTY).getCode() : null);
			orderInfo.setPayType(StringUtils.EMPTY);
			orderInfo.setPayTradeNo(StringUtils.EMPTY);
			orderInfo.setTradeNo(StringUtils.EMPTY);
			orderInfo.setMobile(StringUtils.EMPTY);
			orderInfo.setBankId(0l);
			orderInfo.setServiceProvider(channel);
			if (shopInfo.getInterestFreeId() != 0) {
    			AfInterestFreeRulesDo ruleInfo = afInterestFreeRulesService.getById(shopInfo.getInterestFreeId());
    			orderInfo.setInterestFreeJson(ruleInfo.getRuleJson());
    		}
			calculateOrderRebateAmount(orderInfo, shopInfo);
			
		} else {
			throw new FanbeiException("find order error");
		}
		return orderInfo;
	}
	
	/**
     * 设置订单返利和平台佣金金额
     * @param orderInfo
     * @param shopInfo
     */
    private void calculateOrderRebateAmount(AfOrderDo orderInfo, AfShopDo shopInfo) {
    	if (shopInfo == null || orderInfo == null) {
    		return;
    	}
    	BigDecimal priceAmount = orderInfo.getPriceAmount();
    	if (priceAmount == null) {
    		orderInfo.setPriceAmount(BigDecimal.ZERO);
    		orderInfo.setSaleAmount(BigDecimal.ZERO);
    		orderInfo.setActualAmount(BigDecimal.ZERO);
    		orderInfo.setRebateAmount(BigDecimal.ZERO);
    		orderInfo.setCommissionAmount(BigDecimal.ZERO);
    		return;
    	}
    	String commissionUnit =  shopInfo.getCommissionUnit();
    	BigDecimal commissionAmount = shopInfo.getCommissionAmount();
    	String rebateUnit =  shopInfo.getRebateUnit();
    	BigDecimal rebateAmount = shopInfo.getRebateAmount();
    	
    	if (commissionUnit.equals(UnitType.RMB.getCode())) {
    		orderInfo.setCommissionAmount(commissionAmount);
    	} else {
    		BigDecimal tempAmount = BigDecimalUtil.multiply(priceAmount, commissionAmount);
    		orderInfo.setCommissionAmount(BigDecimalUtil.divide(tempAmount, BigDecimalUtil.ONE_HUNDRED));
    	}
    	if (rebateUnit.equals(UnitType.RMB.getCode())) {
    		orderInfo.setRebateAmount(rebateAmount);
    	} else {
    		BigDecimal tempAmount = BigDecimalUtil.multiply(priceAmount, rebateAmount);
    		orderInfo.setRebateAmount(BigDecimalUtil.divide(tempAmount, BigDecimalUtil.ONE_HUNDRED));
    	}
    }
	
	public  BoluomeOrderCancelResponseBo cancelOrder(String thirdOrderNo,String orderType,String reason) throws UnsupportedEncodingException{
		BoluomeOrderCancelRequestBo reqBo = new BoluomeOrderCancelRequestBo();
		String appKey = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_BOLUOME_APPKEY), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
		reqBo.setOrderId(thirdOrderNo);
		reqBo.setOrderType(orderType.toLowerCase());
		reqBo.setReason(reason);
		reqBo.setTimestamp(System.currentTimeMillis());
		reqBo.setSign(BoluomeCore.builOrderSign(reqBo));
		reqBo.setAppKey(appKey);
		logger.info("OrderCancel begin, reqBo = {}", reqBo);
		String reqResult = HttpUtil.doHttpPostJsonParam(getOrderCancelUrl(), JSONObject.toJSONString(reqBo));
		BoluomeOrderCancelResponseBo responseBo = JSONObject.parseObject(reqResult,BoluomeOrderCancelResponseBo.class);
		logger.info("OrderCancel result , responseBo = {}", responseBo);
		if(responseBo != null && responseBo.getCode().equals(SUCCESS_CODE) ){
			responseBo.setSuccess(true);
		}else{
			responseBo.setSuccess(false);
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

	private static String getOrderSearchUrl(){
		if(orderSearchUrl==null){
			orderSearchUrl = ConfigProperties.get(Constants.CONFKEY_BOLUOME_ORDER_SEARCH_URL);
			return orderSearchUrl;
		}
		return orderSearchUrl;
	}
	
	private static String getOrderCancelUrl(){
		if(orderCancelUrl==null){
			orderCancelUrl = ConfigProperties.get(Constants.CONFKEY_BOLUOME_ORDER_CANCEL_URL);
			return orderCancelUrl;
		}
		return orderCancelUrl;
	}
	public static OrderStatus parseOrderType(String orderStatusStr) {
		//1.已经下单 2.待支付 3.已支付 4.已完成 6.退款中 7.退款成功 8.已取消 9.处理中 11.等待退款 12.支付中
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
		}  else if (orderStatus == 12) {
			status = OrderStatus.DEALING;
		} 
		
		return status;
	}
	
	public static PayStatus parsePayStatus(String orderStatusStr) {
		//1.已经下单 2.待支付 3.已支付 4.已完成 6.退款中 7.已经退款 8.已取消 9.处理中 11.等待退款 12.支付中
		int orderStatus = Integer.parseInt(orderStatusStr);
		PayStatus status = null;
		if (orderStatus == 1 || orderStatus == 2 || orderStatus == 8) {
			status = PayStatus.NOTPAY;
		}
		if (orderStatus == 3 || orderStatus == 4 || orderStatus == 9 ) {
			status = PayStatus.PAYED;
		} else if (orderStatus == 6 || orderStatus == 11 || orderStatus == 7) {
			status = PayStatus.REFUND;
		}else if (orderStatus == 12){
			status = PayStatus.DEALING;
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
