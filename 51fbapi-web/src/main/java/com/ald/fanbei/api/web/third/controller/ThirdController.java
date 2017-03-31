package com.ald.fanbei.api.web.third.controller;

import java.math.BigDecimal;
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
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.biz.service.boluome.ThirdCore;
import com.ald.fanbei.api.biz.service.boluome.ThirdNotify;
import com.ald.fanbei.api.common.enums.PushStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.web.common.AppResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 提供给第三方调用接口
 * @类描述：
 * @author xiaotianjian 2017年3月24日下午1:54:46
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/thirdApi")
public class ThirdController{
	
    protected final Logger   logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	AfOrderService afOrderService;
	@Resource
	AfShopService afShopService;
	@Resource
	BoluomeUtil boluomeUtil;
	
    @RequestMapping(value = {"/orderRefund"}, method = RequestMethod.POST,produces = "text/html;charset=UTF-8")
    @ResponseBody
	public String orderRefund(@RequestBody String requestData, HttpServletRequest request, HttpServletResponse response) throws Exception{
    	logger.info("orderRefund begin requestData = {}", requestData);
    	JSONObject requestParams = JSON.parseObject(requestData);
    	
    	Map<String, String> params = buildParam(requestParams);
    	
    	AppResponse result = null;
    	try {
    		result = checkSignAndParam(params);
    		Map<String, Object> resultData = new HashMap<String, Object>();
    		String orderId = params.get(ThirdCore.ORDER_ID);
        	String plantform = params.get(ThirdCore.PLANT_FORM);
        	BigDecimal refundAmount = NumberUtil.objToBigDecimalDefault(params.get(ThirdCore.AMOUNT), null);
        	AfOrderDo orderInfo = afOrderService.getThirdOrderInfoByOrderTypeAndOrderNo(plantform, orderId);
        	if (orderInfo == null) {
        		throw new FanbeiException(FanbeiExceptionCode.BOLUOME_ORDER_NOT_EXIST);
        	} 
        	
    		afOrderService.dealBrandOrderRefund(orderInfo.getRid(), orderInfo.getUserId(), orderInfo.getBankId(), 
    				orderInfo.getOrderNo(), refundAmount, orderInfo.getActualAmount(), orderInfo.getPayType(), orderInfo.getPayTradeNo());
    				
			boluomeUtil.pushRefundStatus(orderInfo.getRid(), orderInfo.getOrderNo(), orderInfo.getThirdOrderNo(), PushStatus.PAY_SUC, orderInfo.getUserId(), orderInfo.getActualAmount());
        	result.setData(resultData);
    	} catch (FanbeiException e) {
    		result = new AppResponse(e.getErrorCode());
    	} catch (Exception e) {
    		result = new AppResponse(FanbeiExceptionCode.SYSTEM_ERROR);
		}
    	logger.info("result is {}", JSONObject.toJSONString(result));
    	return JSONObject.toJSONString(result);
    }
    
    private AppResponse checkSignAndParam(Map<String, String> params) {
    	AppResponse result = new AppResponse(FanbeiExceptionCode.SUCCESS);
    	if (StringUtils.isEmpty(params.get(ThirdCore.ORDER_ID)) || StringUtils.isEmpty(params.get(ThirdCore.PLANT_FORM))
    			|| StringUtils.isEmpty(params.get(ThirdCore.AMOUNT)) || StringUtils.isEmpty(params.get(ThirdCore.TIME_STAMP))
    			|| StringUtils.isEmpty(params.get(ThirdCore.USER_ID))) {
    		throw new FanbeiException(FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
    	}
    	boolean sign = ThirdNotify.verify(params);
    	if (!sign) {
    		throw new FanbeiException(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR);
    	} 
		return result;
    }
    
    private Map<String, String> buildParam(JSONObject requestParams) {
    	Map<String, String> params = new HashMap<String, String>();
    	String orderId = requestParams.getString(ThirdCore.ORDER_ID);
    	String userId = requestParams.getString(ThirdCore.USER_ID);
    	String timestamp = requestParams.getString(ThirdCore.TIME_STAMP);
    	String plantform = requestParams.getString(ThirdCore.PLANT_FORM);
    	String amount = requestParams.getString(ThirdCore.AMOUNT);
    	String sign = requestParams.getString(ThirdCore.SIGN);
    	String appKey = requestParams.getString(ThirdCore.APP_KEY);
    	
    	params.put(ThirdCore.ORDER_ID, orderId);
    	params.put(ThirdCore.USER_ID, userId);
    	params.put(ThirdCore.TIME_STAMP, timestamp);
    	params.put(ThirdCore.PLANT_FORM, plantform);
    	params.put(ThirdCore.SIGN, sign);
    	params.put(ThirdCore.APP_KEY, appKey);
    	params.put(ThirdCore.AMOUNT, amount);
    	return params;
    }
    
    
}
