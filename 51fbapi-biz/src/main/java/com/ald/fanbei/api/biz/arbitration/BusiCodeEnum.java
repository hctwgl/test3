package com.ald.fanbei.api.biz.arbitration;

/**
 * 业务枚举类
 * @Title：BusiCodeEnum.java
 * @Author：wangyihui
 * @Date 2017年5月10日 下午5:29:40
 * @Description
 */
public enum BusiCodeEnum {

	GETORDERINFO("GETORDERINFO", "获取案件订单"), //获取案件订单信息
	GETFUNDINFO("GETFUNDINFO", "获取案件订单相关金额"), //获取案件订单相关金额
	GETRESPONDENT("GETRESPONDENT", "获取被申请人"), //获取被申请人信息
	GETCREDITAGREEMENT("GETCREDITAGREEMENT", "获取借款协议"), //获取借款协议
	GETCREDITINFO("GETCREDITINFO", "获取借款"), //获取借款信息
	GETREFUNDINFO("GETREFUNDINFO", "获取还款"), //获取还款信息
	GETPAYVOUCHER("GETPAYVOUCHER", "获取打款凭证"), //获取打款凭证
	GETPROMPTINFO("GETPROMPTINFO", "获取打款记录"), //获取打款记录
	SETSTATUS("SETSTATUS", "案件订单状态通知"), //案件订单状态通知
	GETLITIGANTS("GETLITIGANTS", "获取案件订单相关当事人信息"); //获取案件订单相关当事人信息
	
	BusiCodeEnum(String code, String title){
		this.code = code;
		this.title = title;
	}
	private String code;//编码
	private String title;//标题
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
}
