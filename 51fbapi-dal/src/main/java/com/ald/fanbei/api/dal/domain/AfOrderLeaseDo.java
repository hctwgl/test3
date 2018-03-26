package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zhourui on 2018年03月10日 11:31
 * @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfOrderLeaseDo extends AbstractSerial {
    private static final long serialVersionUID = -4712137543511553120L;

    private Long id;
    private Integer isDelete;
    private Date gmtCreate;
    private Date gmtModified;
    private Long userId;
    private Long orderId;
    private BigDecimal monthlyRent;
    private String leaseStatus;
    private String rebateStatus;
    private Date gmtEnd;
    private String userName;
    private String policyNumber;
    private String unique;
    private BigDecimal cashDeposit;
    private Date gmtStart;
    private BigDecimal quotaDeposit;
    private String verifyStatus;
    private BigDecimal buyout;
    private BigDecimal recoverRate;
    private BigDecimal richieAmount;
    private String realName;
    private Integer score;
    private BigDecimal freezeAmount;
    private String orderNo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
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

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getMonthlyRent() {
        return monthlyRent;
    }

    public void setMonthlyRent(BigDecimal monthlyRent) {
        this.monthlyRent = monthlyRent;
    }

    public String getLeaseStatus() {
        return leaseStatus;
    }

    public void setLeaseStatus(String leaseStatus) {
        this.leaseStatus = leaseStatus;
    }

    public String getRebateStatus() {
        return rebateStatus;
    }

    public void setRebateStatus(String rebateStatus) {
        this.rebateStatus = rebateStatus;
    }

    public Date getGmtEnd() {
        return gmtEnd;
    }

    public void setGmtEnd(Date gmtEnd) {
        this.gmtEnd = gmtEnd;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getUnique() {
        return unique;
    }

    public void setUnique(String unique) {
        this.unique = unique;
    }

    public BigDecimal getCashDeposit() {
        return cashDeposit;
    }

    public void setCashDeposit(BigDecimal cashDeposit) {
        this.cashDeposit = cashDeposit;
    }

    public Date getGmtStart() {
        return gmtStart;
    }

    public void setGmtStart(Date gmtStart) {
        this.gmtStart = gmtStart;
    }

    public BigDecimal getQuotaDeposit() {
        return quotaDeposit;
    }

    public void setQuotaDeposit(BigDecimal quotaDeposit) {
        this.quotaDeposit = quotaDeposit;
    }

    public String getVerifyStatus() {
        return verifyStatus;
    }

    public void setVerifyStatus(String verifyStatus) {
        this.verifyStatus = verifyStatus;
    }

    public BigDecimal getBuyout() {
        return buyout;
    }

    public void setBuyout(BigDecimal buyout) {
        this.buyout = buyout;
    }

    public BigDecimal getRecoverRate() {
        return recoverRate;
    }

    public void setRecoverRate(BigDecimal recoverRate) {
        this.recoverRate = recoverRate;
    }

    public BigDecimal getRichieAmount() {
        return richieAmount;
    }

    public void setRichieAmount(BigDecimal richieAmount) {
        this.richieAmount = richieAmount;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public BigDecimal getFreezeAmount() {
        return freezeAmount;
    }

    public void setFreezeAmount(BigDecimal freezeAmount) {
        this.freezeAmount = freezeAmount;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
