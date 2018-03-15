package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.dal.domain.AfOrderDo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zhourui on 2018年03月15日 15:52
 * @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class LeaseOrderDto {
    private Long id;
    private String status;
    private String closedReason;
    private String consignee;
    private String address;
    private String consigneeMobile;
    private String province;
    private String city;
    private String district;
    private String goodsName;
    private String goodsIcon;
    private BigDecimal priceAmount;
    private String goodsPriceName;
    private Date gmtCreate;
    private Date gmtPayEnd;
    private BigDecimal monthlyRent;
    private String leaseStatus;
    private String rebateStatus;
    private Date gmtStart;
    private Date gmtEnd;
    private BigDecimal quotaDeposit;
    private BigDecimal cashDeposit;
    private BigDecimal freezeAmount;
    private String policyNumber;
    private String unique;
    private BigDecimal richieAmount;
    private String logistics;
    private Date logisticsTime;

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

    public Date getGmtStart() {
        return gmtStart;
    }

    public void setGmtStart(Date gmtStart) {
        this.gmtStart = gmtStart;
    }

    public Date getGmtEnd() {
        return gmtEnd;
    }

    public void setGmtEnd(Date gmtEnd) {
        this.gmtEnd = gmtEnd;
    }

    public BigDecimal getQuotaDeposit() {
        return quotaDeposit;
    }

    public void setQuotaDeposit(BigDecimal quotaDeposit) {
        this.quotaDeposit = quotaDeposit;
    }

    public BigDecimal getCashDeposit() {
        return cashDeposit;
    }

    public void setCashDeposit(BigDecimal cashDeposit) {
        this.cashDeposit = cashDeposit;
    }

    public BigDecimal getFreezeAmount() {
        return freezeAmount;
    }

    public void setFreezeAmount(BigDecimal freezeAmount) {
        this.freezeAmount = freezeAmount;
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

    public BigDecimal getRichieAmount() {
        return richieAmount;
    }

    public void setRichieAmount(BigDecimal richieAmount) {
        this.richieAmount = richieAmount;
    }
}
