package com.ald.fanbei.api.biz.bo.assetside.edspay;

import java.math.BigDecimal;

/**
 * 资产方金额分配
 * @author chengkang
 */
public class EdspayCreditDetailInfo {
	/**
	 * 最小借钱天数对应的债权金额
	 */
	private BigDecimal minMoney;
	/**
	 * 最大借钱天数对应的的债权金额
	 */
	private BigDecimal maxMoney;
	
	public BigDecimal getMinMoney() {
		return minMoney;
	}
	public void setMinMoney(BigDecimal minMoney) {
		this.minMoney = minMoney;
	}
	public BigDecimal getMaxMoney() {
		return maxMoney;
	}
	public void setMaxMoney(BigDecimal maxMoney) {
		this.maxMoney = maxMoney;
	}
	
}
