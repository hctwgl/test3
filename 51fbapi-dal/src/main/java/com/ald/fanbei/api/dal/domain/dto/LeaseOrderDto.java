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
    private String rebateAmount;
    private Date gmtStart;
    private Date gmtEnd;
    private BigDecimal quotaDeposit;
    private BigDecimal cashDeposit;
    private BigDecimal freezeAmount;
    private String policyNumber;
    private String unique;
    private BigDecimal richieAmount;
    private String logisticsInfo;
    private String logisticsCompany;
    private String logisticsNo;
    private Date gmtDeliver;
    private String showLogistics;
    private BigDecimal actualAmount;
    private String orderNo;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClosedReason() {
        return closedReason;
    }

    public void setClosedReason(String closedReason) {
        this.closedReason = closedReason;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getConsigneeMobile() {
        return consigneeMobile;
    }

    public void setConsigneeMobile(String consigneeMobile) {
        this.consigneeMobile = consigneeMobile;
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

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsIcon() {
        return goodsIcon;
    }

    public void setGoodsIcon(String goodsIcon) {
        this.goodsIcon = goodsIcon;
    }

    public BigDecimal getPriceAmount() {
        return priceAmount;
    }

    public void setPriceAmount(BigDecimal priceAmount) {
        this.priceAmount = priceAmount;
    }

    public String getGoodsPriceName() {
        return goodsPriceName;
    }

    public void setGoodsPriceName(String goodsPriceName) {
        this.goodsPriceName = goodsPriceName;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtPayEnd() {
        return gmtPayEnd;
    }

    public void setGmtPayEnd(Date gmtPayEnd) {
        this.gmtPayEnd = gmtPayEnd;
    }

    public String getRebateAmount() {
        return rebateAmount;
    }

    public void setRebateAmount(String rebateAmount) {
        this.rebateAmount = rebateAmount;
    }

    public String getLogisticsInfo() {
        return logisticsInfo;
    }

    public void setLogisticsInfo(String logisticsInfo) {
        this.logisticsInfo = logisticsInfo;
    }

    public String getLogisticsCompany() {
        return logisticsCompany;
    }

    public void setLogisticsCompany(String logisticsCompany) {
        this.logisticsCompany = logisticsCompany;
    }

    public String getLogisticsNo() {
        return logisticsNo;
    }

    public void setLogisticsNo(String logisticsNo) {
        this.logisticsNo = logisticsNo;
    }

    public Date getGmtDeliver() {
        return gmtDeliver;
    }

    public void setGmtDeliver(Date gmtDeliver) {
        this.gmtDeliver = gmtDeliver;
    }

    public String getShowLogistics() {
        return showLogistics;
    }

    public void setShowLogistics(String showLogistics) {
        this.showLogistics = showLogistics;
    }

    public BigDecimal getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
