package com.ald.fanbei.api.web.vo.afu;

import java.io.Serializable;

/**
 * @类描述：响应数据节点类
 * @author chenxuankai 2017年11月23日11:29:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class Data implements Serializable {
	
	private String loanRecords;
	
	private String riskResults;

	public Data(String loanRecords, String riskResults) {
		super();
		this.loanRecords = loanRecords;
		this.riskResults = riskResults;
	}

	public Data() {}

	public String getLoanRecords() {
		return loanRecords;
	}

	public void setLoanRecords(String loanRecords) {
		this.loanRecords = loanRecords;
	}

	public String getRiskResults() {
		return riskResults;
	}

	public void setRiskResults(String riskResults) {
		this.riskResults = riskResults;
	}	
	
}
