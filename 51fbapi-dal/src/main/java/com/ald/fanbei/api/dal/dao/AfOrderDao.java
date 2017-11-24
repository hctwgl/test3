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
	 * 获取用户未支付订单数
	 */
	int getNoFinishOrderCount(Long userId);

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
	 * 获取订单详情，不管是否已经被删除掉
	 * @param rid
	 * @return
	 */
	public AfOrderDo getOrderInfoByIdWithoutDeleted(@Param("rid")Long rid,@Param("userId")Long userId);
	
	
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
	
	
	AfOrderDo getThirdOrderInfoBythirdOrderNo( @Param("thirdOrderNo")String thirdOrderNo);

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
	
	
	List<AfOrderDo> get20170801ExceptionOrder();

	/**
	 * 获得当天有效借款订单数
	 * @return
	 */
	Integer getDealAmount(@Param("userId")Long userId ,@Param("orderType") String orderType);
	
	/**
	 * 获取店铺名未空的订单
	 * @return
	 */
	List<AfOrderDo> getNotShopNameByAgentBuyOrder(@Param("pageNo") Long pageNo);

	/**
	 * 根据goodsId与userId查询订单状态
	 * @return
	 */
	
	List<AfOrderDo> getStatusByGoodsAndUserId(@Param("goodsId")Long goodsId ,@Param("userId") long userId);

	int getOrderCountByStatusAndUserId(AfOrderDo queryCount);

	List<AfOrderDo> getOrderByTimeAndType(@Param("startTime")Date startTime,@Param("endTime")Date endTime);
	
	/**
	 * 根据userId查询老用户订单数
	 * @return
	 */
	Integer getOldUserOrderAmount(@Param("userId") long userId);
	
	/**
	 * 根据goodsId,userId查询已完成订单——新人专享
	 * @return
	 */
	List<AfOrderDo> getOverOrderByGoodsIdAndUserId(@Param("goodsId")Long goodsId,@Param("userId")Long userId);
	
	/**
	 * 根据订单号，查询订单信息
	 * @author gaojb
	 * @Time 2017年11月24日 下午5:10:47
	 * @param orderNo
	 * @return
	 */
	AfOrderDo getOrderByOrderNo(String orderNo);
}
