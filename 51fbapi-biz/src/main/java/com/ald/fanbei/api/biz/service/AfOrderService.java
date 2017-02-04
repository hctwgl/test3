package com.ald.fanbei.api.biz.service;

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
	 * 买家点击退款按钮后促发
	 * @param content
	 * @return
	 */
	int updateOrderTradeRefundCreated(String content);
	
	/**
	 * 退款成功
	 * @param content
	 * @return
	 */
	int updateOrderTradeRefundSuccess(String content);
	
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
}
