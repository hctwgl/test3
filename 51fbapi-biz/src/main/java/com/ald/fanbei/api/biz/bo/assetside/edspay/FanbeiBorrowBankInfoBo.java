package com.ald.fanbei.api.biz.bo.assetside.edspay;

import java.io.Serializable;

/**
 * 爱上街资产包使用的开户银行信息
 * @author chengkang
 */
public class FanbeiBorrowBankInfoBo implements Serializable {

	private static final long serialVersionUID = 4204652534348461359L;
	private String acctName;// 借款人户名（可选）
	private String repayName;// 放款账户户名
	private String repayAcct;// 放款账户账号
	private String repayAcctBankNo;// 开户行人行联行号（可选，放款账户为企业时必填）
	private Integer repayAcctType;// 放款账户是否为个人（1：企业，0：个人[default]）
	private Integer isRepayAcctOtherBank;// 放款账户是否为他行（0：否[default]，1是）

	public String getAcctName() {
		return acctName;
	}

	public void setAcctName(String acctName) {
		this.acctName = acctName;
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

}
