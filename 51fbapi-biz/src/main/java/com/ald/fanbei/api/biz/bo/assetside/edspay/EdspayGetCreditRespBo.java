package com.ald.fanbei.api.biz.bo.assetside.edspay;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @类现描述：钱包平台获取债权响应实体
 * @author chengkang 2017年11月30日 16:29:12
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class EdspayGetCreditRespBo implements Serializable {

	private static final long serialVersionUID = -6032550784236690575L;

	private String packageNo;//资产包编号
	private String orderNo;//借款订单号
	private Integer userId;//借款人Id
	private String name;//借款人姓名
	private String cardId;//借款人身份证号
	private String mobile;//借款人手机号码
	private String bankNo;//银行卡号
	private String acctName;//借款人户名（可选）
	private BigDecimal money;//借款金额
	private BigDecimal apr;//借款年化利率
	private Integer timeLimit;//借款期限（单位：天）
	private Integer loanStartTime;//借款开始时间戳（单位：秒）
	private Integer purpose;//借款用途
	private Integer repaymentStatus;//还款状态（0：未还，1：已还）
	private Integer repaymentType;//还款方式（具体说明）
	private String repayName;//放款账户户名
	private String repayAcct;//放款账户账号
	private String repayAcctBankNo;//开户行人行联行号（可选，放款账户为企业时必填）
	private Integer repayAcctType;//放款账户是否为个人（1：企业，0：个人[default]）
	private Integer isRepayAcctOtherBank;//放款账户是否为他行（0：否[default]，1是）
	private BigDecimal manageFee;//分期手续费
	
	
	public EdspayGetCreditRespBo() {
		super();
	}
	
	
	public EdspayGetCreditRespBo(String packageNo, String orderNo,
			Integer userId, String name, String cardId, String mobile,
			String bankNo, String acctName, BigDecimal money, BigDecimal apr,
			Integer timeLimit, Integer loanStartTime, Integer purpose,
			Integer repaymentStatus, Integer repaymentType, String repayName,
			String repayAcct, String repayAcctBankNo, Integer repayAcctType,
			Integer isRepayAcctOtherBank, BigDecimal manageFee) {
		super();
		this.packageNo = packageNo;
		this.orderNo = orderNo;
		this.userId = userId;
		this.name = name;
		this.cardId = cardId;
		this.mobile = mobile;
		this.bankNo = bankNo;
		this.acctName = acctName;
		this.money = money;
		this.apr = apr;
		this.timeLimit = timeLimit;
		this.loanStartTime = loanStartTime;
		this.purpose = purpose;
		this.repaymentStatus = repaymentStatus;
		this.repaymentType = repaymentType;
		this.repayName = repayName;
		this.repayAcct = repayAcct;
		this.repayAcctBankNo = repayAcctBankNo;
		this.repayAcctType = repayAcctType;
		this.isRepayAcctOtherBank = isRepayAcctOtherBank;
		this.manageFee = manageFee;
	}


	public String getPackageNo() {
		return packageNo;
	}
	public void setPackageNo(String packageNo) {
		this.packageNo = packageNo;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getBankNo() {
		return bankNo;
	}
	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}
	public String getAcctName() {
		return acctName;
	}
	public void setAcctName(String acctName) {
		this.acctName = acctName;
	}
	public BigDecimal getMoney() {
		return money;
	}
	public void setMoney(BigDecimal money) {
		this.money = money;
	}
	public BigDecimal getApr() {
		return apr;
	}
	public void setApr(BigDecimal apr) {
		this.apr = apr;
	}
	public Integer getTimeLimit() {
		return timeLimit;
	}
	public void setTimeLimit(Integer timeLimit) {
		this.timeLimit = timeLimit;
	}
	public Integer getLoanStartTime() {
		return loanStartTime;
	}
	public void setLoanStartTime(Integer loanStartTime) {
		this.loanStartTime = loanStartTime;
	}
	public Integer getPurpose() {
		return purpose;
	}
	public void setPurpose(Integer purpose) {
		this.purpose = purpose;
	}
	public Integer getRepaymentStatus() {
		return repaymentStatus;
	}
	public void setRepaymentStatus(Integer repaymentStatus) {
		this.repaymentStatus = repaymentStatus;
	}
	public Integer getRepaymentType() {
		return repaymentType;
	}
	public void setRepaymentType(Integer repaymentType) {
		this.repaymentType = repaymentType;
	}
	public String getRepayName() {
		return repayName;
	}
	public void setRepayName(String repayName) {
		this.repayName = repayName;
	}
	public String getRepayAcct() {
		return repayAcct;
	}
	public void setRepayAcct(String repayAcct) {
		this.repayAcct = repayAcct;
	}
	public String getRepayAcctBankNo() {
		return repayAcctBankNo;
	}
	public void setRepayAcctBankNo(String repayAcctBankNo) {
		this.repayAcctBankNo = repayAcctBankNo;
	}
	public Integer getRepayAcctType() {
		return repayAcctType;
	}
	public void setRepayAcctType(Integer repayAcctType) {
		this.repayAcctType = repayAcctType;
	}
	public Integer getIsRepayAcctOtherBank() {
		return isRepayAcctOtherBank;
	}
	public void setIsRepayAcctOtherBank(Integer isRepayAcctOtherBank) {
		this.isRepayAcctOtherBank = isRepayAcctOtherBank;
	}
	public BigDecimal getManageFee() {
		return manageFee;
	}
	public void setManageFee(BigDecimal manageFee) {
		this.manageFee = manageFee;
	}
	
	
	
}
