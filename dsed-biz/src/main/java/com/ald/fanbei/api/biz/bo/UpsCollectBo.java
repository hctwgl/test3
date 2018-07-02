package com.ald.fanbei.api.biz.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import com.ald.fanbei.api.dal.domain.AfRepaymentDo;

/**
 * 
 * @ClassName: UpsCollectBo
 * @Description: 支持快捷支付，缓存支付请求数据
 * @author gaojb
 * @date 2018年4月2日 上午10:10:57
 *
 */
public class UpsCollectBo implements Serializable {

    private static final long serialVersionUID = 1L;

    public UpsCollectBo() {

    }

    public UpsCollectBo(HashMap<String,Object> bank, String orderNo, BigDecimal amount, String userNo, String realName, String phone, String bankCode, String cardNo, String certNo, String purpose, String remark, String clientType, String merPriv, String bankPayType, String productName) {
	this.orderNo = orderNo;
	this.amount = amount;
	this.userNo = userNo;
	this.realName = realName;
	this.phone = phone;
	this.bankCode = bankCode;
	this.cardNo = cardNo;
	this.certNo = certNo;
	this.purpose = purpose;
	this.remark = remark;
	this.clientType = clientType;
	this.merPriv = merPriv;
	this.bankPayType = bankPayType;
	this.productName = productName;
	this.cardId = cardId;
	this.bank = bank;
    }

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
    Long cardId;
    HashMap<String,Object> bank;

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

    public Long getCardId() {
	return cardId;
    }

    public void setCardId(Long cardId) {
	this.cardId = cardId;
    }

    public HashMap<String,Object> getBank() {
        return bank;
    }

    public void setBank(HashMap<String,Object> bank) {
        this.bank = bank;
    }
}
