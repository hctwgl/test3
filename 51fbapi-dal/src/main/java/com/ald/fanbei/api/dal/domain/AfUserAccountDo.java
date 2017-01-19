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
	private String auAmount;//授信额度,总金额
	private String remainingAmount;//剩余金额
	private BigDecimal usedAmount;//已使用金额
	private Integer freezeAmount;//冻结金额
	private BigDecimal score;//积分
	private Integer alipayAccount;//支付宝账号
	private String commission;//返现金额(注册,签到)
	private String idNumber;//身份证号
	private String rebateAmount;//返利（淘宝返利）
	
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
	public String getAuAmount() {
		return auAmount;
	}
	public void setAuAmount(String auAmount) {
		this.auAmount = auAmount;
	}
	public Integer getFreezeAmount() {
		return freezeAmount;
	}
	public void setFreezeAmount(Integer freezeAmount) {
		this.freezeAmount = freezeAmount;
	}
	public Integer getAlipayAccount() {
		return alipayAccount;
	}
	public void setAlipayAccount(Integer alipayAccount) {
		this.alipayAccount = alipayAccount;
	}
	public String getCommission() {
		return commission;
	}
	public void setCommission(String commission) {
		this.commission = commission;
	}
	public BigDecimal getScore() {
		return score;
	}
	public void setScore(BigDecimal score) {
		this.score = score;
	}
	public String getRemainingAmount() {
		return remainingAmount;
	}
	public void setRemainingAmount(String remainingAmount) {
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
	public String getRebateAmount() {
		return rebateAmount;
	}
	public void setRebateAmount(String rebateAmount) {
		this.rebateAmount = rebateAmount;
	}
	

}
