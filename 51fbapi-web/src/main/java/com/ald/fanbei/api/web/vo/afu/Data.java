package com.ald.fanbei.api.web.vo.afu;

import java.io.Serializable;
import java.util.List;

/**
 * @类描述：响应数据节点类
 * @author chenxuankai 2017年11月23日11:29:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class Data implements Serializable {
	
	private List loanRecords;
	
//	private List riskResults;

	public Data(List loanRecords) {
		super();
		this.loanRecords = loanRecords;
//		this.riskResults = riskResults;
	}

	public Data() {
		super();
		// TODO Auto-generated constructor stub
	}

	public List getLoanRecords() {
		return loanRecords;
	}

	public void setLoanRecords(List loanRecords) {
		this.loanRecords = loanRecords;
	}

//	public List getRiskResults() {
//		return riskResults;
//	}

//	public void setRiskResults(List riskResults) {
//		this.riskResults = riskResults;
//	}

		
	
}
