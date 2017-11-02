package com.ald.fanbei.api.biz.bo;

import com.ald.fanbei.api.common.AbstractSerial;


/**
 *@类描述：铜盾申请结果对象
 *@author 陈金虎 2017年1月20日 下午9:20:54
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的 
 */
public class TongdunResultBo extends AbstractSerial{
	private static final long serialVersionUID = -5621553728474942232L;
	
	private boolean success;     //查询返回结果
	private String reportId;     //查询id 
	private Integer finalScore;  //风险评分
	private String finalDecision;//风险描述
	private String resultStr;    //返回结果
	private String reasonCode;   //返回结果code
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getReportId() {
		return reportId;
	}
	public void setReportId(String reportId) {
		this.reportId = reportId;
	}
	public Integer getFinalScore() {
		return finalScore;
	}
	public void setFinalScore(Integer finalScore) {
		this.finalScore = finalScore;
	}
	public String getFinalDecision() {
		return finalDecision;
	}
	public void setFinalDecision(String finalDecision) {
		this.finalDecision = finalDecision;
	}
	public String getResultStr() {
		return resultStr;
	}
	public void setResultStr(String resultStr) {
		this.resultStr = resultStr;
	}
	public String getReasonCode() {
		return reasonCode;
	}
	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}
	
}
