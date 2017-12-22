package com.ald.fanbei.api.common.enums.afu;

/**
 * @类描述：还款状态枚举类
 * @author chenxuankai 2017年11月23日09:50:21
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum LoanStatusCode {
	
	Normal("301","正常"),
	Overdue("302","逾期"),
	Finish("303","结清");
	
	private String code;//结果码取值
	private String mean;//结果码含义
	
	private LoanStatusCode(String code, String mean) {
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
