package com.ald.fanbei.api.biz.bo;


/**
 *@类现描述：支付路由协议延期交易bo
 *@author chenjinhu 2017年2月19日 下午1:49:59
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class PayRoutSignDelayReqBo extends PayRoutReqBo{
	private static final long serialVersionUID = 8310455391344267354L;
	
	private String startDate		;  //协议开始时间
	private String endDate			;  //协议结束时间
	private String contractNo	;  //用户协议号
	private String returnUrl		;  //同步通知地址
	private String notifyUrl		;  //异步通知地址
	private String remark			;  //备注
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
		this.put("startDate", startDate);
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
		this.put("endDate", endDate);
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
		this.put("contractNo", contractNo);
	}
	public String getReturnUrl() {
		return returnUrl;
	}
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
		this.put("returnUrl", returnUrl);
	}
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
		this.put("notifyUrl", notifyUrl);
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
		this.put("remark", remark);
	}
	

}
