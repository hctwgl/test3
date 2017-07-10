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
	
	private Long rid;//订单id
	private String goodsName;//商品名称
	private BigDecimal saleAmount;//应付金额
	private BigDecimal rebateAmount;//返利金额
	private Date gmtPayEnd;//截止支付时间
	private Date currentTime;//当前时间
	
	private String bankcardStatus;//是否绑定主卡
	
	private String realName;//真实姓名
	private Integer realNameScore;//实名认证分
	
	private Long bankId;//银行卡id
	private String bankCode;//银行编号
	private String bankName;//银行名称
	private String bankIcon;//银行图标
	private char isValid; //银行是否有效
	
	private BigDecimal useableAmount;//可使用额度
	private BigDecimal totalAmount;//总额度
	
	private String allowConsume;//分期/代付的关键字段，Y：已授权；N：未授权
	private String faceStatus;
	private String idNumber;
	
	//3.6.9增加virtualGoodsUsableAmount, isVirtualGoods字段
	private BigDecimal virtualGoodsUsableAmount;
	private String isVirtualGoods;
	private String ovderduedCode;
	private Long billId;
	private BigDecimal jfbAmount;//集分宝
	private BigDecimal userRebateAmount;//用户返利余额
	private BigDecimal repaymentAmount;//应还金额
	
	public char getIsValid() {
		return isValid;
	}

	public void setIsValid(char isValid) {
		this.isValid = isValid;
	}

	/**
	 * @return the rid
	 */
	public Long getRid() {
		return rid;
	}

	/**
	 * @param rid the rid to set
	 */
	public void setRid(Long rid) {
		this.rid = rid;
	}

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
	 * @return the useableAmount
	 */
	public BigDecimal getUseableAmount() {
		return useableAmount;
	}

	/**
	 * @param useableAmount the useableAmount to set
	 */
	public void setUseableAmount(BigDecimal useableAmount) {
		this.useableAmount = useableAmount;
	}

	/**
	 * @return the totalAmount
	 */
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	/**
	 * @param totalAmount the totalAmount to set
	 */
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	/**
	 * @return the allowConsume
	 */
	public String getAllowConsume() {
		return allowConsume;
	}

	/**
	 * @param allowConsume the allowConsume to set
	 */
	public void setAllowConsume(String allowConsume) {
		this.allowConsume = allowConsume;
	}

	public String getFaceStatus() {
		return faceStatus;
	}

	public void setFaceStatus(String faceStatus) {
		this.faceStatus = faceStatus;
	}

	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	/**
	 * @return the virtualGoodsUsableAmount
	 */
	public BigDecimal getVirtualGoodsUsableAmount() {
		return virtualGoodsUsableAmount;
	}

	/**
	 * @param virtualGoodsUsableAmount the virtualGoodsUsableAmount to set
	 */
	public void setVirtualGoodsUsableAmount(BigDecimal virtualGoodsUsableAmount) {
		this.virtualGoodsUsableAmount = virtualGoodsUsableAmount;
	}

	/**
	 * @return the isVirtualGoods
	 */
	public String getIsVirtualGoods() {
		return isVirtualGoods;
	}

	/**
	 * @param isVirtualGoods the isVirtualGoods to set
	 */
	public void setIsVirtualGoods(String isVirtualGoods) {
		this.isVirtualGoods = isVirtualGoods;
	}

	/**
	 * @return the ovderduedCode
	 */
	public String getOvderduedCode() {
		return ovderduedCode;
	}

	/**
	 * @param ovderduedCode the ovderduedCode to set
	 */
	public void setOvderduedCode(String ovderduedCode) {
		this.ovderduedCode = ovderduedCode;
	}

	/**
	 * @return the billId
	 */
	public Long getBillId() {
		return billId;
	}

	/**
	 * @param billId the billId to set
	 */
	public void setBillId(Long billId) {
		this.billId = billId;
	}

	/**
	 * @return the jfbAmount
	 */
	public BigDecimal getJfbAmount() {
		return jfbAmount;
	}

	/**
	 * @param jfbAmount the jfbAmount to set
	 */
	public void setJfbAmount(BigDecimal jfbAmount) {
		this.jfbAmount = jfbAmount;
	}

	/**
	 * @return the userRebateAmount
	 */
	public BigDecimal getUserRebateAmount() {
		return userRebateAmount;
	}

	/**
	 * @param userRebateAmount the userRebateAmount to set
	 */
	public void setUserRebateAmount(BigDecimal userRebateAmount) {
		this.userRebateAmount = userRebateAmount;
	}

	/**
	 * @return the repaymentAmount
	 */
	public BigDecimal getRepaymentAmount() {
		return repaymentAmount;
	}

	/**
	 * @param repaymentAmount the repaymentAmount to set
	 */
	public void setRepaymentAmount(BigDecimal repaymentAmount) {
		this.repaymentAmount = repaymentAmount;
	}
	
}
