package com.ald.fanbei.api.biz.bo;

import java.math.BigDecimal;

public class RiskVerifyRespBo extends RiskRespBo {

	private String result;

	private String data;

	private String orderNo;// 我们自己生成的订单号

	private String consumerNo;// 用户编号
	
	private String virtualCode;//虚拟商品进行编码
	
	private BigDecimal virtualQuota;//虚拟商品限值
	
	private String rejectCode;//不通过原因
	
	private String borrowNo;//借款编号

	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getConsumerNo() {
		return consumerNo;
	}

	public void setConsumerNo(String consumerNo) {
		this.consumerNo = consumerNo;
	}
	/**
	 * @return the virtualCode
	 */
	public String getVirtualCode() {
		return virtualCode;
	}
	/**
	 * @param virtualCode the virtualCode to set
	 */
	public void setVirtualCode(String virtualCode) {
		this.virtualCode = virtualCode;
	}
	/**
	 * @return the virtualQuota
	 */
	public BigDecimal getVirtualQuota() {
		return virtualQuota;
	}
	/**
	 * @param virtualQuota the virtualQuota to set
	 */
	public void setVirtualQuota(BigDecimal virtualQuota) {
		this.virtualQuota = virtualQuota;
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
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RiskVerifyRespBo [result=" + result + ", data=" + data + ", orderNo=" + orderNo + ", consumerNo=" + consumerNo + ", virtualCode=" + virtualCode + ", virtualQuota=" + virtualQuota + ", rejectCode=" + rejectCode + ", borrowNo=" + borrowNo + "]";
	}

}
