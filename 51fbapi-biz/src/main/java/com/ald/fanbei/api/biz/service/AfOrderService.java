package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;


/**
 * 
 * @类描述：
 * @author hexin 2017年2月4日下午4:08:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfOrderService {

	/**
	 * 创建订单消息(下单未付款)
	 */
	int createOrderTrade(String content);
	
	/**
	 * 交易成功消息(确认收货后)
	 * @param content
	 * @return
	 */
	int updateOrderTradeSuccess(String content);
	
	/**
	 * 付款成功(下单已付款)
	 * @param content
	 * @return
	 */
	int updateOrderTradePaidDone(String content);
	
	/**
	 * 交易关闭(包括退款后交易关闭和创建订单后交易关闭)4：退款后交易关闭；8：创建订单后交易关闭
	 * @param content
	 * @return
	 */
	int updateOrderTradeClosed(String content);
	
	/**
	 * 手机充值订单创建
	 * @param userId
	 * @param couponDto
	 * @param money
	 * @param mobile
	 * @param rebateAmount
	 * @return
	 */
	int createMobileChargeOrder(Long userId, AfUserCouponDto couponDto,BigDecimal money,String mobile,BigDecimal rebateAmount);
	
	/**
	 * 获取当天最近的订单
	 * @param currentDate
	 * @param orderType
	 * @return
	 */
	String getCurrentLastOrderNo(Date currentDate,String orderType);
}
