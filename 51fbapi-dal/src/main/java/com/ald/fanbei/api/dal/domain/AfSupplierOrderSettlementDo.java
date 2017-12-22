/*
 *@Copyright (c) 2016, 浙江阿拉丁电子商务股份有限公司 All Rights Reserved. 
 */
package com.ald.fanbei.api.dal.domain;


import com.ald.fanbei.api.common.AbstractSerial;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @类描述：自营商城 订单结算
 * @author weiqingeng 2017年12月12日下午10:22:55
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfSupplierOrderSettlementDo extends AbstractSerial {

	private static final long serialVersionUID = -8091350096823799534L;

	private Long rid;
    private Date gmtCreate;
    private Date ruleGmtCreate;//结算规则配置时间
    private Date gmtModified;
    private String creator;
    private String modifier;
    private String settlementNo; //结算单号
	private Date shouldSettlementDate;//应结算时间
    private Long supId;//商户id
    private String supName;//商户名称
	private Integer supStatus;//商户状态 1：启用  0：禁用
	private String contactPhone;//联系人电话
	private String openBankName;//开户行名称
    private String bankNo;//银行卡号
	private String bankIdNumber;//对私时用户的身份证号
	private String bankUserPhone;//开户行绑定的手机号
	private String bankUserName;//银行户名
	private String bankCode;//银行代号
	private String unionBankNo;//对公的银行账号
	private Integer accountType;//账户类型
	private BigDecimal amount;//结算金额（单笔结算订单）
	private BigDecimal totalAmount;//结算总金额（单笔结算单）
	private Integer status;//结算状态
	private Integer verify;//结算单是否人工审核通过 0：否 1：是
	private Date settlementDate;//应结算日期
	private Date finishDate;//结算完成时间
	private Integer settlementPeriod;//结算周期 结算周期【1:按日，2：按周，3:按月】,每天或每周或每月生成一次结算单
	private Integer transferDays;//结算划款天数，生成结算单后，延迟n天执行打款'
	private Long brandId;
	private String brandName;




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

	public Date getRuleGmtCreate() {
		return ruleGmtCreate;
	}

	public void setRuleGmtCreate(Date ruleGmtCreate) {
		this.ruleGmtCreate = ruleGmtCreate;
	}

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public String getSettlementNo() {
		return settlementNo;
	}

	public void setSettlementNo(String settlementNo) {
		this.settlementNo = settlementNo;
	}

	public Date getShouldSettlementDate() {
		return shouldSettlementDate;
	}

	public void setShouldSettlementDate(Date shouldSettlementDate) {
		this.shouldSettlementDate = shouldSettlementDate;
	}

	public Long getSupId() {
		return supId;
	}

	public void setSupId(Long supId) {
		this.supId = supId;
	}

	public String getSupName() {
		return supName;
	}

	public void setSupName(String supName) {
		this.supName = supName;
	}

	public Integer getSupStatus() {
		return supStatus;
	}

	public void setSupStatus(Integer supStatus) {
		this.supStatus = supStatus;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getOpenBankName() {
		return openBankName;
	}

	public void setOpenBankName(String openBankName) {
		this.openBankName = openBankName;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankNo() {
		return bankNo;
	}

	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}

	public String getBankIdNumber() {
		return bankIdNumber;
	}

	public void setBankIdNumber(String bankIdNumber) {
		this.bankIdNumber = bankIdNumber;
	}

	public String getBankUserPhone() {
		return bankUserPhone;
	}

	public void setBankUserPhone(String bankUserPhone) {
		this.bankUserPhone = bankUserPhone;
	}

	public String getBankUserName() {
		return bankUserName;
	}

	public void setBankUserName(String bankUserName) {
		this.bankUserName = bankUserName;
	}

	public Integer getAccountType() {
		return accountType;
	}

	public void setAccountType(Integer accountType) {
		this.accountType = accountType;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Integer getStatus() {
		return status;
	}

	public Integer getVerify() {
		return verify;
	}

	public void setVerify(Integer verify) {
		this.verify = verify;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getSettlementDate() {
		return settlementDate;
	}

	public void setSettlementDate(Date settlementDate) {
		this.settlementDate = settlementDate;
	}

	public Date getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}

	public String getUnionBankNo() {
		return unionBankNo;
	}

	public void setUnionBankNo(String unionBankNo) {
		this.unionBankNo = unionBankNo;
	}

	public Integer getSettlementPeriod() {
		return settlementPeriod;
	}

	public void setSettlementPeriod(Integer settlementPeriod) {
		this.settlementPeriod = settlementPeriod;
	}

	public Integer getTransferDays() {
		return transferDays;
	}

	public void setTransferDays(Integer transferDays) {
		this.transferDays = transferDays;
	}

	public Long getBrandId() {
		return brandId;
	}

	public void setBrandId(Long brandId) {
		this.brandId = brandId;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
}
