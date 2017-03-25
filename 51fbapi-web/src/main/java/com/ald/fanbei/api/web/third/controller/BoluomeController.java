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

import com.ald.fanbei.api.biz.service.boluome.BoluomeCore;
import com.ald.fanbei.api.biz.service.boluome.BoluomeNotify;
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.dal.dao.AfOrderDao;
import com.ald.fanbei.api.dal.domain.AfOrderDo;

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
	AfOrderDao afOrderDao;
	
    @RequestMapping(value = {"/synchOrder"}, method = RequestMethod.POST)
    @ResponseBody
	public String synchOrder(HttpServletRequest request, HttpServletResponse response){
    	logger.info("synchOrder params = {}",request.getParameterMap());
    	Map<String, String> params = buildOrderParamMap(request);
    	Map<String, String> filterParams = BoluomeCore.paraFilter(params);
    	boolean sign = BoluomeNotify.verify(filterParams);
    	String retunStr = StringUtils.EMPTY;
    	if (sign) {
    		retunStr = response.getStatus() + StringUtils.EMPTY;
    		AfOrderDo orderInfo = buildOrderInfo(params);
    		afOrderDao.createOrder(orderInfo);
    	}  else {
    		retunStr = "error";
    	}
    	logger.info("synchOrder complete, result = {}, sign = {}",response.getStatus(), sign);
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
    	AfOrderDo orderInfo = new AfOrderDo();
    	
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
    	
    	orderInfo.setThirdOrderNo(orderId);
    	orderInfo.setGoodsName(orderTitle);
    	orderInfo.setOrderType(OrderType.BOLUOME.getCode());
    	orderInfo.setSecType(orderType);
    	orderInfo.setUserId(StringUtils.isNotEmpty(userId)  ? Long.parseLong(userId) : null);
    	orderInfo.setMobile(userPhone);
    	//有可能没有价格
    	BigDecimal priceAmount = StringUtils.isNotBlank(price) ? new BigDecimal(price) : BigDecimal.ZERO;
    	orderInfo.setPriceAmount(priceAmount);
    	orderInfo.setGmtPayEnd(StringUtils.isNotEmpty(userId)  ? new Date(Long.parseLong(expiredTime)) : null);
    	orderInfo.setThirdDetailUrl(detailUrl);
    	orderInfo.setStatus(BoluomeUtil.parseOrderType(status).getCode());
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
    	orderInfo.setPayStatus();
    	return orderInfo;
    }

}
