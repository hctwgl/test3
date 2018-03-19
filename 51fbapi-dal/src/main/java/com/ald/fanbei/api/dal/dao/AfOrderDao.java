package com.ald.fanbei.api.dal.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.ald.fanbei.api.dal.domain.AfOrderLeaseDo;
import com.ald.fanbei.api.dal.domain.AfOrderSceneAmountDo;
import com.ald.fanbei.api.dal.domain.dto.*;


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
	/**
	 * @param activityTime 
	 * @param userId
	 * judge the first_order during the second time to light the activity
	* @Title: findFirstOrder
	* @Description:
	* @param orderId
	* @return
	* @return int
	* @throws
	 */
	int findFirstOrder(@Param("orderId")Long orderId, @Param("userId")Long userId,@Param("activityTime") String activityTime);

	List<AfOrderDo> getOverOrderByUserId(Long userId);
	List<AfOrderDto> selectSumCountByGoodsId(List<AfEncoreGoodsDto> list);
	List<AfOrderDo> getDouble12OrderByGoodsIdAndUserId(@Param("goodsId")Long goodsId,@Param("userId")Long userId);
	Integer selectSumCountByGoodsIdAndType(AfOrderDo afOrderDo);


	
	//List<AfOrderDo> getShopOrderByUserIdOrActivityTime(@Param("userId")Long userId,@Param("activityTime") String activityTime);

	int getAuthShoppingByUserId(@Param("userId")Long userId,@Param("activityTime") String activityTime);

	HashMap getCountPaidOrderByUserAndOrderType(@Param("userId")Long userId,@Param("orderType") String orderType);

	List<AfOrderDo> getSelfsupportOrderByUserIdOrActivityTime(@Param("userId")Long userId,@Param("activityTime") String activityTime);


	//int getCountFinishBoluomeOrderByUserId(@Param("userId")Long userId);

	int getCountByUserId(@Param("userId")Long userId);

	int getCountFinishBoluomeOrderByUserId(@Param("userId")Long userId,@Param("activityTime") String activityTime);

	int getCountBoluomeOrderByUserIdByActivityTime(@Param("userId")Long userId,@Param("activityTime") String activityTime);


	

	int updateAuAndUsed(@Param("orderId")Long orderId, @Param("auAmount")BigDecimal auAmount, @Param("usedAmount")BigDecimal usedAmount);

	int addSceneAmount(List<AfOrderSceneAmountDo> list);

	List<AfOrderSceneAmountDto> getSceneAmountByOrderId(@Param("orderId")Long orderId);

	/**
	 * 获取租赁商品是否存在订单
	 * @return
	 */
	String checkLeaseOrder(@Param("userId")Long userId ,@Param("goodsId") Long goodsId);

	/**
	 * 添加租赁订单
	 * @return
	 */
	int addOrderLease(AfOrderLeaseDo afOrderLeaseDo);

	/**
	 * 查询租赁订单
	 * @return
	 */
	AfOrderLeaseDo getOrderLeaseByOrderId(@Param("orderId")Long orderId);

	/**
	 * 支付时修改订单押金金额
	 * @return
	 */
	int updateOrderLeaseByPay(@Param("cashDeposit")BigDecimal cashDeposit,@Param("quotadeposit")BigDecimal quotadeposit,@Param("id")Long id);

	/**
	 * 关闭订单
	 * @return
	 */
	int closeOrder(@Param("closedReason")String closedReason,@Param("closedDetail")String closedDetail,@Param("id")Long id);

	/**
	 * 查询租赁订单
	 * @return
	 */
	LeaseOrderDto getAllOrderLeaseByOrderId(@Param("orderId")Long orderId);

	/**
	 * 查询所有租赁订单
	 * @return
	 */
	List<LeaseOrderListDto> getOrderLeaseList(@Param("pageIndex")Long pageIndex,@Param("pageSize")Long pageSize);

	/**
	 * 查询租赁中订单
	 * @return
	 */
	List<LeaseOrderListDto> getOrderLeasingList(@Param("pageIndex")Long pageIndex,@Param("pageSize")Long pageSize);
}
