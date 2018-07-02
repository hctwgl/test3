package com.ald.fanbei.api.dal.domain.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.ald.fanbei.api.dal.domain.AfOrderDo;


public class AfOverdueOrderDto extends AfOrderDo{

	private static final long serialVersionUID = -4461920296006516968L;

	private Long orderId;
	
	private String orderNo;
	
	private String orderType;
	
	private String shopName;
	
	private String goodsName;
	
	private Integer count;
	
	private BigDecimal priceAmount;
	
	private BigDecimal saleAmount;
	
	private BigDecimal actualAmount;
	
	private BigDecimal borrowAmount;
	
	private BigDecimal bankAmount;
	
	private String payType;
	
	private Date gmtPay;
	
	private String isRefund;
	
	private Date refundDate;
	
	private Integer nper;
	
	private String consignee;
	
	private String consigneeMobile;
	
	private String consigneeAddress;
	
	private List<AfOverdueBillDto> borrows;
	
	private String bankCard;
	
	private String bankName;

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public BigDecimal getPriceAmount() {
		return priceAmount;
	}

	public void setPriceAmount(BigDecimal priceAmount) {
		this.priceAmount = priceAmount;
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

	public BigDecimal getBorrowAmount() {
		return borrowAmount;
	}

	public void setBorrowAmount(BigDecimal borrowAmount) {
		this.borrowAmount = borrowAmount;
	}

	public BigDecimal getBankAmount() {
		return bankAmount;
	}

	public void setBankAmount(BigDecimal bankAmount) {
		this.bankAmount = bankAmount;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public Date getGmtPay() {
		return gmtPay;
	}

	public void setGmtPay(Date gmtPay) {
		this.gmtPay = gmtPay;
	}

	public String getIsRefund() {
		return isRefund;
	}

	public void setIsRefund(String isRefund) {
		this.isRefund = isRefund;
	}

	public Date getRefundDate() {
		return refundDate;
	}

	public void setRefundDate(Date refundDate) {
		this.refundDate = refundDate;
	}

	public Integer getNper() {
		return nper;
	}

	public void setNper(Integer nper) {
		this.nper = nper;
	}

	public String getConsignee() {
		return consignee;
	}

	public void setConsignee(String consignee) {
		this.consignee = consignee;
	}

	public String getConsigneeMobile() {
		return consigneeMobile;
	}

	public void setConsigneeMobile(String consigneeMobile) {
		this.consigneeMobile = consigneeMobile;
	}

	public String getConsigneeAddress() {
		return consigneeAddress;
	}

	public void setConsigneeAddress(String consigneeAddress) {
		this.consigneeAddress = consigneeAddress;
	}

	public List<AfOverdueBillDto> getBorrows() {
		return borrows;
	}

	public void setBorrows(List<AfOverdueBillDto> borrows) {
		this.borrows = borrows;
	}

	public String getBankCard() {
		return bankCard;
	}

	public void setBankCard(String bankCard) {
		this.bankCard = bankCard;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

}
