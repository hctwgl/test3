package com.ald.fanbei.api.biz.bo.assetside.edspay;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


/**
 * @类现描述：钱包平台获取债权响应实体
 * @author chengkang 2017年11月30日 16:29:12
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class EdspayGetCreditRespBo implements Serializable {

	private static final long serialVersionUID = -6032550784236690575L;

	private String packageNo;//资产包编号
	private String orderNo;//借款订单号
	private Long userId;//借款人Id
	private String name;//借款人姓名
	private String cardId;//借款人身份证号
	private String mobile;//借款人手机号码
	private String bankNo;//银行卡号
	private String acctName;//借款人户名（可选）
	private BigDecimal money;//借款金额
	private BigDecimal arriveMoney;//实际到账金额
	private BigDecimal apr;//借款年化利率
	private Integer timeLimit;//借款期限（单位：天）
	private Long loanStartTime;//借款开始时间戳（单位：秒）
	private String purpose;//借款用途
	private Integer repaymentStatus;//还款状态（0：未还，1：已还）
	private Integer repaymentType;//还款方式（具体说明）
	private String repayName;//放款账户户名
	private String repayAcct;//放款账户账号
	private String repayAcctBankNo;//开户行人行联行号（可选，放款账户为企业时必填）
	private Integer repayAcctType;//放款账户是否为个人（1：企业，0：个人[default]）
	private Integer isRepayAcctOtherBank;//放款账户是否为他行（0：否[default]，1是）
	private BigDecimal manageFee;//分期手续费
	
	private Long pushTime;//推送时间(可选，默认为当天)
	private String repaymentSource;//还款来源
	private Integer debtType;//债权类型，(0现金贷[Default]，1消费分期)
	private Integer isPeriod;//是否分期,(0:未分期[Default],1:分期)
	private Integer totalPeriod;//总期数，可选
	private Integer loanerType;//借款人性质(0:个人[Default],1:公司)
	private Integer overdueTimes;//平台总逾期次数
	private BigDecimal overdueAmount;//平台总逾期金额
	private List<RepaymentPlan> repaymentPlans;//还款计划数组
	private Integer isCur;//债权推送类型，(0实时推送[Default]，1非实时推送)
	
	
	public EdspayGetCreditRespBo() {
		super();
	}
	
	public EdspayGetCreditRespBo(String packageNo, String orderNo, Long userId,
			String name, String cardId, String mobile, String bankNo,
			String acctName, BigDecimal money, BigDecimal apr,
			Integer timeLimit, Long loanStartTime, String purpose,
			Integer repaymentStatus, Integer repaymentType, String repayName,
			String repayAcct, String repayAcctBankNo, Integer repayAcctType,
			Integer isRepayAcctOtherBank, BigDecimal manageFee, Long pushTime,
			String repaymentSource, Integer debtType, Integer isPeriod,
			Integer totalPeriod, Integer loanerType, Integer overdueTimes,
			BigDecimal overdueAmount, List<RepaymentPlan> repaymentPlans) {
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
		this.pushTime = pushTime;
		this.repaymentSource = repaymentSource;
		this.debtType = debtType;
		this.isPeriod = isPeriod;
		this.totalPeriod = totalPeriod;
		this.loanerType = loanerType;
		this.overdueTimes = overdueTimes;
		this.overdueAmount = overdueAmount;
		this.repaymentPlans = repaymentPlans;
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
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
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
	public Long getLoanStartTime() {
		return loanStartTime;
	}
	public void setLoanStartTime(Long loanStartTime) {
		this.loanStartTime = loanStartTime;
	}
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
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


	public Long getPushTime() {
		return pushTime;
	}


	public void setPushTime(Long pushTime) {
		this.pushTime = pushTime;
	}


	public String getRepaymentSource() {
		return repaymentSource;
	}


	public void setRepaymentSource(String repaymentSource) {
		this.repaymentSource = repaymentSource;
	}


	public Integer getDebtType() {
		return debtType;
	}


	public void setDebtType(Integer debtType) {
		this.debtType = debtType;
	}


	public Integer getIsPeriod() {
		return isPeriod;
	}


	public void setIsPeriod(Integer isPeriod) {
		this.isPeriod = isPeriod;
	}


	public Integer getTotalPeriod() {
		return totalPeriod;
	}


	public void setTotalPeriod(Integer totalPeriod) {
		this.totalPeriod = totalPeriod;
	}


	public Integer getLoanerType() {
		return loanerType;
	}


	public void setLoanerType(Integer loanerType) {
		this.loanerType = loanerType;
	}


	public Integer getOverdueTimes() {
		return overdueTimes;
	}


	public void setOverdueTimes(Integer overdueTimes) {
		this.overdueTimes = overdueTimes;
	}


	public BigDecimal getOverdueAmount() {
		return overdueAmount;
	}


	public void setOverdueAmount(BigDecimal overdueAmount) {
		this.overdueAmount = overdueAmount;
	}


	public List<RepaymentPlan> getRepaymentPlans() {
		return repaymentPlans;
	}


	public void setRepaymentPlans(List<RepaymentPlan> repaymentPlans) {
		this.repaymentPlans = repaymentPlans;
	}

	public BigDecimal getArriveMoney() {
		return arriveMoney;
	}

	public void setArriveMoney(BigDecimal arriveMoney) {
		this.arriveMoney = arriveMoney;
	}
	
	public Integer getIsCur() {
		return isCur;
	}

	public void setIsCur(Integer isCur) {
		this.isCur = isCur;
	}
	
}
