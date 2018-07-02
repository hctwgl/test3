package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class AfTradeTenementInfoDo {

	private Long id;//主键id
	private Date applyTime;//申请审核时间，第一次为创建时间，后续为重复提交审核的时间
	private Date auditTime;//最后一次审核时间
	private String userName;//用户名
	private String mobile;//手机号
	private String idNumber;//身份证号
	private String homeAddress;//房屋位置
	private String contractImageUrl;//合同图片
	private String rentType;//租房时长类型【5天,6天,11天,12天】
	private BigDecimal rentOnePrice;//房租单价
	private BigDecimal rentSumPrice;//房租总额
	private Date beginTime;//租房开始时间
	private Date endTime;//租房结束时间
	private Integer auditState;//审核状态:0:未审核 1已审核 2审核失败
	private String auditFaildReason;//审核失败原因
	private Integer rentStatus;//租客当前状态【0:正常,1:逾期】
	private Long businessId;//商户id
	private Integer isTermination;//租客是否终止
	private String auditName;//审核人
	
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@JSONField(format = "yyyy-MM-dd")
	public Date getApplyTime() {
		return applyTime;
	}
	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}
	
	@JSONField(format = "yyyy-MM-dd")
	public Date getAuditTime() {
		return auditTime;
	}
	public void setAuditTime(Date auditTime) {
		this.auditTime = auditTime;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getIdNumber() {
		return idNumber;
	}
	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}
	public String getHomeAddress() {
		return homeAddress;
	}
	public void setHomeAddress(String homeAddress) {
		this.homeAddress = homeAddress;
	}
	public String getContractImageUrl() {
		return contractImageUrl;
	}
	public void setContractImageUrl(String contractImageUrl) {
		this.contractImageUrl = contractImageUrl;
	}
	public String getRentType() {
		return rentType;
	}
	public void setRentType(String rentType) {
		this.rentType = rentType;
	}
	public BigDecimal getRentOnePrice() {
		return rentOnePrice;
	}
	public void setRentOnePrice(BigDecimal rentOnePrice) {
		this.rentOnePrice = rentOnePrice;
	}
	public BigDecimal getRentSumPrice() {
		return rentSumPrice;
	}
	public void setRentSumPrice(BigDecimal rentSumPrice) {
		this.rentSumPrice = rentSumPrice;
	}
	
	@JSONField(format = "yyyy-MM-dd")
	public Date getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}
	
	@JSONField(format = "yyyy-MM-dd")
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Integer getAuditState() {
		return auditState;
	}
	public void setAuditState(Integer auditState) {
		this.auditState = auditState;
	}
	public String getAuditFaildReason() {
		return auditFaildReason;
	}
	public void setAuditFaildReason(String auditFaildReason) {
		this.auditFaildReason = auditFaildReason;
	}
	public Integer getRentStatus() {
		return rentStatus;
	}
	public void setRentStatus(Integer rentStatus) {
		this.rentStatus = rentStatus;
	}
	public Long getBusinessId() {
		return businessId;
	}
	public void setBusinessId(Long businessId) {
		this.businessId = businessId;
	}
	public Integer getIsTermination() {
		return isTermination;
	}
	public void setIsTermination(Integer isTermination) {
		this.isTermination = isTermination;
	}
	public String getAuditName() {
		return auditName;
	}
	public void setAuditName(String auditName) {
		this.auditName = auditName;
	}
	
	
}
