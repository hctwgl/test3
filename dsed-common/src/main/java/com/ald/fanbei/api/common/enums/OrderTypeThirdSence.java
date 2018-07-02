package com.ald.fanbei.api.common.enums;

/**
 * @date 2017年8月24日 16:21:28
 * @author qiaopan
 *
 */
public enum OrderTypeThirdSence {
	DIANYING("400101","电影","DIANYING"),
	HUAFEI("400102","话费","HUAFEI"),
	JIAYOUKA("400103","加油卡","JIAYOUKA"),
	LIULIANG("400104","流量","LIULIANG"),
	SHENGXIAN("400105","生鲜","SHENGXIAN"),
	WAIMAI("400106","外卖","WAIMAI");
	
	private String code;
	private String name;
	private String nickName;
	private 
	OrderTypeThirdSence(String code,String name,String nickName){
		this.code = code;
		this.name = name;
		this.nickName = nickName;
	}
	
	public static String getCodeByNickName(String nickName) {
		for(OrderTypeThirdSence orderTypeThirdSence :OrderTypeThirdSence.values()){
			if(orderTypeThirdSence.getNickName().equals(nickName)){
				return orderTypeThirdSence.getCode();
			}
		}
		return null;

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
