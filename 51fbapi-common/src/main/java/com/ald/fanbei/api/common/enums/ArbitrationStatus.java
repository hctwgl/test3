package com.ald.fanbei.api.common.enums;

/**
 * @类描述：在线仲裁系统状态类
 * @author fanmanfu
 * @version 创建时间：2018年4月13日 下午1:59:34
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

public enum ArbitrationStatus {
	
    	ONE("1","ONE","識別狀態"),
    	
    	ZERO("0","ZERO","識別狀態"),
    
	SUCCESS("0000","SUCCESS","成功"),
	
	FAILURE("9999","FAILURE","失败"),
	
	REPAYSUCCESS("Y","REPAYSUCCESS","还款成功"),
	
	REPAYFAILURE("N","REPAYSUCCESS","还款失败"),

	FINSH("FINSH","FINSH","已结清"),
	
	TRANSED("TRANSED","TRANSED","借款成功"),
	
	CLOSED("CLOSED","CLOSED","借款失败"),
	
	ORDERNOTEXIST("221","order not exist","订单信息不存在"),
	
	BORROWCASHV1("222","order is borrowCashV1","订单为搭售V1"),
	
	OLDBORROWCASH("223","order is oldborrowCash","订单为老版借款"),
	
	PERIODSTYPE("D","PERIODSTYPE","Y表示年；M表示月；D表示日；Z表示周；")
	
	;
	
	private String code;
    private String name;
    private String dec;
    
    
	private ArbitrationStatus() {
	}


	private ArbitrationStatus(String code, String name, String dec) {
		this.code = code;
		this.name = name;
		this.dec = dec;
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


	public String getDec() {
		return dec;
	}


	public void setDec(String dec) {
		this.dec = dec;
	}
    
	
    
}
