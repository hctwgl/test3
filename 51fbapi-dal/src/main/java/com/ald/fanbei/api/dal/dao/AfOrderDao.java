package com.ald.fanbei.api.dal.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.query.AfOrderQuery;

/**
 * @类描述：
 * @author hexin 2017年2月16日下午15:07:24
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfOrderDao {

	/**
	 * 新增订单
	 * @param afOrder
	 * @return
	 */
	int createOrder(AfOrderDo afOrder);
	
	/**
	 * 订单列表
	 * @param orderList
	 * @return
	 */
	int createOrderList(@Param("items")List<AfOrderDo> orderList);
	
	/**
	 * 修改订单
	 * @param afOrder
	 * @return
	 */
	int updateOrderByOrderNo(AfOrderDo afOrder);
	

	
	/**
	 * 修改订单
	 * @param afOrder
	 * @return
	 */
	int updateOrderByOutTradeNo(AfOrderDo afOrder);
	
	/**
	 * @param startDate
	 * @param endDate
	 * @param orderType
	 * @return
	 */
	public String getCurrentLastOrderNo(@Param("startDate")Date startDate,@Param("endDate")Date endDate, @Param("orderType")String orderType);
	
	/**
	 * 获取订单详情
	 * @param rid
	 * @return
	 */
	public AfOrderDo getOrderInfoById(@Param("rid")Long rid,@Param("userId")Long userId);
	/**
	 * 获取订单详情
	 * @param rid
	 * @return
	 */
	public AfOrderDo getOrderInfoByOrderNoAndUserId(@Param("orderNo")String orderNo,@Param("userId")Long userId);
	/**
	 * 获取订单详情
	 * @param rid
	 * @return
	 */
	public AfOrderDo getOrderInfoByOrderNo(@Param("orderNo")String orderNo);
	
	/**
	 * 获取订单详情
	 * @param rid
	 * @return
	 */
	public AfOrderDo getOrderInfoByRiskOrderNo(@Param("riskOrderNo")String riskOrderNo);
	
	/**
	 * 获取订单详情
	 * @param rid
	 * @return
	 */
	public AfOrderDo getOrderInfoByPayOrderNo(@Param("payTradeNo")String payTradeNo);
	
	/**
	 * 获取订单列表
	 * @param query
	 * @return
	 */
	public List<AfOrderDo> getOrderListByStatus(AfOrderQuery query);
	
	/**
	 * 根据第三方订单类型和订单编号获取订单信息
	 * @param orderNo
	 * @return
	 */
	AfOrderDo getThirdOrderInfoByOrderTypeAndOrderNo(@Param("orderType")String orderType, @Param("thirdOrderNo")String thirdOrderNo);
	
	/**
	 * 修改订单信息
	 * @param afOrder
	 * @return
	 */
	int updateOrder(AfOrderDo afOrder);
	
	/**
	 * 获取订单详情
	 * @param id
	 * @return
	 */
	AfOrderDo getOrderById(@Param("orderId") Long id);
	
	/**
	 * 删除订单
	 * @param id
	 * @return
	 */
	int deleteOrder(@Param("rid") Long id);
	
	/**
	 * 获取最近支付编号
	 * @param current
	 * @return
	 */
	String getCurrentLastPayNo(@Param("startDate")Date startDate,@Param("endDate")Date endDate);
	
	
	List<AfOrderDo> getNoBorrowOrder();
	
}
