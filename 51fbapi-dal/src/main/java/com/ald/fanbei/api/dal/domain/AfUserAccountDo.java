package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：用户账号相关信息
 * @author Xiaotianjian 2017年1月19日下午4:04:09
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfUserAccountDo extends AbstractSerial {
	
	private static final long serialVersionUID = 570354935460086063L;
	
	private Long rid;
	private Long userId; //用户id
	private String userName;//用户名
	private String realName;
	private BigDecimal auAmount;//授信额度,总金额
	private BigDecimal usedAmount;//已使用金额
	private BigDecimal freezeAmount;//冻结金额
	private BigDecimal score;//积分	
	private String alipayAccount;//支付宝账号	
	private BigDecimal rebateAmount;//返利（淘宝返利）
	private String idNumber;//身份证号
	private BigDecimal jfbAmount;//返利（淘宝返利）
	private String password;//支付密码
	private String salt;//盐值
	private BigDecimal ucAmount;//已取现额度
	private Integer failCount; //密码连续错误次数
	private String bindCard;//是否绑卡：Y：绑卡：N：未绑卡
	private Integer creditScore;//信用分
	private String openId;//芝麻信用openId
	private BigDecimal borrowCashAmount;//借款最高金额

	/**
	 * @return the bindCard
	 */
	public String getBindCard() {
		return bindCard;
	}
	/**
	 * @param bindCard the bindCard to set
	 */
	public void setBindCard(String bindCard) {
		this.bindCard = bindCard;
	}
	public Long getRid() {
		return rid;
	}
	public void setRid(Long rid) {
		this.rid = rid;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public BigDecimal getAuAmount() {
		return auAmount;
	}
	public void setAuAmount(BigDecimal auAmount) {
		this.auAmount = auAmount;
	}
	public BigDecimal getFreezeAmount() {
		return freezeAmount;
	}
	public void setFreezeAmount(BigDecimal freezeAmount) {
		this.freezeAmount = freezeAmount;
	}
	
	public BigDecimal getScore() {
		return score;
	}
	public void setScore(BigDecimal score) {
		this.score = score;
	}

	public BigDecimal getUsedAmount() {
		return usedAmount;
	}
	public void setUsedAmount(BigDecimal usedAmount) {
		this.usedAmount = usedAmount;
	}
	public String getIdNumber() {
		return idNumber;
	}
	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}
	public BigDecimal getRebateAmount() {
		return rebateAmount;
	}
	public void setRebateAmount(BigDecimal rebateAmount) {
		this.rebateAmount = rebateAmount;
	}
	public String getAlipayAccount() {
		return alipayAccount;
	}
	public void setAlipayAccount(String alipayAccount) {
		this.alipayAccount = alipayAccount;
	}

	public BigDecimal getUcAmount() {
		return ucAmount;
	}
	public void setUcAmount(BigDecimal ucAmount) {
		this.ucAmount = ucAmount;
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
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the salt
	 */
	public String getSalt() {
		return salt;
	}
	/**
	 * @param salt the salt to set
	 */
	public void setSalt(String salt) {
		this.salt = salt;
	}
	/**
	 * @return the failCount
	 */
	public Integer getFailCount() {
		return failCount;
	}
	/**
	 * @param failCount the failCount to set
	 */
	public void setFailCount(Integer failCount) {
		this.failCount = failCount;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public Integer getCreditScore() {
		return creditScore;
	}
	public void setCreditScore(Integer creditScore) {
		this.creditScore = creditScore;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	/**
	 * @return the borrowCashAmount
	 */
	public BigDecimal getBorrowCashAmount() {
		return borrowCashAmount;
	}
	/**
	 * @param borrowCashAmount the borrowCashAmount to set
	 */
	public void setBorrowCashAmount(BigDecimal borrowCashAmount) {
		this.borrowCashAmount = borrowCashAmount;
	}
}
