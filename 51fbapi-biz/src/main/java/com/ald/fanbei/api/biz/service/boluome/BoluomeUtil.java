package com.ald.fanbei.api.biz.service.boluome;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.BoluomeOrderCancelRequestBo;
import com.ald.fanbei.api.biz.bo.BoluomeOrderCancelResponseBo;
import com.ald.fanbei.api.biz.bo.BoluomeOrderSearchRequestBo;
import com.ald.fanbei.api.biz.bo.BoluomeOrderSearchResponseBo;
import com.ald.fanbei.api.biz.bo.BoluomePushPayRequestBo;
import com.ald.fanbei.api.biz.bo.BoluomePushPayResponseBo;
import com.ald.fanbei.api.biz.bo.BoluomePushRefundRequestBo;
import com.ald.fanbei.api.biz.bo.BoluomePushRefundResponseBo;
import com.ald.fanbei.api.biz.bo.BrandActivityCouponResponseBo;
import com.ald.fanbei.api.biz.bo.BrandCouponResponseBo;
import com.ald.fanbei.api.biz.bo.BrandUserCouponRequestBo;
import com.ald.fanbei.api.biz.bo.PickBrandCouponRequestBo;
import com.ald.fanbei.api.biz.service.AfInterestFreeRulesService;
import com.ald.fanbei.api.biz.service.AfOrderPushLogService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfShopService;
import com.ald.fanbei.api.biz.service.AfSupOrderService;
import com.ald.fanbei.api.biz.service.AfUserAccountSenceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.OrderSecType;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.PayStatus;
import com.ald.fanbei.api.common.enums.PushStatus;
import com.ald.fanbei.api.common.enums.ShopPlantFormType;
import com.ald.fanbei.api.common.enums.UnitType;
import com.ald.fanbei.api.common.enums.UserAccountSceneType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfInterestFreeRulesDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfOrderPushLogDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfShopDo;
import com.ald.fanbei.api.dal.domain.AfSupOrderDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountSenceDo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 菠萝觅工具类
 * 
 * @类描述：
 * @author xiaotianjian 2017年3月24日下午9:07:55
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("boluomeUtil")
public class BoluomeUtil extends AbstractThird {

    protected static Logger logger = LoggerFactory.getLogger(BoluomeUtil.class);

    @Resource
    AfOrderPushLogService afOrderPushLogService;
    @Resource
    AfShopService afShopService;
    @Resource
    GeneratorClusterNo generatorClusterNo;
    @Resource
    AfInterestFreeRulesService afInterestFreeRulesService;
    @Resource
    BizCacheUtil bizCacheUtil;

    @Resource
    AfResourceService afResourceService;
    @Resource
    AfUserAccountService afUserAccountService;

    @Autowired
    private AfSupOrderService afSupOrderService;
    @Autowired
    private AfOrderService afOrderService;

    @Autowired
    private AfUserAccountSenceService afUserAccountSenceService;
    
    private static String pushPayUrl = null;
    private static String pushRefundUrl = null;
    private static String orderSearchUrl = null;
    private static String orderCancelUrl = null;
    private static String couponListUrl = null;
    private static String activityCouponListUrl = null;
    private static Long SUCCESS_CODE = 1000L;

    public void pushPayStatus(Long orderId, String orderType, String orderNo, String thirdOrderNo, PushStatus pushStatus, Long userId, BigDecimal amount, String secType) {
	if (!OrderSecType.SUP_GAME.getCode().equals(secType)) {
	    if (OrderType.BOLUOME.getCode().equals(orderType)) {
		// 菠萝觅订单
		BoluomePushPayRequestBo reqBo = new BoluomePushPayRequestBo();
		reqBo.setOrderId(thirdOrderNo);
		reqBo.setStatus(pushStatus.getCode());
		reqBo.setAmount(amount);
		reqBo.setUserId(userId);
		reqBo.setTimestamp(System.currentTimeMillis());
		reqBo.setSign(BoluomeCore.builSign(reqBo));
		logger.info("pushPayStatus begin, reqBo = {}", reqBo);

		BoluomePushPayResponseBo responseBo = null;
		int loopCount = 0;
		do {
		    logger.info("pushPayStatus loopCount：" + String.valueOf(loopCount));
		    String reqResult = HttpUtil.doHttpPostJsonParam(getPushPayUrl(), JSONObject.toJSONString(reqBo));
		    logger.info("pushPayStatus result , responseBo = {}", responseBo);
		    if (StringUtils.isNotBlank(reqResult)) {
			responseBo = JSONObject.parseObject(reqResult, BoluomePushPayResponseBo.class);
			if (responseBo != null && responseBo.getCode().equals(SUCCESS_CODE)) {
			    responseBo.setSuccess(true);
			    afOrderPushLogService.addOrderPushLog(buildPushLog(orderId, orderNo, pushStatus, true, JSONObject.toJSONString(reqBo), reqResult));
			} else {
			    responseBo.setSuccess(false);
			    afOrderPushLogService.addOrderPushLog(buildPushLog(orderId, orderNo, pushStatus, false, JSONObject.toJSONString(reqBo), reqResult));
			}

			break;
		    }
		    try {
			Thread.sleep(500);
		    } catch (InterruptedException e) {
			logger.error("pushPayStatus sleep error:", e);
		    }

		    loopCount++;
		} while (loopCount < 4);
	    }
	} else { // 游戏充值业务订单
	    if (PushStatus.PAY_SUC.equals(pushStatus)) {// 充值成功提交订单信息到sup
		AfSupOrderDo supOrderDo = afSupOrderService.getByOrderNo(orderNo);
		String resultXml = afSupOrderService.sendOrderToSup(orderNo, supOrderDo.getGoodsCode(), supOrderDo.getUserName(), supOrderDo.getGameName(), supOrderDo.getGameAcct(), supOrderDo.getGameArea(), supOrderDo.getGameType(), supOrderDo.getAcctType(), supOrderDo.getGoodsNum(), supOrderDo.getGameSrv(), supOrderDo.getUserIp());
		// 记录sup返回xml数据
		afSupOrderService.updateMsgByOrder(orderNo, resultXml);
		// 解析xml，获取充值受理结果
		String resultNodeValue = "";
		try {
		    Document document = DocumentHelper.parseText(resultXml);
		    resultNodeValue = document.selectSingleNode("/root/result").getStringValue();
		} catch (Exception e) {
		    logger.error("sup pushPayStatus error:", e);
		}
		// 受理失败，则执行对款关闭订单
		if (!resultNodeValue.equals("01")) {
		    // 退款
		    AfOrderDo orderInfo = afOrderService.getOrderById(orderId);
		    afOrderService.dealBrandOrderRefund(orderInfo.getRid(), orderInfo.getUserId(), orderInfo.getBankId(), orderInfo.getOrderNo(), orderInfo.getThirdOrderNo(), orderInfo.getActualAmount(), orderInfo.getActualAmount(), orderInfo.getPayType(), orderInfo.getPayTradeNo(), orderInfo.getOrderNo(), "SUP");
		}
	    }
	}
    }

    public AfOrderDo orderSearch(String thirdOrderNo) throws UnsupportedEncodingException {
	BoluomeOrderSearchRequestBo reqBo = new BoluomeOrderSearchRequestBo();
	String appKey = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_BOLUOME_APPKEY), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
	reqBo.setOrderId(thirdOrderNo);
	reqBo.setTimestamp(System.currentTimeMillis()/1000);
	reqBo.setSign(BoluomeCore.builOrderSign(reqBo));
	reqBo.setAppKey(appKey);
	logger.info("OrderSearch begin, reqBo = {}", reqBo);
	String url = getOrderSearchUrl() + "?" + "appKey=" + appKey + "&orderId=" + reqBo.getOrderId() + "&timestamp=" + reqBo.getTimestamp() + "&sign=" + reqBo.getSign();
	String reqResult = HttpUtil.doGet(url, 10);
	JSONObject result = JSONObject.parseObject(reqResult);
	AfOrderDo orderInfo = new AfOrderDo();
	if ("1000".equals(result.getString("code"))) {
	    BoluomeOrderSearchResponseBo responseBo = JSONObject.parseObject(result.getString("data"), BoluomeOrderSearchResponseBo.class);
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
	    orderInfo.setUserId(StringUtils.isNotEmpty(userId) ? Long.parseLong(userId) : null);
	    // orderInfo.setMobile(userPhone);
	    // 有可能没有价格
	    BigDecimal priceAmount = price != null ? price : BigDecimal.ZERO;
	    orderInfo.setPriceAmount(priceAmount);
	    orderInfo.setGmtPayEnd(expiredTime != null ? new Date(System.currentTimeMillis() + expiredTime) : null);
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
	    AfUserAccountSenceDo afUserAccountSenceDo = afUserAccountSenceService.getByUserIdAndScene(UserAccountSceneType.ONLINE.getCode(), NumberUtil.objToLongDefault(userId, 0l));
	    //AfUserAccountDo userAccountInfo = afUserAccountService.getUserAccountByUserId(NumberUtil.objToLongDefault(userId, 0L));
	    if (afUserAccountSenceDo != null) {
		orderInfo.setAuAmount(afUserAccountSenceDo.getAuAmount());
		orderInfo.setUsedAmount(afUserAccountSenceDo.getUsedAmount());
	    }
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
     * 
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
	String commissionUnit = shopInfo.getCommissionUnit();
	BigDecimal commissionAmount = shopInfo.getCommissionAmount();
	String rebateUnit = shopInfo.getRebateUnit();
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

    public BoluomeOrderCancelResponseBo cancelOrder(String thirdOrderNo, String orderType, String reason) throws UnsupportedEncodingException {
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
	BoluomeOrderCancelResponseBo responseBo = JSONObject.parseObject(reqResult, BoluomeOrderCancelResponseBo.class);
	logger.info("OrderCancel result , responseBo = {}", responseBo);
	if (responseBo != null && responseBo.getCode().equals(SUCCESS_CODE)) {
	    responseBo.setSuccess(true);
	} else {
	    responseBo.setSuccess(false);
	}
	return responseBo;
    }

    public BoluomePushRefundResponseBo pushRefundStatus(Long orderId, String orderNo, String thirdOrderNo, PushStatus pushStatus, Long userId, BigDecimal amount, String refundNo) {
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
	if (StringUtil.isBlank(reqResult)) {
	    throw new FanbeiException(FanbeiExceptionCode.PUSH_BRAND_ORDER_STATUS_FAILED);
	}
	BoluomePushRefundResponseBo responseBo = JSONObject.parseObject(reqResult, BoluomePushRefundResponseBo.class);
	logger.info("pushRefundStatus result , responseBo = {}", responseBo);
	if (responseBo != null && responseBo.getCode().equals(SUCCESS_CODE)) {
	    afOrderPushLogService.addOrderPushLog(buildPushLog(orderId, orderNo, pushStatus, true, JSONObject.toJSONString(reqBo), reqResult));
	    responseBo.setSuccess(true);
	    return responseBo;
	} else {
	    afOrderPushLogService.addOrderPushLog(buildPushLog(orderId, orderNo, pushStatus, false, JSONObject.toJSONString(reqBo), reqResult));
	    responseBo.setSuccess(false);
	    return responseBo;
	}
    }

    private static String getPushPayUrl() {
	if (pushPayUrl == null) {
	    pushPayUrl = ConfigProperties.get(Constants.CONFKEY_BOLUOME_PUSH_PAY_URL);
	    return pushPayUrl;
	}
	return pushPayUrl;
    }

    private static String getPushRefundUrl() {
	if (pushRefundUrl == null) {
	    pushRefundUrl = ConfigProperties.get(Constants.CONFKEY_BOLUOME_PUSH_REFUND_URL);
	    return pushRefundUrl;
	}
	return pushRefundUrl;
    }

    private static String getOrderSearchUrl() {
	if (orderSearchUrl == null) {
	    orderSearchUrl = ConfigProperties.get(Constants.CONFKEY_BOLUOME_ORDER_SEARCH_URL);
	    return orderSearchUrl;
	}
	return orderSearchUrl;
    }

    private static String getOrderCancelUrl() {
	if (orderCancelUrl == null) {
	    orderCancelUrl = ConfigProperties.get(Constants.CONFKEY_BOLUOME_ORDER_CANCEL_URL);
	    return orderCancelUrl;
	}
	return orderCancelUrl;
    }

    private static String getCouponListUrl() {
	if (couponListUrl == null) {
	    couponListUrl = ConfigProperties.get(Constants.CONFKEY_BOLUOME_API_URL) + "/api/promotion/get_coupon_list";
	    return couponListUrl;
	}
	return couponListUrl;
    }

    private static String getActivityCouponList() {
	if (activityCouponListUrl == null) {
	    activityCouponListUrl = ConfigProperties.get(Constants.CONFKEY_BOLUOME_SERVER_API_URL) + "/bss/v1/apps/campaigns/all_coupons";
	    return activityCouponListUrl;
	}
	return activityCouponListUrl;
    }

    /**
     * @param userId
     *            用户id
     * @param type
     *            优惠券类型 1可使用的 2已经使用的 3过期的 4 全部的
     * @param pageIndex
     *            第几页
     * @param pageSize
     *            一页多少个数 最大20个
     * @return
     */
    public List<BrandCouponResponseBo> getUserCouponList(Long userId, Integer type, Integer pageIndex, Integer pageSize) {
	BrandUserCouponRequestBo request = new BrandUserCouponRequestBo();
	request.setUserId(userId + StringUtils.EMPTY);
	request.setType(type);
	request.setPageIndex(pageIndex);
	request.setPageSize(BoluomeCore.DEALFT_PAGE_SIZE);
	String resultString = HttpUtil.doHttpPost(getCouponListUrl(), JSONObject.toJSONString(request));
	JSONObject resultJson = JSONObject.parseObject(resultString);
	JSONObject data = resultJson.getJSONObject(BoluomeCore.DATA);
	String couponStr = StringUtils.EMPTY;
	if (type == 1) {
	    couponStr = data.getString(BoluomeCore.AVAILABLE_COUPON);
	} else if (type == 2) {
	    couponStr = data.getString(BoluomeCore.USED_COUPON);
	} else if (type == 3) {
	    couponStr = data.getString(BoluomeCore.EXPIRED_COUPON);
	}
	return JSONObject.parseArray(couponStr, BrandCouponResponseBo.class);
    }

    /**
     * 
     * @param url
     *            领取优惠券地址
     * @return
     */
    public List<BrandActivityCouponResponseBo> getActivityCouponList(String url) {
	try {
	    Map<String, String> map = parseAppIdAndCampaignId(url);
	    if (map == null) {
		return null;
	    }
	    String app_id = map.get(BoluomeCore.APP_ID);
	    String campaign_id = map.get(BoluomeCore.CAMPAIGN_ID);
	    Map<String, String> buildParams = new HashMap<String, String>();
	    buildParams.put(BoluomeCore.APP_ID, app_id);
	    buildParams.put(BoluomeCore.CAMPAIGN_ID, campaign_id);
	    buildParams.put(BoluomeCore.TIME_STAMP, System.currentTimeMillis()/1000 + StringUtils.EMPTY);

	    String sign = BoluomeCore.builOrderSign(buildParams);
	    buildParams.put(BoluomeCore.SIGN, sign);
	    buildParams.put(BoluomeCore.APP_KEY, AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_BOLUOME_APPKEY), ConfigProperties.get(Constants.CONFKEY_AES_KEY)));
	    String paramsStr = BoluomeCore.createLinkString(buildParams);
	    String requestUrl = getActivityCouponList() + "?" + paramsStr;

	    String resultString = HttpUtil.doGet(requestUrl, 10);

	    JSONObject resultJson = JSONObject.parseObject(resultString);
	    if (resultJson != null && "0".equals(resultJson.getString("code"))) {
		String jsonStr = resultJson.getJSONArray(BoluomeCore.DATA).getJSONObject(0).getJSONArray(BoluomeCore.ACTIVITY_COUPONS).toJSONString();
		return JSONObject.parseArray(jsonStr, BrandActivityCouponResponseBo.class);
	    }
	    return null;
	} catch (UnsupportedEncodingException e) {
	    logger.error("getActivityCouponList error ,e = {}", e);
	    e.printStackTrace();
	}
	return null;
    }

    /**
     * 
     * @param url
     *            要领取的优惠券
     * @param userId
     *            用户id
     * @param type
     *            优惠券类型，1可使用，2未使用，3已过期
     * @return
     */

    public boolean isUserHasCoupon(String url, Long userId, Integer type) {
	Map<String, String> map = parseAppIdAndCampaignId(url);
	if (map == null) {
	    return false;
	}
	List<BrandActivityCouponResponseBo> activityCouponList = getActivityCouponList(url);
	if (CollectionUtils.isEmpty(activityCouponList)) {
	    return false;
	}
	Integer nextPageNo = 0;
	boolean constains = false;
	do {
	    nextPageNo++;
	    List<BrandCouponResponseBo> couponList = getUserCouponList(userId, type, nextPageNo, BoluomeCore.DEALFT_PAGE_SIZE);
	    if (CollectionUtils.isNotEmpty(couponList)) {
		for (BrandCouponResponseBo userCoupon : couponList) {
		    for (BrandActivityCouponResponseBo activityCoupon : activityCouponList) {
			if (activityCoupon.getActivity_coupon_id().equals(userCoupon.getActivityCouponId())) {
			    constains = true;
			    break;
			}
		    }
		    if (constains) {
			break;
		    }
		}
	    }
	} while ((nextPageNo = getNextPageNo(userId, type, nextPageNo, BoluomeCore.DEALFT_PAGE_SIZE)) > 0);
	return constains;
    }

    /**
     * 逛逛点亮活动
     * 
     * @param resourceId
     * @param userName
     * @return
     */
    public boolean isHasCoupon(String resourceId, String userName) {
	boolean result = false;
	Object resultObj = bizCacheUtil.getObject("boluome:coupon:" + resourceId + userName);
	if (resultObj != null) {
	    result = true;
	}

	return result;
    }

    /**
     * 解析
     * 
     * @param url
     * @return
     */
    private Map<String, String> parseAppIdAndCampaignId(String url) {
	Map<String, String> result = new HashMap<String, String>();
	if (StringUtils.isEmpty(url)) {
	    return null;
	}
	String[] pieces = url.split("/");
	String app_id = pieces[6];
	String campaign_id = pieces[8];
	result.put(BoluomeCore.APP_ID, app_id);
	result.put(BoluomeCore.CAMPAIGN_ID, campaign_id);

	return result;

    }

    /**
     * @param userId
     *            用户id
     * @param type
     *            优惠券类型
     * @param pageIndex
     *            第几页
     * @param pageSize
     *            唯一标识
     * @return nextPage 当nextPage为0时表示没有下一页了
     */
    public Integer getNextPageNo(Long userId, Integer type, Integer pageIndex, Integer pageSize) {
	BrandUserCouponRequestBo request = new BrandUserCouponRequestBo();
	request.setUserId(userId + StringUtils.EMPTY);
	request.setType(type);
	request.setPageIndex(pageIndex);
	request.setPageSize(pageSize);
	String resultString = HttpUtil.doHttpPost(getCouponListUrl(), JSONObject.toJSONString(request));
	JSONObject resultJson = JSONObject.parseObject(resultString);
	JSONObject data = resultJson.getJSONObject(BoluomeCore.DATA);
	Integer nextPage = data.getInteger(BoluomeCore.NEXT_PAGE_INDEX);
	return nextPage;
    }

    public static OrderStatus parseOrderType(String orderStatusStr) {
	// 1.已经下单 2.待支付 3.已支付 4.已完成 6.退款中 7.退款成功 8.已取消 9.处理中 11.等待退款 12.支付中
	int orderStatus = Integer.parseInt(orderStatusStr);
	OrderStatus status = null;
	if (orderStatus == 1 || orderStatus == 2) {
	    status = OrderStatus.NEW;
	} else if (orderStatus == 3) {// || orderStatus == 9) {
	    status = OrderStatus.PAID;
	} else if (orderStatus == 4) {
	    status = OrderStatus.FINISHED;
	} else if (orderStatus == 6) {
	    status = OrderStatus.DEAL_REFUNDING;
	} else if (orderStatus == 11) {
	    status = OrderStatus.WAITING_REFUND;
	} else if (orderStatus == 8 || orderStatus == 7) {
	    status = OrderStatus.CLOSED;
	} else if (orderStatus == 12) {
	    status = OrderStatus.DEALING;
	}
	else if(orderStatus == 9){
	    status= OrderStatus.PROCESSING;
	}

	return status;
    }

    public static PayStatus parsePayStatus(String orderStatusStr) {
	// 1.已经下单 2.待支付 3.已支付 4.已完成 6.退款中 7.已经退款 8.已取消 9.处理中 11.等待退款 12.支付中
	int orderStatus = Integer.parseInt(orderStatusStr);
	PayStatus status = null;
	if (orderStatus == 1 || orderStatus == 2 || orderStatus == 8) {
	    status = PayStatus.NOTPAY;
	}
	if (orderStatus == 3 || orderStatus == 4 || orderStatus == 9) {
	    status = PayStatus.PAYED;
	} else if (orderStatus == 6 || orderStatus == 11 || orderStatus == 7) {
	    status = PayStatus.REFUND;
	} else if (orderStatus == 12) {
	    status = PayStatus.DEALING;
	}
	return status;
    }

    /**
     * 发菠萝觅优惠券
     * 
     * @param resourceId
     * @param data
     * @param userId
     */
    public void grantCoupon(Long resourceId, Map<String, Object> data, Long userId) {
	logger.info("BoluomeUtil grant start, sceneId = {}, userId = {}", resourceId, userId);
	if (resourceId == null) {
	    throw new FanbeiException(FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
	}
	AfResourceDo resourceInfo = afResourceService.getResourceByResourceId(resourceId);
	if (resourceInfo == null) {
	    logger.error("couponSceneId is invalid");
	    throw new FanbeiException(FanbeiExceptionCode.PARAM_ERROR);
	}
	data.put("prizeName", resourceInfo.getName());
	data.put("prizeType", "BOLUOMI");

	PickBrandCouponRequestBo bo = new PickBrandCouponRequestBo();
	bo.setUser_id(userId + StringUtil.EMPTY);

	Date gmtStart = DateUtil.parseDate(resourceInfo.getValue1(), DateUtil.DATE_TIME_SHORT);
	Date gmtEnd = DateUtil.parseDate(resourceInfo.getValue2(), DateUtil.DATE_TIME_SHORT);

	if (DateUtil.beforeDay(new Date(), gmtStart)) {
	    throw new FanbeiException(FanbeiExceptionCode.PICK_BRAND_COUPON_NOT_START);
	}
	if (DateUtil.afterDay(new Date(), gmtEnd)) {
	    throw new FanbeiException(FanbeiExceptionCode.PICK_BRAND_COUPON_DATE_END);
	}
	String url = resourceInfo.getValue();
	if (url != null) {
	    url = url.replace(" ", "");
	}
	String resultString = HttpUtil.doHttpPostJsonParam(url, JSONObject.toJSONString(bo));
	logger.info("BoluomeUtil grant finish, bo = {}, resultString = {}", JSONObject.toJSONString(bo), resultString);
	JSONObject resultJson = JSONObject.parseObject(resultString);
	if (!"0".equals(resultJson.getString("code"))) {
	    throw new FanbeiException(resultJson.getString("msg"));
	} else if (JSONArray.parseArray(resultJson.getString("data")).size() == 0) {
	    throw new FanbeiException("仅限领取一次，请勿重复领取！");
	}
    }

    private AfOrderPushLogDo buildPushLog(Long orderId, String orderNo, PushStatus pushStatus, boolean status, String params, String result) {
	AfOrderPushLogDo pushLog = new AfOrderPushLogDo();
	pushLog.setOrderId(orderId);
	pushLog.setOrderNo(orderNo);
	pushLog.setParams(params);
	pushLog.setResult(result);
	pushLog.setStatus(status + StringUtils.EMPTY);
	pushLog.setType(pushStatus.getCode());
	return pushLog;
    }

}
