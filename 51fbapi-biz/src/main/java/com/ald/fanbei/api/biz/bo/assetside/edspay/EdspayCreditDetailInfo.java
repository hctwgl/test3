package com.ald.fanbei.api.biz.bo.assetside.edspay;

import java.math.BigDecimal;

/**
 * 资产方7和14天分配金额
 * @author chengkang
 */
public class EdspayCreditDetailInfo {
	/**
	 * 七天借款期限的债权金额
	 */
	private BigDecimal SEVEN;
	/**
	 * 十四天借款期限的债权金额
	 */
	private BigDecimal FOURTEEN;

	public BigDecimal getSEVEN() {
		return SEVEN;
	}

	public void setSEVEN(BigDecimal sEVEN) {
		SEVEN = sEVEN;
	}

	public BigDecimal getFOURTEEN() {
		return FOURTEEN;
	}

	public void setFOURTEEN(BigDecimal fOURTEEN) {
		FOURTEEN = fOURTEEN;
	}
}
