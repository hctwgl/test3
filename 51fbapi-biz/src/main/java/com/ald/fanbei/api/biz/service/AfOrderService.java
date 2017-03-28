package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
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
	Map<String,Object> createMobileChargeOrder(AfUserBankcardDo card,String userName,Long userId, AfUserCouponDto couponDto,
			BigDecimal money,String mobile,BigDecimal rebateAmount,Long bankId,String clientIp,AfUserAccountDo afUserAccountDo);
	
	/**
	 * 手机充值订单充值逻辑
	 * @param orderNo
	 * @param tradeNo
	 * @return
	 */
	void dealMobileChargeOrder(String orderNo,String tradeNo);
	
	/**
	 * 获取当天最近的订单
	 * @param currentDate
	 * @param orderType
	 * @return
	 */
	String getCurrentLastOrderNo(Date currentDate,String orderType);
	
	/**
	 * 获取订单详情
	 * @param id
	 * @return
	 */
	AfOrderDo getOrderInfoById(Long id,Long userId);
	
	/**
	 * 获取订单详情
	 * @param id
	 * @return
	 */
	AfOrderDo getOrderById(Long id);
	
	/**
	 * 获取订单列表
	 * @param pageNo
	 * @param status
	 * @return
	 */
	List<AfOrderDo> getOrderListByStatus(Integer pageNo,String status,Long userId);
	
	/**
	 * 同步用户订单关系
	 * @param userId
	 * @param orderNo
	 * @return
	 */
	int syncOrderNoWithUser(Long userId,String orderNo);
	
	/**
	 * 根据第三方订单类型和订单编号获取订单信息
	 * @param orderType
	 * @param thirdOrderNo
	 * @return
	 */
	AfOrderDo getThirdOrderInfoByOrderTypeAndOrderNo(String orderType, String thirdOrderNo);
	
	/**
	 * 新增订单
	 * @param afOrder
	 * @return
	 */
	int createOrder(AfOrderDo afOrder);
	
	/**
	 * 修改订单信息
	 * @param afOrder
	 * @return
	 */
	int updateOrder(AfOrderDo afOrder);
	
	/**
	 * 处理菠萝么订单
	 * @param afOrder
	 * @return
	 */
	int dealBoluomeOrder(AfOrderDo afOrder);
	
	/**
	 * 支付菠萝觅订单
	 * @param afOrder
	 * @return
	 */
	Map<String,Object> payBrandOrder(AfOrderDo orderInfo, Integer nper);
	
	/**
	 * 处理菠萝觅回调订单 
	 * @param payOrderNo
	 * @param tradeNo
	 */
	int dealBrandOrder(String payOrderNo, String tradeNo, String payType);
	
	/**
	 * 处理菠萝觅退款
	 * @param orderInfo
	 * @return
	 */
	int dealBrandOrderRefund(AfOrderDo orderInfo);
	
}
