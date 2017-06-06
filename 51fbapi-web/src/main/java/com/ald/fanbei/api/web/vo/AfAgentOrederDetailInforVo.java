package com.ald.fanbei.api.web.vo;



import java.math.BigDecimal;

import com.ald.fanbei.api.common.AbstractSerial;


public class AfAgentOrederDetailInforVo extends AbstractSerial {

	
	private static final long serialVersionUID = -5983291447985020112L;
	
	private String goodName; // 商品名称
	private String goodsIcon; // 商品图片
	private Long count; // 商品数量
	private String rebateAmount; // 返利金额
	private String consignee; // 收件人
	private String province; // 省
	private String city; // 城市
	private String county; // 区域
	private String address; // 地址
	private String mobile; 	// 用户电话
	private String capture; // 用户截图
	private String remark; // 用户留言
	private String gmtCreate; // 订单生成的时间
	private String payType; // 支付方式
	private String payStatus; // 支付的状态
	private String payTradeNo; // 支付订单号
	private String gmtPay; // 支付时间
	private String status; // 订单的状态
	private String gmtRebated; // 返利时间
	private String gmtFinished; // 订单完成时间
	private String agentMessage; // 代买人员的备注
	private String gmtAgentBuy; // 代买时间
	private String closedReason; // 关闭理由
	private String cancelReason;// 取消理由
	private String gmtClosed; // 用户取消时间
	private String numId; // 商品的numId
	private BigDecimal saleAmount; // 商品的价格
	private BigDecimal actualAmount; // 用户填写的金额
	private Integer nper;
	
	
	public String getCancelReason() {
		return cancelReason;
	}
	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}
	public BigDecimal getSaleAmount() {
		return saleAmount;
	}
	public void setSaleAmount(BigDecimal saleAmount) {
		this.saleAmount = saleAmount;
	}
	public BigDecimal getActualAmount() {
		return actualAmount;
	}
	public void setActualAmount(BigDecimal actualAmount) {
		this.actualAmount = actualAmount;
	}
	public String getNumId() {
		return numId;
	}
	public void setNumId(String numId) {
		this.numId = numId;
	}
	public String getGmtClosed() {
		return gmtClosed;
	}
	public void setGmtClosed(String gmtClosed) {
		this.gmtClosed = gmtClosed;
	}
	public String getClosedReason() {
		return closedReason;
	}
	public void setClosedReason(String closedReason) {
		this.closedReason = closedReason;
	}
	public String getGmtPay() {
		return gmtPay;
	}
	public void setGmtPay(String gmtPay) {
		this.gmtPay = gmtPay;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public String getCounty() {
		return county;
	}
	public void setCounty(String county) {
		this.county = county;
	}
	public String getGmtRebated() {
		return gmtRebated;
	}
	public void setGmtRebated(String gmtRebated) {
		this.gmtRebated = gmtRebated;
	}
	public String getPayStatus() {
		return payStatus;
	}
	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}
	public String getPayTradeNo() {
		return payTradeNo;
	}
	public void setPayTradeNo(String payTradeNo) {
		this.payTradeNo = payTradeNo;
	}
	public String getConsignee() {
		return consignee;
	}
	public void setConsignee(String consignee) {
		this.consignee = consignee;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getGmtFinished() {
		return gmtFinished;
	}
	public void setGmtFinished(String gmtFinished) {
		this.gmtFinished = gmtFinished;
	}
	public String getAgentMessage() {
		return agentMessage;
	}
	public void setAgentMessage(String agentMessage) {
		this.agentMessage = agentMessage;
	}
	public String getGmtAgentBuy() {
		return gmtAgentBuy;
	}
	public void setGmtAgentBuy(String gmtAgentBuy) {
		this.gmtAgentBuy = gmtAgentBuy;
	}
	public String getGoodName() {
		return goodName;
	}
	public void setGoodName(String goodName) {
		this.goodName = goodName;
	}
	public String getGoodsIcon() {
		return goodsIcon;
	}
	public void setGoodsIcon(String goodsIcon) {
		this.goodsIcon = goodsIcon;
	}
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	
	public String getRebateAmount() {
		return rebateAmount;
	}
	public void setRebateAmount(String rebateAmount) {
		this.rebateAmount = rebateAmount;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getCapture() {
		return capture;
	}
	public void setCapture(String capture) {
		this.capture = capture;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getGmtCreate() {
		return gmtCreate;
	}
	public void setGmtCreate(String gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	/**
	 * @return the nper
	 */
	public Integer getNper() {
		return nper;
	}
	/**
	 * @param nper the nper to set
	 */
	public void setNper(Integer nper) {
		this.nper = nper;
	}
	
	
	

}
