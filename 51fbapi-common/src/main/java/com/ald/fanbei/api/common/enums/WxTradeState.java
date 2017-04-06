package com.ald.fanbei.api.common.enums;

/**
 * 
 *@类描述：微信交易状态
 *@author 陈金虎 2017年1月16日 下午11:48:42
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum WxTradeState {
	/**
	 * NOTPAY—未支付
	 */
	NOTPAY("NOTPAY","未支付"),
	/**
	 * NOPAY--未支付(输入密码或确认支付超时)
	 */
	NOPAY("NOPAY","未支付"),

	/**
	 * USERPAYING--用户支付中
	 */
	USERPAYING("USERPAYING","用户支付中"),

	/**
	 * SUCCESS—支付成功
	 */
	SUCCESS("SUCCESS","支付成功"),

	/**
	 * REFUND—转入退款
	 */
	REFUND("REFUND","转入退款"),

	/**
	 * REVOKED—已撤销
	 */
	REVOKED("REVOKED","已撤销"),

	/**
	 * PAYERROR--支付失败(其他原因，如银行返回失败)
	 */
	PAYERROR("PAYERROR","支付失败"),

	/**
	 * CLOSED—已关闭
	 */
	CLOSED("CLOSED","已关闭");

	public String code;
	public String name;

	private WxTradeState(String code,String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
