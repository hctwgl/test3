package com.ald.fanbei.api.biz.bo.loan;

import java.math.BigDecimal;

public class ApplyLoanBo {
	
	public ApplyLoanBo() {
		this.reqParam = new ReqParam();
	}
	
	public ReqParam reqParam;
	
	/* carrier区域，业务处理中继参数 */
	public Long userId;
	public String userName;
	
	public static class ReqParam{
		public String prdType;
		public BigDecimal amount;
		public int periods;
		public String remark;
		public String loanRemark;
		public String repayRemark;
		public String payPwd;
		public String latitude;
		public String longitude;
		public String province;
		public String city;
		public String county;
		public String address;
		public String blackBox;
		public String bqsBlackBox;
		public Long couponId;
		
		public String ip;
		public String appType;
		public String appName;
		public String getPrdType() {
			return prdType;
		}
		public void setPrdType(String prdType) {
			this.prdType = prdType;
		}
		public BigDecimal getAmount() {
			return amount;
		}
		public void setAmount(BigDecimal amount) {
			this.amount = amount;
		}
		public int getPeriods() {
			return periods;
		}
		public void setPeriods(int periods) {
			this.periods = periods;
		}
		public String getRemark() {
			return remark;
		}
		public void setRemark(String remark) {
			this.remark = remark;
		}
		public String getLoanRemark() {
			return loanRemark;
		}
		public void setLoanRemark(String loanRemark) {
			this.loanRemark = loanRemark;
		}
		public String getRepayRemark() {
			return repayRemark;
		}
		public void setRepayRemark(String repayRemark) {
			this.repayRemark = repayRemark;
		}
		public String getPayPwd() {
			return payPwd;
		}
		public void setPayPwd(String payPwd) {
			this.payPwd = payPwd;
		}
		public String getLatitude() {
			return latitude;
		}
		public void setLatitude(String latitude) {
			this.latitude = latitude;
		}
		public String getLongitude() {
			return longitude;
		}
		public void setLongitude(String longitude) {
			this.longitude = longitude;
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
		public String getCounty() {
			return county;
		}
		public void setCounty(String county) {
			this.county = county;
		}
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public String getBlackBox() {
			return blackBox;
		}
		public void setBlackBox(String blackBox) {
			this.blackBox = blackBox;
		}
		public String getBqsBlackBox() {
			return bqsBlackBox;
		}
		public void setBqsBlackBox(String bqsBlackBox) {
			this.bqsBlackBox = bqsBlackBox;
		}
		public Long getCouponId() {
			return couponId;
		}
		public void setCouponId(Long couponId) {
			this.couponId = couponId;
		}
		public String getIp() {
			return ip;
		}
		public void setIp(String ip) {
			this.ip = ip;
		}
		public String getAppType() {
			return appType;
		}
		public void setAppType(String appType) {
			this.appType = appType;
		}
		public String getAppName() {
			return appName;
		}
		public void setAppName(String appName) {
			this.appName = appName;
		}
	}
	
}
