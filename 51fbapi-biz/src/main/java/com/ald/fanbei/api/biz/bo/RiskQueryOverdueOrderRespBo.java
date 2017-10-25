package com.ald.fanbei.api.biz.bo;


public class RiskQueryOverdueOrderRespBo extends RiskRespBo {

	private String data;
	private String borrowNo; //借款编号
	private String rejectCode;//拒绝原因
	/**
	 * @return the data
	 */
	public String getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(String data) {
		this.data = data;
	}
	/**
	 * @return the borrowNo
	 */
	public String getBorrowNo() {
		return borrowNo;
	}
	/**
	 * @param borrowNo the borrowNo to set
	 */
	public void setBorrowNo(String borrowNo) {
		this.borrowNo = borrowNo;
	}
	/**
	 * @return the rejectCode
	 */
	public String getRejectCode() {
		return rejectCode;
	}
	/**
	 * @param rejectCode the rejectCode to set
	 */
	public void setRejectCode(String rejectCode) {
		this.rejectCode = rejectCode;
	}

	
	
}
