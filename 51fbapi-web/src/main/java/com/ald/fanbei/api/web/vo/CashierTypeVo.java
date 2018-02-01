package com.ald.fanbei.api.web.vo;

import com.ald.fanbei.api.common.enums.YesNoStatus;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class CashierTypeVo {
    private String name;
    private String status = YesNoStatus.NO.getCode();
    private String reasonType;
    private String isVirtualGoods;
    private BigDecimal payAmount;
    private BigDecimal useableAmount;
    private BigDecimal totalVirtualAmount;
    private String categoryName;

    private String overduedCode;
    private Long billId;
    private BigDecimal jfbAmount;// 集分宝
    private BigDecimal userRebateAmount;// 用户返利余额
    private BigDecimal repaymentAmount;// 应还金额
    private BigDecimal virtualGoodsUsableAmount;
    private Long borrowId;// 借钱id

    public CashierTypeVo(String status) {
	this.status = status;
    }

    public CashierTypeVo(String status, String reasonType) {
	this.status = status;
	this.reasonType = reasonType;
    }

    public CashierTypeVo(String status, String reasonType, HashMap data) {
	this.status = status;
	this.reasonType = reasonType;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getStatus() {
	return status;
    }

    public void setStatus(String status) {
	this.status = status;
    }

    public String getReasonType() {
	return reasonType;
    }

    public void setReasonType(String reasonType) {
	this.reasonType = reasonType;
    }

    public BigDecimal getPayAmount() {
	return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
	this.payAmount = payAmount;
    }

    public BigDecimal getUseableAmount() {
	return useableAmount;
    }

    public void setUseableAmount(BigDecimal useableAmount) {
	this.useableAmount = useableAmount;
    }

    public BigDecimal getTotalVirtualAmount() {
	return totalVirtualAmount;
    }

    public void setTotalVirtualAmount(BigDecimal totalVirtualAmount) {
	this.totalVirtualAmount = totalVirtualAmount;
    }

    public String getCategoryName() {
	return categoryName;
    }

    public void setCategoryName(String categoryName) {
	this.categoryName = categoryName;
    }

    public String getIsVirtualGoods() {
	return isVirtualGoods;
    }

    public void setIsVirtualGoods(String isVirtualGoods) {
	this.isVirtualGoods = isVirtualGoods;
    }

    public String getOverduedCode() {
	return overduedCode;
    }

    public void setOverduedCode(String overduedCode) {
	this.overduedCode = overduedCode;
    }

    public Long getBillId() {
	return billId;
    }

    public void setBillId(Long billId) {
	this.billId = billId;
    }

    public BigDecimal getJfbAmount() {
	return jfbAmount;
    }

    public void setJfbAmount(BigDecimal jfbAmount) {
	this.jfbAmount = jfbAmount;
    }

    public BigDecimal getUserRebateAmount() {
	return userRebateAmount;
    }

    public void setUserRebateAmount(BigDecimal userRebateAmount) {
	this.userRebateAmount = userRebateAmount;
    }

    public BigDecimal getRepaymentAmount() {
	return repaymentAmount;
    }

    public void setRepaymentAmount(BigDecimal repaymentAmount) {
	this.repaymentAmount = repaymentAmount;
    }

    public Long getBorrowId() {
	return borrowId;
    }

    public void setBorrowId(Long borrowId) {
	this.borrowId = borrowId;
    }

    public BigDecimal getVirtualGoodsUsableAmount() {
	return virtualGoodsUsableAmount;
    }

    public void setVirtualGoodsUsableAmount(BigDecimal virtualGoodsUsableAmount) {
	this.virtualGoodsUsableAmount = virtualGoodsUsableAmount;
    }
}
