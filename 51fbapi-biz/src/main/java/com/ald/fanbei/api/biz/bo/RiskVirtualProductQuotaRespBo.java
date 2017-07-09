package com.ald.fanbei.api.biz.bo;

import java.math.BigDecimal;

import com.alibaba.fastjson.JSONObject;

public class RiskVirtualProductQuotaRespBo extends RiskRespBo {

	private String data;
	
	private RiskVirtualProductQuotaDetailRespBo details;

	public class RiskVirtualProductQuotaDetailRespBo {
		
		private BigDecimal amount; //虚拟商品的额度
		private String virtualCode;//虚拟商品代码
		
		/**
		 * @return the amount
		 */
		public BigDecimal getAmount() {
			RiskVirtualProductQuotaDetailRespBo bo = JSONObject.parseObject(data, RiskVirtualProductQuotaDetailRespBo.class);
			return bo != null ? bo.getAmount() : amount;
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
			RiskVirtualProductQuotaDetailRespBo bo = JSONObject.parseObject(data, RiskVirtualProductQuotaDetailRespBo.class);
			return bo != null ? bo.getVirtualCode() : virtualCode;
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
			return "RiskVirtualProductQuotaDetailRespBo [amount=" + amount + ", virtualCode=" + virtualCode + "]";
		}
	}

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
	 * @return the details
	 */
	public RiskVirtualProductQuotaDetailRespBo getDetails() {
		return details;
	}

	/**
	 * @param details the details to set
	 */
	public void setDetails(RiskVirtualProductQuotaDetailRespBo details) {
		this.details = details;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RiskVirtualProductQuotaRespBo [data=" + data + ", details=" + details + "]";
	}

}
