/**
 * 
 */
package com.ald.fanbei.api.common.enums;
import java.util.HashMap;
import java.util.Map;
/**
 * @类描述：
 * @author suweili 2017年3月31日下午5:19:12
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum TongdunEventEnmu {
	LOGIN_ANDROID("login_professional_android","android_login", "819453cc372b4e4d8d465ba19f2b0ba7"),
	LOGIN_IOS("login_professional_ios","ios_login", "6d16b93b8da74ba9a522cb523bbf2fa6"),
	LOGIN_WEB("login_professional_web","web_login","d8e4d175ec074e6eac1b71331d2f6135"),
	REGISTER_ANDROID("register_professional_android","android_regist","819453cc372b4e4d8d465ba19f2b0ba7"),
	REGISTER_IOS("register_professional_ios","ios_regist","6d16b93b8da74ba9a522cb523bbf2fa6"),
	REGISTER_WEB("register_professional_web","web_regist","d8e4d175ec074e6eac1b71331d2f6135"),
	MARKETING_ANDROID("marketing_professional_android","android_marketing","819453cc372b4e4d8d465ba19f2b0ba7"),
	MARKETING_IOS("marketing_professional_ios","ios_marketing","6d16b93b8da74ba9a522cb523bbf2fa6"),
	MARKETING_WEB("marketing_professional_web","web_marketing","d8e4d175ec074e6eac1b71331d2f6135"),
	TRADE_ANDROID("trade_professional_android","android_trade","819453cc372b4e4d8d465ba19f2b0ba7"),
	TRADE_IOS("trade_professional_ios","ios_trade","6d16b93b8da74ba9a522cb523bbf2fa6"),
	TRADE_WEB("trade_professional_web","web_trade","d8e4d175ec074e6eac1b71331d2f6135"),
	ACTIVATE_ANDROID("Activate_android_20170316","android_activate","819453cc372b4e4d8d465ba19f2b0ba7");
	
	
	private static Map<String,TongdunEventEnmu> menueMap = null;

    public static TongdunEventEnmu getMenueByCode(String key) {
        for (TongdunEventEnmu enumItem : TongdunEventEnmu.values()) {
            if (enumItem.getClientOperate().equals(key)) {
                return enumItem;
            }
        }
        return null;
    }

    
    public static Map<String,TongdunEventEnmu> getCodeRoleTypeMap(){
        if(menueMap != null && menueMap.size() > 0){
            return menueMap;
        }
        menueMap = new HashMap<String, TongdunEventEnmu>();
        for(TongdunEventEnmu item:TongdunEventEnmu.values()){
        	menueMap.put(item.getClientOperate(), item);
        }
        return menueMap;
    }
	
	private String eventId;
	private String clientOperate;
	private String secretKey;
	
	private TongdunEventEnmu(String eventId, String clientOperate,String secretKey) {
		this.eventId = eventId;
		this.clientOperate = clientOperate;
		this.secretKey = secretKey;
	}
	
	
	
	public String getClientOperate() {
		return clientOperate;
	}

	public void setClientOperate(String clientOperate) {
		this.clientOperate = clientOperate;
	}

	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	public String getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	
}
