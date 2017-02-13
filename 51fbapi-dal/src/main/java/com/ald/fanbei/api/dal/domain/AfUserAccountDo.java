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
	private BigDecimal auAmount;//授信额度,总金额
	private BigDecimal remainingAmount;//剩余金额
	private BigDecimal usedAmount;//已使用金额
	private BigDecimal freezeAmount;//冻结金额
	private BigDecimal score;//积分
	private String alipayAccount;//支付宝账号
	private BigDecimal commission;//返现金额(注册,签到)
	private String idNumber;//身份证号
	private BigDecimal rebateAmount;//返利（淘宝返利）
	private BigDecimal ccAmount;//可取现额度
	private BigDecimal ucAmount;//已取现额度
	
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
	public BigDecimal getCommission() {
		return commission;
	}
	public void setCommission(BigDecimal commission) {
		this.commission = commission;
	}
	public BigDecimal getScore() {
		return score;
	}
	public void setScore(BigDecimal score) {
		this.score = score;
	}
	public BigDecimal getRemainingAmount() {
		return remainingAmount;
	}
	public void setRemainingAmount(BigDecimal remainingAmount) {
		this.remainingAmount = remainingAmount;
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
	public BigDecimal getCcAmount() {
		return ccAmount;
	}
	public void setCcAmount(BigDecimal ccAmount) {
		this.ccAmount = ccAmount;
	}
	public BigDecimal getUcAmount() {
		return ucAmount;
	}
	public void setUcAmount(BigDecimal ucAmount) {
		this.ucAmount = ucAmount;
	}

}
