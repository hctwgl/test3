package com.ald.fanbei.api.biz.service.boluome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DigestUtil;


public class BoluoMeCore {
	
	public static final String CUSTOMER_USER_ID = "customerUserId";
	public static final String CUSTOMER_USER_PHONE = "customerUserPhone";
	public static final String TIME_STAMP = "timestamp";
	public static final String SIGN = "sign";

    /** 
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, String> params) {

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        String prestr = "";

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }

        return prestr;
    }
    
    
    /** 
     * MD5(APPKEY + CUSTOMER_USER_ID + CUSTOMER_USER_PHONE + CUSTOMER_USER_PHONE +APP_SECRET);
     * @param params
     * @return
     */
    public static String buildSignStr(Map<String, String> params) {
    	
    	String customerId = params.get(CUSTOMER_USER_ID);
    	String customerUserPhone  = params.get(CUSTOMER_USER_PHONE);
    	String timestamp  = params.get(TIME_STAMP);
    	
    	String beforeSign = 
    			AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_BOLUOME_APPKEY), ConfigProperties.get(Constants.CONFKEY_AES_KEY)) 
    			+ customerId + customerUserPhone + timestamp 
    			+ AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_BOLUOME_SECRET), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
    			
    	String sign = DigestUtil.MD5(beforeSign);
    	
        return sign;
    }
    

}
