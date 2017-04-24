package com.ald.fanbei.api.web.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：
 * @author hexin 2017年2月18日上午14:57:35
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfBorrowHomeVo extends AbstractSerial{

	private static final long serialVersionUID = 8656064187748575028L;
	private BigDecimal totalAmount;
	private BigDecimal usableAmount;
	private BigDecimal currentAmount;
	private Date repayLimitTime;
	private String realNameStatus;
	private String zmStatus;
	private String ivsStatus;
	private String bankcardStatus;
	private String mobileStatus;
	private String teldirStatus;
	private String ydKey;
	private String ydUrl;
	private String realName;
	private String idNumber;
	private String zmxyAuthUrl;
	private String billYear;
	private String billMonth;
	private String status;
	private Date gmtZm;
	private Integer zmScore;
	private Integer ivsScore;
	private Integer realNameScore;
	private String contactorStatus;
	private String contactorName;
	private String contactorMobile;
	private String contactorType;
	private String locationStatus;
	private String locationAddress;
	private String allowConsume;
	
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public BigDecimal getUsableAmount() {
		return usableAmount;
	}
	public void setUsableAmount(BigDecimal usableAmount) {
		this.usableAmount = usableAmount;
	}
	public BigDecimal getCurrentAmount() {
		return currentAmount;
	}
	public void setCurrentAmount(BigDecimal currentAmount) {
		this.currentAmount = currentAmount;
	}
	public Date getRepayLimitTime() {
		return repayLimitTime;
	}
	public void setRepayLimitTime(Date repayLimitTime) {
		this.repayLimitTime = repayLimitTime;
	}
	public String getRealNameStatus() {
		return realNameStatus;
	}
	public void setRealNameStatus(String realNameStatus) {
		this.realNameStatus = realNameStatus;
	}
	public String getZmStatus() {
		return zmStatus;
	}
	public void setZmStatus(String zmStatus) {
		this.zmStatus = zmStatus;
	}
	public String getIvsStatus() {
		return ivsStatus;
	}
	public void setIvsStatus(String ivsStatus) {
		this.ivsStatus = ivsStatus;
	}
	public String getBankcardStatus() {
		return bankcardStatus;
	}
	public void setBankcardStatus(String bankcardStatus) {
		this.bankcardStatus = bankcardStatus;
	}
	public String getMobileStatus() {
		return mobileStatus;
	}
	public void setMobileStatus(String mobileStatus) {
		this.mobileStatus = mobileStatus;
	}
	public String getTeldirStatus() {
		return teldirStatus;
	}
	public void setTeldirStatus(String teldirStatus) {
		this.teldirStatus = teldirStatus;
	}
	public String getYdKey() {
		return ydKey;
	}
	public void setYdKey(String ydKey) {
		this.ydKey = ydKey;
	}
	public String getYdUrl() {
		return ydUrl;
	}
	public void setYdUrl(String ydUrl) {
		this.ydUrl = ydUrl;
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
	public String getZmxyAuthUrl() {
		return zmxyAuthUrl;
	}
	public void setZmxyAuthUrl(String zmxyAuthUrl) {
		this.zmxyAuthUrl = zmxyAuthUrl;
	}
	public String getBillYear() {
		return billYear;
	}
	public void setBillYear(String billYear) {
		this.billYear = billYear;
	}
	public String getBillMonth() {
		return billMonth;
	}
	public void setBillMonth(String billMonth) {
		this.billMonth = billMonth;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getGmtZm() {
		return gmtZm;
	}
	public void setGmtZm(Date gmtZm) {
		this.gmtZm = gmtZm;
	}
	public Integer getZmScore() {
		return zmScore;
	}
	public void setZmScore(Integer zmScore) {
		this.zmScore = zmScore;
	}
	public Integer getIvsScore() {
		return ivsScore;
	}
	public void setIvsScore(Integer ivsScore) {
		this.ivsScore = ivsScore;
	}
	public Integer getRealNameScore() {
		return realNameScore;
	}
	public void setRealNameScore(Integer realNameScore) {
		this.realNameScore = realNameScore;
	}
	public String getContactorStatus() {
		return contactorStatus;
	}
	public void setContactorStatus(String contactorStatus) {
		this.contactorStatus = contactorStatus;
	}
	public String getContactorName() {
		return contactorName;
	}
	public void setContactorName(String contactorName) {
		this.contactorName = contactorName;
	}
	public String getContactorMobile() {
		return contactorMobile;
	}
	public void setContactorMobile(String contactorMobile) {
		this.contactorMobile = contactorMobile;
	}
	public String getLocationStatus() {
		return locationStatus;
	}
	public void setLocationStatus(String locationStatus) {
		this.locationStatus = locationStatus;
	}
	public String getLocationAddress() {
		return locationAddress;
	}
	public void setLocationAddress(String locationAddress) {
		this.locationAddress = locationAddress;
	}
	public String getContactorType() {
		return contactorType;
	}
	public void setContactorType(String contactorType) {
		this.contactorType = contactorType;
	}
	public String getAllowConsume() {
		return allowConsume;
	}
	public void setAllowConsume(String allowConsume) {
		this.allowConsume = allowConsume;
	}
}
