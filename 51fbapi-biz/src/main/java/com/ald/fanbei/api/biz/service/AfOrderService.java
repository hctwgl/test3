package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.common.enums.BorrowType;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.*;
import com.alibaba.fastjson.JSONObject;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 
 * @类描述：
 * @author hexin 2017年2月4日下午4:08:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfOrderService {

	/**
	 * 查询用户未完成订单数
	 */
	int getNoFinishOrderCount(Long userId);

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
		BigDecimal money,String mobile,BigDecimal rebateAmount,Long bankId,String clientIp,AfUserAccountDo afUserAccountDo,String blackBox,String bqsBlackBox,String bankChannel);

	
	/**
	 * 手机充值订单充值逻辑
	 * @param orderNo
	 * @param tradeNo
	 * @return
	 */
	void dealMobileChargeOrder(String orderNo,String tradeNo);
	
	/**
     * @方法描述：
     * 
     * @author huyang 2017年4月1日上午9:55:26
     * @param orderNo
     *            开心果交易订单号
     * @param status
     *            交易状态 （SUCCESS：成功 FAILED：失败 ）
     * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
     */
    void notifyMobileChargeOrder(String orderNo, String status);
	
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
	 * 删除订单
	 * @param id
	 * @return
	 */
	void deleteOrder(Long userId, Long orderId);
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
	
	int syncOrderNoWithAgencyUser(Long userId,String orderNo,Long orderId);
	/**
	 * 根据第三方订单类型和订单编号获取订单信息
	 * @param orderType
	 * @param thirdOrderNo
	 * @return
	 */
	AfOrderDo getThirdOrderInfoByOrderTypeAndOrderNo(String orderType, String thirdOrderNo);
	
	AfOrderDo getThirdOrderInfoBythirdOrderNo(String thirdOrderNo);
	
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
	 * 处理回调完成的订单业务逻辑
	 * @author gaojb
	 * @Time 2017年11月27日 上午11:38:00
	 * @param afOrder
	 * @return
	 */
	int callbackCompleteOrder(AfOrderDo afOrder);
	
	/**
	 * 支付菠萝觅订单
	 * @param afOrder
	 * @return
	 */
	Map<String,Object> payBrandOrder(String userName, Long payId, String payType, Long rid, Long userId, String orderNo, String thirdOrderNo, String goodsName, BigDecimal actualAmount, Integer nper, String appName, String ipAddress,String bankChannel);
	/**
	 * 支付菠萝觅订单
	 * @param afOrder
	 * @return
	 */
	Map<String,Object> payBrandOrderOld(Long payId, Long orderId, Long userId, String orderNo, String thirdOrderNo, String goodsName, BigDecimal saleAmount, Integer nper,final String appName,final String ipAddress, String bankChannel);
	
	/**
	 * 处理菠萝觅回调订单 成功
	 * @param payOrderNo
	 * @param tradeNo
	 */
	int dealBrandOrderSucc(String payOrderNo, String tradeNo, String payType);


	AfBorrowDo buildAgentPayBorrow(String name, BorrowType type, Long userId, BigDecimal amount, int nper, String status, Long orderId, String orderNo, String borrowRate, String interestFreeJson, String orderType, String secOrderType);


	
	/**
	 * 处理菠萝觅回调订单 失败
	 * @param payOrderNo
	 * @param tradeNo
	 */
	int dealBrandOrderFail(String payOrderNo, String tradeNo, String payType);
	
	/**
	 * 代买组合支付回调订单
	 * @param payOrderNo
	 * @param tradeNo
	 */
	int dealAgentCpOrderSucc(String payOrderNo, String tradeNo, String payType);
	
	/**
	 * 
	 * @param orderId 订单id
	 * @param userId 用户id
	 * @param bankId 银行卡id
	 * @param orderNo 订单编号
	 * @param thirdOrderNo 第三方订单编号
	 * @param refundAmount 退款金额
	 * @param totalAmount 实际付款金额
	 * @param payType 支付方式
	 * @param payTradeNo 支付流水号
	 * @param refundNo 退款流水号
	 * @param refundSource 退款来源
	 * @return
	 */
	int dealBrandOrderRefund(Long orderId,Long userId,  Long bankId, String orderNo, String thirdOrderNo,
			BigDecimal refundAmount, BigDecimal totalAmount, String payType, String payTradeNo, String refundNo, String refundSource);
	
	
	/**
	 * 获取最近支付号码
	 * @param current
	 * @return
	 */
	String getCurrentLastPayNo(Date current);
	
	/**
	 * 获取订单详情，不管是否已经被删除掉
	 * @param rid
	 * @return
	 */
	public AfOrderDo getOrderInfoByIdWithoutDeleted(Long rid, Long userId);
	
	/**
	 * 获取虚拟商品Code以及Amount
	 * @param orderInfo
	 * @return
	 */
	Map<String, Object> getVirtualCodeAndAmount(AfOrderDo orderInfo);

	BigDecimal checkUsedAmount(Map<String, Object> resultMap, AfOrderDo orderInfo, AfUserAccountSenceDo userAccountInfo);

	/**
	 * 判断是否为虚拟商品
	 * @param orderInfo
	 * @return
	 */
	boolean isVirtualGoods(Map<String, Object> resultMap);
	
	/**
	 * 获取虚拟商品的值
	 * @param orderInfo
	 * @return
	 */
	BigDecimal getVirtualAmount(Map<String, Object> resultMap);
	
	/**
	 * 获取虚拟商品的Code
	 * @param orderInfo
	 * @return
	 */
	String getVirtualCode(Map<String, Object> resultMap);
	
	/**
	 *根据订单id，返回此订单是否可以发起售后申请操作
	 * @param orderId
	 * @return
	 */
	public String isCanApplyAfterSale(Long orderId);

	/**
	 * 获取订单详情
	 * @param rid
	 * @return
	 */
	public AfOrderDo getOrderInfoByPayOrderNo(String payTradeNo);
	
	/**
	 * 处理组合支付失败的情况
	 */
	
	int dealPayCpOrderFail(String payOrderNo, String tradeNo,String payType);
	/**
	 * 处理菠萝觅组合支付失败的情况
	 */

	int dealBrandPayCpOrderFail(String outTradeNo, String tradeNo, String code);

	/**
	 * 获得当天有效借款订单数
	 * @return
	 */
	Integer getDealAmount(Long userId,String orderType);
	
	/**
	 * 根据goodsId与userId查询订单状态
	 * @return
	 */
	
	List<AfOrderDo> getStatusByGoodsAndUserId(long goodsId,long userId);
	
	/**
	 * 订单补偿
	 * @author gaojb
	 * @Time 2017年9月25日 下午4:40:28
	 * @param orderId
	 * @param plantform
	 * @param orderInfo
	 */
    void syncOrderInfo(String orderId, String plantform, AfOrderDo orderInfo);
    int getOrderCountByStatusAndUserId(AfOrderDo queryCount);
    
    /**
     * 获取老用户订单数
     * @param userId
     * @return
     */
    int getOldUserOrderAmount(long userId);

    /**
     * 获取已完成的订单
     * @param userId
     * @return
     */
    List<AfOrderDo> getOverOrderByGoodsIdAndUserId(Long goodsId,Long userId);
    
    /**
     * 获取查询菠萝蜜订单详情地址
     * @author gaojb
     * @Time 2017年11月23日 下午6:23:25
     * @param afOrderDo
     * @return
     */
    String getBoluomeOrderDetailUrl(AfOrderDo orderInfo);
    
    /**
	 * 根据订单号，查询订单信息
	 * @author gaojb
	 * @Time 2017年11月24日 下午5:10:47
	 * @param orderNo
	 * @return
	 */
	AfOrderDo getOrderByOrderNo(String orderNo);

	List<AfOrderDo> getOverOrderByUserId(Long userId);

	List<AfOrderDto> selectSumCountByGoodsId(List<AfEncoreGoodsDto> list);

	Integer selectSumCountByGoodsIdAndType(AfOrderDo afOrderDo);
	
	/**
     * 获取已生成的秒杀订单
     * @param userId
     * @return
     */
	List<AfOrderDo> getDouble12OrderByGoodsIdAndUserId(Long goodsId,Long userId);


	/**
	 * 
	* author chenqiwei
	* @Title: getCountOrderByUserAndOrderType 
	* @Description: 统计订单根据用户和订单类型
	* @param userId
	* @return     
	* return HashMap 返回类型 
	* @throws
	 */
	HashMap getCountPaidOrderByUserAndOrderType(Long userId, String orderType);

	List<AfOrderDo> getSelfsupportOrderByUserIdOrActivityTime(Long userId, String activityTime);
	
	int getAuthShoppingByUserId(Long userId, String activityTime);

	int getCountByUserId(Long rid);

	int updateAuAndUsed(Long orderId, BigDecimal auAmount, BigDecimal usedAmount);

	int addSceneAmount(List<AfOrderSceneAmountDo> list);
	AfInterimAuDo getInterimAuDo(AfOrderDo orderInfo);
	/**
	 *  author chenqiwei
	 *  获取商圈订单商户类型名
	 * */

	String getTradeBusinessNameByOrderId(Long orderid);

	/**
	 * 获取租赁商品是否存在订单
	 * @return
	 */
	HashMap checkLeaseOrder(Long userId, Long goodsId);

	/**
	 * 获取租赁商品是否存在订单
	 * @return
	 */
	JSONObject getLeaseFreeze(Map<String, Object> data, BigDecimal goodsPrice, Long userId);

	/**
	 * 添加租赁订单
	 * @return
	 */
	int addOrderLease(AfOrderLeaseDo afOrderLeaseDo);

	/**
	 * 查询租赁订单
	 * @return
	 */
	AfOrderLeaseDo getOrderLeaseByOrderId(Long orderId);

	/**
	 * 关闭订单
	 * @return
	 */
	int closeOrder(String closedReason,String closedDetail,Long id,Long userId);

	/**
	 * 查询租赁订单
	 * @return
	 */
	LeaseOrderDto getAllOrderLeaseByOrderId(Long orderId,Long userId);

	/**
	 * 查询租赁订单
	 * @return
	 */
	List<LeaseOrderListDto> getOrderLeaseList(Long pageIndex,Long pageSize,Integer type,Long userId);

	/**
	 * 修改租赁订单租期开始时间和结束时间
	 * @return
	 */
	int UpdateOrderLeaseTime(Date gmtStart,Date gmtEnd,Long orderId);

	/**
	 * h5删除订单
	 * @return
	 */
	int UpdateOrderLeaseShow(Long orderId,Long userId);

	/**
	 * 获取租赁协议
	 * @return
	 */
	HashMap getLeaseProtocol(Long orderId);

	void updateIagentStatusByOrderId(Long orderId,String iagentStatus);
	AfOrderDo selectTodayIagentStatus(Long userId,BigDecimal amount);
	List<AfOrderDo> selectTodayIagentStatusCOrders(Long userId,Date gmtCreate);

	/**
	 * 统计用户各个状态的订单数
	 *
	 * @author wangli
	 * @date 2018/4/13 9:58
	 */
	AfOrderCountDto countStatusNum(Long userId);

	int getSelfsupportPaySuccessOrderByUserId(Long userId);

	String getUserFirstBigOrderDate(Long userId, Integer amount);

	String getRefundMsg(AfOrderDo order);
	/**
	 * 查询用户完成订单数
	 */
	int getFinishOrderCount(Long userId);


	int getSignFinishOrderCount(Long userId,Date date);


}
