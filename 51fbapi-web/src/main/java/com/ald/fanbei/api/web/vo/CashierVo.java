package com.ald.fanbei.api.web.vo;

import com.ald.fanbei.api.common.AbstractSerial;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.dto.AfBankUserBankDto;
import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class CashierVo extends AbstractSerial {
    private BigDecimal amount;
    private BigDecimal rebatedAmount;
    private Long orderId;
    private String orderType;
    private boolean countDown;
    private Date currentTime;
    private Date gmtPayEnd;
    private String realName;
    private String idNumber;
    private String isSupplyCertify;//是否完成补充认证
    private String faceStatus;
    private String  riskStatus;
    private String  bankCardStatus;
    private String  isValid;//银行卡状态
    private Integer realNameScore;//实名认证分
    private String scene;//额度场景
    
    List<AfBankUserBankDto> bankCardList;
    /**
     * 主卡信息
     */
    private AfUserBankcardDo mainBankCard;
    /**
     * 分期支付
     */
    private CashierTypeVo ap;

    /**
     * 信用支付
     */
    private CashierTypeVo credit;
    /**
     * 组合支付
     */
    private CashierTypeVo cp;
    /**
     * 微信支付
     */
    private CashierTypeVo wx;
    /**
     * 银行卡支付
     */
    private CashierTypeVo bank;
    /**
     * 支付宝支付
     */
    private CashierTypeVo ali;



    public CashierTypeVo getCredit() {
        return credit;
    }

    public void setCredit(CashierTypeVo credit) {
        this.credit = credit;
    }

    public CashierTypeVo getCp() {
        return cp;
    }

    public void setCp(CashierTypeVo cp) {
        this.cp = cp;
    }

    public CashierTypeVo getWx() {
        return wx;
    }

    public void setWx(CashierTypeVo wx) {
        this.wx = wx;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Date getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(Date currentTime) {
        this.currentTime = currentTime;
    }

    public boolean isCountDown() {
        return countDown;
    }

    public void setCountDown(boolean countDown) {
        this.countDown = countDown;
    }

    public CashierTypeVo getAp() {
        return ap;
    }

    public void setAp(CashierTypeVo ap) {
        this.ap = ap;
    }

    public CashierTypeVo getBank() {
        return bank;
    }

    public void setBank(CashierTypeVo bank) {
        this.bank = bank;
    }

    public CashierTypeVo getAli() {
        return ali;
    }

    public void setAli(CashierTypeVo ali) {
        this.ali = ali;
    }

    public BigDecimal getRebatedAmount() {
        return rebatedAmount;
    }

    public void setRebatedAmount(BigDecimal rebatedAmount) {
        this.rebatedAmount = rebatedAmount;
    }

    public AfUserBankcardDo getMainBankCard() {
        return mainBankCard;
    }

    public void setMainBankCard(AfUserBankcardDo mainBankCard) {
        this.mainBankCard = mainBankCard;
    }

    public Date getGmtPayEnd() {
        return gmtPayEnd;
    }

    public void setGmtPayEnd(Date gmtPayEnd) {
        this.gmtPayEnd = gmtPayEnd;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getIsSupplyCertify() {
        return isSupplyCertify;
    }

    public void setIsSupplyCertify(String isSupplyCertify) {
        this.isSupplyCertify = isSupplyCertify;
    }

    public String getFaceStatus() {
        return faceStatus;
    }

    public void setFaceStatus(String faceStatus) {
        this.faceStatus = faceStatus;
    }

    public String getRiskStatus() {
        return riskStatus;
    }

    public void setRiskStatus(String riskStatus) {
        this.riskStatus = riskStatus;
    }

    public String getBankCardStatus() {
        return bankCardStatus;
    }

    public void setBankCardStatus(String bankCardStatus) {
        this.bankCardStatus = bankCardStatus;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public Integer getRealNameScore() {
        return realNameScore;
    }

    public void setRealNameScore(Integer realNameScore) {
        this.realNameScore = realNameScore;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public List<AfBankUserBankDto> getBankCardList() {
        return bankCardList;
    }

    public void setBankCardList(List<AfBankUserBankDto> bankCardList) {
        this.bankCardList = bankCardList;
    }

    @Override
    public String toString() {
	return "CashierVo [amount=" + amount + ", rebatedAmount=" + rebatedAmount + ", orderId=" + orderId + ", orderType=" + orderType + ", countDown=" + countDown + ", currentTime=" + currentTime + ", gmtPayEnd=" + gmtPayEnd + ", realName=" + realName + ", idNumber=" + idNumber + ", isSupplyCertify=" + isSupplyCertify + ", faceStatus=" + faceStatus + ", riskStatus=" + riskStatus + ", bankCardStatus=" + bankCardStatus + ", isValid=" + isValid + ", realNameScore=" + realNameScore + ", scene=" + scene + ", bankCardList=" + bankCardList + ", mainBankCard=" + mainBankCard + ", ap=" + ap + ", credit=" + credit + ", cp=" + cp + ", wx=" + wx + ", bank=" + bank + ", ali=" + ali + "]";
    }
    
}
