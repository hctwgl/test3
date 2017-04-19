package com.ald.fanbei.api.web.vo;

import java.math.BigDecimal;

import com.ald.fanbei.api.common.AbstractSerial;


public class AfAgentOrederDetailInforVo extends AbstractSerial {

	
	private static final long serialVersionUID = -5983291447985020112L;
	
	private String goodName; // 商品名称
	private String goodsIcon; // 商品图片
	private Integer count; // 商品
	private String priceAmount; // 商品价格
	private BigDecimal rebateAmount; // 返利金额
	private String consignee; // 收件人
	private String province; // 省
	private String city; // 城市
	private String address; // 地址
	private String mobile; 	// 用户电话
	private String capture; // 用户截图
	private String remark; // 用户留言
	private String gmtCreate; // 订单生成的时间
	private String payStatus; // 支付的状态
	private String payType; // 支付类型
	private String payTradeNo; // 支付订单号
	private String gmtPay; // 支付时间
	private String status; // 订单的状态
	private String gmtRebated; // 返利时间
	private String gmtModified; //最后的修改时间
	private String agentMessage; // 代买人员的备注
	private String gmtAgentBuy; // 代买时间	
	
	
	public String getGmtPay() {
		return gmtPay;
	}
	public void setGmtPay(String gmtPay) {
		this.gmtPay = gmtPay;
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
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
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
	public String getGmtModified() {
		return gmtModified;
	}
	public void setGmtModified(String gmtModified) {
		this.gmtModified = gmtModified;
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
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public String getPriceAmount() {
		return priceAmount;
	}
	public void setPriceAmount(String priceAmount) {
		this.priceAmount = priceAmount;
	}
	public BigDecimal getRebateAmount() {
		return rebateAmount;
	}
	public void setRebateAmount(BigDecimal rebateAmount) {
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
	
	
	

}
