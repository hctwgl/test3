package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;

import java.util.Date;

/**
 * 
 * @类描述：用户银行卡表
 * @author hexin 2017年2月8日下午17:13:28
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfUserBankcardDo extends AbstractSerial {

    /**
	 * 
	 */
    private static final long serialVersionUID = -5414119532357715149L;
    private Long rid;
    private Date gmtCreate;
    private Date gmtModified;
    private Long userId;
    private String mobile;
    private String bankCode;
    private String cardNumber;
    private String isMain;
    private String bankName;
    private String status;
    private String bankIcon;
    private String isValid;
    private String bankChannel;

    private String payType;

    private Integer cardType;
    private String validDate;
    private String safeCode;

    public Integer getCardType() {
        return cardType;
    }

    public void setCardType(Integer cardType) {
        this.cardType = cardType;
    }

    public String getValidDate() {
        return validDate;
    }

    public void setValidDate(String validDate) {
        this.validDate = validDate;
    }

    public String getSafeCode() {
        return safeCode;
    }

    public void setSafeCode(String safeCode) {
        this.safeCode = safeCode;
    }

    public String getIsValid() {
	return isValid;
    }

    public void setIsValid(String isValid) {
	this.isValid = isValid;
    }

    public Long getRid() {
	return rid;
    }

    public void setRid(Long rid) {
	this.rid = rid;
    }

    public Date getGmtCreate() {
	return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
	this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
	return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
	this.gmtModified = gmtModified;
    }

    public Long getUserId() {
	return userId;
    }

    public void setUserId(Long userId) {
	this.userId = userId;
    }

    public String getMobile() {
	return mobile;
    }

    public void setMobile(String mobile) {
	this.mobile = mobile;
    }

    public String getCardNumber() {
	return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
	this.cardNumber = cardNumber;
    }

    public String getIsMain() {
	return isMain;
    }

    public void setIsMain(String isMain) {
	this.isMain = isMain;
    }

    public String getBankCode() {
	return bankCode;
    }

    public void setBankCode(String bankCode) {
	this.bankCode = bankCode;
    }

    public String getBankName() {
	return bankName;
    }

    public void setBankName(String bankName) {
	this.bankName = bankName;
    }

    /**
     * @return the status
     */
    public String getStatus() {
	return status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(String status) {
	this.status = status;
    }

    public String getBankIcon() {
	return bankIcon;
    }

    public void setBankIcon(String bankIcon) {
	this.bankIcon = bankIcon;
    }

    public String getPayType() {
	return payType;
    }

    public void setPayType(String payType) {
	this.payType = payType;
    }

    public String getBankChannel() {
	return bankChannel;
    }

    public void setBankChannel(String bankChannel) {
	this.bankChannel = bankChannel;
    }

}
