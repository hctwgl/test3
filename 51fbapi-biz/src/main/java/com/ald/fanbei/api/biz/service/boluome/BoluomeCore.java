package com.ald.fanbei.api.biz.service.boluome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DigestUtil;


public class BoluomeCore {
	
	public static final String CUSTOMER_USER_ID = "customerUserId";
	public static final String CUSTOMER_USER_PHONE = "customerUserPhone";
	public static final String TIME_STAMP = "timestamp";
	
	public static final String ORDER_ID = "orderId";
	public static final String PLANT_FORM = "plantform";
	public static final String ORDER_TYPE = "orderType";
	public static final String ORDER_TITLE = "orderTitle";
	public static final String USER_ID = "userId";
	public static final String USER_PHONE = "userPhone";
	public static final String PRICE = "price";
	public static final String STATUS = "status";
	public static final String DISPLAY_STATUS = "displayStatus";
	public static final String CREATED_TIME = "createdTime";
	public static final String EXPIRED_TIME = "expiredTime";
	public static final String DETAIL_URL = "detailUrl";
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
     * 除去数组中的空值和签名参数
     * @param sArray 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    public static Map<String, String> paraFilter(Map<String, String> sArray) {

        Map<String, String> result = new HashMap<String, String>();

        if (sArray == null || sArray.size() <= 0) {
            return result;
        }

        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (StringUtils.isEmpty(value) || key.equalsIgnoreCase(SIGN)) {
                continue;
            }
            result.put(key, value);
        }

        return result;
    }
    
    
    /** 
     * 把数组所有元素排序，然后拼接参数
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String concatParams(Map<String, String> params) {

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            sb.append(params.get(key));
        }

        return sb.toString();
    }
    
    /** 
     * MD5(APPKEY + CUSTOMER_USER_ID + CUSTOMER_USER_PHONE + CUSTOMER_USER_PHONE +APP_SECRET);
     * @param params
     * @return
     */
    public static String buildSignStr(Map<String, String> params) {
    	String beforeSign = 
    			AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_BOLUOME_APPKEY), ConfigProperties.get(Constants.CONFKEY_AES_KEY)) 
    			+ concatParams(params)
    			+ AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_BOLUOME_SECRET), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
    	
        return DigestUtil.MD5(beforeSign);
    }
    

}
