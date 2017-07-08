package com.ald.fanbei.api.biz.bo;

import java.math.BigDecimal;

public class RiskVirtualProductQuotaRespBo extends RiskRespBo {

	
	private RiskVirtualProductQuotaDetailRespBo data;

	public class RiskVirtualProductQuotaDetailRespBo {
		
		private BigDecimal amount; //虚拟商品的额度
		private String virtualCode;//虚拟商品代码
		
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
	}


	/**
	 * @return the data
	 */
	public RiskVirtualProductQuotaDetailRespBo getData() {
		return data;
	}


	/**
	 * @param data the data to set
	 */
	public void setData(RiskVirtualProductQuotaDetailRespBo data) {
		this.data = data;
	}
}
