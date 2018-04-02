package com.ald.fanbei.api.biz.bo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 
* @ClassName: UpsCollectBo 
* @Description: 支持快捷支付，缓存支付请求数据 
* @author gaojb
* @date 2018年4月2日 上午10:10:57 
*
 */
public class UpsCollectBo implements Serializable{

    private static final long serialVersionUID = 1L;
    
    String orderNo;
    BigDecimal amount;
    String userNo;
    String realName;
    String phone;
    String bankCode;
    String cardNo;
    String certNo;
    String purpose;
    String remark;
    String clientType;
    String merPriv;
    String bankPayType;
    String productName;

    public String getOrderNo() {
	return orderNo;
    }

    public void setOrderNo(String orderNo) {
	this.orderNo = orderNo;
    }

    public BigDecimal getAmount() {
	return amount;
    }

    public void setAmount(BigDecimal amount) {
	this.amount = amount;
    }

    public String getUserNo() {
	return userNo;
    }

    public void setUserNo(String userNo) {
	this.userNo = userNo;
    }

    public String getRealName() {
	return realName;
    }

    public void setRealName(String realName) {
	this.realName = realName;
    }

    public String getPhone() {
	return phone;
    }

    public void setPhone(String phone) {
	this.phone = phone;
    }

    public String getBankCode() {
	return bankCode;
    }

    public void setBankCode(String bankCode) {
	this.bankCode = bankCode;
    }

    public String getCardNo() {
	return cardNo;
    }

    public void setCardNo(String cardNo) {
	this.cardNo = cardNo;
    }

    public String getCertNo() {
	return certNo;
    }

    public void setCertNo(String certNo) {
	this.certNo = certNo;
    }

    public String getPurpose() {
	return purpose;
    }

    public void setPurpose(String purpose) {
	this.purpose = purpose;
    }

    public String getRemark() {
	return remark;
    }

    public void setRemark(String remark) {
	this.remark = remark;
    }

    public String getClientType() {
	return clientType;
    }

    public void setClientType(String clientType) {
	this.clientType = clientType;
    }

    public String getMerPriv() {
	return merPriv;
    }

    public void setMerPriv(String merPriv) {
	this.merPriv = merPriv;
    }

    public String getBankPayType() {
	return bankPayType;
    }

    public void setBankPayType(String bankPayType) {
	this.bankPayType = bankPayType;
    }

    public String getProductName() {
	return productName;
    }

    public void setProductName(String productName) {
	this.productName = productName;
    }

    @Override
    public String toString() {
	return "UpsCollectBo [orderNo=" + orderNo + ", amount=" + amount + ", userNo=" + userNo + ", realName=" + realName + ", phone=" + phone + ", bankCode=" + bankCode + ", cardNo=" + cardNo + ", certNo=" + certNo + ", purpose=" + purpose + ", remark=" + remark + ", clientType=" + clientType + ", merPriv=" + merPriv + ", bankPayType=" + bankPayType + ", productName=" + productName + "]";
    }

}
