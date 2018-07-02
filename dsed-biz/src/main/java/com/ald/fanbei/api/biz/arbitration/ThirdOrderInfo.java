package com.ald.fanbei.api.biz.arbitration;

public class ThirdOrderInfo {

	private String loanBillNos;
	private String loanBillNo;
	private String batchNo;
	public String getLoanBillNos() {
		return loanBillNos;
	}
	public void setLoanBillNos(String loanBillNos) {
		this.loanBillNos = loanBillNos;
	}
	public String getLoanBillNo() {
		return loanBillNo;
	}
	public void setLoanBillNo(String loanBillNo) {
		this.loanBillNo = loanBillNo;
	}
	public String getBatchNo() {
		return batchNo;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	public String getMerchantCode() {
		return merchantCode;
	}
	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}
	private String merchantCode;
}
