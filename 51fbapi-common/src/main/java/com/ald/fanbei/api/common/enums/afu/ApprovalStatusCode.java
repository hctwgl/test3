package com.ald.fanbei.api.common.enums.afu;

/**
 * 
 * @类描述：审核状态枚举类
 * @author chenxuankai 2017年11月23日09:35:24
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum ApprovalStatusCode {

	Approving("201","审核中"),
	Approved("202","批贷已放款"),
	UnApprove("203","拒贷"),
	ApproveGiveUp("204","客户放弃");
	
	private String code;//结果码取值
	private String mean;//结果码含义
	
	private ApprovalStatusCode(String code, String mean) {
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
