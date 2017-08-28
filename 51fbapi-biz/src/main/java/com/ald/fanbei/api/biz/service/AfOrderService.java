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
	int deleteOrder(Long id);
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
	 * 支付菠萝觅订单
	 * @param afOrder
	 * @return
	 */
	Map<String,Object> payBrandOrder(Long payId, String payType, Long rid, Long userId, String orderNo, String thirdOrderNo, String goodsName, BigDecimal actualAmount, Integer nper, String appName, String ipAddress);
	/**
	 * 支付菠萝觅订单
	 * @param afOrder
	 * @return
	 */
	Map<String,Object> payBrandOrderOld(Long payId, Long orderId, Long userId, String orderNo, String thirdOrderNo, String goodsName, BigDecimal saleAmount, Integer nper,final String appName,final String ipAddress);
	
	/**
	 * 处理菠萝觅回调订单 成功
	 * @param payOrderNo
	 * @param tradeNo
	 */
	int dealBrandOrderSucc(String payOrderNo, String tradeNo, String payType);
	
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
	Integer getDealAmount(Long userId);
	
}