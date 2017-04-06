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
	LOGIN_ANDROID("Login_android_20170310","android_login", "5c57003c8f314dc4a1b018d8ad4205a0"),
	LOGIN_IOS("Login_ios_20170310","ios_login", "27bf15defb8d4038b7a9373e35025e01"),
	LOGIN_WEB("Login_web_20170310","web_login","a3eec6aecf0c45e296dd94b8845f3b39"),
	REGISTER_ANDROID("Register_android_20170310","android_regist","5c57003c8f314dc4a1b018d8ad4205a0"),
	REGISTER_IOS("Register_ios_20170310","ios_regist","27bf15defb8d4038b7a9373e35025e01"),
	REGISTER_WEB("Register_web_20170310","web_regist","a3eec6aecf0c45e296dd94b8845f3b39"),
	MARKETING_ANDROID("Marketing_android_20170310","android_marketing","5c57003c8f314dc4a1b018d8ad4205a0"),
	MARKETING_IOS("Marketing_ios_20170310","ios_marketing","27bf15defb8d4038b7a9373e35025e01"),
	MARKETING_WEB("Marketing_web_20170310","web_marketing","a3eec6aecf0c45e296dd94b8845f3b39"),
	TRADE_ANDROID("Trade_android_20170310","android_trade","5c57003c8f314dc4a1b018d8ad4205a0"),
	TRADE_IOS("Trade_ios_20170310","ios_trade","27bf15defb8d4038b7a9373e35025e01"),
	TRADE_WEB("Trade_web_20170310","web_trade","a3eec6aecf0c45e296dd94b8845f3b39"),
	ACTIVATE_ANDROID("Activate_android_20170316","android_activate","5c57003c8f314dc4a1b018d8ad4205a0");
	
	
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
