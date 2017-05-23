package com.ald.fanbei.api.common.enums;

/**
 * 
 *@类描述：微信订单来源
 *@author 陈金虎 2017年1月16日 下午11:48:42
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum PayOrderSource {

	ORDER("ORDER","订单"),
	BRAND_ORDER("BRAND_ORDER","品牌订单"),
	BORROW("BORROW","借款"),
	BORROWCASH("BORROWCASH","借钱"),
	REPAYMENTCASH("REPAYMENTCASH","借钱还款"),
	RENEWAL_PAY("RENEWAL_PAY", "续期支付"),
	REPAYMENT("REPAYMENT","还款");

	public String code;
	public String name;

	private PayOrderSource(String code,String name) {
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
