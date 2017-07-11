package com.ald.fanbei.api.biz.bo;

import java.math.BigDecimal;

public class RiskVirtualProductQuotaRespBo extends RiskRespBo {

	private String data;
	private BigDecimal amount; //虚拟商品的额度
	private String virtualCode;//虚拟商品代码
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
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
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
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RiskVirtualProductQuotaRespBo [data=" + data + ", amount=" + amount + ", virtualCode=" + virtualCode + ", getCode()=" + getCode() + ", getMsg()=" + getMsg() + ", isSuccess()=" + isSuccess() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}

	
}
