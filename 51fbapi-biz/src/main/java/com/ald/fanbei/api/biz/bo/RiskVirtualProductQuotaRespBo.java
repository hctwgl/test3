package com.ald.fanbei.api.biz.bo;

import java.math.BigDecimal;

public class RiskVirtualProductQuotaRespBo extends RiskRespBo {

	private VirtualProductQuota data;
	/**
	 * @return the data
	 */
	public VirtualProductQuota getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(VirtualProductQuota data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "RiskVirtualProductQuotaRespBo{" +
				"data=" + data +
				'}';
	}
}
