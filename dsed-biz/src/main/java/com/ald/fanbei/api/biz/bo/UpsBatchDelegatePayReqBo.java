package com.ald.fanbei.api.biz.bo;


/**
 *@类现描述：支付路由批量代付请求bo
 *@author chenjinhu 2017年2月20日 下午1:42:33
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class UpsBatchDelegatePayReqBo extends UpsReqBo {
	private static final long serialVersionUID = 7977070544204337468L;
	private String totalAmount		;   //批量代付总额
	private String totalCount		;//批量代付总笔数
	private String remark			;	//备注
	private String paymentFlag		;   //代付标识
	private String notifyUrl		;	//异步地址
	private String paymentDetails	;	//证件类型
	
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
		this.put("remark", remark);
	}
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
		this.put("notifyUrl", notifyUrl);
	}
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
		this.put("totalAmount", totalAmount);
	}
	public String getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
		this.put("totalCount", totalCount);
	}
	public String getPaymentFlag() {
		return paymentFlag;
	}
	public void setPaymentFlag(String paymentFlag) {
		this.paymentFlag = paymentFlag;
		this.put("paymentFlag", paymentFlag);
	}
	public String getPaymentDetails() {
		return paymentDetails;
	}
	public void setPaymentDetails(String paymentDetails) {
		this.paymentDetails = paymentDetails;
		this.put("paymentDetails", paymentDetails);

	}
}
