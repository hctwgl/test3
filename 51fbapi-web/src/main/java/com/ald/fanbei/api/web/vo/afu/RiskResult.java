package com.ald.fanbei.api.web.vo.afu;

import java.io.Serializable;

/**
 * @类描述：风险项记录类
 * @author chenxuankai 2017年11月23日11:22:55
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class RiskResult implements Serializable {

	private String riskItemTypeCode;//命中项码，指代证件类型 枚举
	private String riskItemValue;//命中的内容，身份证号的具体值
	private String riskDetail;//风险明细，风险类别
	private String riskTime;//最近发现的风险时间 ，1，YYYY 2，YYYYMM 3，YYYYMMDD	
	
	public RiskResult() {}

	public RiskResult(String riskItemTypeCode, String riskItemValue,
			String riskDetail, String riskTime) {
		super();
		this.riskItemTypeCode = riskItemTypeCode;
		this.riskItemValue = riskItemValue;
		this.riskDetail = riskDetail;
		this.riskTime = riskTime;
	}

	public String getRiskItemTypeCode() {
		return riskItemTypeCode;
	}

	public void setRiskItemTypeCode(String riskItemTypeCode) {
		this.riskItemTypeCode = riskItemTypeCode;
	}

	public String getRiskItemValue() {
		return riskItemValue;
	}

	public void setRiskItemValue(String riskItemValue) {
		this.riskItemValue = riskItemValue;
	}

	public String getRiskDetail() {
		return riskDetail;
	}

	public void setRiskDetail(String riskDetail) {
		this.riskDetail = riskDetail;
	}

	public String getRiskTime() {
		return riskTime;
	}

	public void setRiskTime(String riskTime) {
		this.riskTime = riskTime;
	}	
	
}
