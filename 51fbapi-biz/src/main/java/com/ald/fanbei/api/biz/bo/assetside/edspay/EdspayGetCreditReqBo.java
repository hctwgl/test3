package com.ald.fanbei.api.biz.bo.assetside.edspay;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @类现描述：钱包平台获取债权请求实体
 * @author chengkang 2017年11月29日 14:29:12
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class EdspayGetCreditReqBo implements Serializable {

	private static final long serialVersionUID = 1923132956548193237L;
	/**
	 * 债权总金额(单位:元,精度统一到小数点后2位)
	 */
	private BigDecimal money;
	/**
	 * 借款开始时间戳(可选，默认按当前时间00:00:00)
	 */
	private Long loanStartTime;
	/**
	 * 借款结束时间戳(可选，默认按当前时间23:59:59)
	 */
	private Long loanEndTime;
	/**
	 * 债权类型，0现金贷，1消费分期
	 */
	private Integer debtType;
	/**
	 * 如果为空则按照金额分配债权
	 */
	private EdspayCreditDetailInfo creditDetails;

	public Integer getDebtType() {
		return debtType;
	}

	public void setDebtType(Integer debtType) {
		this.debtType = debtType;
	}

	public BigDecimal getMoney() {
		return money;
	}

	public void setMoney(BigDecimal money) {
		this.money = money;
	}

	public Long getLoanStartTime() {
		return loanStartTime;
	}

	public void setLoanStartTime(Long loanStartTime) {
		this.loanStartTime = loanStartTime;
	}

	public Long getLoanEndTime() {
		return loanEndTime;
	}

	public void setLoanEndTime(Long loanEndTime) {
		this.loanEndTime = loanEndTime;
	}

	public EdspayCreditDetailInfo getCreditDetails() {
		return creditDetails;
	}

	public void setCreditDetails(EdspayCreditDetailInfo creditDetails) {
		this.creditDetails = creditDetails;
	}
}
