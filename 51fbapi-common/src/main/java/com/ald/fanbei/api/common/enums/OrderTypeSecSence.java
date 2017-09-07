package com.ald.fanbei.api.common.enums;

/**
 * @date 2017年8月24日 16:21:28
 * @author qiaopan
 *
 */
public enum OrderTypeSecSence {
	BOLUOME("4001","逛逛","BOLUOME"),
	AGENTBUY("4002","代买","AGENTBUY"),
	SELFSUPPORT("4003","自营","SELFSUPPORT"),
	TRADE("4004","商圈","TRADE");

	
	
	private String code;
	private String name;
	private String nickName;
	
	public static String getCodeByNickName(String nickName) {
		for(OrderTypeSecSence orderTypeSecSence :OrderTypeSecSence.values()){
			if(orderTypeSecSence.getNickName().equals(nickName)){
				return orderTypeSecSence.getCode();
			}
		}
		return null;

	}
	
	OrderTypeSecSence(String code,String name,String nickName){
		this.code= code;
		this.name= name;
		this.nickName = nickName;
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

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
}
