package com.ald.fanbei.api.common.enums;

/**
 * 第三方跳转链接类型
 * @类描述:
 *
 * @auther caihuan 2017年8月7日
 * @注意:本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum ThirdPartyLinkType {

	LOAN_BANNER("loanBanner","借贷超市banner跳转");
	
	private String code;
    private String name;
    
    ThirdPartyLinkType(String code,String name){
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
