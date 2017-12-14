package com.ald.fanbei.api.common.enums.afu;

/**
 * @类描述：逾期情况枚举类
 * @author chenxuankai 2017年11月23日10:20:07
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum OverdueStatus {

	One("M1","当前逾期1期"),
	Two("M2","当前逾期2期"),
	Three("M3","当前逾期3期"),
	ThreePlus("M3+","当前逾期3期以上的，不含3期"),
	Four("M4","当前逾期4期"),
	Five("M5","当前逾期5期"),
	Six("M6","当前逾期6期"),
	SixPlus("M6+","当前逾期6期以上的，不含6期");
	
	private String code;//结果码取值
	private String mean;//结果码含义
	private OverdueStatus(String code, String mean) {
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
