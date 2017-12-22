package com.ald.fanbei.api.common.enums.afu;

/**
 * @类描述：命中项枚举类
 * @author chenxuankai 2017年11月23日11:11:04
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum RiskItemTypeCode {
	
	CardNum("101010","证件号码");
	
	private String code;//结果码
	private String mean;//结果码含义
	
	private RiskItemTypeCode(String code, String mean) {
		this.code = code;
		this.mean = mean;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMean() {
		return mean;
	}

	public void setMean(String mean) {
		this.mean = mean;
	}	
		
}
