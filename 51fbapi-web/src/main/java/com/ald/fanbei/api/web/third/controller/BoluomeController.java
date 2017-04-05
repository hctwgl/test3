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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfShopService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeCore;
import com.ald.fanbei.api.biz.service.boluome.BoluomeNotify;
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.ShopPlantFormType;
import com.ald.fanbei.api.common.enums.UnitType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfShopDo;
import com.ald.fanbei.api.web.common.AppResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

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
	
	@Resource
	GeneratorClusterNo generatorClusterNo;
	@Resource
	AfOrderService afOrderService;
	@Resource
	AfShopService afShopService;
	
    @RequestMapping(value = {"/synchOrder","/synchOrderStatus"}, method = RequestMethod.POST,produces = "text/html;charset=UTF-8")
    @ResponseBody
	public String synchOrder(@RequestBody String requestData,HttpServletRequest request, HttpServletResponse response) throws Exception{
    	
    	String uri = StringUtils.EMPTY;
    	if (request.getRequestURI().contains("synchOrderStatus")) {
    		uri = "synchOrderStatus";
    	} else {
    		uri = "synchOrder";
    	}
    	
    	logger.info(uri + "begin requestParams = {}",requestData);
    	JSONObject requestParams = JSON.parseObject(requestData);
        
    	Map<String, String> params = buildOrderParamMap(requestParams);
    	
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
    
    @RequestMapping(value = {"/getOrderId"}, method = RequestMethod.POST,produces = "text/html;charset=UTF-8")
    @ResponseBody
	public String getOrderId(@RequestBody String requestData, HttpServletRequest request, HttpServletResponse response) throws Exception{
    	logger.info("getOrderId begin requestData = {}", requestData);
    	JSONObject requestParams = JSON.parseObject(requestData);
    	
    	Map<String, String> params = buildOrderParamMap(requestParams);
    	
    	AppResponse result = null;
    	try {
    		result = getOrderIdAndRefundcheckSignAndParam(params);
    		Map<String, Object> resultData = new HashMap<String, Object>();
    		String orderId = params.get(BoluomeCore.ORDER_ID);
        	String plantform = params.get(BoluomeCore.PLANT_FORM);
        	AfOrderDo orderInfo = afOrderService.getThirdOrderInfoByOrderTypeAndOrderNo(plantform, orderId);
        	if (orderInfo == null) {
        		result = new AppResponse(FanbeiExceptionCode.BOLUOME_ORDER_NOT_EXIST);
        	}
        	resultData.put("orderId", orderInfo.getRid());
        	result.setData(resultData);
    	} catch (FanbeiException e) {
    		result = new AppResponse(e.getErrorCode());
    	} catch (Exception e) {
    		result = new AppResponse(FanbeiExceptionCode.SYSTEM_ERROR);
		}
    	logger.info("result is {}", JSONObject.toJSONString(result));
    	return JSONObject.toJSONString(result);
    }
    
    @RequestMapping(value = {"/orderRefund"}, method = RequestMethod.POST,produces = "text/html;charset=UTF-8")
    @ResponseBody
	public String orderRefund(@RequestBody String requestData, HttpServletRequest request, HttpServletResponse response) throws Exception{
    	logger.info("orderRefund begin requestData = {}", requestData);
    	JSONObject requestParams = JSON.parseObject(requestData);
    	
    	Map<String, String> params = buildOrderParamMap(requestParams);
    	
    	AppResponse result = new AppResponse(FanbeiExceptionCode.SUCCESS);
    	try {
    		result = getOrderIdAndRefundcheckSignAndParam(params);
    		Map<String, Object> resultData = new HashMap<String, Object>();
    		String orderId = params.get(BoluomeCore.ORDER_ID);
        	String plantform = params.get(BoluomeCore.PLANT_FORM);
        	BigDecimal refundAmount = NumberUtil.objToBigDecimalDefault((params.get(BoluomeCore.AMOUNT)), null);
        	AfOrderDo orderInfo = afOrderService.getThirdOrderInfoByOrderTypeAndOrderNo(plantform, orderId);
        	if (orderInfo == null) {
        		result = new AppResponse(FanbeiExceptionCode.BOLUOME_ORDER_NOT_EXIST);
        	}
        	afOrderService.dealBrandOrderRefund(orderInfo.getRid(), orderInfo.getUserId(), orderInfo.getBankId(), 
        			orderInfo.getOrderNo(), refundAmount, orderInfo.getActualAmount(), orderInfo.getPayType(), orderInfo.getPayTradeNo());
        	result.setData(resultData);
    	} catch (FanbeiException e) {
    		result = new AppResponse(e.getErrorCode());
    	} catch (Exception e) {
    		result = new AppResponse(FanbeiExceptionCode.SYSTEM_ERROR);
		}
    	logger.info("result is {}", JSONObject.toJSONString(result));
    	return JSONObject.toJSONString(result);
    }
    
    private AppResponse getOrderIdAndRefundcheckSignAndParam(Map<String, String> params) {
    	AppResponse result = new AppResponse(FanbeiExceptionCode.SUCCESS);
    	if (StringUtils.isEmpty(params.get(BoluomeCore.ORDER_ID)) || StringUtils.isEmpty(params.get(BoluomeCore.PLANT_FORM))) {
    		throw new FanbeiException(FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
    	}
    	boolean sign = BoluomeNotify.verify(params);
    	if (!sign) {
    		throw new FanbeiException(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR);
    	} 
		return result;
    }
    
    private Map<String, String> buildOrderParamMap(JSONObject requestParams) {
    	Map<String, String> params = new HashMap<String, String>();
    	String orderId = requestParams.getString(BoluomeCore.ORDER_ID);
    	String orderType = requestParams.getString(BoluomeCore.ORDER_TYPE);
    	String orderTitle = requestParams.getString(BoluomeCore.ORDER_TITLE);
    	String userId = requestParams.getString(BoluomeCore.USER_ID);
    	String userPhone = requestParams.getString(BoluomeCore.USER_PHONE);
    	String price = requestParams.getString(BoluomeCore.PRICE);
    	String status = requestParams.getString(BoluomeCore.STATUS);
    	String displayStatus = requestParams.getString(BoluomeCore.DISPLAY_STATUS);
    	String createdTime = requestParams.getString(BoluomeCore.CREATED_TIME);
    	String expiredTime = requestParams.getString(BoluomeCore.EXPIRED_TIME);
    	String detailUrl = requestParams.getString(BoluomeCore.DETAIL_URL);
    	String timestamp = requestParams.getString(BoluomeCore.TIME_STAMP);
    	String plantform = requestParams.getString(BoluomeCore.PLANT_FORM);
    	String sign = requestParams.getString(BoluomeCore.SIGN);
    	String amount = requestParams.getString(BoluomeCore.AMOUNT);
    	
    	params.put(BoluomeCore.ORDER_ID, orderId);
    	params.put(BoluomeCore.ORDER_TYPE, orderType);
    	params.put(BoluomeCore.ORDER_TITLE, orderTitle);
    	params.put(BoluomeCore.USER_ID, userId);
    	params.put(BoluomeCore.USER_PHONE, userPhone);
    	params.put(BoluomeCore.PRICE, price);
    	params.put(BoluomeCore.STATUS, status);
    	params.put(BoluomeCore.DISPLAY_STATUS, displayStatus);
    	params.put(BoluomeCore.CREATED_TIME, createdTime);
    	params.put(BoluomeCore.EXPIRED_TIME, expiredTime);
    	params.put(BoluomeCore.DETAIL_URL, detailUrl);
    	params.put(BoluomeCore.TIME_STAMP, timestamp);
    	params.put(BoluomeCore.PLANT_FORM, plantform);
    	params.put(BoluomeCore.SIGN, sign);
    	params.put(BoluomeCore.AMOUNT, amount);
    	return params;
    }
    
    private AfOrderDo buildOrderInfo(Map<String, String> params) {
    	
    	String orderId = params.get(BoluomeCore.ORDER_ID);
    	//1.已经下单 2.待支付 3.已支付 4.已完成 6.退款中 7.已经退款 8.已取消 9.处理中 11.等待退款
    	String orderType = params.get(BoluomeCore.ORDER_TYPE);
    	String orderTitle = params.get(BoluomeCore.ORDER_TITLE);
    	String userId = params.get(BoluomeCore.USER_ID);
    	String userPhone = params.get(BoluomeCore.USER_PHONE);
    	String price = params.get(BoluomeCore.PRICE);
    	String status = params.get(BoluomeCore.STATUS);
    	String createdTime = params.get(BoluomeCore.CREATED_TIME);
    	String expiredTime = params.get(BoluomeCore.EXPIRED_TIME);
    	String detailUrl = params.get(BoluomeCore.DETAIL_URL);
    	
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
    		orderInfo.setGoodsIcon(shopInfo.getLogo());
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
    
    
}
