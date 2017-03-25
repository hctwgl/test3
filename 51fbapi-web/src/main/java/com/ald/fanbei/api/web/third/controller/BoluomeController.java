package com.ald.fanbei.api.web.third.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfShopService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeNotify;
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.ShopPlantFormType;
import com.ald.fanbei.api.common.enums.UnitType;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfShopDo;

/**
 * 菠萝觅第三方接口
 * @类描述：
 * @author xiaotianjian 2017年3月24日下午1:54:46
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/third/boluome")
public class BoluomeController{
	
    protected final Logger   logger = LoggerFactory.getLogger(this.getClass());
    
    private static final String ORDER_ID = "orderId";
    private static final String ORDER_TYPE = "orderType";
    private static final String ORDER_TITLE = "orderTitle";
    private static final String USER_ID = "userId";
    private static final String USER_PHONE = "userPhone";
    private static final String PRICE = "price";
    private static final String STATUS = "status";
    private static final String DISPLAY_STATUS = "displayStatus";
    private static final String CREATED_TIME = "createdTime";
    private static final String EXPIRED_TIME = "expiredTime";
    private static final String DETAIL_URL = "detailUrl";
    private static final String TIMES_TAMP = "timestamp";
    private static final String SIGN = "sign";
	
	@Resource
	GeneratorClusterNo generatorClusterNo;
	@Resource
	AfOrderService afOrderService;
	@Resource
	AfShopService afShopService;
	
    @RequestMapping(value = {"/synchOrder","/synchOrderStatus"}, method = RequestMethod.POST)
    @ResponseBody
	public String synchOrder(HttpServletRequest request, HttpServletResponse response) throws Exception{
    	String uri = StringUtils.EMPTY;
    	if (request.getRequestURI().contains("synchOrder")) {
    		uri = "synchOrder";
    	} else {
    		uri = "synchOrderStatus";
    	}
    	logger.info(uri + "begin params = {}",request.getParameterMap());
    	Map<String, String> params = buildOrderParamMap(request);
    	
    	boolean sign = BoluomeNotify.verify(params);
    	
    	String retunStr = StringUtils.EMPTY;
    	if (sign) {
    		try {
    			AfOrderDo orderInfo = buildOrderInfo(params);
    			if (orderInfo.getRid() == null) {
    				afOrderService.createOrder(orderInfo);
    			} else {
    				afOrderService.dealBoluomeOrder(orderInfo);
    			}
    			retunStr = "Successs";
    		} catch (Exception e) {
    			retunStr = "error";
    			throw e;
    		}
    	}  else {
    		logger.info("sign is invalid ");
    		throw new Exception("签名不对");
    	}
    	logger.info(uri + " complete, result = {}, sign = {}",retunStr, sign);
    	return retunStr;
    }
    
    private Map<String, String> buildOrderParamMap(HttpServletRequest request) {
    	Map<String, String> params = new HashMap<String, String>();
    	String orderId = request.getParameter(ORDER_ID);
    	String orderType = request.getParameter(ORDER_TYPE);
    	String orderTitle = request.getParameter(ORDER_TITLE);
    	String userId = request.getParameter(USER_ID);
    	String userPhone = request.getParameter(USER_PHONE);
    	String price = request.getParameter(PRICE);
    	String status = request.getParameter(STATUS);
    	String displayStatus = request.getParameter(DISPLAY_STATUS);
    	String createdTime = request.getParameter(CREATED_TIME);
    	String expiredTime = request.getParameter(EXPIRED_TIME);
    	String detailUrl = request.getParameter(DETAIL_URL);
    	String timestamp = request.getParameter(TIMES_TAMP);
    	String sign = request.getParameter(SIGN);
    	
    	params.put(ORDER_ID, orderId);
    	params.put(ORDER_TYPE, orderType);
    	params.put(ORDER_TITLE, orderTitle);
    	params.put(USER_ID, userId);
    	params.put(USER_PHONE, userPhone);
    	params.put(PRICE, price);
    	params.put(STATUS, status);
    	params.put(DISPLAY_STATUS, displayStatus);
    	params.put(CREATED_TIME, createdTime);
    	params.put(EXPIRED_TIME, expiredTime);
    	params.put(DETAIL_URL, detailUrl);
    	params.put(TIMES_TAMP, timestamp);
    	params.put(SIGN, sign);
    	return params;
    }
    
    private AfOrderDo buildOrderInfo(Map<String, String> params) {
    	
    	String orderId = params.get(ORDER_ID);
    	//1.已经下单 2.待支付 3.已支付 4.已完成 6.退款中 7.已经退款 8.已取消 9.处理中 11.等待退款
    	String orderType = params.get(ORDER_TYPE);
    	String orderTitle = params.get(ORDER_TITLE);
    	String userId = params.get(USER_ID);
    	String userPhone = params.get(USER_PHONE);
    	String price = params.get(PRICE);
    	String status = params.get(STATUS);
    	String createdTime = params.get(CREATED_TIME);
    	String expiredTime = params.get(EXPIRED_TIME);
    	String detailUrl = params.get(DETAIL_URL);
    	
    	if (StringUtils.isNotBlank(orderType)) {
    		orderType = orderType.toUpperCase();
    	}
    	
    	AfOrderDo orderInfo = afOrderService.getThirdOrderInfoByOrderTypeAndOrderNo(OrderType.BOLUOME.getCode(), orderId);
    	
    	AfShopDo shopInfo = afShopService.getShopByPlantNameAndType(ShopPlantFormType.BOLUOME.getCode(), orderType);
    	//新建
    	if (orderInfo == null) {
    		orderInfo = new AfOrderDo();
    		orderInfo.setThirdOrderNo(orderId);
    		orderInfo.setGoodsName(orderTitle);
    		orderInfo.setOrderType(OrderType.BOLUOME.getCode());
    		orderInfo.setSecType(orderType);
    		orderInfo.setUserId(StringUtils.isNotEmpty(userId)  ? Long.parseLong(userId) : null);
    		orderInfo.setMobile(userPhone);
    		//有可能没有价格
    		BigDecimal priceAmount = StringUtils.isNotBlank(price) ? new BigDecimal(price) : BigDecimal.ZERO;
    		orderInfo.setPriceAmount(priceAmount);
    		orderInfo.setGmtPayEnd(StringUtils.isNotEmpty(expiredTime)  ? new Date(System.currentTimeMillis() + Long.parseLong(expiredTime)) : null);
    		orderInfo.setThirdDetailUrl(detailUrl);
    		orderInfo.setStatus(StringUtils.isNotBlank(status) ? BoluomeUtil.parseOrderType(status).getCode() : null);
    		orderInfo.setGmtCreate(StringUtils.isNotEmpty(createdTime) ? new Date(Long.parseLong(createdTime)) : null);
    		orderInfo.setOrderNo(generatorClusterNo.getOrderNo(OrderType.BOLUOME));
    		orderInfo.setUserCouponId(0l);
    		orderInfo.setGoodsId(0l);
    		orderInfo.setOpenId(StringUtils.EMPTY);
    		orderInfo.setGoodsIcon(StringUtils.EMPTY);
    		orderInfo.setCount(0);
    		orderInfo.setPriceAmount(priceAmount);
    		orderInfo.setSaleAmount(priceAmount);
    		orderInfo.setActualAmount(BigDecimal.ZERO);
    		orderInfo.setShopName(StringUtils.EMPTY);
    		orderInfo.setPayStatus(StringUtils.isNotBlank(status) ? BoluomeUtil.parsePayStatus(status).getCode() : null);
    		orderInfo.setPayType(StringUtils.EMPTY);
    		orderInfo.setPayTradeNo(StringUtils.EMPTY);
    		orderInfo.setTradeNo(StringUtils.EMPTY);
    		orderInfo.setMobile(userPhone);
    		orderInfo.setBankId(0l);
    		
    		calculateOrderRebateAmount(orderInfo, shopInfo);
    	} else {
    		
    		BigDecimal priceAmount = StringUtils.isNotBlank(price) ? new BigDecimal(price) : null;
    		orderInfo.setPriceAmount(priceAmount);
    		orderInfo.setSaleAmount(priceAmount);
    		orderInfo.setStatus(StringUtils.isNotBlank(status) ? BoluomeUtil.parseOrderType(status).getCode() : null);
    		orderInfo.setPayStatus(StringUtils.isNotBlank(status) ? BoluomeUtil.parsePayStatus(status).getCode() : null);
    		calculateOrderRebateAmount(orderInfo, shopInfo);
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
    	if (priceAmount == null || priceAmount.equals(BigDecimal.ZERO)) {
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
    
    
}
