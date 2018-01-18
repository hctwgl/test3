package com.ald.fanbei.api.common.enums;


/**
 * @类描述：资产包还款方式枚举
 * @author chengkang 2017年11月29日上午10:40:51
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AfAssetPackageRepaymentType {
	ONE_TIME_REPAY(0,2, "一次性还本付息");

	private Integer code;
	private Integer edsCode;
	private String description;

	AfAssetPackageRepaymentType(Integer code,Integer edsCode, String description) {
		this.code = code;
		this.edsCode = edsCode;
		this.description = description;
	}

	public static AfAssetPackageRepaymentType findEnumByCode(Integer code) {
		for (AfAssetPackageRepaymentType repayType : AfAssetPackageRepaymentType.values()) {
			if (repayType.getCode().equals(code)) {
				return repayType;
			}
		}
		return null;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getEdsCode() {
		return edsCode;
	}

	public void setEdsCode(Integer edsCode) {
		this.edsCode = edsCode;
	}
	
}
