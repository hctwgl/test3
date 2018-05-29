package com.ald.fanbei.api.common.enums;

/**
 * 
 * @类描述：
 * @author Jiang Rongbo 2018年2月7日下午2:34:01
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum RiskScene {

	FUND_XJD_PASS("80","FUND_XJD_PASS","公积金现金贷(强风控通过)"),
	FUND_XJD_UNPASS("81","FUND_XJD_UNPASS","公积金现金贷(强风控不通过)"),
	FUND_BLD("85","FUND_BLD","公积金白领贷"),
	FUND_ONLINE("86","FUND_ONLINE","公积金消费分期线上(强风控通过)"),
	INSURANCE_XJD_PASS("70","INSURANCE_XJD_PASS","社保现金贷（强风控通过）"),
	INSURANCE_XJD_UNPASS("71","INSURANCE_XJD_UNPASS","社保现金额（强风控不通过）"),
	INSURANCE_BLD("75","INSURANCE_BLD","社保白领贷"),
	INSURANCE_ONLINE("76","INSURANCE_ONLINE","社保消费分期线上(强风控通过)"),
	ALIPAY_XJD_PASS("93","ALIPAY_XJD_PASS","支付宝现金贷（强风控通过）"),
	ALIPAY_XJD_UNPASS("94","ALIPAY_XJD_UNPASS","支付宝现金贷(强风控不通过)"),
	ALIPAY_BLD("95","ALIPAY_BLD","支付宝白领贷"),
	ALIPAY_ONLINE("96","ALIPAY_ONLINE","支付宝消费分期线上(强风控通过)"),
	ZHENGXIN_XJD_PASS("110","ZHENGXIN_XJD_PASS","现金贷(强风控通过)"),
	ZHENGXIN_XJD_UNPASS("111","ZHENGXIN_XJD_UNPASS","现金贷(强风控不通过)"),
	BANK_BLD("155","BANK_BLD","网银白领贷"),
	CARDMAIL_XJD_PASS("60","CARDMAIL_XJD_PASS","信用卡邮箱现金贷(强风控通过)"),
	CARDMAIL_ONLINE("66","CARDMAIL_ONLINE","信用卡邮箱消费分期线上(强风控通过)"),
	BUBBLE_BLD("172","BUBBLE_BLD","冒泡白领贷"),
	BUBBLE_XJD_PASS("170","BUBBLE_XJD_PASS","冒泡现金贷(强风控通过)"),
	BUBBLE_XJD_UNPASS("171","BUBBLE_XJD_UNPASS","冒泡现金贷(强风控不通过)"),
	BUBBLE_ONLINE("173","BUBBLE_ONLINE","冒泡消费分期线上(强风控通过)"),
	;
	
    private String code;
    private String name;
    private String desc;
    
	private RiskScene(String code, String name, String desc) {
		this.code = code;
		this.name = name;
		this.desc = desc;
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
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
    
}
