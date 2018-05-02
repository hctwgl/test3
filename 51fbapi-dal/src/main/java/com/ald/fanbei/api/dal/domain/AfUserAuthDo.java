package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 *@类现描述：用户授权do
 *@author chenjinhu 2017年2月15日 下午2:46:19
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfUserAuthDo extends AbstractSerial {
	private static final long serialVersionUID = -6047858096252444067L;
	private Long rid;
	private Date gmtModified;
	private Long userId;
	private String zmStatus;
	private Date gmtZm;
	private Integer zmScore;
	private String ivsStatus;
	private Date gmtIvs;
	private Integer ivsScore;
	private String realnameStatus;
	private Date gmtRealname;
	private Integer realnameScore;
	private String facesStatus;
	private BigDecimal similarDegree;
	private BigDecimal thresholds;
	private String mobileStatus;
	private Date gmtMobile;
	private String bankcardStatus;
	private String teldirStatus;
	private String ydStatus;
	private String watchlistStatus;
	private Date gmtWatchlist;
	private String watchlistScore;
	private String contactorStatus;
	private String contactorName;
	private String contactorMobile;
	private String contactorType;
	private String locationStatus;
	private String locationAddress;
	private String riskStatus; //强风控审核状态【A:未审核，N:未通过审核，P: 审核中，Y:已通过审核】
	private Date gmtRisk;
	private String fundStatus;
	private Date gmtFund;
	private String jinpoStatus;
	private Date gmtJinpo;
	private String creditStatus;
	private Date gmtCredit;
	private String alipayStatus;
	private Date gmtAlipay;
	private String faceType;//YITU：依图 FACE_PLUS:face++
	private Date gmtFaces;//人脸识别时间
	private String basicStatus;//基础认证状态【A:未审核，N:未通过审核，P: 审核中，:已通过审核】
	private Date gmtBasic;//基础认证时间
	private Date gmtChsi;
	private String chsiStatus; //学信网认证
	private Date gmtZhengxin;
	private String zhengxinStatus;
	private Date gmtOnlinebank;
	private String onlinebankStatus;
	private Date gmtEcommerce;
	private String ecommerceStatus;//公信宝电商认证
	private String bubbleStatus;
	private Date gtmBubble;

	public String getBubbleStatus() {
		return bubbleStatus;
	}

	public void setBubbleStatus(String bubbleStatus) {
		this.bubbleStatus = bubbleStatus;
	}

	public Date getGtmBubble() {
		return gtmBubble;
	}

	public void setGtmBubble(Date gtmBubble) {
		this.gtmBubble = gtmBubble;
	}

	public String getBasicStatus() {
		return basicStatus;
	}

	public void setBasicStatus(String basicStatus) {
		this.basicStatus = basicStatus;
	}

	public Date getGmtBasic() {
		return gmtBasic;
	}

	public void setGmtBasic(Date gmtBasic) {
		this.gmtBasic = gmtBasic;
	}

	public Long getRid() {
		return rid;
	}
	public void setRid(Long rid) {
		this.rid = rid;
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
	public String getZmStatus() {
		return zmStatus;
	}
	public void setZmStatus(String zmStatus) {
		this.zmStatus = zmStatus;
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
	
	public String getIvsStatus() {
		return ivsStatus;
	}
	public void setIvsStatus(String ivsStatus) {
		this.ivsStatus = ivsStatus;
	}
	public Date getGmtIvs() {
		return gmtIvs;
	}
	public void setGmtIvs(Date gmtIvs) {
		this.gmtIvs = gmtIvs;
	}
	public Integer getIvsScore() {
		return ivsScore;
	}
	public void setIvsScore(Integer ivsScore) {
		this.ivsScore = ivsScore;
	}
	public String getRealnameStatus() {
		return realnameStatus;
	}
	public void setRealnameStatus(String realnameStatus) {
		this.realnameStatus = realnameStatus;
	}
	public Date getGmtRealname() {
		return gmtRealname;
	}
	public void setGmtRealname(Date gmtRealname) {
		this.gmtRealname = gmtRealname;
	}
	public Integer getRealnameScore() {
		return realnameScore;
	}
	public void setRealnameScore(Integer realnameScore) {
		this.realnameScore = realnameScore;
	}
	public String getFacesStatus() {
		return facesStatus;
	}
	public void setFacesStatus(String facesStatus) {
		this.facesStatus = facesStatus;
	}
	public BigDecimal getSimilarDegree() {
		return similarDegree;
	}
	public void setSimilarDegree(BigDecimal similarDegree) {
		this.similarDegree = similarDegree;
	}
	public String getMobileStatus() {
		return mobileStatus;
	}
	public void setMobileStatus(String mobileStatus) {
		this.mobileStatus = mobileStatus;
	}
	public String getBankcardStatus() {
		return bankcardStatus;
	}
	public void setBankcardStatus(String bankcardStatus) {
		this.bankcardStatus = bankcardStatus;
	}
	public String getTeldirStatus() {
		return teldirStatus;
	}
	public void setTeldirStatus(String teldirStatus) {
		this.teldirStatus = teldirStatus;
	}
	public String getYdStatus() {
		return ydStatus;
	}
	public void setYdStatus(String ydStatus) {
		this.ydStatus = ydStatus;
	}
	public String getWatchlistStatus() {
		return watchlistStatus;
	}
	public void setWatchlistStatus(String watchlistStatus) {
		this.watchlistStatus = watchlistStatus;
	}
	public Date getGmtWatchlist() {
		return gmtWatchlist;
	}
	public void setGmtWatchlist(Date gmtWatchlist) {
		this.gmtWatchlist = gmtWatchlist;
	}
	public String getWatchlistScore() {
		return watchlistScore;
	}
	public void setWatchlistScore(String watchlistScore) {
		this.watchlistScore = watchlistScore;
	}
	public Date getGmtMobile() {
		return gmtMobile;
	}
	public void setGmtMobile(Date gmtMobile) {
		this.gmtMobile = gmtMobile;
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
	public String getRiskStatus() {
		return riskStatus;
	}
	public void setRiskStatus(String riskStatus) {
		this.riskStatus = riskStatus;
	}
	public Date getGmtRisk() {
		return gmtRisk;
	}
	public void setGmtRisk(Date gmtRisk) {
		this.gmtRisk = gmtRisk;
	}
	public String getFundStatus() {
		return fundStatus;
	}
	public void setFundStatus(String fundStatus) {
		this.fundStatus = fundStatus;
	}
	public Date getGmtFund() {
		return gmtFund;
	}
	public void setGmtFund(Date gmtFund) {
		this.gmtFund = gmtFund;
	}
	public String getJinpoStatus() {
		return jinpoStatus;
	}
	public void setJinpoStatus(String jinpoStatus) {
		this.jinpoStatus = jinpoStatus;
	}
	public Date getGmtJinpo() {
		return gmtJinpo;
	}
	public void setGmtJinpo(Date gmtJinpo) {
		this.gmtJinpo = gmtJinpo;
	}
	public String getCreditStatus() {
		return creditStatus;
	}
	public void setCreditStatus(String creditStatus) {
		this.creditStatus = creditStatus;
	}
	public Date getGmtCredit() {
		return gmtCredit;
	}
	public void setGmtCredit(Date gmtCredit) {
		this.gmtCredit = gmtCredit;
	}
	public String getAlipayStatus() {
		return alipayStatus;
	}
	public void setAlipayStatus(String alipayStatus) {
		this.alipayStatus = alipayStatus;
	}
	public Date getGmtAlipay() {
		return gmtAlipay;
	}
	public void setGmtAlipay(Date gmtAlipay) {
		this.gmtAlipay = gmtAlipay;
	}
	/**
	 * @return the thresholds
	 */
	public BigDecimal getThresholds() {
		return thresholds;
	}
	/**
	 * @param thresholds the thresholds to set
	 */
	public void setThresholds(BigDecimal thresholds) {
		this.thresholds = thresholds;
	}
	/**
	 * @return the faceType
	 */
	public String getFaceType() {
		return faceType;
	}
	/**
	 * @param faceType the faceType to set
	 */
	public void setFaceType(String faceType) {
		this.faceType = faceType;
	}
	/**
	 * @return the gmtFaces
	 */
	public Date getGmtFaces() {
		return gmtFaces;
	}
	/**
	 * @param gmtFaces the gmtFaces to set
	 */
	public void setGmtFaces(Date gmtFaces) {
		this.gmtFaces = gmtFaces;
	}

	public Date getGmtChsi() {
		return gmtChsi;
	}

	public void setGmtChsi(Date gmtChsi) {
		this.gmtChsi = gmtChsi;
	}

	public String getChsiStatus() {
		return chsiStatus;
	}

	public void setChsiStatus(String chsiStatus) {
		this.chsiStatus = chsiStatus;
	}

	public Date getGmtZhengxin() {
		return gmtZhengxin;
	}

	public void setGmtZhengxin(Date gmtZhengxin) {
		this.gmtZhengxin = gmtZhengxin;
	}

	public String getZhengxinStatus() {
		return zhengxinStatus;
	}

	public void setZhengxinStatus(String zhengxinStatus) {
		this.zhengxinStatus = zhengxinStatus;
	}

	public Date getGmtOnlinebank() {
		return gmtOnlinebank;
	}

	public void setGmtOnlinebank(Date gmtOnlinebank) {
		this.gmtOnlinebank = gmtOnlinebank;
	}

	public String getOnlinebankStatus() {
		return onlinebankStatus;
	}

	public void setOnlinebankStatus(String onlinebankStatus) {
		this.onlinebankStatus = onlinebankStatus;
	}
	public Date getGmtEcommerce() {
		return gmtEcommerce;
	}

	public void setGmtEcommerce(Date gmtEcommerce) {
		this.gmtEcommerce = gmtEcommerce;
	}

	public String getEcommerceStatus() {
		return ecommerceStatus;
	}

	public void setEcommerceStatus(String ecommerceStatus) {
		this.ecommerceStatus = ecommerceStatus;
	}
}
