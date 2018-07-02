package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 实体
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2017-12-10 10:14:21 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfBorrowLegalOrderDo extends AbstractSerial {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键Rid
	 */
	private Long rid;

	/**
	 * 
	 */
	private Date gmtCreate;

	/**
	 * 
	 */
	private Date gmtModified;

	/**
	 * 
	 */
	private Long userId;

	/**
	 * 对应af_borrow_cash.id
	 */
	private Long borrowId;

	/**
	 * '订单序列号'
	 */
	private String orderNo;

	/**
	 * 订单状态：UNPAID-未支付，AWAIT_DELIVER-待发货，DELIVERED-已发货，CONFIRM_RECEIVED-已确认收货，CLOSED-订单关闭
	 */
	private String status;

	/**
	 * 对应商品订单
	 */
	private Long goodsId;

	/**
	 * 订单价格
	 */
	private BigDecimal priceAmount;

	/**
	 * 
	 */
	private Date gmtDeliver;

	/**
	 * 
	 */
	private Date gmtConfirmReceived;

	/**
	 * 
	 */
	private Date gmtClosed;

	/**
	 * 订单关闭原因
	 */
	private String closedDetail;

	/**
	 * 收货地址
	 */
	private String address;

	/**
	 * 商品名称
	 */
	private String goodsName;

	private String logisticsInfo;
	
	private String logisticsCompany;
	
	private String logisticsNo;
	
	private String deliveryUser;
	
	private String deliveryPhone;

	private int smartAddressScore;

	/**
	 * 获取主键Id
	 *
	 * @return rid
	 */
	public Long getRid() {
		return rid;
	}

	/**
	 * 设置主键Id
	 * 
	 * @param 要设置的主键Id
	 */
	public void setRid(Long rid) {
		this.rid = rid;
	}

	/**
	 * 获取
	 *
	 * @return
	 */
	public Date getGmtCreate() {
		return gmtCreate;
	}

	/**
	 * 设置
	 * 
	 * @param gmtCreate
	 *            要设置的
	 */
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	/**
	 * 获取
	 *
	 * @return
	 */
	public Date getGmtModified() {
		return gmtModified;
	}

	/**
	 * 设置
	 * 
	 * @param gmtModified
	 *            要设置的
	 */
	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	/**
	 * 获取
	 *
	 * @return
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * 设置
	 * 
	 * @param userId
	 *            要设置的
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * 获取对应af_borrow_cash.id
	 *
	 * @return 对应af_borrow_cash.id
	 */
	public Long getBorrowId() {
		return borrowId;
	}

	/**
	 * 设置对应af_borrow_cash.id
	 * 
	 * @param borrowId
	 *            要设置的对应af_borrow_cash.id
	 */
	public void setBorrowId(Long borrowId) {
		this.borrowId = borrowId;
	}

	/**
	 * 获取'订单序列号'
	 *
	 * @return '订单序列号'
	 */
	public String getOrderNo() {
		return orderNo;
	}

	/**
	 * 设置'订单序列号'
	 * 
	 * @param orderNo
	 *            要设置的'订单序列号'
	 */
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	/**
	 * 获取订单状态：UNPAID-未支付，AWAIT_DELIVER-待发货，DELIVERED-已发货，CONFIRM_RECEIVED-已确认收货，CLOSED-订单关闭
	 *
	 * @return 订单状态：UNPAID-未支付，AWAIT_DELIVER-待发货，DELIVERED-已发货，CONFIRM_RECEIVED-已确认收货，CLOSED-订单关闭
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * 设置订单状态：UNPAID-未支付，AWAIT_DELIVER-待发货，DELIVERED-已发货，CONFIRM_RECEIVED-已确认收货，CLOSED-订单关闭
	 * 
	 * @param status
	 *            要设置的订单状态：UNPAID-未支付，AWAIT_DELIVER-待发货，DELIVERED-已发货，CONFIRM_RECEIVED-已确认收货，CLOSED-订单关闭
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * 获取对应商品订单
	 *
	 * @return 对应商品订单
	 */
	public Long getGoodsId() {
		return goodsId;
	}

	/**
	 * 设置对应商品订单
	 * 
	 * @param goodsId
	 *            要设置的对应商品订单
	 */
	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	/**
	 * 获取订单价格
	 *
	 * @return 订单价格
	 */
	public BigDecimal getPriceAmount() {
		return priceAmount;
	}

	/**
	 * 设置订单价格
	 * 
	 * @param priceAmount
	 *            要设置的订单价格
	 */
	public void setPriceAmount(BigDecimal priceAmount) {
		this.priceAmount = priceAmount;
	}

	/**
	 * 获取
	 *
	 * @return
	 */
	public Date getGmtDeliver() {
		return gmtDeliver;
	}

	/**
	 * 设置
	 * 
	 * @param gmtDeliver
	 *            要设置的
	 */
	public void setGmtDeliver(Date gmtDeliver) {
		this.gmtDeliver = gmtDeliver;
	}

	/**
	 * 获取
	 *
	 * @return
	 */
	public Date getGmtConfirmReceived() {
		return gmtConfirmReceived;
	}

	/**
	 * 设置
	 * 
	 * @param gmtConfirmReceived
	 *            要设置的
	 */
	public void setGmtConfirmReceived(Date gmtConfirmReceived) {
		this.gmtConfirmReceived = gmtConfirmReceived;
	}

	/**
	 * 获取
	 *
	 * @return
	 */
	public Date getGmtClosed() {
		return gmtClosed;
	}

	/**
	 * 设置
	 * 
	 * @param gmtClosed
	 *            要设置的
	 */
	public void setGmtClosed(Date gmtClosed) {
		this.gmtClosed = gmtClosed;
	}

	/**
	 * 获取订单关闭原因
	 *
	 * @return 订单关闭原因
	 */
	public String getClosedDetail() {
		return closedDetail;
	}

	/**
	 * 设置订单关闭原因
	 * 
	 * @param closedDetail
	 *            要设置的订单关闭原因
	 */
	public void setClosedDetail(String closedDetail) {
		this.closedDetail = closedDetail;
	}

	/**
	 * 获取收货地址
	 *
	 * @return 收货地址
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * 设置收货地址
	 * 
	 * @param address
	 *            要设置的收货地址
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * 获取商品名称
	 *
	 * @return 商品名称
	 */
	public String getGoodsName() {
		return goodsName;
	}

	/**
	 * 设置商品名称
	 * 
	 * @param goodsName
	 *            要设置的商品名称
	 */
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getLogisticsInfo() {
		return logisticsInfo;
	}

	public void setLogisticsInfo(String logisticsInfo) {
		this.logisticsInfo = logisticsInfo;
	}

	public String getLogisticsCompany() {
		return logisticsCompany;
	}

	public void setLogisticsCompany(String logisticsCompany) {
		this.logisticsCompany = logisticsCompany;
	}

	public String getLogisticsNo() {
		return logisticsNo;
	}

	public void setLogisticsNo(String logisticsNo) {
		this.logisticsNo = logisticsNo;
	}

	

	public String getDeliveryUser() {
		return deliveryUser;
	}

	public void setDeliveryUser(String deliveryUser) {
		this.deliveryUser = deliveryUser;
	}

	public String getDeliveryPhone() {
		return deliveryPhone;
	}

	public void setDeliveryPhone(String deliveryPhone) {
		this.deliveryPhone = deliveryPhone;
	}

	public int getSmartAddressScore() {
		return smartAddressScore;
	}

	public void setSmartAddressScore(int smartAddressScore) {
		this.smartAddressScore = smartAddressScore;
	}
}