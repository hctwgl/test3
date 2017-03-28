package com.ald.fanbei.api.web.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年3月25日下午9:25:54
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class BoluomeConfirmOrderVo extends AbstractSerial{

	private static final long serialVersionUID = 8312782486382699082L;
	
	private String goodsName;//商品名称
	private BigDecimal saleAmount;//应付金额
	private BigDecimal rebateAmount;//返利金额
	private Date gmtPayEnd;//截止支付时间
	private Date currentTime;//当前时间
	private String mobileStatus;//手机运营商认证
	private String teldirStatus;//通讯录匹配
	
	private String realNameStatus;//实名认证
	private String zmStatus;//芝麻信用授权
	private String bankcardStatus;//是否绑定主卡
	
	private String ydKey;//有盾key
	private String ydUrl;//有盾Url
	private String realName;//真实姓名
	private String idNumber;//身份证
	private String zmxyAuthUrl;//芝麻信用授权地址
	private Date gmtZm;//芝麻信用授权时间
	private Integer zmScore;//芝麻信用分
	private Integer realNameScore;//实名认证分
	
	private Long bankId;//银行卡id
	private String bankCode;//银行编号
	private String bankName;//银行名称
	private String bankIcon;//银行图标
	
	private BigDecimal usableAmount;//可使用额度
	
	/**
	 * @return the goodsName
	 */
	public String getGoodsName() {
		return goodsName;
	}
	/**
	 * @param goodsName the goodsName to set
	 */
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	/**
	 * @return the saleAmount
	 */
	public BigDecimal getSaleAmount() {
		return saleAmount;
	}
	/**
	 * @param saleAmount the saleAmount to set
	 */
	public void setSaleAmount(BigDecimal saleAmount) {
		this.saleAmount = saleAmount;
	}
	/**
	 * @return the rebateAmount
	 */
	public BigDecimal getRebateAmount() {
		return rebateAmount;
	}
	/**
	 * @param rebateAmount the rebateAmount to set
	 */
	public void setRebateAmount(BigDecimal rebateAmount) {
		this.rebateAmount = rebateAmount;
	}
	/**
	 * @return the gmtPayEnd
	 */
	public Date getGmtPayEnd() {
		return gmtPayEnd;
	}
	/**
	 * @param gmtPayEnd the gmtPayEnd to set
	 */
	public void setGmtPayEnd(Date gmtPayEnd) {
		this.gmtPayEnd = gmtPayEnd;
	}
	/**
	 * @return the mobileStatus
	 */
	public String getMobileStatus() {
		return mobileStatus;
	}
	/**
	 * @param mobileStatus the mobileStatus to set
	 */
	public void setMobileStatus(String mobileStatus) {
		this.mobileStatus = mobileStatus;
	}
	/**
	 * @return the teldirStatus
	 */
	public String getTeldirStatus() {
		return teldirStatus;
	}
	/**
	 * @param teldirStatus the teldirStatus to set
	 */
	public void setTeldirStatus(String teldirStatus) {
		this.teldirStatus = teldirStatus;
	}
	/**
	 * @return the realNameStatus
	 */
	public String getRealNameStatus() {
		return realNameStatus;
	}
	/**
	 * @param realNameStatus the realNameStatus to set
	 */
	public void setRealNameStatus(String realNameStatus) {
		this.realNameStatus = realNameStatus;
	}
	/**
	 * @return the zmStatus
	 */
	public String getZmStatus() {
		return zmStatus;
	}
	/**
	 * @param zmStatus the zmStatus to set
	 */
	public void setZmStatus(String zmStatus) {
		this.zmStatus = zmStatus;
	}
	/**
	 * @return the bankcardStatus
	 */
	public String getBankcardStatus() {
		return bankcardStatus;
	}
	/**
	 * @param bankcardStatus the bankcardStatus to set
	 */
	public void setBankcardStatus(String bankcardStatus) {
		this.bankcardStatus = bankcardStatus;
	}
	/**
	 * @return the ydKey
	 */
	public String getYdKey() {
		return ydKey;
	}
	/**
	 * @param ydKey the ydKey to set
	 */
	public void setYdKey(String ydKey) {
		this.ydKey = ydKey;
	}
	/**
	 * @return the ydUrl
	 */
	public String getYdUrl() {
		return ydUrl;
	}
	/**
	 * @param ydUrl the ydUrl to set
	 */
	public void setYdUrl(String ydUrl) {
		this.ydUrl = ydUrl;
	}
	/**
	 * @return the realName
	 */
	public String getRealName() {
		return realName;
	}
	/**
	 * @param realName the realName to set
	 */
	public void setRealName(String realName) {
		this.realName = realName;
	}
	/**
	 * @return the idNumber
	 */
	public String getIdNumber() {
		return idNumber;
	}
	/**
	 * @param idNumber the idNumber to set
	 */
	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}
	/**
	 * @return the zmxyAuthUrl
	 */
	public String getZmxyAuthUrl() {
		return zmxyAuthUrl;
	}
	/**
	 * @param zmxyAuthUrl the zmxyAuthUrl to set
	 */
	public void setZmxyAuthUrl(String zmxyAuthUrl) {
		this.zmxyAuthUrl = zmxyAuthUrl;
	}
	/**
	 * @return the gmtZm
	 */
	public Date getGmtZm() {
		return gmtZm;
	}
	/**
	 * @param gmtZm the gmtZm to set
	 */
	public void setGmtZm(Date gmtZm) {
		this.gmtZm = gmtZm;
	}
	/**
	 * @return the zmScore
	 */
	public Integer getZmScore() {
		return zmScore;
	}
	/**
	 * @param zmScore the zmScore to set
	 */
	public void setZmScore(Integer zmScore) {
		this.zmScore = zmScore;
	}
	/**
	 * @return the realNameScore
	 */
	public Integer getRealNameScore() {
		return realNameScore;
	}
	/**
	 * @param realNameScore the realNameScore to set
	 */
	public void setRealNameScore(Integer realNameScore) {
		this.realNameScore = realNameScore;
	}
	/**
	 * @return the bankId
	 */
	public Long getBankId() {
		return bankId;
	}
	/**
	 * @param bankId the bankId to set
	 */
	public void setBankId(Long bankId) {
		this.bankId = bankId;
	}
	/**
	 * @return the bankCode
	 */
	public String getBankCode() {
		return bankCode;
	}
	/**
	 * @param bankCode the bankCode to set
	 */
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	/**
	 * @return the bankName
	 */
	public String getBankName() {
		return bankName;
	}
	/**
	 * @param bankName the bankName to set
	 */
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	/**
	 * @return the bankIcon
	 */
	public String getBankIcon() {
		return bankIcon;
	}
	/**
	 * @param bankIcon the bankIcon to set
	 */
	public void setBankIcon(String bankIcon) {
		this.bankIcon = bankIcon;
	}
	/**
	 * @return the usableAmount
	 */
	public BigDecimal getUsableAmount() {
		return usableAmount;
	}
	/**
	 * @param usableAmount the usableAmount to set
	 */
	public void setUsableAmount(BigDecimal usableAmount) {
		this.usableAmount = usableAmount;
	}
	/**
	 * @return the currentTime
	 */
	public Date getCurrentTime() {
		return currentTime;
	}
	/**
	 * @param currentTime the currentTime to set
	 */
	public void setCurrentTime(Date currentTime) {
		this.currentTime = currentTime;
	}
	
	
	
}
